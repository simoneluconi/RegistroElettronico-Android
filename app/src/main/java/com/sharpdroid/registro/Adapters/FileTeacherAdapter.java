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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.sharpdroid.registro.Utils.Metodi.NomeDecente;
import static com.sharpdroid.registro.Utils.Metodi.getListLayouts;

public class FileTeacherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final static String TAG = FileTeacherAdapter.class.getSimpleName();

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Integer> listLayouts = new ArrayList<>();
    private List<FileTeacher> data = new ArrayList<>();
    private SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

    private int current_subheader, current_folder = 0;

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
                SubheaderHolder subHolder = (SubheaderHolder) holder;

                String profHeader = data.get(current_subheader).getName();

                subHolder.teacher.setText(NomeDecente(profHeader));

                current_folder = 0;
                current_subheader++;
                break;
            case R.layout.adapter_folder:
                FileTeacherHolder folderHolder = (FileTeacherHolder) holder;

                Folder folder = data.get(current_subheader - 1).getFolders().get(current_folder);
                String last = folder.getLast();

                folderHolder.teacher.setText(NomeDecente(folder.getName().trim()));
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
        this.data = data;
        listLayouts = getListLayouts(this.data);
        notifyDataSetChanged();
    }


    public void clear() {
        data.clear();
        listLayouts.clear();
        current_subheader = current_folder = 0;
    }

    private class SubheaderHolder extends RecyclerView.ViewHolder {
        TextView teacher;

        SubheaderHolder(View layout) {
            super(layout);
            teacher = (TextView) layout.findViewById(R.id.title);
        }
    }

    private class FileTeacherHolder extends RecyclerView.ViewHolder {
        TextView teacher, date;

        FileTeacherHolder(View layout) {
            super(layout);
            teacher = (TextView) layout.findViewById(R.id.title);
            date = (TextView) layout.findViewById(R.id.date);
        }
    }

}
