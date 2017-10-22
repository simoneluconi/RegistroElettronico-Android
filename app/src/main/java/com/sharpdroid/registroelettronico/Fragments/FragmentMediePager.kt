package com.sharpdroid.registroelettronico.Fragments


import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.widget.SwipeRefreshLayout
import android.view.*
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.Activities.MainActivity
import com.sharpdroid.registroelettronico.BottomSheet.OrderMedieBS
import com.sharpdroid.registroelettronico.Databases.Entities.Grade
import com.sharpdroid.registroelettronico.Databases.Entities.Lesson
import com.sharpdroid.registroelettronico.Interfaces.Client.Average
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.Account
import com.sharpdroid.registroelettronico.Utils.EventType
import com.sharpdroid.registroelettronico.Utils.Metodi.CalculateScholasticCredits
import com.sharpdroid.registroelettronico.Utils.Metodi.updateMarks
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
            EventType.UPDATE_MARKS_OK -> {
                load()
                if (swiperefresh.isRefreshing) swiperefresh.isRefreshing = false
            }
            EventType.UPDATE_LESSONS_KO -> {
                if (swiperefresh.isRefreshing) swiperefresh.isRefreshing = false
            }
        }
    }

    private lateinit var pagerAdapter: PagerAdapter

    private var pagerSelected: Boolean = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_medie_pager, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.instance.addObserver(this, EventType.UPDATE_MARKS_OK, EventType.UPDATE_MARKS_START, EventType.UPDATE_MARKS_KO, EventType.UPDATE_PERIODS_START, EventType.UPDATE_PERIODS_OK, EventType.UPDATE_PERIODS_KO)

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
        load()

        if (savedInstanceState != null) {
            view_pager.currentItem = savedInstanceState.getInt("position")
            pagerSelected = true
        } else if (!pagerSelected && Grade.hasMarksSecondPeriod(activity)) {
            view_pager.setCurrentItem(1, false)
            pagerSelected = true
        }
        //download()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("position", view_pager.currentItem)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NotificationManager.instance.removeObserver(this, EventType.UPDATE_MARKS_OK, EventType.UPDATE_MARKS_START, EventType.UPDATE_MARKS_KO, EventType.UPDATE_PERIODS_START, EventType.UPDATE_PERIODS_OK, EventType.UPDATE_PERIODS_KO)
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
            0 -> PreferenceManager.getDefaultSharedPreferences(context).edit().putString("order", "ORDER BY lower(`NAME`) ASC").apply()
            1 -> PreferenceManager.getDefaultSharedPreferences(context).edit().putString("order", "ORDER BY `AVG` DESC").apply()
            2 -> PreferenceManager.getDefaultSharedPreferences(context).edit().putString("order", "ORDER BY `COUNT` DESC").apply()
        }
        load()
    }

    private fun download() {
        updateMarks(activity)
    }

    private fun getSnackBarMessage(pos: Int): String {
        val p = if (pos == 0) 3 else if (pos == 1) 1 else 0
        val average = SugarRecord.findWithQuery(Average::class.java, "SELECT 0 as ID, AVG(M_VALUE) as AVG FROM GRADE WHERE PROFILE=? AND M_VALUE!=0 AND M_PERIOD!=?", Account.with(activity).user.toString(), p.toString())[0].avg.toDouble()
        var className: String? = SugarRecord.find(Lesson::class.java, "PROFILE=?", arrayOf(Account.with(activity).user.toString()), "M_CLASS_DESCRIPTION", null, "1")?.getOrNull(0)?.mClassDescription
        if (className != null) {
            className = className.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            val classyear: Int
            try {
                classyear = Integer.parseInt(className[0].toString())
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
        var fragment: FragmentMedie
        for (i in 0..pagerAdapter.count) {
            fragment = pagerAdapter.instantiateItem(view_pager, i) as FragmentMedie
            when (i) {
                0 -> fragment.addSubjects(Grade.getAverages(activity, "M_PERIOD=1 AND", order), 1)
                1 -> fragment.addSubjects(Grade.getAverages(activity, "M_PERIOD!=1 AND", order), 3)
                2 -> fragment.addSubjects(Grade.getAverages(activity, "", order), -1)
            }
        }
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
