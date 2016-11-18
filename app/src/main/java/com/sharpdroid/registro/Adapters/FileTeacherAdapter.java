package com.sharpdroid.registro.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registro.Interfaces.FileTeacher;
import com.sharpdroid.registro.Interfaces.Folder;
import com.sharpdroid.registro.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.sharpdroid.registro.Utils.Metodi.getHashmapFromFileTeacher;
import static com.sharpdroid.registro.Utils.Metodi.getListLayouts;


public class FileTeacherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Integer> listLayouts;
    private HashMap<String, List<Folder>> data; //  <Prof, <Cartelle>>

    private int current_subheader = 0, current_folder = 0;

    public FileTeacherAdapter(Context mContext) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        data = new HashMap<>();
        listLayouts = new ArrayList<>();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case R.layout.subheader:
                return new SubheaderHolder(mInflater.inflate(viewType, parent, false));
            case R.layout.adapter_folder:
                return new FileTeacherHolder(mInflater.inflate(viewType, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int layout = getItemViewType(position);

        switch (layout) {
            case R.layout.subheader:
                current_folder = 0;

                SubheaderHolder subHolder = (SubheaderHolder) holder;

                String profHeader = ((String) data.keySet().toArray()[current_subheader]).trim();

                subHolder.teacher.setText(profHeader);
                current_subheader++;

                break;
            case R.layout.adapter_folder:

                FileTeacherHolder folderHolder = (FileTeacherHolder) holder;

                String prof = ((String) data.keySet().toArray()[current_subheader]).trim();
                Folder folder = data.get(prof).get(current_folder);

                folderHolder.teacher.setText(prof);
                folderHolder.date.setText(folder.getLast());

                current_folder++;
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return listLayouts.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setAbsences(List<FileTeacher> data) {
        this.data = getHashmapFromFileTeacher(data);
        listLayouts = getListLayouts(this.data);
        notifyDataSetChanged();
    }


    public void clear() {
        data.clear();
        listLayouts.clear();
    }

    private class SubheaderHolder extends RecyclerView.ViewHolder {
        public TextView teacher;

        public SubheaderHolder(View layout) {
            super(layout);
            teacher = (TextView) layout.findViewById(R.id.title);
        }
    }

    private class FileTeacherHolder extends RecyclerView.ViewHolder {
        public TextView teacher, date;

        public FileTeacherHolder(View layout) {
            super(layout);
            teacher = (TextView) layout.findViewById(R.id.title);
            date = (TextView) layout.findViewById(R.id.date);
        }
    }

}
