package com.sharpdroid.registro.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.sharpdroid.registro.API.SpiaggiariApiClient;
import com.sharpdroid.registro.Adapters.AgendaAdapter;
import com.sharpdroid.registro.Databases.AgendaDB;
import com.sharpdroid.registro.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sharpdroid.registro.Utils.Metodi.convertEvents;

public class FragmentAgenda extends Fragment implements CompactCalendarView.CompactCalendarViewListener {
    private static CompactCalendarView calendarView;
    final private String TAG = FragmentAgenda.class.getSimpleName();
    SimpleDateFormat month = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.place_holder)
    View place_holder;
    private Toolbar actionBar;
    private Context mContext;
    private AgendaDB db;
    private AgendaAdapter adapter;

    public FragmentAgenda() {
    }

    public void getInstance(CompactCalendarView c, Toolbar month) {
        calendarView = c;
        actionBar = month;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        View layout = inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, layout);
        db = AgendaDB.from(mContext);
        calendarView.setVisibility(View.VISIBLE);

        actionBar.setTitle(month.format(new Date()));
        calendarView.setLocale(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALIAN);
        calendarView.setUseThreeLetterAbbreviation(true);
        calendarView.setListener(this);
        calendarView.removeAllEvents();
        calendarView.addEvents(convertEvents(db.getEvents()));
        calendarView.invalidate();
        updateDB();

        adapter = new AgendaAdapter(mContext, place_holder);
        recycler.setLayoutManager(new LinearLayoutManager(mContext));
        recycler.setAdapter(adapter);
        adapter.addAllCalendarEvents(calendarView.getEvents(new Date()));

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void updateDB() {
        new SpiaggiariApiClient(mContext).mService.getEvents(0L, Long.MAX_VALUE)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(objects -> {
                    Log.d(TAG, "Scaricati " + objects.size() + " eventi");
                    db.addEvents(objects);
                    calendarView.removeAllEvents();
                    calendarView.addEvents(convertEvents(db.getEvents()));
                    calendarView.invalidate();
                }, Throwable::printStackTrace);
    }

    @Override
    public void onDayClick(Date dateClicked) {
        adapter.clear();
        adapter.addAllCalendarEvents(calendarView.getEvents(dateClicked));
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        actionBar.setTitle(month.format(firstDayOfNewMonth));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        calendarView.setVisibility(View.GONE);
        db.close();
    }
}
