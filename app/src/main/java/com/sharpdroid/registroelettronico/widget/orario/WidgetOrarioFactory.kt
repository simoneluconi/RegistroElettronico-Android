package com.sharpdroid.registroelettronico.widget.orario

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.TimetableItem
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Metodi
import com.sharpdroid.registroelettronico.utils.Metodi.convertFloatTimeToString
import java.util.*

class WidgetOrarioFactory(private val applicationContext: Context, val intent: Intent?) : RemoteViewsService.RemoteViewsFactory {
    val list = mutableListOf<Any>()
    val profile = intent?.getLongExtra("profile", 0L)
            ?: throw IllegalStateException("'profile' not found within the intent's extras")
    val subjects by lazy { DatabaseHelper.database.subjectsDao().getAllSubjectsPOJOBlocking(profile) }

    override fun onCreate() {
        // Should load data in onDataSetChanged()
    }

    override fun onDataSetChanged() {
        list.clear()

        val schedule = DatabaseHelper.database.timetableDao().queryProfileSync(profile)
        val dayOfWeek = getDayOfWeekToDisplay()
        val content = schedule.filter { it.dayOfWeek == dayOfWeek }.sortedBy { it.start }
        if (content.isNotEmpty()) {
            // Header
            list.add(applicationContext.resources.getStringArray(R.array.days_of_week)[dayOfWeek])
            // List
            list.addAll(content)
        }
    }

    private fun getDayOfWeekToDisplay(): Int {
        val cal = Calendar.getInstance()

        val isOrarioScolastico = cal.get(Calendar.HOUR_OF_DAY) < 14
        if (cal[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY && !isOrarioScolastico) {
            cal.add(Calendar.DATE, 2)
        } else if (cal[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) {
            cal.add(Calendar.DATE, 1)
        } else if (!isOrarioScolastico)
            cal.add(Calendar.DATE, 1)

        return cal[Calendar.DAY_OF_WEEK] - 2 //monday: 0, tuesday: 1, ...
    }

    override fun getViewAt(i: Int): RemoteViews {
        return if (i == 0) {
            val rv = RemoteViews(applicationContext.packageName, R.layout.big_header)
            rv.setTextViewText(R.id.text, Metodi.capitalizeFirst(list[0] as String))
            rv
        } else {
            val current = list[i] as TimetableItem
            val rv = RemoteViews(applicationContext.packageName, R.layout.widget_agenda_event)
            rv.setTextViewText(R.id.title, subjects.first { it.subject.id == current.subject }.getSubjectName())
            rv.setTextViewText(R.id.subtitle, convertFloatTimeToString(current.start) + if (current.where != null) " - ${convertFloatTimeToString(current.end)} (${current.where})" else " - ${convertFloatTimeToString(current.end)}") // 9:15 - 10:15 (A205) // 9:15 - 10:15
            rv
        }
    }

    override fun getLoadingView() = null
    override fun hasStableIds() = true
    override fun getItemId(i: Int) = 0L
    override fun getCount() = list.size
    override fun getViewTypeCount() = 2
    override fun onDestroy() {
        list.clear()
    }

}