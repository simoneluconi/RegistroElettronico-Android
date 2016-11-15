package com.sharpdroid.registro.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.sharpdroid.registro.Adapters.NoteAdapter;
import com.sharpdroid.registro.Interfaces.Note;
import com.sharpdroid.registro.R;
import com.sharpdroid.registro.Tasks.NoteTask;
import com.sharpdroid.registro.Utils.CacheTask;

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

import static com.sharpdroid.registro.Interfaces.Metodi.isNetworkAvailable;

public class FragmentNote extends Fragment implements RecyclerRefreshLayout.OnRefreshListener {
    final private String TAG = FragmentNote.class.getSimpleName();

    private Context mContext;
    private RecyclerRefreshLayout mRecyclerRefreshLayout;
    private NoteAdapter mRVAdapter;
    private CoordinatorLayout mCoordinatorLayout;

    public FragmentNote() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        View layout = inflater.inflate(R.layout.fragment_note, container, false);

        mRecyclerRefreshLayout = (RecyclerRefreshLayout) layout.findViewById(R.id.refresh_layout);
        mRecyclerRefreshLayout.setOnRefreshListener(this);

        mCoordinatorLayout = (CoordinatorLayout) layout.findViewById(R.id.coordinator_layout);

        RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);

        mRVAdapter = new NoteAdapter(mContext, new CopyOnWriteArrayList<>());
        mRecyclerView.setAdapter(mRVAdapter);

        bindNoteCache();

        mRecyclerRefreshLayout.setRefreshing(true);

        new CommunicationTask().execute();

        return layout;
    }

    void addNotes(List<Note> notes) {
        if (!notes.isEmpty()) {
            mRVAdapter.clear();
            mRVAdapter.addAll(notes);

            // Update cache
            new CacheTask(mContext.getCacheDir(), TAG).execute((List) notes);
        }
        mRecyclerRefreshLayout.setRefreshing(false);
    }

    public void onRefresh() {
        if (isNetworkAvailable(mContext)) {
            new CommunicationTask().execute();
        } else {
            Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
            mRecyclerRefreshLayout.setRefreshing(false);
        }
    }

    private void bindNoteCache() {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(mContext.getCacheDir(), TAG));
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            List<Note> cachedData = new LinkedList<>();
            Note temp;
            while ((temp = (Note) objectInputStream.readObject()) != null) {
                cachedData.add(temp);
            }
            objectInputStream.close();
            mRVAdapter.clear();
            mRVAdapter.addAll(cachedData);
            Log.d(TAG, "Restored cache");
        } catch (FileNotFoundException e) {
            Log.w(TAG, "Cache not found.");
        } catch (EOFException e) {
            Log.e(TAG, "Error while reading cache! (EOF) ");
        } catch (StreamCorruptedException e) {
            Log.e(TAG, "Corrupted cache!");
        } catch (IOException e) {
            Log.e(TAG, "Error while reading cache!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private class CommunicationTask extends AsyncTask<Void, Void, Void> {
        private NoteTask notetask;

        @UiThread
        @Override
        protected void onPreExecute() {
            notetask = new NoteTask(mContext);
        }

        @WorkerThread
        @Override
        protected Void doInBackground(Void... voids) {
            return notetask.update();
        }

        @UiThread
        @Override
        protected void onPostExecute(Void v) {
            addNotes(notetask.getNotes());
        }
    }
}
