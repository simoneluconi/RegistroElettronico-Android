package com.sharpdroid.registroelettronico.fragments


import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.NestedScrollView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.activities.AddTimetableItemActivity
import com.sharpdroid.registroelettronico.database.entities.TimetableItem
import com.sharpdroid.registroelettronico.database.pojos.SubjectPOJO
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.LayoutHelper
import com.sharpdroid.registroelettronico.utils.LayoutHelper.MATCH_PARENT
import com.sharpdroid.registroelettronico.utils.LayoutHelper.WRAP_CONTENT
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeFirst
import com.sharpdroid.registroelettronico.utils.Metodi.dp
import com.sharpdroid.registroelettronico.views.cells.ComplexCell
import com.sharpdroid.registroelettronico.views.timetable.TimetableLayout

class FragmentTimetable : Fragment() {
    lateinit var timetable: TimetableLayout

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        timetable = TimetableLayout(context)
        val ll = NestedScrollView(context)
        ll.layoutParams = LayoutHelper.createFrame(MATCH_PARENT, WRAP_CONTENT, 0, 0f, 0f, 0f, 0f)
        ll.addView(timetable)
        ll.setBackgroundColor(0xffffffff.toInt())
        return ll
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.title = "Orario"

        timetable.addListener = { col, row ->
            startActivity(Intent(context, AddTimetableItemActivity::class.java).putExtra("day", col).putExtra("start", row))
        }
        timetable.itemListener = { item ->
            DatabaseHelper.database.subjectsDao().getSubjectPOJOBlocking(item.subject, Account.with(context).user)?.let {
                val details = getDetailsListView(it, item)
                MaterialDialog.Builder(context)
                        .customView(details, true)
                        .title(capitalizeEach(it.getSubjectName(), false))
                        .neutralText(R.string.elimina)
                        .positiveText(android.R.string.ok)
                        .onNeutral { _, _ -> DatabaseHelper.database.timetableDao().delete(item) }
                        .show()
            }
        }
        timetable.itemLongListener = { item ->
            startActivity(Intent(context, AddTimetableItemActivity::class.java).putExtra("id", item.id))
        }

        DatabaseHelper.database.timetableDao().queryProfile(Account.with(context).user).observe(this, Observer {
            timetable.setupData(it.orEmpty())
        })


        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Orario"))


        if (savedInstanceState != null && savedInstanceState["scrollY"] != null) {
            (timetable.parent as NestedScrollView).postDelayed({
                (timetable.parent as NestedScrollView?)?.scrollY = savedInstanceState["scrollY"] as Int
            }, 20)
        } else {
            (timetable.parent as NestedScrollView).postDelayed({
                val minY = Math.max(timetable.data.minBy { it.start }?.start?.times(timetable.tileHeight)?.minus(dp(6)) ?: 0f, 0f)
                (timetable.parent as NestedScrollView?)?.scrollY = Math.round(minY)
            }, 20)
        }
    }

    private fun getDetailsListView(subject: SubjectPOJO, item: TimetableItem): LinearLayout {
        val days = arrayOf("lunedì", "martedì", "mercoledì", "giovedì", "venerdì", "sabato", "domenica")

        val linear = LinearLayout(context)
        linear.orientation = LinearLayout.VERTICAL

        var complexCell = ComplexCell(context)
        complexCell.setup(subject.getSubjectName(), ContextCompat.getDrawable(context, R.drawable.event_subject), true, null)
        complexCell.setPaddingDp(16, 8, 0, 8)
        linear.addView(complexCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT))


        complexCell = ComplexCell(context)
        complexCell.setup(capitalizeFirst(days[item.dayOfWeek]), ContextCompat.getDrawable(context, R.drawable.ic_today_black_24dp), true, null)
        complexCell.setPaddingDp(16, 8, 0, 8)
        linear.addView(complexCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT))

        complexCell = ComplexCell(context)
        complexCell.setup("${hhmm(item.start)} - ${hhmm(item.end)}", ContextCompat.getDrawable(context, R.drawable.ic_access_time_black_24dp), true, null)
        complexCell.setPaddingDp(16, 8, 0, 8)
        linear.addView(complexCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT))

        if (!item.where.isNullOrEmpty()) {
            complexCell = ComplexCell(context)
            complexCell.setup(item.where!!, ContextCompat.getDrawable(context, R.drawable.ic_place_black_24dp), true, null)
            complexCell.setPaddingDp(16, 8, 0, 8)
            linear.addView(complexCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT))
        }

        if (!item.notes.isNullOrEmpty()) {
            complexCell = ComplexCell(context)
            complexCell.setup(item.notes!!, ContextCompat.getDrawable(context, R.drawable.ic_filter_list_black_24dp), true, null)
            complexCell.setPaddingDp(16, 8, 0, 8)
            linear.addView(complexCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT))

        }

        linear.setBackgroundColor(Color.parseColor("#FEFEFE"))
        return linear
    }

    fun hhmm(time: Float) = hhmm(time.toDouble())
    fun hhmm(time: Double) = hhmm(Math.floor(time).toInt(), ((time - Math.floor(time).toInt()) * 60).toInt())
    fun hhmm(hourOfDay: Int, minute: Int) = "$hourOfDay:${if (minute >= 10) minute.toString() else "0$minute"}"

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        timetable.saveInstanceState(outState)
        outState?.putInt("scrollY", (timetable.parent as NestedScrollView).scrollY)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        timetable.restoreInstanceState(savedInstanceState)
    }

}
