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
import java.util.List;

public class FileTeacherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<FileTeacher> CVDataList;
    List<Integer> listLayouts;

    public FileTeacherAdapter(Context mContext) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
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
                int position_headers = countHeadersBefore(position);

                SubheaderHolder subheaderHolder = (SubheaderHolder) holder;

                subheaderHolder.teacher.setText(CVDataList.get(position_headers).getName().trim());

                break;
            case R.layout.adapter_folder:
                // TODO: 17/11/2016 count dall'elemento dopo l'ultimo header
                int position_folder;


                break;

        }

    }

    int countHeadersBefore(int i) {
        int acc = 0;
        for (int j = 0; j < i; j++) {
            if (listLayouts.get(j) == R.layout.subheader) acc++;
        }
        return acc;
    }

    @Override
    public int getItemViewType(int position) {
        return listLayouts.get(position);
    }

    List<Integer> getListLayouts() {
        List<Integer> list = new ArrayList<>();

        for (FileTeacher fileTeacher : CVDataList) {
            list.add(R.layout.subheader);
            for (Folder folder : fileTeacher.getFolders()) {
                list.add(R.layout.adapter_folder);
            }
        }
        return list;
    }

    @Override
    public int getItemCount() {
        int acc = CVDataList.size();    //Numero di prof
        for (FileTeacher f : CVDataList) //Numero di cartelle per ogni prof
            acc += f.getFolders().size();

        return acc;
    }

    public void setAbsences(List<FileTeacher> data) {
        CVDataList = data;
        listLayouts = getListLayouts();
        notifyDataSetChanged();
    }

    public void clear() {
        CVDataList = null;
        listLayouts.clear();
    }

    class SubheaderHolder extends RecyclerView.ViewHolder {
        public TextView teacher;

        public SubheaderHolder(View layout) {
            super(layout);
            teacher = (TextView) layout.findViewById(R.id.title);
        }
    }

    class FileTeacherHolder extends RecyclerView.ViewHolder {
        public TextView teacher, date;

        public FileTeacherHolder(View layout) {
            super(layout);
            teacher = (TextView) layout.findViewById(R.id.title);
            date = (TextView) layout.findViewById(R.id.date);
        }
    }

}
