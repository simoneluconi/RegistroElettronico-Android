package com.sharpdroid.registro.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sharpdroid.registro.API.SpiaggiariApiClient;
import com.sharpdroid.registro.Interfaces.API.Communication;
import com.sharpdroid.registro.R;

import java.io.File;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sharpdroid.registro.Utils.Metodi.writeResponseBodyToDisk;

public class CommunicationAdapter extends RecyclerView.Adapter<CommunicationAdapter.CommunicationHolder> {
    private final List<Communication> CVDataList;
    private final Context mContext;
    private final CoordinatorLayout mCoordinatorLayout;

    private final SimpleDateFormat formatter = new SimpleDateFormat("d MMMM", Locale.ITALIAN);

    public CommunicationAdapter(Context mContext, CoordinatorLayout mCoordinatorLayout, List<Communication> CVDataList) {
        this.CVDataList = CVDataList;
        this.mContext = mContext;
        this.mCoordinatorLayout = mCoordinatorLayout;
    }

    public void addAll(Collection<Communication> list) {
        CVDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        CVDataList.clear();
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
        final Communication communication = CVDataList.get(ViewHolder.getAdapterPosition());
        ViewHolder.Title.setText(communication.getTitle().trim());
        ViewHolder.Date.setText(formatter.format(communication.getDate()));
        ViewHolder.Type.setText(communication.getType());

        // TODO: 18/11/2016 aggiungere listener solo con allegati presenti
        ViewHolder.mRelativeLayout.setOnClickListener(v -> {
            Snackbar DownloadProgressSnak = Snackbar.make(mCoordinatorLayout, R.string.download_in_corso, Snackbar.LENGTH_INDEFINITE);

            File dir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS).toString() +
                            File.separator +
                            "Registro Elettronico");


            if (!dir.exists()) dir.mkdir();

            int index = fileexists(communication.getId(), dir);

            if (index < 0) {
                DownloadProgressSnak.show();
                new SpiaggiariApiClient(mContext).mService.getcommunicationDownload(communication.getId())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(communication_file -> {
                            String mime = communication_file.contentType().toString();
                            String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime);
                            File file = new File(dir + File.separator + communication.getId() + "." + ext);
                            writeResponseBodyToDisk(communication_file, file);
                            askfileopen(file, DownloadProgressSnak);
                        }, error -> {
                            DownloadProgressSnak.setText(mContext.getResources().getString(R.string.download_fallito, error.getCause()));
                            DownloadProgressSnak.setDuration(Snackbar.LENGTH_SHORT).show();
                        });

            } else openfile(index, dir);
        });
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
    }

    private void askfileopen(File file, Snackbar DownloadProgressSnak) {
        DownloadProgressSnak.setText(mContext.getString(R.string.file_downloaded, file.getName()));
        DownloadProgressSnak.setAction(R.string.open, v -> {
            openfile(file);
        });
        DownloadProgressSnak.show();
    }

    private void openfile(File file) {
        String mime = URLConnection.guessContentTypeFromName(file.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(FileProvider.getUriForFile(mContext, "com.sharpdroid.registro.fileprovider", file), mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Snackbar.make(mCoordinatorLayout, mContext.getResources().getString(R.string.missing_app, file.getName()), Snackbar.LENGTH_SHORT).show();
        }

    }

    private void openfile(int index, File dir) {
        File file = dir.listFiles()[index];
        openfile(file);
    }

    private int fileexists(int id, File dir) {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName().substring(0, files[i].getName().lastIndexOf("."));
            if (name.equals(String.valueOf(id))) return i;
        }

        return -1;
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
}
