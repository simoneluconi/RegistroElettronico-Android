package com.sharpdroid.registroelettronico.fragments

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.widget.SwipeRefreshLayout
import android.view.*
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.activities.MainActivity
import com.sharpdroid.registroelettronico.database.entities.Average
import com.sharpdroid.registroelettronico.database.entities.Grade
import com.sharpdroid.registroelettronico.database.entities.Lesson
import com.sharpdroid.registroelettronico.fragments.bottomSheet.OrderMedieBS
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.EventType
import com.sharpdroid.registroelettronico.utils.Metodi.CalculateScholasticCredits
import com.sharpdroid.registroelettronico.utils.Metodi.updateMarks
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_medie_pager.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class FragmentMediePager : Fragment(), SwipeRefreshLayout.OnRefreshListener, OrderMedieBS.OrderListener, NotificationManager.NotificationReceiver {
    override fun didReceiveNotification(code: Int, args: Array<in Any>) {
        when (code) {
            EventType.UPDATE_MARKS_START -> if (!swiperefresh.isRefreshing) swiperefresh.isRefreshing = true
            EventType.UPDATE_MARKS_OK,
            EventType.UPDATE_MARKS_KO,
            EventType.UPDATE_SUBJECTS_OK -> {
                load()
                if (swiperefresh.isRefreshing) swiperefresh.isRefreshing = false
            }
        }
    }

    private lateinit var pagerAdapter: PagerAdapter

    private var pagerSelected: Boolean = false
    private val grades = mutableListOf<Grade>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_medie_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.instance.addObserver(this, EventType.UPDATE_MARKS_OK, EventType.UPDATE_MARKS_START, EventType.UPDATE_MARKS_KO, EventType.UPDATE_PERIODS_START, EventType.UPDATE_PERIODS_OK, EventType.UPDATE_PERIODS_KO, EventType.UPDATE_SUBJECTS_OK)

        setHasOptionsMenu(true)

        with(activity as MainActivity) {
            title = getString(R.string.medie)
            tab_layout?.visibility = View.VISIBLE
            (toolbar.layoutParams as AppBarLayout.LayoutParams?)?.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
        }

        pagerAdapter = MediePager(childFragmentManager)
        view_pager.adapter = pagerAdapter
        view_pager.offscreenPageLimit = 2
        activity.tab_layout.setupWithViewPager(view_pager)

        swiperefresh.setOnRefreshListener(this)
        swiperefresh.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial)

        if (savedInstanceState != null) {
            view_pager.currentItem = savedInstanceState.getInt("position")
            pagerSelected = true
        } else if (!pagerSelected && Grade.hasMarksSecondPeriod(activity)) {
            view_pager.setCurrentItem(1, false)
            pagerSelected = true
        }

        //load()
        //download()
        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Medie"))
    }

    override fun onResume() {
        super.onResume()
        load()
        download()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("position", view_pager.currentItem)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NotificationManager.instance.removeObserver(this, EventType.UPDATE_MARKS_OK, EventType.UPDATE_MARKS_START, EventType.UPDATE_MARKS_KO, EventType.UPDATE_PERIODS_START, EventType.UPDATE_PERIODS_OK, EventType.UPDATE_PERIODS_KO, EventType.UPDATE_SUBJECTS_OK)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.voti_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.sort) {
            OrderMedieBS().show(childFragmentManager, "dialog")
        }
        return super.onOptionsItemSelected(item)
    }

    /**
    Listener for Bottom Sheet
     */
    override fun onItemClicked(position: Int) {
        when (position) {
            0 -> PreferenceManager.getDefaultSharedPreferences(context).edit().putString("order", "name").apply()
            1 -> PreferenceManager.getDefaultSharedPreferences(context).edit().putString("order", "avg").apply()
            2 -> PreferenceManager.getDefaultSharedPreferences(context).edit().putString("order", "count").apply()
        }
        load()
    }

    private fun download() {
        updateMarks(context)
    }

    private fun getSnackBarMessage(pos: Int): String {
        val p = if (pos == 0) 3 else if (pos == 1) 1 else 0
        val average = SugarRecord.findWithQuery(Average::class.java, "SELECT 0 as ID, AVG(M_VALUE) as AVG FROM GRADE WHERE PROFILE=? AND M_VALUE!=0 AND M_PERIOD!=?", Account.with(activity).user.toString(), p.toString())[0].avg.toDouble()
        var className: String? = SugarRecord.find(Lesson::class.java, "PROFILE=?", arrayOf(Account.with(activity).user.toString()), "M_CLASS_DESCRIPTION", null, "1")?.getOrNull(0)?.mClassDescription
        if (className != null) {
            className = className.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            val classyear: Int
            classyear = try {
                Integer.parseInt(className[0].toString())
            } catch (ex: Exception) {
                ex.printStackTrace()
                return "Media totale: " + String.format(Locale.getDefault(), "%.2f", average)
            }

            return if (classyear > 2)
                String.format(Locale.getDefault(), "Media Totale: %.2f | Crediti: %2\$d + %3\$d", average, CalculateScholasticCredits(classyear, average), 1)
            else
                "Media totale: " + String.format(Locale.getDefault(), "%.2f", average)
        } else
            return ""
    }

    private fun load() {
        val order = PreferenceManager.getDefaultSharedPreferences(context).getString("order", "")

        Grade.clearSubjectCache()
        Grade.setupSubjectCache(Account.with(context).user)

        grades.clear()
        grades.addAll(SugarRecord.find(Grade::class.java, "PROFILE = ?", Account.with(context).user.toString()))

        var fragment: FragmentMedie
        for (i in 0..pagerAdapter.count) {
            fragment = pagerAdapter.instantiateItem(view_pager, i) as FragmentMedie
            when (i) {
                0 -> fragment.addSubjects(Grade.getAverages(grades.filter { it.mPeriod == 1 }, order), 1)
                1 -> fragment.addSubjects(Grade.getAverages(grades.filter { it.mPeriod != 1 }, order), 3)
                2 -> fragment.addSubjects(Grade.getAverages(grades, order), -1)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Grade.clearSubjectCache()
    }

    override fun onRefresh() {
        download()
    }

    private inner class MediePager internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getPageTitle(position: Int): CharSequence {
            return if (position == 2) "Generale" else (position + 1).toString() + "° periodo"
        }

        override fun getItem(position: Int): Fragment {
            val f = FragmentMedie()
            val args = Bundle()
            args.putInt("q", position)
            f.arguments = args
            return f
        }

        override fun getCount(): Int {
            return 3
        }
    }

}