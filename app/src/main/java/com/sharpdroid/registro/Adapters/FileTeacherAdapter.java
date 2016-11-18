package com.sharpdroid.registro.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registro.Interfaces.FileTeacher;
import com.sharpdroid.registro.Interfaces.Folder;
import com.sharpdroid.registro.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.sharpdroid.registro.Utils.Metodi.getHashmapFromFileTeacher;
import static com.sharpdroid.registro.Utils.Metodi.getListLayouts;


public class FileTeacherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final static String TAG = FileTeacherAdapter.class.getSimpleName();

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Integer> listLayouts;
    private HashMap<String, List<Folder>> data; //  <Prof, <Cartelle>>
    private SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

    private int current_subheader = -1, current_folder = 0;

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
                current_subheader++;
                current_folder = 0;

                SubheaderHolder subHolder = (SubheaderHolder) holder;

                String profHeader = new ArrayList<>(data.keySet()).get(current_subheader);

                subHolder.teacher.setText(profHeader);

                break;
            case R.layout.adapter_folder:

                FileTeacherHolder folderHolder = (FileTeacherHolder) holder;

                String prof = ((String) data.keySet().toArray()[current_subheader]).trim();
                Folder folder = data.get(prof).get(current_folder);
                String last = folder.getLast();

                folderHolder.teacher.setText(folder.getName().trim());
                try {
                    folderHolder.date.setText(dateFormat.format(apiFormat.parse(last.split("T")[0])));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

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
        return listLayouts.size();
    }

    public void setAbsences(List<FileTeacher> data) {
        this.data = getHashmapFromFileTeacher(data);
        listLayouts = getListLayouts(this.data);
        notifyDataSetChanged();
    }


    public void clear() {
        data.clear();
        listLayouts.clear();
        current_subheader = -1;
        current_folder = 0;
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
