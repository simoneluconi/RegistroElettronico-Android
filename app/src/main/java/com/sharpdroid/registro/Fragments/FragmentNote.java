package com.sharpdroid.registro.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.sharpdroid.registro.API.RESTFulAPI;
import com.sharpdroid.registro.Adapters.NoteAdapter;
import com.sharpdroid.registro.Interfaces.Note;
import com.sharpdroid.registro.R;
import com.sharpdroid.registro.Utils.CacheTask;

import java.util.List;

import static com.sharpdroid.registro.Interfaces.Metodi.isNetworkAvailable;

public class FragmentNote extends Fragment implements RecyclerRefreshLayout.OnRefreshListener {
    final private String TAG = FragmentNote.class.getSimpleName();

    Context mContext;
    RecyclerRefreshLayout mRecyclerRefreshLayout;
    NoteAdapter mRVAdapter;
    CoordinatorLayout mCoordinatorLayout;

    public FragmentNote() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        View v = inflater.inflate(R.layout.fragment_note, container, false);

        mRecyclerRefreshLayout = (RecyclerRefreshLayout) v.findViewById(R.id.refresh_layout);
        mRecyclerRefreshLayout.setOnRefreshListener(this);

        RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mRVAdapter = new NoteAdapter(mContext);
        mRecyclerView.setAdapter(mRVAdapter);

        mRecyclerRefreshLayout.setRefreshing(true);
        new Handler().post(new RESTFulAPI.Notes(mContext) {
            @Override
            public void then(List<Note> notes) {
                addNotes(notes);
            }
        });

        return v;
    }

    void addNotes(List<Note> notes) {
        mRVAdapter.addAll(notes);
        new CacheTask(mContext.getCacheDir(), TAG).execute((List) notes);
        mRecyclerRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        if (isNetworkAvailable(mContext)) {
            new Handler().post(new RESTFulAPI.Notes(mContext) {
                @Override
                public void then(List<Note> notes) {
                    addNotes(notes);
                }
            });
        } else {
            Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
            mRecyclerRefreshLayout.setRefreshing(false);
        }
    }
}
