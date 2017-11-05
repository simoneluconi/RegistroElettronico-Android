package com.sharpdroid.registroelettronico.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.activities.MainActivity
import com.sharpdroid.registroelettronico.database.entities.Grade
import com.sharpdroid.registroelettronico.database.viewModels.GradesViewModel
import com.sharpdroid.registroelettronico.fragments.bottomSheet.OrderMedieBS
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.EventType
import com.sharpdroid.registroelettronico.utils.Metodi.updateMarks
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_medie_pager.*

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
                if (swiperefresh.isRefreshing) swiperefresh.isRefreshing = false
            }
        }
    }

    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var viewModel: GradesViewModel

    private var pagerSelected: Boolean = false

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

        val profile = Account.with(context).user

        if (savedInstanceState != null) {
            view_pager.currentItem = savedInstanceState.getInt("position")
            pagerSelected = true
        } else if (!pagerSelected && Grade.hasMarksSecondPeriod(profile)) {
            view_pager.setCurrentItem(1, false)
            pagerSelected = true
        }

        viewModel = ViewModelProviders.of(this)[GradesViewModel::class.java]

        //Observe remote data changes
        viewModel.getFirstPeriod(profile).observe(this, Observer {
            val fragment = pagerAdapter.instantiateItem(view_pager, 0) as FragmentMedie
            fragment.addSubjects(it.orEmpty(), 1, PreferenceManager.getDefaultSharedPreferences(context).getString("order", ""))
        })

        viewModel.getSecondPeriod(profile).observe(this, Observer {
            val fragment = pagerAdapter.instantiateItem(view_pager, 1) as FragmentMedie
            fragment.addSubjects(it.orEmpty(), 3, PreferenceManager.getDefaultSharedPreferences(context).getString("order", ""))
        })

        viewModel.getAllPeriods(profile).observe(this, Observer {
            val fragment = pagerAdapter.instantiateItem(view_pager, 2) as FragmentMedie
            fragment.addSubjects(it.orEmpty(), -1, PreferenceManager.getDefaultSharedPreferences(context).getString("order", ""))
        })


        viewModel.getOrder().observe(this, Observer {
            for (i in 0..pagerAdapter.count) {
                val fragment = pagerAdapter.instantiateItem(view_pager, i) as FragmentMedie
                when (i) {
                    0 -> fragment.addSubjects(viewModel.getFirstPeriod(profile).value.orEmpty(), 1, it.orEmpty())
                    1 -> fragment.addSubjects(viewModel.getSecondPeriod(profile).value.orEmpty(), 3, it.orEmpty())
                    2 -> fragment.addSubjects(viewModel.getAllPeriods(profile).value.orEmpty(), -1, it.orEmpty())
                }
            }
        })

        if (savedInstanceState == null) {
            download()
        }
        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Medie"))
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
        val pref = PreferenceManager.getDefaultSharedPreferences(context)

        val order = when (position) {
            0 -> "name"
            1 -> "avg"
            2 -> "count"
            else -> ""
        }

        pref.edit().putString("order", order).apply()
        viewModel.setOrder(order)
    }

    private fun download() {
        updateMarks(context)
    }

    private fun getSnackBarMessage(pos: Int): String {/*
        val p = if (pos == 0) 3 else if (pos == 1) 1 else 0
        val average = SugarRecord.findWithQuery(Average::class.java, "SELECT 0 as ID, AVG(M_VALUE) as AVG FROM GRADE WHERE PROFILE=? AND M_VALUE!=0 AND M_PERIOD!=?", Account.with(activity).user.toString(), p.toString())[0].sum.toDouble()
        var className: String? = SugarRecord.find(Lesson::class.java, "PROFILE=?", arrayOf(Account.with(activity).user.toString()), "M_CLASS_DESCRIPTION", null, "1").getOrNull(0).mClassDescription
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
        } else*/
        return ""
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
