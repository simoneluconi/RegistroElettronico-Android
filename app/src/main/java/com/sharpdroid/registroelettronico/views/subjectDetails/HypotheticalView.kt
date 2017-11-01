package com.sharpdroid.registroelettronico.views.subjectDetails

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.activities.MarkSubjectDetailActivity
import com.sharpdroid.registroelettronico.adapters.Holders.Holder
import com.sharpdroid.registroelettronico.database.entities.LocalGrade
import com.sharpdroid.registroelettronico.utils.Metodi
import com.sharpdroid.registroelettronico.utils.Metodi.getMarkColor
import com.transitionseverywhere.TransitionManager
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.adapter_mark.view.*
import kotlinx.android.synthetic.main.view_hypothetical.view.*

class HypotheticalView : CardView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle)

    interface HypotheticalDelegate {
        fun hypotheticalAddListener()
        fun hypotheticalClickListener(grade: LocalGrade, position: Int)
    }

    private var target = 0f

    private var realGradeSum = 0f
    private var realCount = 0

    private var hypoGradeSum = 0f
    private var hypoCount = 0

    private var grades: MutableList<LocalGrade> = mutableListOf()

    var delegate: HypotheticalDelegate? = null
        set(value) {
            field = value
            add_hypo.setOnClickListener { _ -> field?.hypotheticalAddListener() }
        }

    init {
        View.inflate(context, R.layout.view_hypothetical, this)

        with(recycler) {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = MarksAdapter()
            addItemDecoration(HorizontalDividerItemDecoration.Builder(context).margin(Metodi.dp(64), Metodi.dp(16)).colorResId(R.color.divider).build())
        }
        empty.setTextAndDrawable("Nessun voto ipotetico", R.drawable.ic_timeline)
    }

    fun setRealData(avg: MarkSubjectDetailActivity.AverageType) {
        realGradeSum = avg.avg * avg.count
        realCount = avg.count
        invalidate()
    }

    fun setTarget(target: Float) {
        this.target = target
        recycler.adapter.notifyDataSetChanged()
    }

    fun setHypoGrades(grades: List<LocalGrade>) {
        TransitionManager.beginDelayedTransition(rootView as ViewGroup)
        hypoGradeSum = grades.foldRight(0f, { localGrade, acc -> acc + localGrade.value })
        hypoCount = grades.size

        grades.forEachIndexed { index, localGrade ->
            localGrade.index = index
        }

        this.grades.clear()
        this.grades.addAll(grades)

        recycler.adapter.notifyDataSetChanged()

        updateHypoAvg()
        updatePercentage()

        empty.visibility = if (grades.isEmpty()) View.VISIBLE else View.GONE
        recycler.visibility = if (grades.isNotEmpty()) View.VISIBLE else View.GONE
    }

    fun add(grade: LocalGrade) {
        if (grades.size != 0)
            grade.index = grades.last().index + 1

        grades.add(grades.lastIndex + 1, grade)
        println(grades.toString())
        val index = grades.indexOf(grade)
        recycler.adapter.notifyItemInserted(index)

        TransitionManager.beginDelayedTransition(rootView as ViewGroup)
        hypoGradeSum = grades.foldRight(0f, { localGrade, acc -> acc + localGrade.value })
        hypoCount = grades.size

        updateHypoAvg()
        updatePercentage()

        empty.visibility = if (grades.isEmpty()) View.VISIBLE else View.GONE
        recycler.visibility = if (grades.isNotEmpty()) View.VISIBLE else View.GONE

    }

    fun remove(grade: LocalGrade) {
        val index = grades.indexOf(grade)
        grades.removeAt(index)
        println(grades.toString())
        recycler.adapter.notifyItemRemoved(index)

        TransitionManager.beginDelayedTransition(rootView as ViewGroup)
        hypoGradeSum = grades.foldRight(0f, { localGrade, acc -> acc + localGrade.value })
        hypoCount = grades.size

        updateHypoAvg()
        updatePercentage()

        empty.visibility = if (grades.isEmpty()) View.VISIBLE else View.GONE
        recycler.visibility = if (grades.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun updateHypoAvg() {
        if (hypoCount == 0 && realCount == 0) return

        avg.text = String.format("%.2f", (realGradeSum + hypoGradeSum) / (realCount + hypoCount))
    }

    private fun updatePercentage() {
        if (realCount == 0 || hypoCount == 0) {
            media_layout.visibility = View.GONE
            return
        }

        media_layout.visibility = View.VISIBLE
        val realAvg = realGradeSum / realCount
        val newAvg = (realGradeSum + hypoGradeSum) / (realCount + hypoCount)
        val toDisplay = ((newAvg * 100) / realAvg) - 100
        percentage.text = (if (toDisplay >= 0) "+" else "") + String.format("%.2f", toDisplay) + "%"
    }

    private inner class MarksAdapter : RecyclerView.Adapter<Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
            val r = Holder(LayoutInflater.from(context).inflate(R.layout.adapter_mark, parent, false))
            r.itemView.isClickable = true
            r.itemView.isFocusable = true

            val attrs = intArrayOf(android.R.attr.selectableItemBackground)
            val ta = context.obtainStyledAttributes(attrs)
            val drawableFromTheme = ta.getDrawable(0)
            val wrapper = LayerDrawable(arrayOf<Drawable>(ColorDrawable(Color.WHITE), drawableFromTheme))
            ta.recycle()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                r.itemView.background = wrapper
            } else {
                @Suppress("DEPRECATION")
                r.itemView.setBackgroundDrawable(wrapper)
            }
            return r
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: Holder, position: Int) {
            val grade = grades[holder.adapterPosition]
            with(holder.itemView!!) {
                mark.text = grade.value_name
                date.setText(R.string.generale)
                content.text = "${grade.index + 1}° voto ipotetico"
                color.setImageDrawable(ColorDrawable(ContextCompat.getColor(context, getMarkColor(grade.value, target))))
                setOnClickListener { v ->
                    v.postDelayed({ delegate?.hypotheticalClickListener(grade, holder.adapterPosition) }, ViewConfiguration.getTapTimeout().toLong())
                }
            }
        }

        override fun getItemCount() = grades.size
    }
}