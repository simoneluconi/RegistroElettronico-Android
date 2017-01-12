package com.sharpdroid.registroelettronico.Adapters;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.API.SpiaggiariApiClient;
import com.sharpdroid.registroelettronico.Databases.FilesDB;
import com.sharpdroid.registroelettronico.Interfaces.API.File;
import com.sharpdroid.registroelettronico.R;

import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.Headers;

import static com.sharpdroid.registroelettronico.Utils.Metodi.getFileNamefromHeaders;
import static com.sharpdroid.registroelettronico.Utils.Metodi.writeResponseBodyToDisk;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileHolder> {
    final static String TAG = FileAdapter.class.getSimpleName();

    private final SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy", Locale.ITALIAN);
    FilesDB db;
    private Context mContext;
    private CoordinatorLayout mCoordinatorLayout;
    private List<File> CVDataList;

    public FileAdapter(Context mContext, CoordinatorLayout mCoordinatorLayout, FilesDB db) {
        this.mContext = mContext;
        this.mCoordinatorLayout = mCoordinatorLayout;
        CVDataList = new ArrayList<>();
        this.db = db;
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

        java.io.File dir = new java.io.File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS).toString() +
                        java.io.File.separator +
                        "Registro Elettronico" + java.io.File.separator + "Didattica");

        holder.title.setText(file.getName().trim());
        holder.date.setText(formatter.format(file.getDate()));

        FilesDB tmpdb = new FilesDB(mContext);
        if (file.isLink()) {
            holder.image.setImageResource(R.drawable.link);
        } else {
            holder.image.setImageResource(R.drawable.file);
        }

        tmpdb.close();

        holder.mRelativeLayout.setOnClickListener(v -> {

            if (!file.isLink()) {
                Snackbar DownloadProgressSnak = Snackbar.make(mCoordinatorLayout, R.string.download_in_corso, Snackbar.LENGTH_INDEFINITE);

                if (!dir.exists()) dir.mkdir();

                if (!db.isPresent(file.getId(), file.getCksum())) {
                    DownloadFile(file, dir, db, DownloadProgressSnak, true);
                } else {
                    String filename = db.getFileName(file.getId(), file.getCksum());
                    java.io.File f = new java.io.File(dir + java.io.File.separator + filename);
                    if (f.exists())
                        openfile(f);
                    else DownloadFile(file, dir, db, DownloadProgressSnak, false);
                }

                db.close();
            } else openlink(file.getLink(), mContext);
        });
    }

    private void DownloadFile(File file, java.io.File dir, FilesDB db, Snackbar DownloadProgressSnak, boolean addRecord) {
        DownloadProgressSnak.show();
        new SpiaggiariApiClient(mContext).getDownload(file.getId(), file.getCksum())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(files_file -> {
                    Headers headers = files_file.headers();
                    String filename = getFileNamefromHeaders(headers);
                    java.io.File f = new java.io.File(dir + java.io.File.separator + filename);
                    writeResponseBodyToDisk(files_file.body(), f);
                    if (addRecord)
                        db.addRecord(filename, file.getId(), file.getCksum());
                    askfileopen(f, DownloadProgressSnak);

                }, error -> {
                    error.printStackTrace();
                    DownloadProgressSnak.setText(mContext.getResources().getString(R.string.download_fallito, error.getCause()));
                    DownloadProgressSnak.setDuration(Snackbar.LENGTH_SHORT).show();
                });
    }

    private void askfileopen(java.io.File file, Snackbar DownloadProgressSnak) {
        DownloadProgressSnak.setText(mContext.getString(R.string.file_downloaded, file.getName()));
        DownloadProgressSnak.setAction(R.string.open, v -> openfile(file));
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
