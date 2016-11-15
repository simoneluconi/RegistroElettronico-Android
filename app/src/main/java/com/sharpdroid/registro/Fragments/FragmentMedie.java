package com.sharpdroid.registro.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import com.sharpdroid.registro.API.RESTFulAPI;
import com.sharpdroid.registro.Adapters.MedieAdapter;
import com.sharpdroid.registro.Interfaces.MarkSubject;
import com.sharpdroid.registro.R;
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

public class FragmentMedie extends Fragment implements RecyclerRefreshLayout.OnRefreshListener {
    final private String TAG = FragmentMedie.class.getSimpleName();

    private CoordinatorLayout mCoordinatorLayout;
    private RecyclerRefreshLayout mRecyclerRefreshLayout;
    private MedieAdapter mRVAdapter;
    private Context mContext;

    public FragmentMedie() {

    }

    private void addSubjects(List<MarkSubject> markSubjects) {
        if (markSubjects.size() != 0) {
            mRVAdapter.clear();
            mRVAdapter.addAll(markSubjects);

            // Update cache
            new CacheTask(mContext.getCacheDir(), TAG).execute((List) markSubjects);
        }
        mRecyclerRefreshLayout.setRefreshing(false);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        View layout = inflater.inflate(R.layout.fragment_marks, container, false);
        mRecyclerRefreshLayout = (RecyclerRefreshLayout) layout.findViewById(R.id.refresh_layout);
        mRecyclerRefreshLayout.setOnRefreshListener(this);

        mCoordinatorLayout = (CoordinatorLayout) layout.findViewById(R.id.coordinator_layout);

        RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mRVAdapter = new MedieAdapter(mContext, new CopyOnWriteArrayList<>());
        mRecyclerView.setAdapter(mRVAdapter);

        bindMarksCache();

        mRecyclerRefreshLayout.setRefreshing(true);

        new Handler().post(new RESTFulAPI.Marks(mContext) {
            @Override
            public void then(List<MarkSubject> markSubjects) {
                addSubjects(markSubjects);
            }
        });

        return layout;
    }

    public void onRefresh() {
        if (isNetworkAvailable(mContext)) {
            new Handler().post(new RESTFulAPI.Marks(mContext) {
                @Override
                public void then(List<MarkSubject> markSubjects) {
                    addSubjects(markSubjects);
                }
            });
        } else {
            Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
            mRecyclerRefreshLayout.setRefreshing(false);
        }
    }

    private void bindMarksCache() {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(mContext.getCacheDir(), TAG));
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            List<MarkSubject> cachedData = new LinkedList<>();
            MarkSubject temp;
            while ((temp = (MarkSubject) objectInputStream.readObject()) != null) {
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
}
