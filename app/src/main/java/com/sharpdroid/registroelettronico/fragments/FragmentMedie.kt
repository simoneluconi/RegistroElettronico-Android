package com.sharpdroid.registroelettronico.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.MedieAdapter
import com.sharpdroid.registroelettronico.database.entities.Average
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.database.viewModels.GradesViewModel
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.ItemOffsetDecoration
import com.sharpdroid.registroelettronico.utils.Metodi.CalculateScholasticCredits
import com.sharpdroid.registroelettronico.views.EmptyFragment
import kotlinx.android.synthetic.main.coordinator_swipe_recycler_padding.*
import kotlinx.android.synthetic.main.fragment_medie_pager.*
import java.util.*

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
            setHasFixedSize(true)
            layoutManager = if (resources.getBoolean(R.bool.isTablet) || resources.configuration.orientation == ORIENTATION_LANDSCAPE) {
                GridLayoutManager(context, 3)
            } else {
                GridLayoutManager(context, 2)
            }
            addItemDecoration(ItemOffsetDecoration(context, R.dimen.cards_margin))

            adapter = mRVAdapter
        }

        val snackbar = Snackbar.make(activity.coordinator_layout, "", Snackbar.LENGTH_SHORT)

        viewModel.getGrades(Account.with(context).user, periodo).observe(this, Observer {
            addSubjects(it.orEmpty(), periodo, viewModel.order.value.orEmpty())
            var acc = 0f
            var count = 0

            it.orEmpty().forEach {
                if (it.count > 0) {
                    acc += it.sum / it.count
                    count++
                }
            }

            val classe: String = DatabaseHelper.database.lessonsDao().getClassDescription(Account.with(context).user)

            if (acc > 0 && viewModel.selected == periodo) {
                snackbar.setText(getSnackBarMessage(acc / count, classe))
                snackbar.show()
            }
        })
        viewModel.order.observe(this, Observer {
            addSubjects(mRVAdapter.getAll(), periodo, it.orEmpty())
        })
    }

    private fun getSnackBarMessage(average: Float, classDescription: String): String {
        val maybeClass: String = classDescription.substring(0, 1)
        val classe: Int? = maybeClass.toIntOrNull()

        return classe?.let {
            String.format(Locale.getDefault(), "Media Totale: %.2f | Crediti: %2\$d + %3\$d", average, CalculateScholasticCredits(classe, average), 1)
        } ?: "Media totale: " + String.format(Locale.getDefault(), "%.2f", average)
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
