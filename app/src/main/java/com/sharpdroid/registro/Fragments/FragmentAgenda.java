package com.sharpdroid.registro.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.sharpdroid.registro.API.SpiaggiariApiClient;
import com.sharpdroid.registro.Adapters.AgendaAdapter;
import com.sharpdroid.registro.Databases.AgendaDB;
import com.sharpdroid.registro.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sharpdroid.registro.Utils.Metodi.convertEvents;

public class FragmentAgenda extends Fragment implements CompactCalendarView.CompactCalendarViewListener {
    final private String TAG = FragmentAgenda.class.getSimpleName();

    SimpleDateFormat month = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    private Context mContext;
    private static CompactCalendarView calendarView;
    private static ActionBar actionBar;
    private AgendaDB db;
    private AgendaAdapter adapter;

    @BindView(R.id.recycler)
    RecyclerView recycler;

    public static FragmentAgenda getInstance(CompactCalendarView c, ActionBar month) {
        calendarView = c;
        actionBar = month;

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

        actionBar.setTitle(month.format(new Date()));
        calendarView.setLocale(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALIAN);
        calendarView.setUseThreeLetterAbbreviation(true);
        calendarView.setListener(this);
        calendarView.removeAllEvents();
        calendarView.addEvents(convertEvents(db.getEvents()));
        calendarView.invalidate();
        updateDB();

        adapter = new AgendaAdapter(mContext);
        recycler.setLayoutManager(new LinearLayoutManager(mContext));
        recycler.setAdapter(adapter);

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
        List<com.sharpdroid.registro.Interfaces.API.Event> events = new ArrayList<>();
        for (Event e : calendarView.getEvents(dateClicked)) {
            events.add((com.sharpdroid.registro.Interfaces.API.Event) e.getData());
        }
        adapter.clear();
        adapter.addAll(events);
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        actionBar.setTitle(month.format(firstDayOfNewMonth));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}
