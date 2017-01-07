package com.sharpdroid.registro.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.sharpdroid.registro.Databases.AgendaDB;
import com.sharpdroid.registro.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.ButterKnife;

public class FragmentAgenda extends Fragment implements CompactCalendarView.CompactCalendarViewListener {
    final private String TAG = FragmentAgenda.class.getSimpleName();

    SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());

    private Context mContext;
    private static CompactCalendarView calendarView;
    private AgendaDB db;

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
        db = AgendaDB.from(mContext);

        calendarView.setLocale(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALIAN);
        calendarView.setUseThreeLetterAbbreviation(true);
        calendarView.setListener(this);

        return layout;
    }

    @Override
    public void onDayClick(Date dateClicked) {
        Toast.makeText(mContext, format.format(dateClicked), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        Toast.makeText(mContext, format.format(firstDayOfNewMonth), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}
