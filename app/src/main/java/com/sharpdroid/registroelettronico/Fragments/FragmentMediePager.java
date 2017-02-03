package com.sharpdroid.registroelettronico.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.API.SpiaggiariApiClient;
import com.sharpdroid.registroelettronico.Databases.AgendaDB;
import com.sharpdroid.registroelettronico.Interfaces.API.Mark;
import com.sharpdroid.registroelettronico.Interfaces.API.MarkSubject;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Tasks.CacheListObservable;
import com.sharpdroid.registroelettronico.Tasks.CacheListTask;
import com.sharpdroid.registroelettronico.Views.CSwipeRefreshLayout;

import java.io.File;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sharpdroid.registroelettronico.Utils.Metodi.CalculateScholasticCredits;
import static com.sharpdroid.registroelettronico.Utils.Metodi.getMarksOfThisPeriod;
import static com.sharpdroid.registroelettronico.Utils.Metodi.getOverallAverage;
import static com.sharpdroid.registroelettronico.Utils.Metodi.isNetworkAvailable;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMediePager extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    final private String TAG = FragmentMedie.class.getSimpleName();

    Context mContext;

    /**
     * PARENT VIEWS
     **/
    TabLayout tabLayout;
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.swiperefresh)
    CSwipeRefreshLayout mCSwipeRefreshLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    PagerAdapter pagerAdapter;
    Snackbar snackbar;

    public FragmentMediePager() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_medie_pager, container, false);
        ButterKnife.bind(this, layout);

        mContext = getContext();

        mCoordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinator_layout);
        tabLayout = (TabLayout) getActivity().findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.VISIBLE);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) getActivity().findViewById(R.id.toolbar).getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

        pagerAdapter = new MediePager(getChildFragmentManager());

        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(mViewPager);

        mCSwipeRefreshLayout.setOnRefreshListener(this);
        mCSwipeRefreshLayout.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial);

        bindMarksSubjectsCache();

        UpdateMedie();

        return layout;
    }

    private void UpdateMedie() {
        mCSwipeRefreshLayout.setRefreshing(true);
        new SpiaggiariApiClient(mContext)
                .getMarks()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(marks -> {
                    addSubjects(marks, true);
                    mCSwipeRefreshLayout.setRefreshing(false);
                    String msg = getSnackBarMessage(marks);
                    snackbar = Snackbar.make(mCoordinatorLayout, msg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }, error -> {
                    if (!isNetworkAvailable(mContext)) {
                        Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
                    }
                    mCSwipeRefreshLayout.setRefreshing(false);
                });
    }

    private String getSnackBarMessage(List<MarkSubject> marks) {
        double average = getOverallAverage(marks);

        String className = AgendaDB.from(mContext).getClassDescription();
        if (className != null) {
            className = className.split("\\s+")[0];
            int classyear;
            try {
                classyear = Integer.parseInt(String.valueOf(className.charAt(0)));
            } catch (Exception ex) {
                ex.printStackTrace();
                return "Media totale: " + String.format(Locale.getDefault(), "%.2f", average);
            }
            if (classyear > 2)
                return String.format(Locale.getDefault(), "Media Totale: %.2f | Crediti: %2$d + %3$d", average, CalculateScholasticCredits(classyear, average), 1);
            else return "Media totale: " + String.format(Locale.getDefault(), "%.2f", average);
        } else return null;
    }

    private void addSubjects(List<MarkSubject> markSubjects, boolean docache) {
        if (!markSubjects.isEmpty()) {

            FragmentMedie fragment;
            for (int i = 0; i < pagerAdapter.getCount(); i++) {
                fragment = (FragmentMedie) pagerAdapter.instantiateItem(mViewPager, i);
                fragment.addSubjects(markSubjects);
                if (i == 1 && !getMarksOfThisPeriod(markSubjects, Mark.SECONDO_PERIODO).isEmpty()) {
                    mViewPager.setCurrentItem(i, false);
                }
            }

            if (docache) {
                // Update cache
                new CacheListTask(mContext.getCacheDir(), TAG).execute((List) markSubjects);
            }
        }
    }


    private void bindMarksSubjectsCache() {
        new CacheListObservable(new File(mContext.getCacheDir(), TAG))
                .getCachedList(MarkSubject.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(marksSubjects -> {
                    addSubjects(marksSubjects, false);
                }, Throwable::printStackTrace);
    }

    @Override
    public void onRefresh() {
        UpdateMedie();
    }

    protected class MediePager extends FragmentPagerAdapter {
        MediePager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 2) return "Generale";
            return String.valueOf(position + 1) + "° periodo";
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = new FragmentMedie();
            Bundle args = new Bundle();
            args.putInt("q", position);
            f.setArguments(args);
            return f;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
