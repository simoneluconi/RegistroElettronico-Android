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
import com.sharpdroid.registro.Adapters.CommunicationAdapter;
import com.sharpdroid.registro.Interfaces.Communication;
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

public class FragmentCommunications extends Fragment implements RecyclerRefreshLayout.OnRefreshListener {
    final private String TAG = FragmentCommunications.class.getSimpleName();

    private CoordinatorLayout mCoordinatorLayout;
    private RecyclerRefreshLayout mSwipeRefreshLayout;
    private CommunicationAdapter mRVAdapter;
    private Context mContext;

    public FragmentCommunications() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        View layout = inflater.inflate(R.layout.fragment_communications, container, false);
        mSwipeRefreshLayout = (RecyclerRefreshLayout) layout.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mCoordinatorLayout = (CoordinatorLayout) layout.findViewById(R.id.coordinator_layout);

        RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRVAdapter = new CommunicationAdapter(new CopyOnWriteArrayList<>());
        mRecyclerView.setAdapter(mRVAdapter);

        bindCommunicationsCache();

        mSwipeRefreshLayout.setRefreshing(true);

        new Handler().post(new RESTFulAPI.Communications(mContext) {
            @Override
            public void then(List<Communication> communications) {
                addCommunications(communications);
            }
        });

        return layout;
    }

    private void addCommunications(List<Communication> communications) {
        if (communications.size() != 0) {
            mRVAdapter.clear();
            mRVAdapter.addAll(communications);

            // Update cache
            new CacheTask(mContext.getCacheDir(), TAG).execute((List) communications);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void onRefresh() {
        if (isNetworkAvailable(mContext)) {
            new Handler().post(new RESTFulAPI.Communications(mContext) {
                @Override
                public void then(List<Communication> communications) {
                    addCommunications(communications);
                }
            });
        } else {
            Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void bindCommunicationsCache() {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(mContext.getCacheDir(), TAG));
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            List<Communication> cachedData = new LinkedList<>();
            Communication temp;
            while ((temp = (Communication) objectInputStream.readObject()) != null) {
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
