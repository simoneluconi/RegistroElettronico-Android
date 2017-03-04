package com.sharpdroid.registroelettronico.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.Adapters.SubjectsAdapter;
import com.sharpdroid.registroelettronico.Databases.SubjectsDB;
import com.sharpdroid.registroelettronico.R;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_lessons, container, false);
        ButterKnife.bind(this, layout);
        mContext = getContext();
        db = new SubjectsDB(mContext);

        adapter = new SubjectsAdapter(mContext);
        recycler.setLayoutManager(new LinearLayoutManager(mContext));
        recycler.setAdapter(adapter);
        adapter.addAll(db.getSubjects());

        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}