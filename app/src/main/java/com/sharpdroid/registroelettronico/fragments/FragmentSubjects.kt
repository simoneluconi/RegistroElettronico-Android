package com.sharpdroid.registroelettronico.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.FrameLayout
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.activities.EditSubjectDetailsActivity
import com.sharpdroid.registroelettronico.adapters.SubjectsAdapter
import com.sharpdroid.registroelettronico.adapters.holders.Holder
import com.sharpdroid.registroelettronico.database.entities.Lesson
import com.sharpdroid.registroelettronico.database.entities.Profile
import com.sharpdroid.registroelettronico.database.pojos.SubjectWithLessons
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi.updateLessons
import com.sharpdroid.registroelettronico.utils.Metodi.updateSubjects
import com.sharpdroid.registroelettronico.viewModels.LessonsViewModel
import com.sharpdroid.registroelettronico.views.EmptyFragment
import com.sharpdroid.registroelettronico.views.cells.BigHeader
import com.sharpdroid.registroelettronico.views.cells.LessonCellMini
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_lessons.*

class FragmentSubjects : Fragment(), SubjectsAdapter.SubjectListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    lateinit var adapter: SubjectsAdapter
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var emptyHolder: EmptyFragment
    private lateinit var viewModel: LessonsViewModel
    private var queryDisposable: Disposable? = null
    private var querying = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.fragment_lessons, container, false)
        emptyHolder = EmptyFragment(context)
        emptyHolder.visibility = View.GONE
        emptyHolder.setTextAndDrawable("Nessun risultato", R.drawable.ic_search_black_24dp)
        (layout as FrameLayout).addView(emptyHolder)
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        viewModel = ViewModelProviders.of(activity)[LessonsViewModel::class.java]

        activity.title = getString(R.string.lessons)

        searchAdapter = SearchAdapter()
        adapter = SubjectsAdapter(this)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.addItemDecoration(HorizontalDividerItemDecoration.Builder(context).colorResId(R.color.divider).build())
        recycler.adapter = adapter

        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Lezioni").putContentType("Materie"))

        if (savedInstanceState == null)
            download()

        viewModel.getSubjectsWithLessons(Account.with(context).user).observe(this, Observer {
            if (it?.isNotEmpty() == true)
                setAdapterData(it)
        })

        viewModel.query.observe(this, Observer { query ->
            if (query == null) return@Observer
            queryDisposable?.dispose()

            if (query.isEmpty()) {
                emptyHolder.visibility = View.GONE
                recycler.adapter = adapter
            } else {
                recycler.adapter = searchAdapter
                searchAdapter.query = query
                queryDisposable = DatabaseHelper.database.lessonsDao().query("%$query%", Account.with(context).user).observeOn(AndroidSchedulers.mainThread()).subscribe({
                    searchAdapter.setLessons(it)
                    emptyHolder.visibility = if (it.isEmpty()) View.VISIBLE else View.INVISIBLE
                })
            }
        })
    }

    override fun onStop() {
        super.onStop()
        queryDisposable?.dispose()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.getItem(0)
        val searchView = menu.getItem(0).actionView as SearchView

        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setOnQueryTextListener(this)
        searchView.setOnCloseListener(this)

        if (!viewModel.query.value.isNullOrEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(viewModel.query.value, true)
            searchView.clearFocus()
        }
    }

    override fun onQueryTextSubmit(query: String) = false

    override fun onQueryTextChange(newText: String): Boolean {
        querying = newText.isNotEmpty()
        viewModel.query.postValue(newText)
        return false
    }

    //On SearchBar closed
    override fun onClose(): Boolean {
        if (recycler.adapter is SearchAdapter)
            recycler.adapter = adapter
        return false
    }

    private fun download() {
        val p = Profile.getProfile(context)
        updateSubjects(p)
        updateLessons(p)
    }

    private fun setAdapterData(data: List<SubjectWithLessons>) {
        adapter.clear()
        adapter.addAll(data)
    }

    override fun onSubjectClick(subject: SubjectWithLessons) {
        viewModel.selected.postValue(subject)
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)/*setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)*/.replace(R.id.fragment_container,
                FragmentLessons()
        ).addToBackStack(null)
        transaction.commit()
    }

    override fun onSubjectLongClick(subject: SubjectWithLessons) {
        startActivity(Intent(activity, EditSubjectDetailsActivity::class.java).putExtra("code", subject.subject.id))
    }

    inner class SearchAdapter : RecyclerView.Adapter<Holder>() {
        var data = mutableListOf<Any>()
        var query = ""
        fun setLessons(data: List<Lesson>) {
            this.data.clear()
            val grouped = data.groupBy { it.mSubjectDescription }

            grouped.keys.sortedBy { it }.forEach {
                this.data.add(it)
                this.data.addAll(grouped[it]?.map {
                    it.mArgument = it.mArgument.replace(Regex(query, RegexOption.IGNORE_CASE), "<b>$0</b>")
                    it
                }?.sortedByDescending { it.mDate } ?: emptyList())
            }

            notifyDataSetChanged()
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            when (holder.itemView) {
                is BigHeader -> holder.itemView.setText(data[position] as String)
                is LessonCellMini -> holder.itemView.bindData(data[position] as Lesson)
            }
        }

        override fun getItemViewType(position: Int) = if (data[position] is String) 0 else 1

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = if (viewType == 0) Holder(BigHeader(context)) else Holder(LessonCellMini(context))

        override fun getItemCount() = data.size

    }
}