package com.sharpdroid.registroelettronico.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.Databases.Entities.File;
import com.sharpdroid.registroelettronico.Fragments.FragmentFiles;
import com.sharpdroid.registroelettronico.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileHolder> {
    private final SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy", Locale.ITALIAN);
    private DownloadListener listener;
    private Context mContext;
    private List<File> CVDataList = new ArrayList<>();

    public FileAdapter(FragmentFiles fragmentFiles) {
        this.mContext = fragmentFiles.getActivity();
        listener = fragmentFiles;
    }

    public void addAll(List<File> data) {
        CVDataList.addAll(data);
        notifyDataSetChanged();
    }

    public void clear() {
        CVDataList.clear();
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

        holder.title.setText(!TextUtils.isEmpty(file.getContentName().trim()) ? file.getContentName().trim() : String.format("[%1$s]", mContext.getString(R.string.senza_nome)));
        holder.date.setText(formatter.format(file.getDate()));

        if (file.getType().equals("link")) {
            holder.image.setImageResource(R.drawable.link);
        } else {
            holder.image.setImageResource(R.drawable.file);
        }

        holder.mRelativeLayout.setOnClickListener(v -> listener.onFileClick(file));
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
    }

    public interface DownloadListener {
        void onFileClick(@NotNull File file);
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
