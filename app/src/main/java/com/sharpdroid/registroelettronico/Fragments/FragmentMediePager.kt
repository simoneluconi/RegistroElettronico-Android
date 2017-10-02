package com.sharpdroid.registroelettronico.Fragments


import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.*
import butterknife.ButterKnife
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.API.V2.APIClient
import com.sharpdroid.registroelettronico.BottomSheet.OrderMedieBS
import com.sharpdroid.registroelettronico.Databases.Entities.Grade
import com.sharpdroid.registroelettronico.Databases.Entities.Profile
import com.sharpdroid.registroelettronico.Databases.RegistroDB
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.EventType
import com.sharpdroid.registroelettronico.Utils.Metodi.CalculateScholasticCredits
import com.sharpdroid.registroelettronico.Utils.Metodi.isNetworkAvailable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_medie_pager.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class FragmentMediePager : Fragment(), SwipeRefreshLayout.OnRefreshListener, OrderMedieBS.OrderListener, NotificationManager.NotificationReceiver {
    override fun didReceiveNotification(code: Int, vararg args: Array<out Any>) {
        when (code) {
            EventType.UPDATE_MARKS_OK -> load()
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
        ButterKnife.bind(this, view!!)
        NotificationManager.instance.addObserver(this, EventType.UPDATE_MARKS_OK)

        setHasOptionsMenu(true)
        activity.title = getString(R.string.medie)

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
        download()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NotificationManager.instance.removeObserver(this, EventType.UPDATE_MARKS_OK)
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

    override fun onItemClicked(position: Int) {
        when (position) {
            0 -> PreferenceManager.getDefaultSharedPreferences(context).edit().putString("order", "ORDER BY lower(_name) ASC").apply()
            1 -> PreferenceManager.getDefaultSharedPreferences(context).edit().putString("order", "ORDER BY _avg DESC").apply()
            2 -> PreferenceManager.getDefaultSharedPreferences(context).edit().putString("order", "ORDER BY _avg ASC").apply()
        }
        load()
    }

    private fun download() {
        if (isNetworkAvailable(activity)) {
            swiperefresh!!.isRefreshing = true
            val mContext = activity
            APIClient.with(activity)
                    .getGrades()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ marks ->
                        Log.d("MARKS", marks.grades.size.toString())
                        SugarRecord.saveInTx(marks.getGrades(Profile.getProfile(mContext)!!))
                        load()
                        Snackbar.make(activity.tab_layout, getSnackBarMessage(view_pager!!.currentItem), Snackbar.LENGTH_LONG).show()
                    }, { it.printStackTrace() }) { swiperefresh!!.isRefreshing = false }
        }
    }

    private fun getSnackBarMessage(pos: Int): String {
        val p = if (pos == 0) RegistroDB.Period.FIRST else if (pos == 1) RegistroDB.Period.SECOND else RegistroDB.Period.ALL
        //TODO: update references
        val average = 6.2
        var className: String? = "4FSA"
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
                0 -> fragment.addSubjects(Grade.getAverages(activity, "M_PERIOD=1 AND"), i)
                1 -> fragment.addSubjects(Grade.getAverages(activity, "M_PERIOD!=1 AND"), i)
                2 -> fragment.addSubjects(Grade.getAverages(activity, ""), i)
            }
        }

        if (!pagerSelected && Grade.hasMarksSecondPeriod(activity)) {
            view_pager!!.setCurrentItem(1, false)
            pagerSelected = true
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
