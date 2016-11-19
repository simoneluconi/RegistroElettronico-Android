package com.sharpdroid.registro.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharpdroid.registro.Interfaces.File;
import com.sharpdroid.registro.R;

import java.util.List;

import butterknife.ButterKnife;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileHolder> {

    private Context mContext;
    private List<File> CVDataList;

    public FileAdapter(Context mContext) {
        this.mContext = mContext;
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
        File current = CVDataList.get(position);



    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class FileHolder extends RecyclerView.ViewHolder {


        public FileHolder(View layout) {
            super(layout);
            ButterKnife.bind(this, layout);
        }
    }
}
