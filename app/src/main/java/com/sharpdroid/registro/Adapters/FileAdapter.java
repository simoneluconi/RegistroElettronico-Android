package com.sharpdroid.registro.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registro.Interfaces.File;
import com.sharpdroid.registro.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.sharpdroid.registro.Adapters.FolderAdapter.apiFormat;
import static com.sharpdroid.registro.Adapters.FolderAdapter.dateFormat;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileHolder> {
    final static String TAG = FileAdapter.class.getSimpleName();

    private Context mContext;
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
        try {
            holder.date.setText(dateFormat.format(apiFormat.parse(file.getDate().split("T")[0])));
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing DATE");
        }

        if (file.getType().equals("link")) {
            holder.image.setImageResource(R.drawable.link);
        }
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

        FileHolder(View layout) {
            super(layout);
            ButterKnife.bind(this, layout);
        }
    }
}
