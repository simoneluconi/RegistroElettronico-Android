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

import com.sharpdroid.registroelettronico.API.SpiaggiariApiClient;
import com.sharpdroid.registroelettronico.Activities.MainActivity;
import com.sharpdroid.registroelettronico.Adapters.AllLessonsAdapter;
import com.sharpdroid.registroelettronico.Databases.SubjectsDB;
import com.sharpdroid.registroelettronico.Interfaces.API.Lesson;
import com.sharpdroid.registroelettronico.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.sharpdroid.registroelettronico.Utils.Metodi.getProfessorOfThisSubject;
import static com.sharpdroid.registroelettronico.Utils.Metodi.getSubjectName;
import static com.sharpdroid.registroelettronico.Utils.Metodi.isNetworkAvailable;


public class FragmentLessons extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final static String TAG = "Lessons";
    static int code;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    AllLessonsAdapter mRVAdapter;
    SubjectsDB db;

    public FragmentLessons() {
    }

    public static FragmentLessons newInstance(int code) {
        FragmentLessons fragment = new FragmentLessons();
        Bundle b = new Bundle();
        b.putInt("code", code);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            code = getArguments().getInt("code");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_refresh_scrollbar, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        db = new SubjectsDB(getContext());
        MainActivity activity = (MainActivity) getActivity();

        activity.setTitle(getSubjectName(db.getSubject(code)));

        mRVAdapter = new AllLessonsAdapter(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mRVAdapter);

        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial);
        bindLessonsCache();

        UpdateLessons();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        db.close();
    }

    @Override
    public void onRefresh() {
        UpdateLessons();
    }

    private void addLessons(List<Lesson> lessons) {
        if (!lessons.isEmpty()) {
            mRVAdapter.clear();
            mRVAdapter.addAll(lessons);
        }
    }

    private void bindLessonsCache() {
        addLessons(db.getLessons(code));
    }

    private void UpdateLessons() {
        if (!isNetworkAvailable(getContext())) {
            Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        } else {
            for (Integer prof : db.getProfessorCodes(code)) {
                new SpiaggiariApiClient(getContext())
                        .getLessons(code, String.valueOf(prof))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(lessons -> {
                            //update subjectsDB
                            db.removeLessons(code);
                            db.addLessons(code, prof, lessons);
                            db.addProfessor(code, prof, getProfessorOfThisSubject(lessons));
                            bindLessonsCache();

                            mSwipeRefreshLayout.setRefreshing(false);
                        }, error -> {
                            error.printStackTrace();
                            mSwipeRefreshLayout.setRefreshing(false);
                        });
            }
        }
    }
}
