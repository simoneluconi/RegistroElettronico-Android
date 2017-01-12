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
import com.sharpdroid.registroelettronico.Adapters.AllAbsencesAdapter;
import com.sharpdroid.registroelettronico.Interfaces.API.Absences;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Tasks.CacheObjectObservable;
import com.sharpdroid.registroelettronico.Tasks.CacheObjectTask;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sharpdroid.registroelettronico.Utils.Metodi.isNetworkAvailable;

public class FragmentAllAbsences extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    final private String TAG = FragmentAllAbsences.class.getSimpleName();

    @BindView(R.id.recycler)
    RecyclerView recycler;
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
        View root = inflater.inflate(R.layout.coordinator_swipe_recycler, container, false);
        mContext = getContext();

        ButterKnife.bind(this, root);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial);

        adapter = new AllAbsencesAdapter(mContext);
        recycler.setLayoutManager(new LinearLayoutManager(mContext));
        recycler.setAdapter(adapter);

        bindAbsencesCache();

        UpdateAllAbsences();

        return root;
    }

    void addAbsences(Absences absences, boolean docache) {
        adapter.clear();
        adapter.addAll(absences);

        if (docache) {
            new CacheObjectTask(mContext.getCacheDir(), TAG).execute(absences);
        }
    }

    private void bindAbsencesCache() {
        new CacheObjectObservable(new File(mContext.getCacheDir(), TAG))
                .getCachedList(Absences.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(absences -> {
                    addAbsences(absences, false);
                    Log.d(TAG, "Restored cache");
                }, error -> {
                });
    }

    public void onRefresh() {
        UpdateAllAbsences();
    }

    private void UpdateAllAbsences() {
        mSwipeRefreshLayout.setRefreshing(true);
        new SpiaggiariApiClient(mContext)
                .getAbsences()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(absences -> {
                    addAbsences(absences, true);
                    mSwipeRefreshLayout.setRefreshing(false);
                }, error -> {
                    if (!isNetworkAvailable(mContext)) {
                        Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                });
    }
}