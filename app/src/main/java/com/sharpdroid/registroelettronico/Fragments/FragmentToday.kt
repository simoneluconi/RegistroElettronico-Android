package com.sharpdroid.registroelettronico.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.Adapters.Holders.Holder
import com.sharpdroid.registroelettronico.Databases.Entities.Absence
import com.sharpdroid.registroelettronico.Databases.Entities.Lesson
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.EventType.*
import com.sharpdroid.registroelettronico.Utils.Metodi.dp
import com.sharpdroid.registroelettronico.Utils.flat
import com.sharpdroid.registroelettronico.Views.Cells.LessonCell
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


    val lessonsAdapter by lazy {
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
        lessons_recycler.adapter = lessonsAdapter
        lessons_recycler.addItemDecoration(HorizontalDividerItemDecoration.Builder(context).margin(dp(16), dp(16)).build())
        lessons_recycler.isNestedScrollingEnabled = false

        initializeDay(Date().flat())
    }

    private fun initializeDay(date: Date) {
        absences.addAll(SugarRecord.find(Absence::class.java, "DATE = ${date.time}"))
        lessons.addAll(SugarRecord.find(Lesson::class.java, "M_DATE = ${date.time} ORDER BY M_HOUR_POSITION ASC"))

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
