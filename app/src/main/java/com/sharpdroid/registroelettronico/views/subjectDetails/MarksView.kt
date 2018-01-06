package com.sharpdroid.registroelettronico.views.subjectDetails

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.util.AttributeSet
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.Utils
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.MarkAdapter
import com.sharpdroid.registroelettronico.database.entities.Grade
import com.sharpdroid.registroelettronico.database.entities.SubjectInfo
import com.sharpdroid.registroelettronico.utils.Metodi
import com.transitionseverywhere.AutoTransition
import com.transitionseverywhere.TransitionManager
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.view_marks.view.*
import java.text.SimpleDateFormat
import java.util.*

class MarksView : CardView, PopupMenu.OnMenuItemClickListener {
    internal lateinit var mContext: Context
    internal var format = SimpleDateFormat("d MMM", Locale.ITALIAN)

    lateinit internal var menu: PopupMenu
    lateinit internal var adapter: MarkAdapter
    private var showChart: Boolean = false

    var markClickListener: ((Grade) -> Unit)? = null
        set(value) {
            field = value
            adapter.listener = value
            adapter.notifyDataSetChanged()
        }

    val itemCount: Int
        get() = adapter.itemCount

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    internal fun init(context: Context) {
        View.inflate(context, R.layout.view_marks, this)

        mContext = context

        adapter = MarkAdapter(mContext)

        recycler.adapter = adapter
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(mContext)
        recycler.addItemDecoration(HorizontalDividerItemDecoration.Builder(mContext).colorResId(R.color.divider).marginResId(R.dimen.padding_left_divider2, R.dimen.activity_vertical_margin).size(Metodi.dp(1)).build())
        recycler.isNestedScrollingEnabled = false

        menu = PopupMenu(mContext, options)
        menu.menuInflater.inflate(R.menu.view_marks_menu, menu.menu)
        options.setOnClickListener { menu.show() }
        menu.setOnMenuItemClickListener(this)

        val xAxis = chart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setValueFormatter { value, _ -> format.format(Date(value.toLong())) }
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = (24 * 3600 * 1000).toFloat()

        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false

        val leftAxis = chart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.gridColor = Color.parseColor("#22000000")
        leftAxis.axisMinimum = 1f
        leftAxis.axisMaximum = 10f

        //not zoomable nor draggable
        chart.isDragEnabled = false
        chart.setScaleEnabled(false)
        chart.setPinchZoom(false)

        //do not show description nor legend
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
    }

    fun setupAvgAndTarget(subject: SubjectInfo?, media: Float) {
        val pref = PreferenceManager.getDefaultSharedPreferences(mContext).getString("voto_obiettivo", "8")
        val prefTarget = if (pref == "Auto") Math.ceil(media.toDouble()).toFloat() else pref.toFloat()

        val target = if (subject == null || subject.target == 0f) prefTarget else subject.target

        setLimitLines(target, media)
        Log.d("MarksView", "target=$target")
        adapter.setTarget(target)

    }

    fun addAll(marks: List<Grade>) {
        adapter.clear()
        adapter.addAll(marks)
    }

    private fun setLimitLines(target: Float, media: Float) {
        var t = target
        if (t == 0f) {
            val pref = PreferenceManager.getDefaultSharedPreferences(mContext).getString("voto_obiettivo", "8")
            t = if (pref == "Auto") {
                Math.ceil(media.toDouble()).toFloat()
            } else {
                java.lang.Float.parseFloat(pref)
            }
        }
        val ll2 = LimitLine(t, "Il tuo obiettivo")
        ll2.lineWidth = 1f
        ll2.lineColor = ContextCompat.getColor(mContext, R.color.md_pink_400)
        ll2.enableDashedLine(15f, 0f, 0f)
        ll2.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
        ll2.textSize = 10f
        ll2.textColor = ContextCompat.getColor(mContext, R.color.md_pink_400)


        val ll1 = LimitLine(media, "La tua media")
        ll1.lineWidth = 1f
        ll1.lineColor = Color.parseColor("#22000000")
        ll1.enableDashedLine(15f, 0f, 0f)
        ll1.labelPosition = LimitLine.LimitLabelPosition.LEFT_BOTTOM
        ll1.textSize = 10f
        ll1.textColor = Color.parseColor("#444444")

        chart.axisLeft.limitLines.clear()
        chart.axisLeft.addLimitLine(ll1)
        chart.axisLeft.addLimitLine(ll2)
        chart.invalidate()
    }

    fun setShowChart(show: Boolean) {
        showChart = show
        menu.menu.findItem(R.id.show).isChecked = show
        chart.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun setChart(marks: List<Entry>) {
        val line = LineDataSet(marks, "")
        line.mode = LineDataSet.Mode.LINEAR
        line.color = ContextCompat.getColor(mContext, R.color.colorPrimary)
        line.setDrawFilled(true)
        line.setDrawCircles(true)
        line.setDrawCircleHole(false)
        line.circleRadius = 2f
        line.setDrawValues(false)
        line.setDrawHighlightIndicators(false)
        line.setCircleColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
        line.axisDependency = YAxis.AxisDependency.LEFT
        //drawable gradient
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            val drawable = ContextCompat.getDrawable(mContext, R.drawable.chart_fill)
            line.fillDrawable = drawable
        } else {
            line.fillColor = ContextCompat.getColor(mContext, R.color.colorPrimary)
        }
        chart.data = LineData(line)
        chart.invalidate()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (item.itemId == R.id.show && showChart) {
            item.isChecked = !item.isChecked
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean("show_chart", item.isChecked).apply()
            TransitionManager.beginDelayedTransition(rootView as ViewGroup, AutoTransition().setInterpolator(DecelerateInterpolator(1.2f)).setDuration(300))
            chart.visibility = if (item.isChecked) View.VISIBLE else View.GONE
        }
        return true
    }
}
