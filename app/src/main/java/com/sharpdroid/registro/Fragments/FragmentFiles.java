package com.sharpdroid.registro.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sharpdroid.registro.Adapters.FileAdapter;
import com.sharpdroid.registro.Interfaces.Folder;
import com.sharpdroid.registro.R;

import java.io.IOException;
import java.text.ParseException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.sharpdroid.registro.Adapters.FolderAdapter.apiFormat;
import static com.sharpdroid.registro.Adapters.FolderAdapter.dateFormat;

public class FragmentFiles extends Fragment {
    final static String TAG = FragmentFiles.class.getSimpleName();

    Context mContext;
    FileAdapter adapter;

    public FragmentFiles() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragmentfiles, container, false);

        RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler);

        //region FOLDER LAYOUT & VIEWS
        View folder_layout = layout.findViewById(R.id.relative_layout);
        TextView folder_title = (TextView) folder_layout.findViewById(R.id.title);
        TextView folder_date = (TextView) folder_layout.findViewById(R.id.date);
        View folder_divider = folder_layout.findViewById(R.id.divider);
        CircleImageView folder_image = (CircleImageView) folder_layout.findViewById(R.id.circleImageView);
        Folder data = null;
        try {
            //GET DATA VIA JSON
            data = new Gson().getAdapter(Folder.class).fromJson(getArguments().getString("folder"));
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } finally {
            if (data != null) {
                folder_title.setText(data.getName().trim());
                try {
                    folder_date.setText(dateFormat.format(apiFormat.parse(data.getLast().split("T")[0])));
                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing DATE from FOLDER");
                }
                adapter = new FileAdapter(mContext);
                adapter.addAll(data.getElements());
            }
        }
        //endregion

        mRecyclerView.setAdapter(adapter);

        return layout;
    }

}
