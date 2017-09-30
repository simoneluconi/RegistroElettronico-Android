package com.sharpdroid.registroelettronico.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.sharpdroid.registroelettronico.API.V2.APIClient;
import com.sharpdroid.registroelettronico.Activities.AddEventActivity;
import com.sharpdroid.registroelettronico.Adapters.AgendaAdapter;
import com.sharpdroid.registroelettronico.BottomSheet.AgendaBS;
import com.sharpdroid.registroelettronico.Databases.Entities.Agenda;
import com.sharpdroid.registroelettronico.Databases.Entities.EventInfo;
import com.sharpdroid.registroelettronico.Databases.Entities.Profile;
import com.sharpdroid.registroelettronico.Databases.Entities.RemoteAgenda;
import com.sharpdroid.registroelettronico.Databases.Entities.SuperAgenda;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Utils.Account;
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

// DONE: 19/01/2017 Aggiungere eventi all'agenda
// DONE: 19/01/2017 Aggiungere eventi dell'agenda nel calendario del telefono

public class FragmentAgenda extends Fragment implements CompactCalendarView.CompactCalendarViewListener, AgendaAdapter.AgendaClickListener, AgendaBS.Listener {
    final private String TAG = FragmentAgenda.class.getSimpleName();
    SimpleDateFormat month = new SimpleDateFormat("MMMM", Locale.getDefault());
    SimpleDateFormat year = new SimpleDateFormat("yyyy", Locale.getDefault());
    SimpleDateFormat agenda = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
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
    //private RegistroDB mRegistroDB;
    private AgendaAdapter adapter;
    private Date mDate;
    private List<SuperAgenda> events = new ArrayList<>();


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

        mToolbar = getActivity().findViewById(R.id.toolbar);
        //mRegistroDB = RegistroDB.getInstance(mContext);

        mCompactCalendarView = getActivity().findViewById(R.id.calendar);
        mCompactCalendarView.setLocale(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALIAN);
        mCompactCalendarView.setUseThreeLetterAbbreviation(true);
        mCompactCalendarView.setListener(this);
        mCompactCalendarView.shouldSelectFirstDayOfMonthOnScroll(false);

        addFAB.setClosedOnTouchOutside(true);

        verificaFAB.setOnClickListener(v -> startActivity(new Intent(mContext, AddEventActivity.class).putExtra("type", "Verifica").putExtra("time", mDate.getTime())));
        eserciziFAB.setOnClickListener(v -> startActivity(new Intent(mContext, AddEventActivity.class).putExtra("type", "Compiti").putExtra("time", mDate.getTime())));
        altroFAB.setOnClickListener(v -> startActivity(new Intent(mContext, AddEventActivity.class).putExtra("type", "Altro").putExtra("time", mDate.getTime())));

        adapter = new AgendaAdapter(mContext, place_holder);
        adapter.setItemClickListener(this);
        recycler.setLayoutManager(new LinearLayoutManager(mContext));
        recycler.setAdapter(adapter);

        prepareDate(true);
        mCompactCalendarView.setCurrentDate(mDate);

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

    private List<SuperAgenda> fetch() {
        return RemoteAgenda.Companion.getSuperAgenda(Account.Companion.with(getActivity()).getUser());
    }

    private void load() {
        events.clear();
        events.addAll(fetch());
        // TODO: 30/09/2017 ADD LOCAL EVENTS
        //events.addAll(Agenda.Companion.getSuperAgenda(getActivity()));
    }

    private void updateAdapter() {
        setAdapterEvents(fetch());
    }

    private void updateCalendar() {
        mCompactCalendarView.removeAllEvents();
        load();
        mCompactCalendarView.addEvents(convertEvents(events));
        mCompactCalendarView.invalidate();

    }

    private Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    private void updateDB() {
        /*Calendar cal = Calendar.getInstance();
        if(cal.get(Calendar.MONTH)>=9){ // Prima di gennaio
            cal.
        }else{

        }*/

        APIClient.Companion.with(getActivity()).getAgenda(agenda.format(new Date(0)), agenda.format(new Date()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(events -> {
                    Log.d(TAG, "Scaricati " + events.getAgenda().size() + " eventi");
                    //mRegistroDB.addEvents(events);

                    Agenda.saveInTx(events.getAgenda(Profile.Companion.getProfile(getActivity())));

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

    private void setAdapterEvents(List<SuperAgenda> events) {
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

            // TODO: 30/09/2017 Get today's agenda
            //setAdapterEvents(mRegistroDB.getAllEvents(mDate.getTime()));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mToolbar.setSubtitle("");
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.calendar).setVisibility(View.VISIBLE);
        setTitleSubtitle(mDate);
        updateCalendar();
        updateAdapter();
    }

    @Override
    public void onAgendaItemClicked(SuperAgenda e) {
        AgendaBS bottomSheetAgenda = new AgendaBS();
        bottomSheetAgenda.setEvent(e);
        bottomSheetAgenda.show(getChildFragmentManager(), "dialog");
    }

    @Override
    public void onBottomSheetItemClicked(int position, @NonNull SuperAgenda event) {
        // TODO: 30/09/2017 Head?
        //String head = getSubjectNameOrProfessorName(event, mRegistroDB);
        switch (position) {
            case 0:
                EventInfo found = EventInfo.find(EventInfo.class, "ID=" + event.getAgenda().getId()).get(0);
                if (found != null)
                    found.delete();
                else new EventInfo(true, event.getAgenda().getId().intValue(), true, false).save();
                updateAdapter();
                break;
            case 1:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, eventToString(event, ""));
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_with)));
                break;
            case 2:
                addEventToCalendar(mContext, event);
                break;
            case 3:
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Evento copiato", eventToString(event, ""));
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                }

                break;
            case 4:
                EventInfo found_ = EventInfo.find(EventInfo.class, "ID=" + event.getAgenda().getId()).get(0);
                if (found_ != null) {
                    found_.setArchived(true);
                    found_.save();
                } else
                    new EventInfo(true, event.getAgenda().getId().intValue(), false, true).save();
                updateAdapter();
                updateCalendar();
                break;
        }
    }
}
