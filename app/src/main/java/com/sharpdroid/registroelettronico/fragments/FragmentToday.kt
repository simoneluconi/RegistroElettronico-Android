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
                initializeAbsence(Date(1508277600000))
            }
            UPDATE_LESSONS_START -> {

            }
            UPDATE_LESSONS_KO,
            UPDATE_LESSONS_OK -> {
                initializeLessons(Date(1508277600000))
            }
            UPDATE_AGENDA_START -> {

            }
            UPDATE_AGENDA_OK,
            UPDATE_AGENDA_KO -> {
                initializeEvents(Date(1508277600000), false)
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
    private val eventsAdapter by lazy {
        EventsAdapter()
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = inflater?.inflate(R.layout.fragment_today, container, false)


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.instance.addObserver(this,
                UPDATE_ABSENCES_KO, UPDATE_ABSENCES_OK, UPDATE_ABSENCES_START,
                UPDATE_LESSONS_KO, UPDATE_LESSONS_OK, UPDATE_LESSONS_START,
                UPDATE_AGENDA_KO, UPDATE_AGENDA_OK, UPDATE_AGENDA_START
        )

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
        with(incoming_recycler) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            isNestedScrollingEnabled = false
            adapter = eventsAdapter
            addItemDecoration(HorizontalDividerItemDecoration.Builder(context).margin(dp(0), dp(0)).colorResId(R.color.divider).build())
        }

        lessons_empty.setTextAndDrawable("Nessuna lezione", R.drawable.ic_view_agenda)

        initializeDay(Date(1508277600000).flat())
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
        lessons.addAll(SugarRecord.findWithQuery(Lesson::class.java, "SELECT ID, M_ARGUMENT, M_AUTHOR_NAME, M_DATE, M_HOUR_POSITION, M_SUBJECT_DESCRIPTION, COUNT(ID) as M_DURATION FROM LESSON WHERE M_DATE = ${date.time} AND PROFILE=${Account.with(context).user} GROUP BY M_ARGUMENT, M_SUBJECT_ID ORDER BY M_HOUR_POSITION ASC "))
        lessons_empty.visibility = if (lessons.isEmpty()) View.VISIBLE else View.GONE
        lessons_recycler.adapter.notifyDataSetChanged()
    }

    private fun initializeEvents(date: Date, invalidate: Boolean) {
        if (invalidate) {
            RemoteAgenda.clearCache()
            RemoteAgenda.setupCache(Account.with(context).user)
        }

        events.clear()
        events.addAll(RemoteAgenda.getSuperAgenda(Account.with(activity).user, date, true))
        events.addAll(SugarRecord.find(LocalAgenda::class.java, "PROFILE=? AND ARCHIVED=0 AND DAY>=${date.time}", Account.with(activity).user.toString()))
        events.sortWith(Comparator { t1: Any, t2: Any ->
            val date1 = (t1 as? SuperAgenda)?.agenda?.start ?: ((t1 as? LocalAgenda)?.day ?: Date(0))
            val date2 = (t2 as? SuperAgenda)?.agenda?.start ?: ((t2 as? LocalAgenda)?.day ?: Date(0))
            return@Comparator date1.compareTo(date2)
        })

        incoming_recycler.adapter.notifyDataSetChanged()
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

    inner class EventsAdapter : RecyclerView.Adapter<Holder>() {

        override fun onBindViewHolder(holder: Holder, position: Int) {
            (holder.itemView as EventCell).bindData(events[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = Holder(EventCell(context))

        override fun getItemCount() = events.size

    }
}
