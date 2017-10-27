package com.sharpdroid.registroelettronico.Fragments

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.sharpdroid.registroelettronico.Adapters.MedieAdapter
import com.sharpdroid.registroelettronico.Interfaces.Client.Average
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.ItemOffsetDecoration
import com.sharpdroid.registroelettronico.Views.EmptyFragment
import kotlinx.android.synthetic.main.coordinator_swipe_recycler_padding.*
import kotlinx.android.synthetic.main.coordinator_swipe_recycler_padding.view.*

class FragmentMedie : Fragment() {
    private var periodo: Int = 0
    lateinit private var mRVAdapter: MedieAdapter
    lateinit private var emptyHolder: EmptyFragment

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater!!.inflate(R.layout.coordinator_swipe_recycler_padding, container, false)
        emptyHolder = EmptyFragment(context)
        emptyHolder.visibility = View.GONE
        emptyHolder.setTextAndDrawable("Nessun voto", R.drawable.ic_timeline)
        layout.relative.addView(emptyHolder)
        return layout
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view!!)

        periodo = arguments.getInt("q")
        mRVAdapter = MedieAdapter(context)

        with(recycler) {
            setBackgroundColor(Color.parseColor("#F1F1F1"))
            setHasFixedSize(true)
            layoutManager = if (resources.getBoolean(R.bool.isTablet) || resources.configuration.orientation == ORIENTATION_LANDSCAPE) {
                GridLayoutManager(context, 3)
            } else {
                GridLayoutManager(context, 2)
            }
            addItemDecoration(ItemOffsetDecoration(context, R.dimen.cards_margin))

            adapter = mRVAdapter
        }
    }

    fun addSubjects(markSubjects: List<Average>, p: Int) {
        mRVAdapter.clear()
        mRVAdapter.addAll(markSubjects, p)

        emptyHolder.visibility = if (markSubjects.isEmpty()) View.VISIBLE else View.GONE

    }
}
