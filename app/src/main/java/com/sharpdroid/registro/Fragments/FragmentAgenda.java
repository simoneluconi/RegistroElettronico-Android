package com.sharpdroid.registro.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.sharpdroid.registro.API.SpiaggiariApiClient;
import com.sharpdroid.registro.Databases.AgendaDB;
import com.sharpdroid.registro.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sharpdroid.registro.Utils.Metodi.convertEvents;

public class FragmentAgenda extends Fragment implements CompactCalendarView.CompactCalendarViewListener {
    final private String TAG = FragmentAgenda.class.getSimpleName();

    SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
    SimpleDateFormat month = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    private Context mContext;
    private static CompactCalendarView calendarView;
    private static TextView monthView;
    private AgendaDB db;

    public static FragmentAgenda getInstance(CompactCalendarView c, TextView month) {
        calendarView = c;
        monthView = month;
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

        monthView.setText(month.format(new Date()));
        calendarView.setLocale(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALIAN);
        calendarView.setUseThreeLetterAbbreviation(true);
        calendarView.setListener(this);
        calendarView.removeAllEvents();
        calendarView.addEvents(convertEvents(db.getEvents()));
        calendarView.invalidate();
        updateDB();

        return layout;
    }

    private void updateDB() {
        new SpiaggiariApiClient(mContext).mService.getEvents(0L, Long.MAX_VALUE)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(objects -> {
                    db.addEvents(objects);
                    calendarView.removeAllEvents();
                    Log.d(TAG, "Scaricati " + objects.size() + " eventi");
                    calendarView.addEvents(convertEvents(db.getEvents()));
                    calendarView.invalidate();
                }, throwable -> Log.e(TAG, throwable.getLocalizedMessage()));
    }

    @Override
    public void onDayClick(Date dateClicked) {
        // TODO: 07/01/2017 update recycler
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        monthView.setText(month.format(firstDayOfNewMonth));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}
