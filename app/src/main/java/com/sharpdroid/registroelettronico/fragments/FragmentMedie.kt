package com.sharpdroid.registroelettronico.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.MedieAdapter
import com.sharpdroid.registroelettronico.database.entities.Average
import com.sharpdroid.registroelettronico.database.viewModels.GradesViewModel
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.ItemOffsetDecoration
import com.sharpdroid.registroelettronico.views.EmptyFragment
import kotlinx.android.synthetic.main.coordinator_swipe_recycler_padding.*

class FragmentMedie : Fragment() {
    private var periodo: Int = 0
    private val mRVAdapter by lazy {
        MedieAdapter(context)
    }
    private var emptyHolder: EmptyFragment? = null
    private lateinit var viewModel: GradesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.coordinator_swipe_recycler_padding, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emptyHolder = EmptyFragment(context)
        emptyHolder?.visibility = View.GONE
        emptyHolder?.setTextAndDrawable("Nessun voto", R.drawable.ic_timeline)
        relative.addView(emptyHolder)

        periodo = arguments.getInt("q")

        viewModel = ViewModelProviders.of(activity)[GradesViewModel::class.java]

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

        viewModel.getGrades(Account.with(context).user, periodo).observe(this, Observer {
            addSubjects(it.orEmpty(), periodo, viewModel.order.value.orEmpty())
        })
        viewModel.order.observe(this, Observer {
            addSubjects(mRVAdapter.getAll(), periodo, it.orEmpty())
        })
    }

    fun addSubjects(markSubjects: List<Average>, p: Int, order: String) {
        val ordered = when (order) {
            "avg" -> markSubjects.sortedByDescending { it.avg() }
            "count" -> markSubjects.sortedByDescending { it.count }
            else -> markSubjects.sortedBy { it.name }
        }
        mRVAdapter.addAll(ordered, p)
        emptyHolder?.visibility = if (markSubjects.isEmpty()) View.VISIBLE else View.GONE
    }
}
