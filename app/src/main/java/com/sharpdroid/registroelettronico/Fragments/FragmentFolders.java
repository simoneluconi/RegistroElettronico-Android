package com.sharpdroid.registroelettronico.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.API.SpiaggiariApiClient;
import com.sharpdroid.registroelettronico.Adapters.FolderAdapter;
import com.sharpdroid.registroelettronico.Interfaces.API.FileTeacher;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Tasks.CacheListObservable;
import com.sharpdroid.registroelettronico.Tasks.CacheListTask;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sharpdroid.registroelettronico.Utils.Metodi.isNetworkAvailable;

// TODO: 19/01/2017 Risolvere crash quando si chiude una cartella e poi si scorre verso il basso 

public class FragmentFolders extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    final private String TAG = FragmentFolders.class.getSimpleName();
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    Context mContext;
    FolderAdapter mRVAdapter;
    ActionBar supportActionBar;

    public FragmentFolders() {
    }

    public void getInstance(ActionBar supportActionBar) {
        this.supportActionBar = supportActionBar;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.coordinator_swipe_recycler, container, false);
        ButterKnife.bind(this, layout);
        mContext = getContext();

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial);

        RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(null);

        mRVAdapter = new FolderAdapter(mContext, getFragmentManager());
        mRecyclerView.setAdapter(mRVAdapter);

        bindFileTeacherCache();

        UpdateFiles();

        return layout;
    }

    private void addFiles(List<FileTeacher> result, boolean docache) {
        if (!result.isEmpty()) {
            mRVAdapter.clear();
            mRVAdapter.setFileTeachers(result);

            if (docache) {
                // Update cache
                new CacheListTask(mContext.getCacheDir(), TAG).execute((List) result);
            }
        }
    }

    private void bindFileTeacherCache() {
        new CacheListObservable(new File(mContext.getCacheDir(), TAG))
                .getCachedList(FileTeacher.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(files -> {
                    addFiles(files, false);
                    Log.d(TAG, "Restored cache");
                });
    }

    public void onRefresh() {
        UpdateFiles();
    }

    private void UpdateFiles() {
        mSwipeRefreshLayout.setRefreshing(true);
        new SpiaggiariApiClient(mContext)
                .getFiles()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(files -> {
                    addFiles(files, true);
                    mSwipeRefreshLayout.setRefreshing(false);
                }, error -> {
                    if (!isNetworkAvailable(mContext)) {
                        Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                });
    }
}
