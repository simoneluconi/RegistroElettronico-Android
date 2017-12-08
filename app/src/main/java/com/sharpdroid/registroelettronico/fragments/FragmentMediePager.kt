package com.sharpdroid.registroelettronico.fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.view.*
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.activities.MainActivity
import com.sharpdroid.registroelettronico.database.entities.Grade
import com.sharpdroid.registroelettronico.fragments.bottomSheet.OrderMedieBS
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.EventType
import com.sharpdroid.registroelettronico.utils.Metodi.updateMarks
import com.sharpdroid.registroelettronico.viewModels.GradesViewModel
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_medie_pager.*

class FragmentMediePager : Fragment(), SwipeRefreshLayout.OnRefreshListener, OrderMedieBS.OrderListener, NotificationManager.NotificationReceiver {
    override fun didReceiveNotification(code: EventType, args: Array<in Any>) {
        when (code) {
            EventType.UPDATE_MARKS_START -> if (!swiperefresh.isRefreshing) swiperefresh.isRefreshing = true
            EventType.UPDATE_MARKS_OK,
            EventType.UPDATE_MARKS_KO,
            EventType.UPDATE_SUBJECTS_OK -> {
                if (swiperefresh.isRefreshing) swiperefresh.isRefreshing = false
            }
            else -> { // Ignore
            }
        }
    }

    private lateinit var pagerAdapter: PagerAdapter
    private val viewModel: GradesViewModel by lazy {
        ViewModelProviders.of(activity)[GradesViewModel::class.java]
    }

    private var pagerSelected: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_medie_pager, container, false)

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
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                viewModel.selected = when (position) {
                    0 -> 1
                    1 -> 3
                    2 -> -1
                    else -> 0
                }
            }

            override fun onPageSelected(position: Int) {
                viewModel.selected = when (position) {
                    0 -> 1
                    1 -> 3
                    2 -> -1
                    else -> 0
                }
            }
        })
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
        viewModel.order.value = order
    }

    private fun download() {
        updateMarks(context)
    }

    override fun onRefresh() {
        download()
    }

    private inner class MediePager internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getPageTitle(position: Int) =
                if (position == 2) "Generale" else (position + 1).toString() + "° periodo"

        override fun getItem(position: Int): Fragment {
            val f = FragmentMedie()
            val args = Bundle()
            args.putInt("q", when (position) { 0 -> 1; 1 -> 3; else -> -1
            })
            f.arguments = args
            return f
        }

        override fun getCount() = 3
    }

}
