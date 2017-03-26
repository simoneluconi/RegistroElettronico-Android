package com.sharpdroid.registroelettronico.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.Adapters.FileAdapter;
import com.sharpdroid.registroelettronico.Databases.FilesDB;
import com.sharpdroid.registroelettronico.Interfaces.API.Folder;
import com.sharpdroid.registroelettronico.R;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import static com.sharpdroid.registroelettronico.Utils.Metodi.dpToPx;

public class FragmentFiles extends Fragment {
    private Folder data;
    private FileAdapter mRVAdapter;
    private FilesDB db;
    private CoordinatorLayout coordinatorLayout;

    public FragmentFiles() {
    }

    public static FragmentFiles newInstance(Folder data, CoordinatorLayout coordinatorLayout) {
        FragmentFiles fragment = new FragmentFiles();
        fragment.setData(data);
        fragment.setCoordinatorLayout(coordinatorLayout);
        return fragment;
    }

    public void setData(Folder data) {
        this.data = data;
    }

    public void setCoordinatorLayout(CoordinatorLayout coordinatorLayout) {
        this.coordinatorLayout = coordinatorLayout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_RIGHT);
        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return recyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mRecyclerView = (RecyclerView) view;

        db = new FilesDB(getContext());
        mRVAdapter = new FileAdapter(getContext(), coordinatorLayout, db);
        addSubjects(data);
        setTitle(data.getName().trim());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).margin(dpToPx(72), dpToPx(16)).colorResId(R.color.divider).size(dpToPx(1)).build());
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAdapter(mRVAdapter);

    }

    public void setTitle(CharSequence title) {
        getActivity().setTitle(title);
    }

    private void addSubjects(Folder folder) {
        mRVAdapter.clear();
        mRVAdapter.addAll(folder.getElements());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
