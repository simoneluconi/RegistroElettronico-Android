package com.sharpdroid.registroelettronico.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.sharpdroid.registroelettronico.API.SpiaggiariApiClient;
import com.sharpdroid.registroelettronico.Activities.AddEventActivity;
import com.sharpdroid.registroelettronico.Adapters.AgendaAdapter;
import com.sharpdroid.registroelettronico.Databases.AgendaDB;
import com.sharpdroid.registroelettronico.Databases.SubjectsDB;
import com.sharpdroid.registroelettronico.Interfaces.Client.AdvancedEvent;
import com.sharpdroid.registroelettronico.R;
import com.transitionseverywhere.ChangeText;
import com.transitionseverywhere.TransitionManager;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.sharpdroid.registroelettronico.Utils.Metodi.addEventToCalendar;
import static com.sharpdroid.registroelettronico.Utils.Metodi.convertEvents;
import static com.sharpdroid.registroelettronico.Utils.Metodi.eventToString;
import static com.sharpdroid.registroelettronico.Utils.Metodi.getSubjectNameOrProfessorName;

// DONE: 19/01/2017 Aggiungere eventi all'agenda
// DONE: 19/01/2017 Aggiungere eventi dell'agenda nel calendario del telefono

public class FragmentAgenda extends Fragment implements CompactCalendarView.CompactCalendarViewListener, AgendaAdapter.AgendaClickListener, LongClickAgenda.Listener {
    final private String TAG = FragmentAgenda.class.getSimpleName();
    SimpleDateFormat month = new SimpleDateFormat("MMMM", Locale.getDefault());
    SimpleDateFormat year = new SimpleDateFormat("yyyy", Locale.getDefault());
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.place_holder)
    View place_holder;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.fab_big_add)
    FloatingActionMenu addFAB;
    @BindView(R.id.fab_mini_verifica)
    FloatingActionButton verificaFAB;
    @BindView(R.id.fab_mini_esercizi)
    FloatingActionButton eserciziFAB;
    @BindView(R.id.fab_mini_altro)
    FloatingActionButton altroFAB;

    private CompactCalendarView mCompactCalendarView;
    private Toolbar mToolbar;
    private Context mContext;
    private AgendaDB mAgendaDB;
    private SubjectsDB mSubjectsDB;
    private AgendaAdapter adapter;
    private Date mDate;
    private List<AdvancedEvent> events = new ArrayList<>();

    public FragmentAgenda() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mContext = getContext();
        return inflater.inflate(R.layout.fragment_agenda, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mAgendaDB = new AgendaDB(mContext);
        mSubjectsDB = new SubjectsDB(mContext);

        mCompactCalendarView = (CompactCalendarView) getActivity().findViewById(R.id.calendar);
        mCompactCalendarView.setVisibility(View.VISIBLE);
        mCompactCalendarView.setLocale(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALIAN);
        mCompactCalendarView.setUseThreeLetterAbbreviation(true);
        mCompactCalendarView.setListener(this);
        mCompactCalendarView.shouldSelectFirstDayOfMonthOnScroll(false);

        addFAB.setClosedOnTouchOutside(true);
        verificaFAB.setOnClickListener(v -> startActivity(new Intent(mContext, AddEventActivity.class).putExtra("type", "Verifica").putExtra("time", mDate.getTime())));
        eserciziFAB.setOnClickListener(v -> startActivity(new Intent(mContext, AddEventActivity.class).putExtra("type", "Compiti").putExtra("time", mDate.getTime())));
        altroFAB.setOnClickListener(v -> startActivity(new Intent(mContext, AddEventActivity.class).putExtra("type", "Altro").putExtra("time", mDate.getTime())));

        adapter = new AgendaAdapter(mContext, place_holder, mSubjectsDB);
        adapter.setItemClickListener(this);
        recycler.setLayoutManager(new LinearLayoutManager(mContext));
        recycler.setAdapter(adapter);

        prepareDate(true);
        mCompactCalendarView.setCurrentDate(mDate);

        //updateCalendar(); update onResume()
        updateAdapter();
        updateDB();
    }

    private void prepareDate(boolean predictNextDay) {
        mDate = new Date();

        Calendar cal = toCalendar(mDate);

        if (predictNextDay) {
            boolean isOrarioScolastico = cal.get(Calendar.HOUR_OF_DAY) < 14;
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && !isOrarioScolastico) {
                cal.add(Calendar.DATE, 2);
            } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                cal.add(Calendar.DATE, 1);
            } else if (!isOrarioScolastico)
                cal.add(Calendar.DATE, 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        mDate = cal.getTime();
    }

    private void fetchEvents() {
        events.clear();
        events.addAll(mAgendaDB.getAllEvents());
    }

    private void updateAdapter() {
        setAdapterEvents(mAgendaDB.getAllEvents(mDate.getTime()));
    }

    private void updateCalendar() {
        mCompactCalendarView.removeAllEvents();
        fetchEvents();
        mCompactCalendarView.addEvents(convertEvents(events));
        mCompactCalendarView.invalidate();

    }

    private Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    private void updateDB() {
        new SpiaggiariApiClient(mContext)
                .getEvents(0L, Long.MAX_VALUE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(events -> {
                    Log.d(TAG, "Scaricati " + events.size() + " eventi");
                    mAgendaDB.addEvents(events);

                    updateCalendar();
                    updateAdapter();

                }, error -> {
                    error.printStackTrace();
                    Snackbar.make(mCoordinatorLayout, error.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                });
    }

    @Override
    public void onDayClick(Date dateClicked) {
        mDate = dateClicked;
        updateAdapter();
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        mDate = firstDayOfNewMonth;
        setTitleSubtitle(firstDayOfNewMonth);
    }

    private void setTitleSubtitle(Date d) {
        TransitionManager.beginDelayedTransition(mToolbar, new ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_IN));
        mToolbar.setTitle(WordUtils.capitalizeFully(month.format(d)));
        mToolbar.setSubtitle(WordUtils.capitalizeFully(year.format(d)));
    }

    private void setAdapterEvents(List<AdvancedEvent> events) {
        adapter.clear();
        adapter.addAll(events);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.agenda, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.today) {
            prepareDate(false);
            mCompactCalendarView.setCurrentDate(mDate);
            setTitleSubtitle(mDate);

            setAdapterEvents(mAgendaDB.getAllEvents(mDate.getTime()));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAgendaDB.close();
        mSubjectsDB.close();
        mToolbar.setSubtitle("");
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitleSubtitle(mDate);
        updateCalendar();
    }

    @Override
    public void onAgendaItemClicked(AdvancedEvent e, int p) {
        LongClickAgenda longClickAgenda = LongClickAgenda.newInstance();
        longClickAgenda.setEvent(e);
        longClickAgenda.show(getChildFragmentManager(), "dialog");
        Log.i("CLICK", String.valueOf(e.isCompleted()));
    }

    @Override
    public void onBottomSheetItemClicked(int position, AdvancedEvent event) {
        String head = getSubjectNameOrProfessorName(event, mSubjectsDB);
        switch (position) {
            case 0:
                if (mAgendaDB.isCompleted(event.getId()))
                    mAgendaDB.setUncompleted(event.getId());
                else mAgendaDB.setCompleted(event.getId());
                fetchEvents();
                updateAdapter();
                break;
            case 1:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, eventToString(event, head));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case 2:
                addEventToCalendar(mContext, event);
                break;
            case 3:
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Evento copiato", eventToString(event, head));
                clipboard.setPrimaryClip(clip);

                break;
        }
    }
}
