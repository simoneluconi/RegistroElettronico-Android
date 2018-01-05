package com.sharpdroid.registroelettronico.fragments


import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.activities.AddTimetableItemActivity
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.LayoutHelper
import com.sharpdroid.registroelettronico.utils.LayoutHelper.MATCH_PARENT
import com.sharpdroid.registroelettronico.utils.LayoutHelper.WRAP_CONTENT
import com.sharpdroid.registroelettronico.utils.Metodi.dp
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
            //TODO: Show details + delete button

            DatabaseHelper.database.subjectsDao().getSubjectInfoBlocking(item.subject)?.let {
                MaterialDialog.Builder(context)
                        .title("Eliminare?")
                        .content("Sei sicuro di eliminare \"" + it.getSubjectName() + "\" dal ${resources.getStringArray(R.array.days_of_week)[item.dayOfWeek]}?")
                        .positiveText("SI")
                        .neutralText("NO")
                        .onPositive { _, _ ->
                            DatabaseHelper.database.timetableDao().delete(item)
                        }
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
