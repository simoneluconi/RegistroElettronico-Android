package com.sharpdroid.registroelettronico.views.subjectDetails

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.holders.Holder
import com.sharpdroid.registroelettronico.database.entities.LocalGrade
import com.sharpdroid.registroelettronico.database.pojos.AverageType
import com.sharpdroid.registroelettronico.utils.Metodi
import com.sharpdroid.registroelettronico.utils.Metodi.getMarkColor
import com.transitionseverywhere.AutoTransition
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
            add_hypo.setOnClickListener { field?.hypotheticalAddListener() }
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

    fun setRealData(avg: AverageType) {
        realGradeSum = avg.sum
        realCount = avg.count
        invalidate()
    }

    fun setTarget(target: Float) {
        this.target = target
        recycler.adapter.notifyDataSetChanged()
    }

    fun setHypoGrades(grades: List<LocalGrade>, animate: Boolean) {
        hypoGradeSum = grades.foldRight(0f, { localGrade, acc -> acc + localGrade.value })
        hypoCount = grades.size

        grades.forEachIndexed { index, localGrade ->
            localGrade.index = index
        }
        if (hypoCount > 0) {
            updateHypoAvg()
            updatePercentage()
        }
        if (animate)
            TransitionManager.beginDelayedTransition(rootView as ViewGroup, AutoTransition()
                    .excludeChildren(R.id.info, true)
                    .excludeChildren(R.id.overall, true)
                    .excludeChildren(R.id.target, true)
                    .excludeChildren(R.id.marks, true)
                    .excludeChildren(R.id.lessons, true)
                    .setInterpolator(DecelerateInterpolator(1.8f))
            )

        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = this@HypotheticalView.grades[oldItemPosition].id == grades[newItemPosition].id
            override fun getOldListSize() = this@HypotheticalView.grades.size
            override fun getNewListSize() = grades.size
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = this@HypotheticalView.grades[oldItemPosition].value == grades[newItemPosition].value && this@HypotheticalView.grades[oldItemPosition].index == grades[newItemPosition].index
        }).dispatchUpdatesTo(recycler.adapter)
        recycler.invalidateItemDecorations()

        this.grades.clear()
        this.grades.addAll(grades)

        empty.visibility = if (grades.isEmpty()) View.VISIBLE else View.GONE
        media_layout.visibility = if (grades.isNotEmpty()) View.VISIBLE else View.GONE
        recycler.visibility = if (grades.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun updateHypoAvg() {
        avg.text = String.format("%.2f", (realGradeSum + hypoGradeSum) / (realCount + hypoCount))
    }

    private fun updatePercentage() {
        if (realCount == 0) {
            percentage.visibility = View.INVISIBLE
            return
        }
        percentage.visibility = View.VISIBLE

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
                color.circleBackgroundColor = ContextCompat.getColor(context, getMarkColor(grade.value, target))
                setOnClickListener { v ->
                    v.postDelayed({ delegate?.hypotheticalClickListener(grade, holder.adapterPosition) }, ViewConfiguration.getTapTimeout().toLong())
                }
            }
        }

        override fun getItemCount() = grades.size
    }
}