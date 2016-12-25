package com.sharpdroid.registro.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.sharpdroid.registro.Interfaces.API.File;
import com.sharpdroid.registro.R;

import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sharpdroid.registro.Utils.Metodi.writeResponseBodyToDisk;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileHolder> {
    final static String TAG = FileAdapter.class.getSimpleName();

    private final SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy", Locale.ITALIAN);
    private Context mContext;
    private CoordinatorLayout mCoordinatorLayout;
    private List<File> CVDataList;

    public FileAdapter(Context mContext) {
        this.mContext = mContext;
        CVDataList = new ArrayList<>();
    }

    public void addAll(List<File> CVDataList) {
        this.CVDataList = CVDataList;
        notifyDataSetChanged();
    }

    public void clear() {
        CVDataList = null;
        notifyDataSetChanged();
    }

    @Override
    public FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_file, parent, false);
        return new FileHolder(view);
    }

    @Override
    public void onBindViewHolder(FileHolder holder, int position) {
        File file = CVDataList.get(position);

        holder.title.setText(file.getName().trim());
        holder.date.setText(formatter.format(file.getDate()));

        if (file.isLink()) {
            holder.image.setImageResource(R.drawable.link);
        }

        holder.mRelativeLayout.setOnClickListener(v -> {

            if (!file.isLink()) {
                Snackbar DownloadProgressSnak = Snackbar.make(mCoordinatorLayout, R.string.download_in_corso, Snackbar.LENGTH_INDEFINITE);

                java.io.File dir = new java.io.File(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS).toString() +
                                java.io.File.separator +
                                "Registro Elettronico");


                if (!dir.exists()) dir.mkdir();

                int index = fileexists(file.getId(), dir);

                if (index < 0) {
                    DownloadProgressSnak.show();
                    new SpiaggiariApiClient(mContext).mService.getDownload(file.getId(), file.getCksum())
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(files_file -> {
                                String mime = files_file.contentType().toString();
                                String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime);
                                java.io.File files = new java.io.File(dir + java.io.File.separator + file.getId() + "." + ext);
                                writeResponseBodyToDisk(files_file, files);
                                askfileopen(files, DownloadProgressSnak);
                            }, error -> {
                                DownloadProgressSnak.setText(mContext.getResources().getString(R.string.download_fallito, error.getCause()));
                                DownloadProgressSnak.setDuration(Snackbar.LENGTH_SHORT).show();
                            });

                } else openfile(index, dir);
            } else openlink("http://google.it/", mContext);
        });
    }

    private void askfileopen(java.io.File file, Snackbar DownloadProgressSnak) {
        DownloadProgressSnak.setText(mContext.getString(R.string.file_downloaded, file.getName()));
        DownloadProgressSnak.setAction(R.string.open, v -> {
            openfile(file);
        });
        DownloadProgressSnak.show();
    }

    private void openfile(java.io.File file) {
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

    private void openlink(String url, Context context) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }

    private void openfile(int index, java.io.File dir) {
        java.io.File file = dir.listFiles()[index];
        openfile(file);
    }

    private int fileexists(String id, java.io.File dir) {
        java.io.File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName().substring(0, files[i].getName().lastIndexOf("."));
            if (name.equals(id)) return i;
        }

        return -1;
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
    }

    class FileHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        public TextView title;
        @BindView(R.id.date)
        public TextView date;
        @BindView(R.id.circleImageView)
        public CircleImageView image;
        @BindView(R.id.relative_layout)
        RelativeLayout mRelativeLayout;

        FileHolder(View layout) {
            super(layout);
            ButterKnife.bind(this, layout);
        }
    }
}
