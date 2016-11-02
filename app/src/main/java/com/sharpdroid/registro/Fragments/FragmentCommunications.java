package com.sharpdroid.registro.Fragments;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;

public class FragmentCommunications extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    final private String TAG = FragmentCommunications.class.getCanonicalName();

    private CoordinatorLayout mCoordinatorLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public FragmentCommunications() {

    }

    @Override
    public void onRefresh() {

    }
}
