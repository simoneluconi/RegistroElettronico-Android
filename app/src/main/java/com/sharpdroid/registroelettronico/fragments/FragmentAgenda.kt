package com.sharpdroid.registroelettronico.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.activities.AddEventActivity
import com.sharpdroid.registroelettronico.adapters.AgendaAdapter
import com.sharpdroid.registroelettronico.database.entities.*
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.fragments.bottomSheet.AgendaBS
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.EventType
import com.sharpdroid.registroelettronico.utils.Metodi
import com.sharpdroid.registroelettronico.utils.Metodi.*
import com.transitionseverywhere.ChangeText
import com.transitionseverywhere.TransitionManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_agenda.*
import java.text.SimpleDateFormat
import java.util.*


// DONE: 19/01/2017 Aggiungere eventi all'agenda
// DONE: 19/01/2017 Aggiungere eventi dell'agenda nel calendario del telefono

class FragmentAgenda : Fragment(), CompactCalendarView.CompactCalendarViewListener, AgendaAdapter.AgendaClickListener, AgendaBS.Listener, NotificationManager.NotificationReceiver {

    override fun didReceiveNotification(code: Int, args: Array<in Any>) {
        when (code) {
            EventType.UPDATE_AGENDA_START -> {

            }
            EventType.UPDATE_AGENDA_OK,
            EventType.UPDATE_AGENDA_KO -> {
                load(true)
                updateCalendar()
                updateAdapter()
            }
        }
    }

    private var month = SimpleDateFormat("MMMM", Locale.getDefault())
    private var year = SimpleDateFormat("yyyy", Locale.getDefault())
    internal var agenda = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    private lateinit var adapter: AgendaAdapter
    private var mDate: Date = Date()
    private val events = ArrayList<Any>()

    private var active: Boolean = false //avoid updating views if fragment is gone

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        active = true
        return inflater.inflate(R.layout.fragment_agenda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.instance.addObserver(this, EventType.UPDATE_AGENDA_OK, EventType.UPDATE_AGENDA_KO, EventType.UPDATE_AGENDA_START)

        with(activity.calendar) {
            visibility = View.VISIBLE
            setLocale(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALIAN)
            setUseThreeLetterAbbreviation(true)
            setListener(this@FragmentAgenda)
            shouldSelectFirstDayOfMonthOnScroll(false)

            if (savedInstanceState != null) {
                mDate = Date(savedInstanceState.getLong("date"))
            } else {
                prepareDate(true)
            }
            setCurrentDate(mDate)
        }

        with(activity) {
            fab_big_add.visibility = View.VISIBLE

            fab_big_add.setClosedOnTouchOutside(true)
            fab_mini_verifica.setOnClickListener { _ -> startActivity(Intent(context, AddEventActivity::class.java).putExtra("type", "Verifica").putExtra("time", mDate.time)); fab_big_add.close(true) }
            fab_mini_esercizi.setOnClickListener { _ -> startActivity(Intent(context, AddEventActivity::class.java).putExtra("type", "Compiti").putExtra("time", mDate.time)); fab_big_add.close(true) }
            fab_mini_altro.setOnClickListener { _ -> startActivity(Intent(context, AddEventActivity::class.java).putExtra("type", "Altro").putExtra("time", mDate.time)); fab_big_add.close(true) }
        }

        adapter = AgendaAdapter(place_holder)
        adapter.setItemClickListener(this)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter

        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Agenda"))

        download()
    }

    private fun download() {
        val p = Profile.getProfile(context)
        updateAgenda(p)
        updatePeriods(p)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("date", mDate.time)
    }

    override fun onResume() {
        super.onResume()
        load(true)
        activity.calendar.visibility = View.VISIBLE
        setTitleSubtitle(mDate)
        updateCalendar()
        updateAdapter()
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

    private fun fetch(currentDate: Boolean?): List<Any> {
        return if (currentDate == true)
            events.filter {
                if (it is SuperAgenda) {
                    return@filter it.agenda.end.time in mDate.time until mDate.time + 86400000 && it.agenda.start.time in mDate.time until mDate.time + 86400000
                } else if (it is LocalAgenda) {
                    return@filter it.day in mDate.time until mDate.time + 86400000
                }
                true
            }
        else events
    }

    private fun load(invalidate: Boolean) {
        //if (invalidate) invalidateCache()

        events.clear()
        events.addAll(RemoteAgenda.getSuperAgenda(Account.with(activity).user))
        //events.addAll(SugarRecord.find(LocalAgenda::class.java, "PROFILE=? AND ARCHIVED=0", Account.with(activity).user.toString()))
    }

    private fun updateAdapter() {
        setAdapterEvents(fetch(true))
    }

    private fun updateCalendar() {
        activity.calendar.removeAllEvents()
        activity.calendar.addEvents(convertEvents(events))
        activity.calendar.invalidate()

    }

    private fun toCalendar(date: Date): Calendar {
        val cal = Calendar.getInstance()
        cal.time = date
        return cal
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
        activity.toolbar.title = capitalizeEach(month.format(d))
        activity.toolbar.subtitle = capitalizeEach(year.format(d))
    }

    private fun setAdapterEvents(events: List<Any>) {
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

            setAdapterEvents(fetch(true))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDetach() {
        super.onDetach()
        activity.toolbar.subtitle = ""
        active = false
    }


    override fun onAgendaItemClicked(e: Any) {
        val bottomSheetAgenda = AgendaBS()
        bottomSheetAgenda.setEvent(e)
        bottomSheetAgenda.show(childFragmentManager, "dialog")
    }

    private fun invalidateCache() {
        /*SubjectTeacher.clearCache()
        Teacher.clearCache()
        Subject.clearCache()

        SubjectTeacher.setupCache(Account.with(context).user)
        Teacher.setupCache()
        Subject.setupCache()

        RemoteAgenda.clearCache()
        RemoteAgenda.setupCache(Account.with(context).user)*/
    }

    override fun onBottomSheetItemClicked(position: Int, e: Any) {
        if (e is SuperAgenda) {
            when (position) {
                0 -> {
                    val found: RemoteAgendaInfo? = e.agenda.getInfo()
                    if (found != null) {
                        found.completed = !found.completed
                        DatabaseHelper.database.eventsDao().update(found)
                    } else {
                        DatabaseHelper.database.eventsDao().insert(RemoteAgendaInfo(e.agenda.id, true, false, isEventTest(e)))
                    }

                    e.completed = !e.completed
                    load(true)
                    updateAdapter()
                }
                1 -> {
                    val found = e.agenda.getInfo()
                    if (found != null) {
                        found.test = !found.test
                        DatabaseHelper.database.eventsDao().update(found)
                    } else {
                        DatabaseHelper.database.eventsDao().insert(RemoteAgendaInfo(e.agenda.id, false, false, !isEventTest(e)))
                    }

                    load(true)
                    updateAdapter()
                    updateCalendar()
                }
                2 -> {
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, eventToString(e, ""))
                    sendIntent.type = "text/plain"
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.share_with)))
                }
                3 -> {
                    val found: RemoteAgendaInfo? = e.agenda.getInfo()
                    if (found != null) {
                        found.archived = true
                        DatabaseHelper.database.eventsDao().update(found)
                    } else {
                        DatabaseHelper.database.eventsDao().insert(RemoteAgendaInfo(e.agenda.id, false, true, isEventTest(e)))
                    }

                    load(true)
                    updateAdapter()
                    updateCalendar()
                }
            }
        } else if (e is LocalAgenda) { //no need to invalidate since cache is used only for RemoteAgenda
            when (position) {
                0 -> {
                    e.completed_date = if (e.completed_date != null) null else Date()
                    DatabaseHelper.database.eventsDao().insert(e)

                    load(false)
                    updateAdapter()
                }
                1 -> {
                    e.type = if (e.type == "verifica") "altro" else "verifica"
                    DatabaseHelper.database.eventsDao().insert(e)

                    load(false)
                    updateAdapter()
                    updateCalendar()
                }
                2 -> {
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, Metodi.complex.format(e.day) + "\n" + e.title)
                    sendIntent.type = "text/plain"
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.share_with)))
                }
                3 -> {
                    e.archived = !e.archived
                    DatabaseHelper.database.eventsDao().insert(e)

                    load(false)
                    updateAdapter()
                    updateCalendar()
                }
            }
        }
    }
}
