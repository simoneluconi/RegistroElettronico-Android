package com.sharpdroid.registro.Adapters;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sharpdroid.registro.Fragments.FragmentFiles;
import com.sharpdroid.registro.Interfaces.API.FileTeacher;
import com.sharpdroid.registro.Interfaces.API.Folder;
import com.sharpdroid.registro.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registro.Utils.Metodi.NomeDecente;
import static com.sharpdroid.registro.Utils.Metodi.getListLayouts;

public class FolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final static String TAG = FolderAdapter.class.getSimpleName();

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy", Locale.ITALIAN);
    private List<Integer> listLayouts = new ArrayList<>();
    private List<FileTeacher> fileteachers = new ArrayList<>();
    private FragmentManager fragmentManager;
    private int current_subheader, current_folder = 0;

    public FolderAdapter(Context context, FragmentManager fragmentManager) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.fragmentManager = fragmentManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case R.layout.adapter_file_teacher:
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
            case R.layout.adapter_file_teacher:
                SubheaderHolder subHolder = (SubheaderHolder) holder;

                String profHeader = fileteachers.get(current_subheader).getName();

                subHolder.teacher.setText(NomeDecente(profHeader));

                if (current_folder == 0)
                    subHolder.padding_view.setVisibility(View.GONE);

                current_folder = 0;
                current_subheader++;
                break;
            case R.layout.adapter_folder:
                FileTeacherHolder folderHolder = (FileTeacherHolder) holder;
                Folder folder = fileteachers.get(current_subheader - 1).getFolders().get(current_folder);
                if (getItemViewType(position - 1) == R.layout.adapter_file_teacher) {
                    folderHolder.divider.setVisibility(View.INVISIBLE);
                } else {
                    folderHolder.divider.setVisibility(View.VISIBLE);
                }
                folderHolder.layout.setOnClickListener(view -> {
                    FragmentFiles fragment = new FragmentFiles();
                    Bundle intent_data = new Bundle();
                    intent_data.putString("folder", new Gson().toJson(folder));
                    fragment.setArguments(intent_data);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        fragment.setSharedElementEnterTransition(TransitionInflater.from(mContext).inflateTransition(R.transition.shared_element));
                    }

                    fragmentManager.beginTransaction()
                            .addSharedElement(folderHolder.date, "folder_date")
                            .addSharedElement(folderHolder.title, "folder_title")
                            .addSharedElement(folderHolder.layout, "folder_layout")
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                });

                folderHolder.title.setText(NomeDecente(folder.getName().trim()));
                folderHolder.date.setText(formatter.format(folder.getLast()));

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

    public void setFileTeachers(List<FileTeacher> fileteachers) {
        this.fileteachers = fileteachers;
        listLayouts = getListLayouts(this.fileteachers);
        notifyDataSetChanged();
    }

    public void clear() {
        fileteachers.clear();
        listLayouts.clear();
        current_subheader = current_folder = 0;
    }

    class SubheaderHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView teacher;
        @BindView(R.id.paddingTop)
        View padding_view;

        SubheaderHolder(View layout) {
            super(layout);
            ButterKnife.bind(this, layout);
        }
    }

    class FileTeacherHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.divider)
        View divider;
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
