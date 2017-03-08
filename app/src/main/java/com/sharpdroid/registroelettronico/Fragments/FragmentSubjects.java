package com.sharpdroid.registroelettronico.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.Adapters.SubjectsAdapter;
import com.sharpdroid.registroelettronico.Databases.SubjectsDB;
import com.sharpdroid.registroelettronico.R;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registroelettronico.Utils.Metodi.dpToPx;

public class FragmentSubjects extends Fragment {
    Context mContext;

    @BindView(R.id.recycler)
    RecyclerView recycler;

    SubjectsAdapter adapter;
    SubjectsDB db;

    public FragmentSubjects() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lessons, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mContext = getContext();
        db = new SubjectsDB(mContext);

        adapter = new SubjectsAdapter(mContext);
        recycler.setLayoutManager(new LinearLayoutManager(mContext));
        recycler.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext).size(1).margin(dpToPx(16), dpToPx(16)).build());
        recycler.setAdapter(adapter);
        adapter.addAll(db.getSubjects());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}