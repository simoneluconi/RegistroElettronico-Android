package com.sharpdroid.registroelettronico.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.sharpdroid.registroelettronico.API.SpiaggiariApiClient;
import com.sharpdroid.registroelettronico.Adapters.AgendaAdapter;
import com.sharpdroid.registroelettronico.Databases.AgendaDB;
import com.sharpdroid.registroelettronico.R;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.sharpdroid.registroelettronico.Utils.Metodi.convertEvents;

// TODO: 19/01/2017 Aggiungere eventi all'agenda 
// TODO: 19/01/2017 Aggiungere eventi dell'agenda nel calendario del telefono 

public class FragmentAgenda extends Fragment implements CompactCalendarView.CompactCalendarViewListener {
    final private String TAG = FragmentAgenda.class.getSimpleName();
    SimpleDateFormat month = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.place_holder)
    View place_holder;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    private CompactCalendarView mCompactCalendarView;
    private Toolbar mToolbar;
    private Context mContext;
    private AgendaDB db;
    private AgendaAdapter adapter;
    private Date mDate;

    public FragmentAgenda() {
    }

    private Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        View layout = inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, layout);

        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mCompactCalendarView = (CompactCalendarView) getActivity().findViewById(R.id.calendar);

        db = AgendaDB.from(mContext);

        mCompactCalendarView.setLocale(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALIAN);
        mCompactCalendarView.setUseThreeLetterAbbreviation(true);
        mCompactCalendarView.setListener(this);
        mCompactCalendarView.removeAllEvents();
        mCompactCalendarView.addEvents(convertEvents(db.getEvents()));
        mCompactCalendarView.invalidate();

        adapter = new AgendaAdapter(mContext, place_holder);
        recycler.setLayoutManager(new LinearLayoutManager(mContext));
        recycler.setAdapter(adapter);
        mDate = new Date();

        Calendar cal = toCalendar(mDate);

        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && cal.get(Calendar.HOUR_OF_DAY) > 14) {
            cal.add(Calendar.DATE, 2);
        } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            cal.add(Calendar.DATE, 1);
        }

        mDate = cal.getTime();

        mCompactCalendarView.setCurrentDate(mDate);
        adapter.addAllCalendarEvents(mCompactCalendarView.getEvents(mDate));

        updateDB();

        return layout;
    }

    private void updateDB() {
        new SpiaggiariApiClient(mContext)
                .getEvents(0L, Long.MAX_VALUE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(events -> {
                    Log.d(TAG, "Scaricati " + events.size() + " eventi");
                    db.addEvents(events);
                    mCompactCalendarView.removeAllEvents();
                    mCompactCalendarView.addEvents(convertEvents(events));
                    mCompactCalendarView.invalidate();
                    adapter.addAllCalendarEvents(mCompactCalendarView.getEvents(mDate));
                }, error -> {
                    error.printStackTrace();
                    Snackbar.make(mCoordinatorLayout, error.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                });
    }

    @Override
    public void onDayClick(Date dateClicked) {
        adapter.clear();
        mDate = dateClicked;
        adapter.addAllCalendarEvents(mCompactCalendarView.getEvents(dateClicked));
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        mDate = firstDayOfNewMonth;
        mToolbar.setTitle(WordUtils.capitalizeFully(month.format(firstDayOfNewMonth)));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCompactCalendarView.setVisibility(View.GONE);
        db.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbar.setTitle(WordUtils.capitalizeFully(month.format(mDate)));
        mCompactCalendarView.setVisibility(View.VISIBLE);
    }
}
