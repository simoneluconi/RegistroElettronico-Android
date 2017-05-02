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
import com.sharpdroid.registroelettronico.Databases.RegistroDB;
import com.sharpdroid.registroelettronico.Interfaces.Client.SuperCommunication;
import com.sharpdroid.registroelettronico.R;

import java.io.File;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
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
    private final List<SuperCommunication> CVDataList = new CopyOnWriteArrayList<>();
    private final List<SuperCommunication> filtered = new CopyOnWriteArrayList<>();
    private final Context mContext;
    private final CoordinatorLayout mCoordinatorLayout;
    private final SimpleDateFormat formatter = new SimpleDateFormat("d MMM", Locale.ITALIAN);
    private ItemFilter mFilter = new ItemFilter();
    private RegistroDB db;

    public CommunicationAdapter(Context mContext, CoordinatorLayout mCoordinatorLayout) {
        this.mContext = mContext;
        this.mCoordinatorLayout = mCoordinatorLayout;
        this.db = RegistroDB.Companion.getInstance(mContext);
    }

    public void addAll(Collection<SuperCommunication> list) {
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
        final SuperCommunication communication = filtered.get(i);

        ViewHolder.Title.setText(communication.getTitle().trim());
        ViewHolder.Date.setText(formatter.format(communication.getDate()));
        ViewHolder.Type.setText(communication.getType());

        ViewHolder.mRelativeLayout.setOnClickListener(v -> {
            if (communication.isContent()) {
                dialog(communication.getId(), communication.getTitle(), communication.getContent(), communication.isAttachment(), communication.getFilename()).build().show();
            } else {
                // SCARICA PIU' INFORMAZIONI
                new SpiaggiariApiClient(mContext)
                        .getCommunicationDesc(communication.getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(communicationDescription -> {
                            db.updateCommunication(communication.getId(), communicationDescription);
                            dialog(communication.getId(), communication.getTitle(), communicationDescription.getDesc(), communicationDescription.isAttachment(), null).build().show();
                        }, Throwable::printStackTrace);
            }
        });
    }

    private MaterialDialog.Builder dialog(int id, String title, String content, boolean attachment, String filename) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
                .title(title.trim())
                .content(content.trim())
                .positiveText(R.string.ok);

        return showActions(dialog, id, attachment, filename);
    }

    private MaterialDialog.Builder showActions(MaterialDialog.Builder dialog, int id, boolean attachment, String filename) {
        if (attachment) {
            dialog.neutralText(!TextUtils.isEmpty(filename) ? "APRI" : "SCARICA");

            dialog.onNeutral((dialog1, which) -> {
                Snackbar DownloadProgressSnack = Snackbar.make(mCoordinatorLayout, R.string.download_in_corso, Snackbar.LENGTH_INDEFINITE);

                File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "Registro Elettronico" + File.separator + "Circolari");

                if (!dir.exists() && !dir.mkdirs()) {
                    Log.d(TAG, "Failed to create download directory");
                    return;
                }

                if (TextUtils.isEmpty(filename)) {
                    download(id, dir, DownloadProgressSnack, true);
                } else {
                    File file = new File(dir + File.separator + filename);
                    if (file.exists())
                        open(file);
                    else
                        download(id, dir, DownloadProgressSnack, false);
                }


            });
        }
        return dialog;
    }

    @Override
    public int getItemCount() {
        return filtered.size();
    }

    private void download(int id, File dir, Snackbar DownloadProgressSnak, boolean addRecord) {
        DownloadProgressSnak.show();
        new SpiaggiariApiClient(mContext)
                .getCommunicationDownload(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(communication_file -> {
                    Headers headers = communication_file.headers();
                    String filename = getFileNamefromHeaders(headers);
                    File file = new File(dir + File.separator + filename);
                    writeResponseBodyToDisk(communication_file.body(), file);
                    if (addRecord)
                        db.setCommunicationFilename(id, filename);
                    onDownloadCompleted(file, DownloadProgressSnak);
                }, error -> {
                    error.printStackTrace();
                    DownloadProgressSnak.setText(mContext.getResources().getString(R.string.download_fallito, error.getLocalizedMessage()));
                    DownloadProgressSnak.setDuration(Snackbar.LENGTH_SHORT).show();
                });

    }

    private void onDownloadCompleted(File file, Snackbar DownloadProgressSnak) {
        DownloadProgressSnak.setText(mContext.getString(R.string.file_downloaded, file.getName()));
        DownloadProgressSnak.setAction(R.string.open, v -> open(file));
        DownloadProgressSnak.show();
    }

    private void open(File file) {
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
            List<SuperCommunication> list = new ArrayList<>();
            FilterResults filterResults = new FilterResults();

            if (!TextUtils.isEmpty(constraint)) {
                for (SuperCommunication c : CVDataList) {
                    if (c.getTitle().toLowerCase().contains(constraint.toString().toLowerCase()))
                        list.add(c);
                }
            } else {
                list.addAll(CVDataList);
            }

            filterResults.values = list;
            filterResults.count = list.size();
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered.clear();
            filtered.addAll((Collection<? extends SuperCommunication>) results.values);
            notifyDataSetChanged();
        }
    }
}
