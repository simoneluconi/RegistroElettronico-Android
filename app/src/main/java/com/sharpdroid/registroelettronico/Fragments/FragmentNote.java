package com.sharpdroid.registroelettronico.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.API.SpiaggiariApiClient;
import com.sharpdroid.registroelettronico.Adapters.NoteAdapter;
import com.sharpdroid.registroelettronico.Databases.RegistroDB;
import com.sharpdroid.registroelettronico.Interfaces.API.Note;
import com.sharpdroid.registroelettronico.R;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.sharpdroid.registroelettronico.Utils.Metodi.dpToPx;
import static com.sharpdroid.registroelettronico.Utils.Metodi.isNetworkAvailable;

public class FragmentNote extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    final private String TAG = FragmentNote.class.getSimpleName();

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private NoteAdapter mRVAdapter;
    private RegistroDB db;

    public FragmentNote() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.coordinator_swipe_recycler, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial);

        db = RegistroDB.Companion.getInstance(getContext());
        getActivity().setTitle(getString(R.string.note));

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).colorResId(R.color.divider).size(dpToPx(1)).build());
        mRecyclerView.setItemAnimator(null);

        mRVAdapter = new NoteAdapter(getContext(), new CopyOnWriteArrayList<>());
        mRecyclerView.setAdapter(mRVAdapter);

        load();
        download();
    }

    void addNotes(List<Note> notes) {
        if (!notes.isEmpty()) {
            mRVAdapter.clear();
            mRVAdapter.addAll(notes);
        }
    }

    private void load() {
        addNotes(db.getNotes());
    }

    private void save(List<Note> list) {
        db.addNotes(list);
    }

    public void onRefresh() {
        download();
    }

    private void download() {
        mSwipeRefreshLayout.setRefreshing(true);
        new SpiaggiariApiClient(getContext())
                .getNotes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notes -> {
                    save(notes);
                    load();
                    mSwipeRefreshLayout.setRefreshing(false);
                }, error -> {
                    error.printStackTrace();
                    if (!isNetworkAvailable(getContext())) {
                        Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(mCoordinatorLayout, error.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                });
    }
}
