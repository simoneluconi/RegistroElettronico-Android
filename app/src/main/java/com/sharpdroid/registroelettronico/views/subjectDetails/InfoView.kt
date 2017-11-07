package com.sharpdroid.registroelettronico.views.subjectDetails

import android.content.Context
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.pojos.SubjectPOJO
import com.sharpdroid.registroelettronico.utils.Metodi
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import com.sharpdroid.registroelettronico.utils.Metodi.dp
import com.sharpdroid.registroelettronico.utils.or
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.adapter_view_info.view.*
import kotlinx.android.synthetic.main.view_info.view.*

class InfoView : CardView {
    internal var mContext: Context

    lateinit private var adapter: InfoAdapter

    constructor(context: Context) : super(context) {
        mContext = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        init()
    }

    internal fun init() {
        View.inflate(mContext, R.layout.view_info, this)

        adapter = InfoAdapter(mContext)

        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(mContext)
        recycler.addItemDecoration(HorizontalDividerItemDecoration.Builder(mContext).colorResId(R.color.divider).size(Metodi.dp(1)).marginResId(R.dimen.padding_left_divider1, R.dimen.nav_header_vertical_spacing).build())
    }

    fun setSubjectDetails(data: SubjectPOJO) {
        adapter.setData(data)
    }

    fun setEditListener(listener: (Any) -> Unit) {
        edit.setOnClickListener(listener)
    }

    internal inner class InfoAdapter(private val mContext: Context) : RecyclerView.Adapter<InfoAdapter.InfoHolder>() {

        private var data: MutableList<Pair<Int, String>> = ArrayList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoHolder {
            return InfoHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_view_info, parent, false))
        }

        override fun onBindViewHolder(holder: InfoHolder, position: Int) {
            holder.bindData(data[position])
        }

        override fun getItemCount(): Int {
            return data.size
        }

        fun clear() {
            data.clear()
            notifyDataSetChanged()
        }

        fun setData(data: SubjectPOJO) {
            this.data = convertToList(data)
            notifyDataSetChanged()
        }

        private fun convertToList(data: SubjectPOJO): MutableList<Pair<Int, String>> {
            val list = ArrayList<Pair<Int, String>>()

            list.add(Pair(R.drawable.ic_title, capitalizeEach(data.subjectInfo.getOrNull(0)?.description.or(data.subject.description))))

            if (data.subject.teachers.isNotEmpty()) {
                val prof = data.subject.teachers.map { it.teacherName }
                list.add(Pair(R.drawable.ic_person, capitalizeEach(TextUtils.join(" - ", prof), true)))
            }
            if (!(data.subjectInfo.getOrNull(0)?.classroom).isNullOrEmpty())
                list.add(Pair(R.drawable.ic_room, data.subjectInfo.getOrNull(0)?.classroom.orEmpty()))
            if (!(data.subjectInfo.getOrNull(0)?.details).isNullOrEmpty())
                list.add(Pair(R.drawable.ic_description, data.subjectInfo.getOrNull(0)?.details.orEmpty()))

            return list
        }

        internal inner class InfoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bindData(pair: Pair<Int, String>) {
                with(itemView) {
                    //image.setImageResource(pair.first)
                    val drawable = ContextCompat.getDrawable(context, pair.first)
                    drawable.setColorFilter(ContextCompat.getColor(context, R.color.textcolorgrey), PorterDuff.Mode.SRC_ATOP)
                    content.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    content.compoundDrawablePadding = dp(16)
                    content.text = pair.second
                }
            }
        }
    }
}
