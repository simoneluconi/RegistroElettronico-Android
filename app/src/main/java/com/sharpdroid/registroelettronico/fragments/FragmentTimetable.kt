package com.sharpdroid.registroelettronico.fragments


import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.NestedScrollView
import android.view.*
import android.widget.LinearLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.activities.AddTimetableItemActivity
import com.sharpdroid.registroelettronico.database.entities.Profile
import com.sharpdroid.registroelettronico.database.entities.TimetableItem
import com.sharpdroid.registroelettronico.database.pojos.SubjectPOJO
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.LayoutHelper
import com.sharpdroid.registroelettronico.utils.LayoutHelper.MATCH_PARENT
import com.sharpdroid.registroelettronico.utils.LayoutHelper.WRAP_CONTENT
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeFirst
import com.sharpdroid.registroelettronico.utils.Metodi.convertGeniusToTimetable
import com.sharpdroid.registroelettronico.utils.Metodi.dp
import com.sharpdroid.registroelettronico.utils.Metodi.mapColorsToSubjects
import com.sharpdroid.registroelettronico.utils.Metodi.updateLessons
import com.sharpdroid.registroelettronico.utils.Metodi.updateSubjects
import com.sharpdroid.registroelettronico.views.cells.ComplexCell
import com.sharpdroid.registroelettronico.views.timetable.TimetableLayout
import kotlinx.android.synthetic.main.app_bar_main.*

class FragmentTimetable : Fragment() {
    private lateinit var timetable: TimetableLayout
    private lateinit var subjects: List<SubjectPOJO>

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
        setHasOptionsMenu(true)

        subjects = DatabaseHelper.database.subjectsDao().getAllSubjectsPOJOBlocking(Account.with(context).user)

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
                        .negativeText("Modifica")
                        .onNegative { _, _ -> startActivity(Intent(context, AddTimetableItemActivity::class.java).putExtra("id", item.id)) }
                        .onNeutral { _, _ -> DatabaseHelper.database.timetableDao().delete(item) }
                        .show()
            }
        }
        timetable.itemLongListener = { item ->
            startActivity(Intent(context, AddTimetableItemActivity::class.java).putExtra("id", item.id))
        }


        DatabaseHelper.database.timetableDao().queryProfile(Account.with(context).user).observe(this, Observer {
            timetable.setupData(it.orEmpty(), subjects)

            with(timetable.parent as NestedScrollView) {
                if (scrollY == 0) {
                    postDelayed({
                        if (scrollY == 0) {
                            scrollY = initialScrollY(savedInstanceState)
                        }
                    }, 20)
                }
            }
        })

        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Orario"))

        showTutorial()

        val p = Profile.getProfile(context)
        updateSubjects(p)
        updateLessons(p)
    }

    private fun initialScrollY(savedInstanceState: Bundle?): Int {
        return if (savedInstanceState?.get("scrollY") != null) savedInstanceState["scrollY"] as Int else adaptTableScrollY()
    }

    private fun adaptTableScrollY(): Int {
        val minY = Math.max(timetable.data.minBy { it.start }?.start?.times(timetable.tileHeight)?.minus(dp(6))
                ?: 0f, 0f)
        return Math.round(minY)
    }

    private fun showTutorial() {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        if (!pref.getBoolean("has_seen_feature_AUTO_TIMETABLE", false)) {
            timetable.postDelayed({
                TapTargetView.showFor(activity, TapTarget.forToolbarMenuItem(activity.toolbar, R.id.sync, "Orario automatico", "Ora puoi rilevare automaticamente il tuo orario scolastico in modo intelligente!").transparentTarget(false))

                pref.edit().putBoolean("has_seen_feature_AUTO_TIMETABLE", true).apply()
            }, 200)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sync_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.sync) {
            if (timetable.data.size > 0) {
                MaterialDialog.Builder(context)
                        .title("Cancellare orario?")
                        .content("L'orario corrente sarà eliminato e ne verrà generato uno automaticamente.")
                        .positiveText("SI")
                        .neutralText("NO")
                        .onPositive { _, _ ->
                            handleGeniusTimetableResult()
                        }.show()
            } else {
                handleGeniusTimetableResult()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun handleGeniusTimetableResult() {
        if (generateGeniusTimetable() != 0)
            MaterialDialog.Builder(context)
                    .title("Errore")
                    .content("Non sono state trovate lezioni sufficienti per rilevare l'orario.")
                    .show()

    }

    private fun generateGeniusTimetable(): Int {
        val profile = Account.with(context).user

        val genius = DatabaseHelper.database.lessonsDao().geniusTimetable(profile)
        val subjects = genius.map { it.subject }.groupBy { it }.keys.toList()
        val mapColorsSubjects = mapColorsToSubjects(subjects)
        if (mapColorsSubjects.isEmpty()) return -1

        val convertedGenius = convertGeniusToTimetable(profile, genius, mapColorsSubjects)
        if (convertedGenius.isEmpty()) return 1

        DatabaseHelper.database.timetableDao().removeAndInsert(profile, convertedGenius)
        return 0
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

    private fun hhmm(time: Float) = hhmm(time.toDouble())
    private fun hhmm(time: Double) = hhmm(Math.floor(time).toInt(), ((time - Math.floor(time).toInt()) * 60).toInt())
    private fun hhmm(hourOfDay: Int, minute: Int) = "$hourOfDay:${if (minute >= 10) minute.toString() else "0$minute"}"

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
