package com.sharpdroid.registro.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.sharpdroid.registro.R;

import java.util.Locale;
import java.util.TimeZone;

import butterknife.ButterKnife;

public class FragmentAgenda extends Fragment {
    final private String TAG = FragmentAgenda.class.getSimpleName();

    private Context mContext;
    private static CompactCalendarView calendarView;

    public static FragmentAgenda getInstance(CompactCalendarView c) {
        calendarView = c;
        return new FragmentAgenda();
    }

    public FragmentAgenda() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        View layout = inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, layout);

        calendarView.setLocale(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALIAN);
        calendarView.setUseThreeLetterAbbreviation(true);

        return layout;
    }
}
