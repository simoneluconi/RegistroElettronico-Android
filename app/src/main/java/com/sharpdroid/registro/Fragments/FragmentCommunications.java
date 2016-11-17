package com.sharpdroid.registro.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
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

import com.sharpdroid.registro.Adapters.CommunicationAdapter;
import com.sharpdroid.registro.Interfaces.Communication;
import com.sharpdroid.registro.R;
import com.sharpdroid.registro.Tasks.CacheListTask;
import com.sharpdroid.registro.Tasks.CommunicationTask;
import com.sharpdroid.registro.Utils.Divider;

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

import static com.sharpdroid.registro.Utils.Metodi.isNetworkAvailable;

public class FragmentCommunications extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    final private String TAG = FragmentCommunications.class.getSimpleName();

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private CommunicationAdapter mRVAdapter;
    private Context mContext;

    public FragmentCommunications() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        View layout = inflater.inflate(R.layout.fragment_communications, container, false);

        ButterKnife.bind(this, layout);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial);

        RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new Divider(mContext));
        mRecyclerView.setItemAnimator(null);

        mRVAdapter = new CommunicationAdapter(new CopyOnWriteArrayList<>());
        mRecyclerView.setAdapter(mRVAdapter);

        bindCommunicationsCache();

        new CommunicationsTask().execute();

        return layout;
    }

    private void addCommunications(List<Communication> communications, boolean docache) {
        if (!communications.isEmpty()) {
            mRVAdapter.clear();
            mRVAdapter.addAll(communications);

            if (docache) {
                // Update cache
                new CacheListTask(mContext.getCacheDir(), TAG).execute((List) communications);
            }
        }
    }

    public void onRefresh() {
        if (isNetworkAvailable(mContext)) {
            new CommunicationsTask().execute();
        } else {
            Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void bindCommunicationsCache() {
        ObjectInputStream objectInputStream = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(mContext.getCacheDir(), TAG));
            objectInputStream = new ObjectInputStream(fileInputStream);
            List<Communication> cachedData = new LinkedList<>();
            Communication temp;
            while ((temp = (Communication) objectInputStream.readObject()) != null) {
                cachedData.add(temp);
            }
            addCommunications(cachedData, false);
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

    private class CommunicationsTask extends AsyncTask<Void, Void, Void> {
        private CommunicationTask communicationstask;

        @UiThread
        @Override
        protected void onPreExecute() {
            mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));
            communicationstask = new CommunicationTask(mContext);
        }

        @WorkerThread
        @Override
        protected Void doInBackground(Void... voids) {
            return communicationstask.update();
        }

        @UiThread
        @Override
        protected void onPostExecute(Void v) {
            addCommunications(communicationstask.getCommunications(), true);
            mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));
        }
    }
}
