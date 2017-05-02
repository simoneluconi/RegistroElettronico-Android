package com.sharpdroid.registroelettronico.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.API.SpiaggiariApiClient;
import com.sharpdroid.registroelettronico.BottomSheet.OrderMedieBS;
import com.sharpdroid.registroelettronico.Databases.RegistroDB;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Views.CSwipeRefreshLayout;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.sharpdroid.registroelettronico.Utils.Metodi.CalculateScholasticCredits;
import static com.sharpdroid.registroelettronico.Utils.Metodi.isNetworkAvailable;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMediePager extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OrderMedieBS.OrderListener {
    final private String TAG = FragmentMedie.class.getSimpleName();

    Context mContext;

    /**
     * PARENT VIEWS
     **/
    TabLayout tabLayout;

    @BindView(R.id.swiperefresh)
    CSwipeRefreshLayout mCSwipeRefreshLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    PagerAdapter pagerAdapter;
    RegistroDB db;

    private boolean pager_selected;

    public FragmentMediePager() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        return inflater.inflate(R.layout.fragment_medie_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        getActivity().setTitle(getString(R.string.medie));

        db = RegistroDB.Companion.getInstance(mContext);
        pagerAdapter = new MediePager(getChildFragmentManager());
        tabLayout = (TabLayout) getActivity().findViewById(R.id.tab_layout);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(mViewPager);

        mCSwipeRefreshLayout.setOnRefreshListener(this);
        mCSwipeRefreshLayout.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial);
        download();
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.voti_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sort) {
            new OrderMedieBS().show(getChildFragmentManager(), "dialog");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(int position) {
        switch (position) {
            case 0:
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("order", "ORDER BY lower(_name) ASC").apply();
                break;
            case 1:
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("order", "ORDER BY _avg DESC").apply();
                break;
            case 2:
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("order", "ORDER BY _avg ASC").apply();
                break;
        }
        load();
    }

    private void download() {
        mCSwipeRefreshLayout.setRefreshing(isNetworkAvailable(mContext));
        if (isNetworkAvailable(mContext))
            new SpiaggiariApiClient(mContext)
                    .getMarks()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(marks -> {
                        mCSwipeRefreshLayout.setRefreshing(false);
                        db.addMarks(marks);
                        load();
                        Snackbar.make(tabLayout, getSnackBarMessage(mViewPager.getCurrentItem()), Snackbar.LENGTH_LONG).show();
                    }, Throwable::printStackTrace);
    }

    private String getSnackBarMessage(int pos) {
        RegistroDB.Period p = (pos == 0) ? RegistroDB.Period.FIRST : ((pos == 1) ? RegistroDB.Period.SECOND : RegistroDB.Period.ALL);
        double average = db.getAverage(p);

        String className = db.getClassDescription();
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
        } else return "";
    }

    private void load() {
        String order = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("order", "");
        FragmentMedie fragment;
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            fragment = (FragmentMedie) pagerAdapter.instantiateItem(mViewPager, i);
            switch (i) {
                case 0:
                    fragment.addSubjects(db.getAverages(RegistroDB.Period.FIRST, order), i);
                    break;
                case 1:
                    fragment.addSubjects(db.getAverages(RegistroDB.Period.SECOND, order), i);
                    break;
                case 2:
                    fragment.addSubjects(db.getAverages(RegistroDB.Period.ALL, order), i);
                    break;
            }
        }

        if (!pager_selected && db.hasMarks(RegistroDB.Period.SECOND)) {
            mViewPager.setCurrentItem(1, false);
        }
        pager_selected = true;
    }

    @Override
    public void onRefresh() {
        download();
    }

    private class MediePager extends FragmentPagerAdapter {
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
