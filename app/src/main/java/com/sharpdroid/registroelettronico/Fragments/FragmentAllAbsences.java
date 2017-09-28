package com.sharpdroid.registroelettronico.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.API.V1.SpiaggiariApiClient;
import com.sharpdroid.registroelettronico.Adapters.AllAbsencesAdapter;
import com.sharpdroid.registroelettronico.Databases.RegistroDB;
import com.sharpdroid.registroelettronico.Interfaces.API.Absences;
import com.sharpdroid.registroelettronico.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

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

    RegistroDB db;

    public FragmentAllAbsences() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        return inflater.inflate(R.layout.coordinator_swipe_recycler, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial);

        getActivity().setTitle(getString(R.string.absences));

        db = RegistroDB.getInstance(getContext());

        adapter = new AllAbsencesAdapter(mContext);
        recycler.setLayoutManager(new LinearLayoutManager(mContext));
        recycler.setAdapter(adapter);

        load();
        download();

    }

    void addAbsences(Absences absences) {
        adapter.clear();
        adapter.addAll(absences);
    }

    private void load() {
        addAbsences(db.getAbsences());
    }

    private void save(Absences abs) {
        db.addAbsences(abs);
    }

    public void onRefresh() {
        download();
    }

    private void download() {
        mSwipeRefreshLayout.setRefreshing(true);
        new SpiaggiariApiClient(mContext)
                .getAbsences()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(absences -> {
                    save(absences);
                    load();
                    mSwipeRefreshLayout.setRefreshing(false);
                }, error -> {
                    error.printStackTrace();
                    if (!isNetworkAvailable(mContext)) {
                        Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
                    } else
                        Snackbar.make(mCoordinatorLayout, error.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                });
    }
}