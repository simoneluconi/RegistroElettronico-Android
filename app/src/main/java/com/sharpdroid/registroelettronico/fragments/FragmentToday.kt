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
import com.sharpdroid.registroelettronico.database.entities.Absence
import com.sharpdroid.registroelettronico.database.entities.Lesson
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.EventType.*
import com.sharpdroid.registroelettronico.utils.Metodi.dp
import com.sharpdroid.registroelettronico.utils.flat
import com.sharpdroid.registroelettronico.views.cells.LessonCell
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_today.*
import java.util.*

class FragmentToday : Fragment(), NotificationManager.NotificationReceiver {
    override fun didReceiveNotification(code: Int, args: Array<in Any>) {
        when (code) {
            UPDATE_ABSENCES_START -> {

            }
            UPDATE_ABSENCES_KO,
            UPDATE_ABSENCES_OK -> {

            }
            UPDATE_LESSONS_START -> {

            }
            UPDATE_LESSONS_KO,
            UPDATE_LESSONS_OK -> {

            }
            UPDATE_AGENDA_START -> {

            }
            UPDATE_AGENDA_OK,
            UPDATE_AGENDA_KO -> {

            }
        }
    }

    private val absences = mutableListOf<Absence>()
    private val lessons = mutableListOf<Lesson>()


    private val lessonsAdapter by lazy {
        LessonsAdapter()
    }
    val absenceAdapter by lazy {

    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_today, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.instance.addObserver(this,
                UPDATE_ABSENCES_KO, UPDATE_ABSENCES_OK, UPDATE_ABSENCES_START,
                UPDATE_LESSONS_KO, UPDATE_LESSONS_OK, UPDATE_LESSONS_START,
                UPDATE_AGENDA_KO, UPDATE_AGENDA_OK, UPDATE_AGENDA_START
        )

        activity.title = "Oggi a scuola"

        absence_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        lessons_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        lessons_recycler.isNestedScrollingEnabled = false
        lessons_recycler.setHasFixedSize(true)
        lessons_recycler.adapter = lessonsAdapter
        lessons_recycler.addItemDecoration(HorizontalDividerItemDecoration.Builder(context).margin(dp(0), dp(0)).colorResId(R.color.divider).build())

        lessons_empty.setTextAndDrawable("Nessuna lezione", R.drawable.ic_view_agenda)

        initializeDay(Date().flat())
    }

    private fun initializeDay(date: Date) {
        absences.addAll(SugarRecord.find(Absence::class.java, "DATE = ${date.time} AND PROFILE=${Account.with(context).user}"))

        lessons.addAll(SugarRecord.findWithQuery(Lesson::class.java, "SELECT ID, M_ARGUMENT, M_AUTHOR_NAME, M_DATE, M_HOUR_POSITION, M_SUBJECT_DESCRIPTION, COUNT(ID) as M_DURATION FROM LESSON WHERE M_DATE = ${date.time} AND PROFILE=${Account.with(context).user} GROUP BY M_ARGUMENT, M_SUBJECT_ID ORDER BY M_HOUR_POSITION ASC "))

        absence_card.visibility = if (absences.isNotEmpty()) View.VISIBLE else View.GONE
        lessons_empty.visibility = if (lessons.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onStop() {
        super.onStop()
        NotificationManager.instance.removeObserver(this,
                UPDATE_ABSENCES_KO, UPDATE_ABSENCES_OK, UPDATE_ABSENCES_START,
                UPDATE_LESSONS_KO, UPDATE_LESSONS_OK, UPDATE_LESSONS_START,
                UPDATE_AGENDA_KO, UPDATE_AGENDA_OK, UPDATE_AGENDA_START
        )
    }

    inner class LessonsAdapter : RecyclerView.Adapter<Holder>() {
        override fun getItemCount() = lessons.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val cell = holder.itemView as LessonCell
            cell.bindData(lessons[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
            return Holder(LessonCell(context))
        }
    }
}
