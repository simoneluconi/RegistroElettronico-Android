package com.sharpdroid.registro.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sharpdroid.registro.Adapters.FileAdapter;
import com.sharpdroid.registro.Interfaces.API.Folder;
import com.sharpdroid.registro.R;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FragmentFiles extends Fragment {
    final static String TAG = FragmentFiles.class.getSimpleName();

    private final SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy", Locale.ITALIAN);

    Context mContext;
    FileAdapter mRVAdapter;

    public FragmentFiles() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragmentfiles, container, false);

        mContext = getContext();

        RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler);

        //region FOLDER LAYOUT & VIEWS
        View folder_layout = layout.findViewById(R.id.relative_layout);
        TextView folder_title = (TextView) folder_layout.findViewById(R.id.title);
        TextView folder_date = (TextView) folder_layout.findViewById(R.id.date);
        View folder_divider = folder_layout.findViewById(R.id.divider);

        folder_divider.setVisibility(View.GONE);
        folder_layout.setClickable(false);
        folder_layout.setFocusable(false);

        Folder data = null;
        try {
            //GET DATA VIA JSON
            data = new Gson().getAdapter(Folder.class).fromJson(getArguments().getString("folder"));
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (data != null) {
                folder_title.setText(data.getName().trim());
                folder_date.setText(formatter.format(data.getLast()));
                mRVAdapter = new FileAdapter(mContext);
                addSubjects(data);
            }
        }
        //endregion

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext).marginProvider(new HorizontalDividerItemDecoration.MarginProvider() {
            @Override
            public int dividerLeftMargin(int position, RecyclerView parent) {
                return (int) getContext().getResources().getDimension(R.dimen.padding_left_divider);
            }

            @Override
            public int dividerRightMargin(int position, RecyclerView parent) {
                return 0;
            }
        }).size(1).build());
        mRecyclerView.setItemAnimator(null);

        mRecyclerView.setAdapter(mRVAdapter);

        return layout;
    }

    private void addSubjects(Folder folder) {
        mRVAdapter.clear();
        mRVAdapter.addAll(folder.getElements());
    }
}
