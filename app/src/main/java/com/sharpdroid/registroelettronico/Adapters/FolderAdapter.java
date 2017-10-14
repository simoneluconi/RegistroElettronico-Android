package com.sharpdroid.registroelettronico.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.Databases.Entities.Folder;
import com.sharpdroid.registroelettronico.Databases.Entities.Teacher;
import com.sharpdroid.registroelettronico.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registroelettronico.Utils.Metodi.capitalizeEach;

public class FolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final SimpleDateFormat formatter = new SimpleDateFormat("d MMMM yyyy", Locale.ITALIAN);
    private List<Object> list = new ArrayList<>();
    private Listener listener;

    public FolderAdapter(Listener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case R.layout.adapter_folder:
                return new FileTeacherHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
            case R.layout.adapter_header:
                return new SubheaderHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int layout = getItemViewType(position);

        Object fe = list.get(position);

        switch (layout) {
            case R.layout.adapter_folder:
                Folder f = (Folder) fe;
                FileTeacherHolder folderHolder = (FileTeacherHolder) holder;

                folderHolder.layout.setOnClickListener(view -> {
                    if (listener != null)
                        folderHolder.layout.postDelayed(() -> listener.onFolderClick(f), ViewConfiguration.getTapTimeout());
                });

                folderHolder.title.setText(f.getName().trim());
                folderHolder.date.setText(formatter.format(f.getLastUpdate()));
                break;
            case R.layout.adapter_header:
                SubheaderHolder subHolder = (SubheaderHolder) holder;
                Teacher teacher = (Teacher) fe;

                String profHeader = teacher.getTeacherName();
                subHolder.teacher.setText(capitalizeEach(profHeader, true));

                break;
            default:
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof Teacher)
            return R.layout.adapter_header;
        if (list.get(position) instanceof Folder)
            return R.layout.adapter_folder;
        return 0;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setTeacherFolder(List<Teacher> teachers) {
        list.clear();
        for (Teacher teacher : teachers) {
            list.add(teacher);
            list.addAll(teacher.getFolders());
        }
        notifyDataSetChanged();
    }

    public interface Listener {
        void onFolderClick(Folder f);
    }

    class SubheaderHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.content)
        TextView teacher;

        SubheaderHolder(View layout) {
            super(layout);
            ButterKnife.bind(this, layout);
        }
    }

    class FileTeacherHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.relative_layout)
        View layout;

        FileTeacherHolder(View layout) {
            super(layout);
            ButterKnife.bind(this, layout);
        }
    }

}
