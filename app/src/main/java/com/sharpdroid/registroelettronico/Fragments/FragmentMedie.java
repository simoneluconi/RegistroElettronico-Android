package com.sharpdroid.registroelettronico.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.IntegerRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.API.SpiaggiariApiClient;
import com.sharpdroid.registroelettronico.Adapters.MedieAdapter;
import com.sharpdroid.registroelettronico.Databases.AgendaDB;
import com.sharpdroid.registroelettronico.Databases.SubjectsDB;
import com.sharpdroid.registroelettronico.Interfaces.API.Mark;
import com.sharpdroid.registroelettronico.Interfaces.API.MarkSubject;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Tasks.CacheListObservable;
import com.sharpdroid.registroelettronico.Tasks.CacheListTask;
import com.sharpdroid.registroelettronico.Utils.ItemOffsetDecoration;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sharpdroid.registroelettronico.Utils.Metodi.CalculateScholasticCredits;
import static com.sharpdroid.registroelettronico.Utils.Metodi.getOverallAverage;
import static com.sharpdroid.registroelettronico.Utils.Metodi.isNetworkAvailable;

// TODO: 19/01/2017 Divisione P1 e P2
// TODO: 19/01/2017 Visualizzare media generale e crediti scolastici in modo decente

public class FragmentMedie extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    final private String TAG = FragmentMedie.class.getSimpleName();

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    SubjectsDB subjectsDB;
    Snackbar snackbar;
    private MedieAdapter mRVAdapter;
    private Context mContext;

    public FragmentMedie() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        View layout = inflater.inflate(R.layout.coordinator_swipe_recycler_padding, container, false);

        ButterKnife.bind(this, layout);

        subjectsDB = SubjectsDB.from(mContext);

        RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        mRecyclerView.addItemDecoration(new ItemOffsetDecoration(mContext, R.dimen.cards_margin));

        mRVAdapter = new MedieAdapter(mContext, new CopyOnWriteArrayList<>(), subjectsDB);
        mRecyclerView.setAdapter(mRVAdapter);

        UpdateMedie();

        return layout;
    }

    @Override
    public void onResume() {
        bindMarksCache();
        super.onResume();
    }

    private void addSubjects(List<MarkSubject> markSubjects, boolean docache) {
        if (!markSubjects.isEmpty()) {
            mRVAdapter.clear();
            mRVAdapter.addAll(markSubjects);

            if (docache) {
                // Update cache
                new CacheListTask(mContext.getCacheDir(), TAG).execute((List) markSubjects);
            }
        }
    }

    private void bindMarksCache() {
        new CacheListObservable(new File(mContext.getCacheDir(), TAG))
                .getCachedList(MarkSubject.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(marks -> {
                    addSubjects(marks, false);
                    Log.d(TAG, "Restored cache");
                });
    }

    public void onRefresh() {
        UpdateMedie();
    }

    private void UpdateMedie() {
        mSwipeRefreshLayout.setRefreshing(true);
        new SpiaggiariApiClient(mContext)
                .getMarks()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(marks -> {
                    addSubjects(marks, true);
                    mSwipeRefreshLayout.setRefreshing(false);
                    String msg = getSnackBarMessage(marks);
                    snackbar = Snackbar.make(mCoordinatorLayout, msg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }, error -> {
                    if (!isNetworkAvailable(mContext)) {
                        Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                });
    }

    private String getSnackBarMessage(List<MarkSubject> marks) {
        double average = getOverallAverage(marks);

        String className = AgendaDB.from(mContext).getClassDescription();
        if (className != null) className = className.split("\\s+")[0];

        int classyear;
        try {
            classyear = Integer.parseInt(String.valueOf(className.toCharArray()[0]));
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Media totale: " + String.format(Locale.getDefault(), "%.2f", average);
        }
        if (classyear > 2)
            return String.format(Locale.getDefault(), "Media Totale: %.2f | Crediti: %2$d + %3$d", average, CalculateScholasticCredits(classyear, average), 1);
        else return "Media totale: " + String.format(Locale.getDefault(), "%.2f", average);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subjectsDB.close();
    }
}
