package com.sharpdroid.registroelettronico.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sharpdroid.registroelettronico.Adapters.FileAdapter;
import com.sharpdroid.registroelettronico.Databases.FilesDB;
import com.sharpdroid.registroelettronico.Interfaces.API.Folder;
import com.sharpdroid.registroelettronico.R;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.sharpdroid.registroelettronico.Utils.Metodi.Delimeters;

public class FragmentFiles extends Fragment {
    final static String TAG = FragmentFiles.class.getSimpleName();
    private final SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy", Locale.ITALIAN);

    Context mContext;
    FileAdapter mRVAdapter;
    FilesDB db;
    Folder data;

    public FragmentFiles() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragmentfiles, container, false);

        mContext = getContext();
        db = new FilesDB(mContext);
        RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler);
        CoordinatorLayout mCoordinatorLayout = (CoordinatorLayout) layout.findViewById(R.id.coordinator_layout);


        //region FOLDER LAYOUT & VIEWS
        View folder_layout = layout.findViewById(R.id.relative_layout);
        TextView folder_title = (TextView) folder_layout.findViewById(R.id.title);
        TextView folder_prof = (TextView) folder_layout.findViewById(R.id.date);
        View folder_divider = folder_layout.findViewById(R.id.divider);

        folder_layout.setOnClickListener(view -> getFragmentManager().popBackStack());
        folder_divider.setVisibility(View.GONE);

        try {
            //GET DATA VIA JSON
            data = new Gson().getAdapter(Folder.class).fromJson(getArguments().getString("folder"));
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (data != null) {
                folder_title.setText(data.getName().trim());
                mRVAdapter = new FileAdapter(mContext, mCoordinatorLayout, db);
                addSubjects(data);
            }
        }
        folder_prof.setText(WordUtils.capitalizeFully(getArguments().getString("name"), Delimeters));

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
                return (int) getContext().getResources().getDimension(R.dimen.activity_horizontal_margin);
            }
        }).color(Color.parseColor("#11000000")).size(1).build());
        mRecyclerView.setItemAnimator(null);

        mRecyclerView.setAdapter(mRVAdapter);

        return layout;
    }

    private void addSubjects(Folder folder) {
        mRVAdapter.clear();
        mRVAdapter.addAll(folder.getElements());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}
