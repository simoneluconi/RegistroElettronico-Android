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

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;
import com.sharpdroid.registro.Adapters.FileTeacherAdapter;
import com.sharpdroid.registro.Interfaces.FileTeacher;
import com.sharpdroid.registro.R;
import com.sharpdroid.registro.Tasks.CacheListTask;
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

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registro.Utils.Metodi.isNetworkAvailable;

public class FragmentFiles extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    final private String TAG = FragmentFiles.class.getSimpleName();

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    Context mContext;
    FileTeacherAdapter mRVAdapter;

    public FragmentFiles() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_files, container, false);
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
        mRecyclerView.addItemDecoration(new Divider(mContext));
        mRecyclerView.setItemAnimator(null);

        mRVAdapter = new FileTeacherAdapter(mContext);
        mRecyclerView.setAdapter(mRVAdapter);

        bindFileTeacherCache();

        UpdateFiles();

        return layout;
    }

    private void addFiles(List<FileTeacher> result, boolean docache) {
        if (!result.isEmpty()) {
            mRVAdapter.clear();
            mRVAdapter.setAbsences(result);

            if (docache) {
                // Update cache
                new CacheListTask(mContext.getCacheDir(), TAG).execute((List) result);
            }
        }
    }

    private void bindFileTeacherCache() {
        ObjectInputStream objectInputStream = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(mContext.getCacheDir(), TAG));
            objectInputStream = new ObjectInputStream(fileInputStream);
            List<FileTeacher> cachedData = new LinkedList<>();
            FileTeacher temp;
            while ((temp = (FileTeacher) objectInputStream.readObject()) != null) {
                cachedData.add(temp);
            }
            addFiles(cachedData, false);
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
        UpdateFiles();
    }

    private void UpdateFiles() {
        if (isNetworkAvailable(mContext)) {
            mSwipeRefreshLayout.setRefreshing(true);
            Ion.with(mContext)
                    .load(/*RESTFulAPI.FILES_URL*/ "https://gist.githubusercontent.com/luca020400/da76cedd586a5cec08788f7f9ffabe9a/raw/files.json")
                    .as(new TypeToken<List<FileTeacher>>() {
                    })
                    .withResponse()
                    .setCallback((e, result) -> {
                        if (result.getHeaders().code() == 200) {
                            addFiles(result.getResult(), true);
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    });
        } else {
            Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
