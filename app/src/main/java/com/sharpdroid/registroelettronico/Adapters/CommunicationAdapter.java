package com.sharpdroid.registroelettronico.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sharpdroid.registroelettronico.API.SpiaggiariApiClient;
import com.sharpdroid.registroelettronico.Databases.CommunicationsDB;
import com.sharpdroid.registroelettronico.Interfaces.API.Communication;
import com.sharpdroid.registroelettronico.R;

import java.io.File;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.Headers;

import static com.sharpdroid.registroelettronico.Utils.Metodi.getFileNamefromHeaders;
import static com.sharpdroid.registroelettronico.Utils.Metodi.writeResponseBodyToDisk;

public class CommunicationAdapter extends RecyclerView.Adapter<CommunicationAdapter.CommunicationHolder> implements Filterable {
    private static final String TAG = CommunicationAdapter.class.getSimpleName();
    private final List<Communication> CVDataList = new CopyOnWriteArrayList<>();
    private final Context mContext;
    private final CoordinatorLayout mCoordinatorLayout;
    private final SimpleDateFormat formatter = new SimpleDateFormat("d MMM", Locale.ITALIAN);
    private List<Communication> filtered = new CopyOnWriteArrayList<>();
    private ItemFilter mFilter = new ItemFilter();
    private CommunicationsDB db;

    public CommunicationAdapter(Context mContext, CoordinatorLayout mCoordinatorLayout, CommunicationsDB db) {
        this.mContext = mContext;
        this.mCoordinatorLayout = mCoordinatorLayout;
        this.db = db;
    }

    public void addAll(Collection<Communication> list) {
        CVDataList.addAll(list);
        filtered.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        CVDataList.clear();
        filtered.clear();
        notifyDataSetChanged();
    }

    @Override
    public CommunicationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_communications, parent, false);
        return new CommunicationHolder(v);
    }

    @Override
    public void onBindViewHolder(CommunicationHolder ViewHolder, int i) {
        final Communication communication = filtered.get(ViewHolder.getAdapterPosition());

        ViewHolder.Title.setText(communication.getTitle().trim());
        ViewHolder.Date.setText(formatter.format(communication.getDate()));
        ViewHolder.Type.setText(communication.getType());

        ViewHolder.mRelativeLayout.setOnClickListener(v -> {
            // QUANDO L'UTENTE CLICCA SCARICARE MAGGIORI INFORMAZIONI
            new SpiaggiariApiClient(mContext)
                    .getCommunicationDesc(communication.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(communicationDescription -> {
                        MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
                                .title(communicationDescription.getLongTitle().trim())
                                .content(communicationDescription.getDesc().trim())
                                .positiveText(R.string.ok);

                        if (communicationDescription.isAttachment()) {
                            dialog.neutralText(db.isPresent(communication.getId()) ? "APRI" : "SCARICA");

                            dialog.onNeutral((dialog1, which) -> {
                                Snackbar DownloadProgressSnack = Snackbar.make(mCoordinatorLayout, R.string.download_in_corso, Snackbar.LENGTH_INDEFINITE);

                                File dir = new File(
                                        Environment.getExternalStorageDirectory() +
                                                File.separator +
                                                "Registro Elettronico" + File.separator + "Circolari");

                                if (!dir.exists() && !dir.mkdirs()) {
                                    Log.d(TAG, "Failed to create download directory");
                                    return;
                                }

                                if (!db.isPresent(communication.getId())) {
                                    DownloadFile(communication, dir, db, DownloadProgressSnack, true);
                                } else {
                                    String filename = db.getFileName(communication.getId());
                                    File file = new File(dir + File.separator + filename);
                                    if (file.exists())
                                        openfile(file);
                                    else
                                        DownloadFile(communication, dir, db, DownloadProgressSnack, false);
                                }

                                db.close();
                            });
                        }

                        dialog.build().show();

                    }, Throwable::printStackTrace);
        });
    }

    @Override
    public int getItemCount() {
        return filtered.size();
    }

    private void askfileopen(File file, Snackbar DownloadProgressSnak) {
        DownloadProgressSnak.setText(mContext.getString(R.string.file_downloaded, file.getName()));
        DownloadProgressSnak.setAction(R.string.open, v -> openfile(file));
        DownloadProgressSnak.show();
    }

    private void DownloadFile(Communication communication, File dir, CommunicationsDB db, Snackbar DownloadProgressSnak, boolean addRecord) {
        DownloadProgressSnak.show();
        new SpiaggiariApiClient(mContext)
                .getCommunicationDownload(communication.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(communication_file -> {
                    Headers headers = communication_file.headers();
                    String filename = getFileNamefromHeaders(headers);
                    File file = new File(dir + File.separator + filename);
                    writeResponseBodyToDisk(communication_file.body(), file);
                    if (addRecord)
                        db.addRecord(filename, communication.getId());
                    askfileopen(file, DownloadProgressSnak);
                }, error -> {
                    error.printStackTrace();
                    DownloadProgressSnak.setText(mContext.getResources().getString(R.string.download_fallito, error.getLocalizedMessage()));
                    DownloadProgressSnak.setDuration(Snackbar.LENGTH_SHORT).show();
                });

    }

    private void openfile(File file) {
        String mime = URLConnection.guessContentTypeFromName(file.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", file), mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Snackbar.make(mCoordinatorLayout, mContext.getResources().getString(R.string.missing_app, file.getName()), Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    class CommunicationHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.relative_layout)
        RelativeLayout mRelativeLayout;
        @BindView(R.id.title)
        TextView Title;
        @BindView(R.id.date)
        TextView Date;
        @BindView(R.id.type)
        TextView Type;

        CommunicationHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (!TextUtils.isEmpty(constraint)) {
                List<Communication> filt = new LinkedList<>();

                for (Communication c : CVDataList) {
                    if (c.getTitle().toLowerCase().contains(constraint.toString().toLowerCase()))
                        filt.add(c);
                }
                results.values = filt;
                results.count = filt.size();
            } else {
                results.values = CVDataList;
                results.count = CVDataList.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered = (List<Communication>) results.values;
            notifyDataSetChanged();
        }
    }
}
