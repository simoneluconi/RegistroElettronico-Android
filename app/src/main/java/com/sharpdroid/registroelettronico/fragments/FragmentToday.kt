package com.sharpdroid.registroelettronico.fragments

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.holders.Holder
import com.sharpdroid.registroelettronico.database.entities.Absence
import com.sharpdroid.registroelettronico.database.entities.Lesson
import com.sharpdroid.registroelettronico.database.entities.LocalAgenda
import com.sharpdroid.registroelettronico.database.entities.Profile
import com.sharpdroid.registroelettronico.database.pojos.RemoteAgendaPOJO
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.EventType.*
import com.sharpdroid.registroelettronico.utils.Metodi
import com.sharpdroid.registroelettronico.utils.Metodi.dp
import com.sharpdroid.registroelettronico.utils.add
import com.sharpdroid.registroelettronico.utils.flat
import com.sharpdroid.registroelettronico.views.cells.AbsenceCell
import com.sharpdroid.registroelettronico.views.cells.EventCell
import com.sharpdroid.registroelettronico.views.cells.LessonCell
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_today.*
import java.util.*

class FragmentToday : Fragment(), NotificationManager.NotificationReceiver {
    override fun didReceiveNotification(code: Int, args: Array<in Any>) {
        when (code) {
            UPDATE_AGENDA_OK -> {
            }
        }
    }

    private val absences = mutableListOf<Absence>()
    private val lessons = mutableListOf<Lesson>()
    private val remoteEvents = mutableListOf<RemoteAgendaPOJO>()
    private val localEvents = mutableListOf<LocalAgenda>()


    private val lessonsAdapter by lazy {
        LessonsAdapter()
    }
    private val absenceAdapter by lazy {
        AbsencesAdapter()
    }
    private val tomorrowAdapter by lazy {
        EventsAdapter(emptyList(), Date(), false)
    }
    private val weekAdapter by lazy {
        EventsAdapter(emptyList(), Date(), true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater?.inflate(R.layout.fragment_today, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.title = "Oggi a scuola"

        with(absence_recycler) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            isNestedScrollingEnabled = false
            addItemDecoration(HorizontalDividerItemDecoration.Builder(context).margin(dp(0), dp(0)).colorResId(R.color.divider).build())
            adapter = absenceAdapter
        }

        with(lessons_recycler) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            isNestedScrollingEnabled = false
            adapter = lessonsAdapter
            addItemDecoration(HorizontalDividerItemDecoration.Builder(context).margin(dp(0), dp(0)).colorResId(R.color.divider).build())
        }
        with(tomorrow_recycler) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            isNestedScrollingEnabled = false
            adapter = tomorrowAdapter
            addItemDecoration(HorizontalDividerItemDecoration.Builder(context).margin(dp(0), dp(0)).colorResId(R.color.divider).build())
        }
        with(week_recycler) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            isNestedScrollingEnabled = false
            adapter = weekAdapter
            addItemDecoration(HorizontalDividerItemDecoration.Builder(context).margin(dp(0), dp(0)).colorResId(R.color.divider).build())
        }

        lessons_empty.setTextAndDrawable("Nessuna lezione", R.drawable.ic_view_agenda)
        tomorrow_empty.setTextAndDrawable("Giornata libera", R.drawable.ic_event_available)
        week_empty.setTextAndDrawable("Settimana libera", R.drawable.ic_event_available)

        DatabaseHelper.database.lessonsDao().loadLessons(Account.with(context).user, Date().flat().time)
                .observe(this, Observer<List<Lesson>> {
                    initializeLessons(it ?: emptyList())
                })

        DatabaseHelper.database.absencesDao().getAbsences(Date().flat(), Account.with(context).user).observe(this, Observer {
            absences.clear()
            absences.addAll(it ?: emptyList())
            absence_card.visibility = if (absences.isNotEmpty()) View.VISIBLE else View.GONE
            absence_recycler.adapter.notifyDataSetChanged()
        })

        val mediatorLiveData = MediatorLiveData<List<Any>>()

        mediatorLiveData.addSource(DatabaseHelper.database.eventsDao().getRemote(Account.with(context).user), {
            remoteEvents.clear()
            remoteEvents.addAll(it.orEmpty())
            mediatorLiveData.value = null
        })

        mediatorLiveData.addSource(DatabaseHelper.database.eventsDao().getTodayAtSchool(Account.with(context).user), {
            localEvents.clear()
            localEvents.addAll(it.orEmpty())
            mediatorLiveData.value = null
        })

        mediatorLiveData.observe(this, Observer {
            val list = mutableListOf<Any>()
            list.addAll(remoteEvents)
            list.addAll(localEvents)
            initializeEvents(Date().flat(), list)
        })

        NotificationManager.instance.addObserver(this,
                UPDATE_ABSENCES_KO, UPDATE_ABSENCES_OK, UPDATE_ABSENCES_START,
                UPDATE_LESSONS_KO, UPDATE_LESSONS_OK, UPDATE_LESSONS_START,
                UPDATE_AGENDA_KO, UPDATE_AGENDA_OK, UPDATE_AGENDA_START
        )
        if (savedInstanceState == null)
            download()

        if (savedInstanceState != null && savedInstanceState["scrollY"] != null) {
            nested_scroll_view.scrollY = savedInstanceState.getInt("scrollY")
        }

        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Panoramica"))
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("scrollY", nested_scroll_view.scrollY)
    }

    private fun download() {
        val p = Profile.getProfile(context)
        Metodi.updateAbsence(p)
        Metodi.updateLessons(p)
        Metodi.updateAgenda(p)
    }

    private fun initializeLessons(lesson: List<Lesson>) {
        lessons.clear()
        lessons.addAll(lesson)
        lessons_empty.visibility = if (lessons.isEmpty()) View.VISIBLE else View.GONE
        lessons_recycler.adapter.notifyDataSetChanged()
    }

    private fun initializeEvents(date: Date, e: List<Any>) {
        val events = e.sortedWith(Comparator { t1: Any, t2: Any ->
            val date1 = (t1 as? RemoteAgendaPOJO)?.event?.start ?: Date((t1 as? LocalAgenda)?.day ?: 0)
            val date2 = (t2 as? RemoteAgendaPOJO)?.event?.start ?: Date((t2 as? LocalAgenda)?.day ?: 0)
            return@Comparator date1.flat().compareTo(date2.flat())
        })

        val tomorrow = date.add(Calendar.HOUR_OF_DAY, 24)

        tomorrowAdapter.events = events.filter {
            when (it) {
                is RemoteAgendaPOJO -> it.event.start.flat().time == tomorrow.time
                is LocalAgenda -> it.day == tomorrow.time
                else -> false
            }
        }
        tomorrowAdapter.date = date
        tomorrow_recycler.adapter.notifyDataSetChanged()
        tomorrow_empty.visibility = if (tomorrowAdapter.events.isEmpty()) View.VISIBLE else View.GONE

        val sevenDaysFromDate = date.flat().add(Calendar.DATE, 7)

        weekAdapter.date = date
        weekAdapter.events = events.filter {
            when (it) {
                is RemoteAgendaPOJO -> it.event.start.flat().after(tomorrow) && it.event.start.flat().before(sevenDaysFromDate)
                is LocalAgenda -> Date(it.day).after(tomorrow) && Date(it.day).before(sevenDaysFromDate)
                else -> false
            }
        }
        week_recycler.adapter.notifyDataSetChanged()

        week_card.visibility = if (weekAdapter.events.isNotEmpty()) View.VISIBLE else View.GONE
        textView4.visibility = if (weekAdapter.events.isNotEmpty()) View.VISIBLE else View.GONE
    }

    override fun onStop() {
        super.onStop()
        NotificationManager.instance.removeObserver(this,
                UPDATE_ABSENCES_KO, UPDATE_ABSENCES_OK, UPDATE_ABSENCES_START,
                UPDATE_LESSONS_KO, UPDATE_LESSONS_OK, UPDATE_LESSONS_START,
                UPDATE_AGENDA_KO, UPDATE_AGENDA_OK, UPDATE_AGENDA_START
        )
    }

    inner class AbsencesAdapter : RecyclerView.Adapter<Holder>() {
        override fun onBindViewHolder(holder: Holder, position: Int) {
            val cell = holder.itemView as AbsenceCell
            cell.bindData(absences[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = Holder(AbsenceCell(context))
        override fun getItemCount() = absences.size
    }

    inner class LessonsAdapter : RecyclerView.Adapter<Holder>() {
        override fun getItemCount() = lessons.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val cell = holder.itemView as LessonCell
            cell.bindData(lessons[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = Holder(LessonCell(context))
    }

    inner class EventsAdapter(var events: List<Any>, var date: Date, private val withDateDiff: Boolean) : RecyclerView.Adapter<Holder>() {
        override fun onBindViewHolder(holder: Holder, position: Int) {
            (holder.itemView as EventCell).bindData(events[position], date)
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = Holder(EventCell(context, withDateDiff))

        override fun getItemCount() = events.size

    }
}
