package com.sharpdroid.registroelettronico.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.API.SpiaggiariApiClient;
import com.sharpdroid.registroelettronico.Adapters.CommunicationAdapter;
import com.sharpdroid.registroelettronico.Databases.CommunicationsDB;
import com.sharpdroid.registroelettronico.Interfaces.API.Communication;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Tasks.CacheListObservable;
import com.sharpdroid.registroelettronico.Tasks.CacheListTask;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.sharpdroid.registroelettronico.Utils.Metodi.dpToPx;
import static com.sharpdroid.registroelettronico.Utils.Metodi.isNetworkAvailable;

public class FragmentCommunications extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        SearchView.OnQueryTextListener {
    final private String TAG = FragmentCommunications.class.getSimpleName();

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private CommunicationAdapter mRVAdapter;
    private CommunicationsDB db;

    public FragmentCommunications() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        db = new CommunicationsDB(getContext());
        return inflater.inflate(R.layout.coordinator_swipe_recycler, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial);
        getActivity().setTitle(getString(R.string.communications));

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).colorResId(R.color.divider).size(dpToPx(1)).build());
        mRecyclerView.setItemAnimator(null);

        mRVAdapter = new CommunicationAdapter(getContext(), mCoordinatorLayout, db);
        mRecyclerView.setAdapter(mRVAdapter);

        bindCommunicationsCache();
        UpdateCommunications();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.communication_menu, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        if (searchView != null) {
            searchView.setMaxWidth(Integer.MAX_VALUE);
            searchView.setOnQueryTextListener(this);
        }
    }

    private void addCommunications(List<Communication> communications, boolean docache) {
        if (!communications.isEmpty()) {
            mRVAdapter.clear();
            mRVAdapter.addAll(communications);

            if (docache) {
                // Update cache
                new CacheListTask(getContext().getCacheDir(), TAG).execute((List) communications);
            }
        }
    }

    private void bindCommunicationsCache() {
        new CacheListObservable(new File(getContext().getCacheDir(), TAG))
                .getCachedList(Communication.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(communications -> {
                    addCommunications(communications, false);
                    Log.d(TAG, "Restored cache");
                }, Throwable::printStackTrace);
    }

    public void onRefresh() {
        UpdateCommunications();
    }

    private void UpdateCommunications() {
        mSwipeRefreshLayout.setRefreshing(true);
        new SpiaggiariApiClient(getContext())
                .getCommunications()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(communications -> {
                    addCommunications(communications, true);
                    mSwipeRefreshLayout.setRefreshing(false);
                }, error -> {
                    error.printStackTrace();
                    if (!isNetworkAvailable(getContext())) {
                        Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
                    } else
                        Snackbar.make(mCoordinatorLayout, error.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mRVAdapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mRVAdapter.getFilter().filter(newText);
        return false;
    }
}
