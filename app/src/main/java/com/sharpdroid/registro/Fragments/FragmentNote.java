package com.sharpdroid.registro.Fragments;

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

import com.sharpdroid.registro.API.SpiaggiariApiClient;
import com.sharpdroid.registro.Adapters.NoteAdapter;
import com.sharpdroid.registro.Interfaces.API.Note;
import com.sharpdroid.registro.R;
import com.sharpdroid.registro.Tasks.CacheListTask;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sharpdroid.registro.Utils.Metodi.isNetworkAvailable;

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
        View layout = inflater.inflate(R.layout.fragment_note, container, false);

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
        ObjectInputStream objectInputStream = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(mContext.getCacheDir(), TAG));
            objectInputStream = new ObjectInputStream(fileInputStream);
            List<Note> cachedData = new LinkedList<>();
            Note temp;
            while ((temp = (Note) objectInputStream.readObject()) != null) {
                cachedData.add(temp);
            }
            addNotes(cachedData, false);
            Log.d(TAG, "Restored cache");
        } catch (FileNotFoundException e) {
            Log.w(TAG, "Cache not found.");
        } catch (EOFException e) {
            Log.e(TAG, "Error while reading cache! (EOF) ");
        } catch (StreamCorruptedException e) {
            Log.e(TAG, "Corrupted cache!");
        } catch (IOException e) {
            Log.e(TAG, "Error while reading cache!");
        } catch (ClassCastException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onRefresh() {
        UpdateNotes();

    }

    private void UpdateNotes() {
        if (isNetworkAvailable(mContext)) {
            mSwipeRefreshLayout.setRefreshing(true);
            new SpiaggiariApiClient(mContext).mService.getNotes()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(notes -> {
                        addNotes(notes, true);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }, error -> mSwipeRefreshLayout.setRefreshing(false));
        } else {
            Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
