package com.sharpdroid.registroelettronico.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.API.V2.APIClient
import com.sharpdroid.registroelettronico.Activities.AddEventActivity
import com.sharpdroid.registroelettronico.Adapters.AgendaAdapter
import com.sharpdroid.registroelettronico.BottomSheet.AgendaBS
import com.sharpdroid.registroelettronico.Databases.Entities.Profile
import com.sharpdroid.registroelettronico.Databases.Entities.RemoteAgenda
import com.sharpdroid.registroelettronico.Databases.Entities.RemoteAgendaInfo
import com.sharpdroid.registroelettronico.Databases.Entities.SuperAgenda
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.EventType
import com.sharpdroid.registroelettronico.Utils.Metodi.*
import com.transitionseverywhere.ChangeText
import com.transitionseverywhere.TransitionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_agenda.*
import org.apache.commons.lang3.text.WordUtils
import java.text.SimpleDateFormat
import java.util.*


// DONE: 19/01/2017 Aggiungere eventi all'agenda
// DONE: 19/01/2017 Aggiungere eventi dell'agenda nel calendario del telefono

class FragmentAgenda : Fragment(), CompactCalendarView.CompactCalendarViewListener, AgendaAdapter.AgendaClickListener, AgendaBS.Listener, NotificationManager.NotificationReceiver {

    override fun didReceiveNotification(code: Int, args: Array<in Any>) {
        when (code) {
            EventType.UPDATE_AGENDA_START -> {
                //started
            }
            EventType.UPDATE_AGENDA_OK -> {
                updateCalendar()
                updateAdapter()
            }
            EventType.UPDATE_AGENDA_KO -> {
                //failed
            }
        }
    }

    private val TAG = FragmentAgenda::class.java.simpleName
    internal var month = SimpleDateFormat("MMMM", Locale.getDefault())
    internal var year = SimpleDateFormat("yyyy", Locale.getDefault())
    internal var agenda = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    private var mContext: Context? = null
    //private RegistroDB mRegistroDB;
    private lateinit var adapter: AgendaAdapter
    private var mDate: Date = Date()
    private val events = ArrayList<SuperAgenda>()

    private var active: Boolean = false //avoid updating views if fragment is gone

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        mContext = activity.applicationContext
        active = true
        return inflater!!.inflate(R.layout.fragment_agenda, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.instance.addObserver(this, EventType.UPDATE_AGENDA_OK, EventType.UPDATE_AGENDA_KO, EventType.UPDATE_AGENDA_START)

        with(activity.calendar) {
            setLocale(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALIAN)
            setUseThreeLetterAbbreviation(true)
            setListener(this@FragmentAgenda)
            shouldSelectFirstDayOfMonthOnScroll(false)
            prepareDate(true)
            setCurrentDate(mDate)
        }

        fab_big_add.setClosedOnTouchOutside(true)
        fab_mini_verifica.setOnClickListener { _ -> startActivity(Intent(mContext, AddEventActivity::class.java).putExtra("type", "Verifica").putExtra("time", mDate.time)) }
        fab_mini_esercizi.setOnClickListener { _ -> startActivity(Intent(mContext, AddEventActivity::class.java).putExtra("type", "Compiti").putExtra("time", mDate.time)) }
        fab_mini_altro.setOnClickListener { _ -> startActivity(Intent(mContext, AddEventActivity::class.java).putExtra("type", "Altro").putExtra("time", mDate.time)) }

        adapter = AgendaAdapter(place_holder)
        adapter.setItemClickListener(this)
        recycler.layoutManager = LinearLayoutManager(mContext)
        recycler.adapter = adapter

        download()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NotificationManager.instance.removeObserver(this, EventType.UPDATE_AGENDA_OK, EventType.UPDATE_AGENDA_KO, EventType.UPDATE_AGENDA_START)
    }

    private fun prepareDate(predictNextDay: Boolean) {
        mDate = Date()

        val cal = toCalendar(mDate)

        if (predictNextDay) {
            val isOrarioScolastico = cal.get(Calendar.HOUR_OF_DAY) < 14
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && !isOrarioScolastico) {
                cal.add(Calendar.DATE, 2)
            } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                cal.add(Calendar.DATE, 1)
            } else if (!isOrarioScolastico)
                cal.add(Calendar.DATE, 1)
        }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        mDate = cal.time
    }

    private fun fetch(currentDate: Boolean?): List<SuperAgenda> {
        val profile = Profile.getProfile(activity)
        if (profile != null) {
            return if (currentDate == true) RemoteAgenda.getAgenda(profile.id, mDate) else RemoteAgenda.getSuperAgenda(profile.id)
        }
        return listOf()
    }

    private fun load() {
        events.clear()
        events.addAll(fetch(false))
        // TODO: 30/09/2017 ADD LOCAL EVENTS
        //events.addAll(Agenda.Companion.getSuperAgenda(getActivity()));
    }

    private fun updateAdapter() {
        setAdapterEvents(fetch(true))
    }

    private fun updateCalendar() {
        activity.calendar.removeAllEvents()
        load()
        activity.calendar.addEvents(convertEvents(events))
        activity.calendar.invalidate()

    }

    private fun toCalendar(date: Date): Calendar {
        val cal = Calendar.getInstance()
        cal.time = date
        return cal
    }

    private fun download() {

        val dates = getStartEnd("yyyyMMdd")

        val p = Profile.getProfile(activity)
        if (p != null)
            APIClient.with(activity).getAgenda(dates[0], dates[1])
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ events ->
                        Log.d(TAG, "Scaricati " + events.agenda.size + " eventi")
                        //mRegistroDB.addEvents(events);

                        save(events.getAgenda(p))
                        Log.d(TAG, "Salvati " + events.agenda.size + " eventi per " + p.name)

                        if (active) {
                            updateCalendar()
                            updateAdapter()
                        }
                    }) { error ->
                        error.printStackTrace()
                        if (active)
                            Snackbar.make(coordinator_layout, error.localizedMessage, Snackbar.LENGTH_LONG).show()
                    }
    }

    fun save(events: List<RemoteAgenda>) {
        SugarRecord.saveInTx(events)
    }

    override fun onDayClick(dateClicked: Date) {
        mDate = dateClicked
        updateAdapter()
    }

    override fun onMonthScroll(firstDayOfNewMonth: Date) {
        mDate = firstDayOfNewMonth
        setTitleSubtitle(firstDayOfNewMonth)
    }

    private fun setTitleSubtitle(d: Date?) {
        TransitionManager.beginDelayedTransition(activity.toolbar, ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_IN))
        activity.toolbar.title = WordUtils.capitalizeFully(month.format(d))
        activity.toolbar.subtitle = WordUtils.capitalizeFully(year.format(d))
    }

    private fun setAdapterEvents(events: List<SuperAgenda>) {
        adapter.clear()
        adapter.addAll(events)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.agenda, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.today) {
            prepareDate(false)
            activity.calendar.setCurrentDate(mDate)
            setTitleSubtitle(mDate)

            setAdapterEvents(RemoteAgenda.getAgenda(Profile.getProfile(activity)!!.id, mDate))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDetach() {
        super.onDetach()
        activity.toolbar.subtitle = ""
        active = false
    }

    override fun onResume() {
        super.onResume()
        activity.calendar.visibility = View.VISIBLE
        setTitleSubtitle(mDate)
        updateCalendar()
        updateAdapter()
    }

    override fun onAgendaItemClicked(e: SuperAgenda) {
        val bottomSheetAgenda = AgendaBS()
        bottomSheetAgenda.setEvent(e)
        bottomSheetAgenda.show(childFragmentManager, "dialog")
    }

    override fun onBottomSheetItemClicked(position: Int, e: SuperAgenda) {
        // TODO: 30/09/2017 Head?
        //String head = getSubjectNameOrProfessorName(event, mRegistroDB);
        when (position) {
            0 -> {
                val found: RemoteAgendaInfo? = e.agenda.getInfo()
                if (found != null) {
                    found.completed = !found.completed
                    SugarRecord.update(found)
                } else {
                    SugarRecord.save(RemoteAgendaInfo(e.agenda.id, true, false))
                }

                e.completed = !e.completed
                updateAdapter()
            }
            1 -> {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, eventToString(e, ""))
                sendIntent.type = "text/plain"
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_with)))
            }
            2 -> addEventToCalendar(mContext, e)
            3 -> {
                val clipboard = mContext!!.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager?
                val clip = android.content.ClipData.newPlainText("Evento copiato", eventToString(e, ""))
                clipboard?.primaryClip = clip

            }
            4 -> {
                val found: RemoteAgendaInfo? = e.agenda.getInfo()
                if (found != null) {
                    found.archived = true
                    SugarRecord.update(found)
                } else {
                    SugarRecord.save(RemoteAgendaInfo(e.agenda.id, false, true))
                }

                updateAdapter()
                updateCalendar()
            }
        }
    }
}
