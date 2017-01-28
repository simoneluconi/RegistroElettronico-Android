package com.sharpdroid.registroelettronico.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.API.SpiaggiariApiClient;
import com.sharpdroid.registroelettronico.Adapters.NoteAdapter;
import com.sharpdroid.registroelettronico.Interfaces.API.Note;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Tasks.CacheListObservable;
import com.sharpdroid.registroelettronico.Tasks.CacheListTask;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sharpdroid.registroelettronico.Utils.Metodi.isNetworkAvailable;

public class FragmentNote extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    final private String TAG = FragmentNote.class.getSimpleName();

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private NoteAdapter mRVAdapter;
    private Context mContext;

    public FragmentNote() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        View layout = inflater.inflate(R.layout.coordinator_swipe_recycler, container, false);

        ButterKnife.bind(this, layout);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial);

        RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext).size(1).build());
        mRecyclerView.setItemAnimator(null);

        mRVAdapter = new NoteAdapter(mContext, new CopyOnWriteArrayList<>());
        mRecyclerView.setAdapter(mRVAdapter);

        bindNoteCache();

        UpdateNotes();

        return layout;
    }

    void addNotes(List<Note> notes, boolean docache) {
        if (!notes.isEmpty()) {
            mRVAdapter.clear();
            mRVAdapter.addAll(notes);

            if (docache) {
                // Update cache
                new CacheListTask(mContext.getCacheDir(), TAG).execute((List) notes);
            }
        }
    }

    private void bindNoteCache() {
        new CacheListObservable(new File(mContext.getCacheDir(), TAG))
                .getCachedList(Note.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notes -> {
                    addNotes(notes, false);
                    Log.d(TAG, "Restored cache");
                }, Throwable::printStackTrace);
    }

    public void onRefresh() {
        UpdateNotes();

    }

    private void UpdateNotes() {
        mSwipeRefreshLayout.setRefreshing(true);
        new SpiaggiariApiClient(mContext)
                .getNotes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notes -> {
                    addNotes(notes, true);
                    mSwipeRefreshLayout.setRefreshing(false);
                }, error -> {
                    error.printStackTrace();
                    if (!isNetworkAvailable(mContext)) {
                        Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
                    } else
                        Snackbar.make(mCoordinatorLayout, error.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                });
    }
}
