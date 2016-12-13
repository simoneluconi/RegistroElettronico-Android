package com.sharpdroid.registro.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.sharpdroid.registro.API.SpiaggiariApiClient;
import com.sharpdroid.registro.Adapters.AllAbsencesAdapter;
import com.sharpdroid.registro.Interfaces.Absences;
import com.sharpdroid.registro.R;
import com.sharpdroid.registro.Tasks.CacheObjectTask;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial);

        adapter = new AllAbsencesAdapter(mContext);

        bindAbsencesCache();

        UpdateAllAbsences();

        return root;
    }

    void addAbsences(Absences absences, boolean docache) {
        adapter.clear();
        adapter.setAbsences(absences);
        expandableListView.setAdapter(adapter);

        if (docache) {
            new CacheObjectTask(mContext.getCacheDir(), TAG).execute(absences);
        }
    }

    private void bindAbsencesCache() {
        ObjectInputStream objectInputStream = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(mContext.getCacheDir(), TAG));
            objectInputStream = new ObjectInputStream(fileInputStream);
            Object obj = objectInputStream.readObject();
            if (obj instanceof Absences) {
                addAbsences((Absences) obj, false);
                Log.d(TAG, "Restored cache");
            } else {
                Log.e(TAG, "Corrupterd cache");
            }
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
        UpdateAllAbsences();
    }

    private void UpdateAllAbsences() {
        if (isNetworkAvailable(mContext)) {
            mSwipeRefreshLayout.setRefreshing(true);
            new SpiaggiariApiClient(mContext).mService.getAbsences()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(absences -> {
                        addAbsences(absences, true);
                        mSwipeRefreshLayout.setRefreshing(false);
                    });
        } else {
            Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
