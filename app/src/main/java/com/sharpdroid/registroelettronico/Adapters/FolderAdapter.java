package com.sharpdroid.registroelettronico.Adapters;

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
import com.sharpdroid.registroelettronico.Fragments.FragmentFiles;
import com.sharpdroid.registroelettronico.Interfaces.API.FileTeacher;
import com.sharpdroid.registroelettronico.Interfaces.API.Folder;
import com.sharpdroid.registroelettronico.Interfaces.Client.FileElement;
import com.sharpdroid.registroelettronico.R;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registroelettronico.Utils.Metodi.Delimeters;

// DONE: 19/01/2017 Risolvere crash quando si chiude una cartella e poi si scorre verso il basso

public class FolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final static String TAG = FolderAdapter.class.getSimpleName();

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy", Locale.ITALIAN);
    private FileElement fileElements = new FileElement();
    private FragmentManager fragmentManager;

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

        FileElement fe = fileElements.get(position);

        switch (layout) {
            case R.layout.adapter_file_teacher:
                SubheaderHolder subHolder = (SubheaderHolder) holder;

                FileTeacher ft = (FileTeacher) fe;
                String profHeader = ft.getName();

                subHolder.teacher.setText(WordUtils.capitalizeFully(profHeader, Delimeters));

                if (position == 0)
                    subHolder.padding_view.setVisibility(View.GONE);

                break;
            case R.layout.adapter_folder:

                Folder f = (Folder) fe;

                FileTeacherHolder folderHolder = (FileTeacherHolder) holder;
                if (getItemViewType(position - 1) == R.layout.adapter_file_teacher) {
                    folderHolder.divider.setVisibility(View.INVISIBLE);
                } else {
                    folderHolder.divider.setVisibility(View.VISIBLE);
                }
                folderHolder.layout.setOnClickListener(view -> {
                    FragmentFiles fragment = new FragmentFiles();
                    Bundle intent_data = new Bundle();
                    intent_data.putString("folder", new Gson().toJson(f));
                    intent_data.putString("name", f.getProfName().toLowerCase());
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

                folderHolder.title.setText(f.getName().trim());
                folderHolder.date.setText(formatter.format(f.getLast()));
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (fileElements.get(position) instanceof FileTeacher)
            return R.layout.adapter_file_teacher;
        else if (fileElements.get(position) instanceof Folder)
            return R.layout.adapter_folder;

        return 0;
    }

    @Override
    public int getItemCount() {
        return fileElements.size();
    }

    public void setFileTeachers(List<FileTeacher> fileteachers) {
        fileElements.clear();
        fileElements.ConvertFileTeachertoFileElement(fileteachers);
        notifyDataSetChanged();
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
