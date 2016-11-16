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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sharpdroid.registro.Adapters.AllAbsencesAdapter;
import com.sharpdroid.registro.Interfaces.Absence;
import com.sharpdroid.registro.Interfaces.Absences;
import com.sharpdroid.registro.R;
import com.sharpdroid.registro.Tasks.AbsencesTask;
import com.sharpdroid.registro.Tasks.CacheTask;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registro.Utils.Metodi.isNetworkAvailable;

public class FragmentAllAbsences extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    final private String TAG = FragmentAllAbsences.class.getSimpleName();

    @BindView(R.id.expandable_list)
    ExpandableListView expandableListView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    AllAbsencesAdapter adapter;
    Context mContext;

    public FragmentAllAbsences() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_all_absences, container, false);
        mContext = getContext();

        ButterKnife.bind(this, root);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        adapter = new AllAbsencesAdapter(mContext);
        expandableListView.setAdapter(adapter);

        bindAbsencesCache();
        return root;
    }

    void addAbsences(Absences absences, boolean docache) {
        adapter.clear();
        adapter.setAbsences(absences);

        if (docache) {
            new CacheTask(mContext.getCacheDir(), TAG).execute((List) absences);
        }

    }


    private void bindAbsencesCache() {
        ObjectInputStream objectInputStream = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(mContext.getCacheDir(), TAG));
            objectInputStream = new ObjectInputStream(fileInputStream);
            Absences cachedData;
            cachedData = (Absences) objectInputStream.readObject();

            addAbsences(cachedData, false);

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

    @Override
    public void onRefresh() {
        if (isNetworkAvailable(mContext)) {
            new AbsencesTaskLocal().execute();
        } else {
            Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private class AbsencesTaskLocal extends AsyncTask<Void, Void, Void> {
        private AbsencesTask absencesTask;

        @UiThread
        @Override
        protected void onPreExecute() {
            mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));
            absencesTask = new AbsencesTask(mContext);
        }

        @WorkerThread
        @Override
        protected Void doInBackground(Void... voids) {
            return absencesTask.update();
        }

        @UiThread
        @Override
        protected void onPostExecute(Void v) {
            addAbsences(absencesTask.getAbsences(), true);
            mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));
        }
    }
}
