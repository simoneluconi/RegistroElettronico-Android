package com.sharpdroid.registroelettronico.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.Holders.Holder
import com.sharpdroid.registroelettronico.database.entities.*
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.EventType.*
import com.sharpdroid.registroelettronico.utils.Metodi
import com.sharpdroid.registroelettronico.utils.Metodi.dp
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
            UPDATE_ABSENCES_START -> {

            }
            UPDATE_ABSENCES_OK -> {
                initializeAbsence(Date().flat())
            }
            UPDATE_LESSONS_START -> {

            }
            UPDATE_LESSONS_OK -> {
                initializeLessons(Date().flat())
            }
            UPDATE_AGENDA_START -> {

            }
            UPDATE_AGENDA_OK -> {
                initializeEvents(Date().flat(), false)
            }
        }
    }

    private val absences = mutableListOf<Absence>()
    private val lessons = mutableListOf<Lesson>()
    private val events = mutableListOf<Any>()


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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = inflater?.inflate(R.layout.fragment_today, container, false)


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

        initializeDay(Date().flat())
        NotificationManager.instance.addObserver(this,
                UPDATE_ABSENCES_KO, UPDATE_ABSENCES_OK, UPDATE_ABSENCES_START,
                UPDATE_LESSONS_KO, UPDATE_LESSONS_OK, UPDATE_LESSONS_START,
                UPDATE_AGENDA_KO, UPDATE_AGENDA_OK, UPDATE_AGENDA_START
        )
        download()

        if (savedInstanceState != null && savedInstanceState["scrollY"] != null) {
            nested_scroll_view.scrollY = savedInstanceState.getInt("scrollY")
        }

        Teacher.clearCache()
        Subject.clearCache()
        SubjectTeacher.clearCache()

        SubjectTeacher.setupCache(Account.with(context).user)
        Subject.setupCache()
        Teacher.setupCache()

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

    private fun initializeDay(date: Date) {
        initializeAbsence(date)
        initializeLessons(date)
        initializeEvents(date, true)
    }

    private fun initializeAbsence(date: Date) {
        absences.clear()
        absences.addAll(SugarRecord.find(Absence::class.java, "DATE = ${date.time} AND PROFILE=${Account.with(context).user}"))
        absence_card.visibility = if (absences.isNotEmpty()) View.VISIBLE else View.GONE
        absence_recycler.adapter.notifyDataSetChanged()
    }

    private fun initializeLessons(date: Date) {
        lessons.clear()
        lessons.addAll(SugarRecord.findWithQuery(Lesson::class.java, "SELECT ID, M_ARGUMENT, M_AUTHOR_NAME, M_DATE, M_HOUR_POSITION, M_SUBJECT_DESCRIPTION, COUNT(ID) as `M_DURATION` FROM LESSON WHERE M_DATE = ${date.time} AND PROFILE=${Account.with(context).user} GROUP BY M_ARGUMENT, M_AUTHOR_NAME ORDER BY M_HOUR_POSITION ASC "))
        lessons_empty.visibility = if (lessons.isEmpty()) View.VISIBLE else View.GONE
        lessons_recycler.adapter.notifyDataSetChanged()
    }

    private fun initializeEvents(date: Date, invalidate: Boolean) {
        if (invalidate) {
            RemoteAgenda.clearCache()
            RemoteAgenda.setupCache(Account.with(context).user)
        }

        events.clear()
        events.addAll(RemoteAgenda.getSuperAgenda(Account.with(context).user, date, true))
        events.addAll(SugarRecord.find(LocalAgenda::class.java, "PROFILE=? AND ARCHIVED=0 AND DAY>=${date.time}", Account.with(context).user.toString()))
        events.sortWith(Comparator { t1: Any, t2: Any ->
            val date1 = (t1 as? SuperAgenda)?.agenda?.start ?: ((t1 as? LocalAgenda)?.day ?: Date(0))
            val date2 = (t2 as? SuperAgenda)?.agenda?.start ?: ((t2 as? LocalAgenda)?.day ?: Date(0))
            return@Comparator date1.compareTo(date2)
        })

        val tomorrow = Date(date.time + 86400000)

        tomorrowAdapter.events = events.filter {
            when (it) {
                is SuperAgenda -> it.agenda.start == tomorrow
                is LocalAgenda -> it.day == tomorrow
                else -> false
            }
        }
        tomorrowAdapter.date = date
        tomorrow_recycler.adapter.notifyDataSetChanged()
        tomorrow_empty.visibility = if (tomorrowAdapter.events.isEmpty()) View.VISIBLE else View.GONE

        val sevenDaysFromDate = Date(date.flat().time + 604800000)

        weekAdapter.date = date
        weekAdapter.events = events.filter {
            when (it) {
                is SuperAgenda -> it.agenda.start.after(tomorrow) && it.agenda.start.before(sevenDaysFromDate)
                is LocalAgenda -> it.day.after(tomorrow) && it.day.before(sevenDaysFromDate)
                else -> false
            }
        }
        week_card.visibility = if (weekAdapter.events.isEmpty()) View.VISIBLE else View.GONE
        textView4.visibility = if (weekAdapter.events.isEmpty()) View.VISIBLE else View.GONE
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
