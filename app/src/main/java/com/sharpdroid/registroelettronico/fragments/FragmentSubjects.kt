package com.sharpdroid.registroelettronico.fragments


import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.activities.EditSubjectDetailsActivity
import com.sharpdroid.registroelettronico.adapters.holders.Holder
import com.sharpdroid.registroelettronico.adapters.SubjectsAdapter
import com.sharpdroid.registroelettronico.database.entities.*
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.EventType
import com.sharpdroid.registroelettronico.utils.Metodi.updateLessons
import com.sharpdroid.registroelettronico.utils.Metodi.updateSubjects
import com.sharpdroid.registroelettronico.views.cells.BigHeader
import com.sharpdroid.registroelettronico.views.cells.LessonCellMini
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_lessons.*

class FragmentSubjects : Fragment(), SubjectsAdapter.SubjectListener, NotificationManager.NotificationReceiver, SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    lateinit var adapter: SubjectsAdapter
    private lateinit var searchAdapter: SearchAdapter

    private var selectedSubject: Subject? = null

    override fun didReceiveNotification(code: Int, args: Array<in Any>) {
        when (code) {
            EventType.UPDATE_SUBJECTS_OK -> {
                //Lesson.clearCache()
                //Lesson.setupCache(Account.with(context).user)

                Teacher.clearCache()
                SubjectTeacher.clearCache()
                SubjectTeacher.setupCache(Account.with(context).user)
                Teacher.setupCache()

                load()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lessons, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.instance.addObserver(this, EventType.UPDATE_SUBJECTS_START, EventType.UPDATE_SUBJECTS_OK, EventType.UPDATE_SUBJECTS_KO)

        setHasOptionsMenu(true)

        Log.d("FragmentSubjects", "onViewCreated")

        val bundle = arguments
        if (bundle != null && bundle.getInt("lessons", -1) != -1) {
            onSubjectClick(DatabaseHelper.database.subjectsDao().getSubject(arguments?.getInt("lessons")?.toLong() ?: -1))
        }

        if (savedInstanceState != null) {
            selectedSubject = savedInstanceState["subject"] as Subject?
            //if (selectedSubject != null) onSubjectClick(selectedSubject!!)
        } else {
            Lesson.clearCache()
            Lesson.setupCache(Account.with(context).user)

            Teacher.clearCache()
            SubjectTeacher.clearCache()
            SubjectTeacher.setupCache(Account.with(context).user)
            Teacher.setupCache()
        }

        //updateSubjects(activity) //This will fire didReceiveNotification(...)
        activity.title = getString(R.string.lessons)

        searchAdapter = SearchAdapter()
        adapter = SubjectsAdapter(this)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.addItemDecoration(HorizontalDividerItemDecoration.Builder(context).colorResId(R.color.divider).build())
        recycler.adapter = adapter
        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Lezioni").putContentType("Materie"))

        load()
        download()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        val searchView = menu.getItem(0).actionView as SearchView

        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setOnQueryTextListener(this)
        searchView.setOnCloseListener(this)

    }

    override fun onQueryTextSubmit(query_: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        if (newText.isEmpty()) {
            recycler.adapter = adapter
        } else if (newText.length >= 3) {
            if (recycler.adapter !is SearchAdapter)
                recycler.adapter = searchAdapter
            println("filter")
            searchAdapter.setLessons(Lesson.allLessons.map {
                it.mArgument = it.mArgument.replace("<b>", "")
                it.mArgument = it.mArgument.replace("</b>", "")
                it
            }.filter {
                it.mArgument.contains(newText, true) || it.mSubjectDescription.contains(newText, true)
            }, newText)
        }
        return false
    }

    override fun onClose(): Boolean {
        if (recycler.adapter is SearchAdapter)
            recycler.adapter = adapter
        return false
    }

    private fun load() {
        DatabaseHelper.database.subjectsDao().getSubjects(Account.with(context).user).observe(this, Observer {

            setAdapterData(it?.map { it.getInfo(context) } ?: emptyList())
        })
    }

    private fun download() {
        val p = Profile.getProfile(context)
        updateSubjects(p)
        updateLessons(p)
    }

    override fun onResume() {
        super.onResume()
        selectedSubject = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (selectedSubject != null) outState.putSerializable("subject", selectedSubject)
    }

    private fun setAdapterData(data: List<SubjectInfo>) {
        println("setAdapterData")
        adapter.clear()
        adapter.addAll(data)
    }

    override fun onSubjectClick(subject: Subject) {
        selectedSubject = subject
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)/*setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)*/.replace(R.id.fragment_container,
                FragmentLessons.newInstance(subject.id.toInt())
        ).addToBackStack(null)
        transaction.commit()
    }

    override fun onSubjectLongClick(subject: Subject) {
        startActivity(Intent(activity, EditSubjectDetailsActivity::class.java).putExtra("code", subject.id))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NotificationManager.instance.removeObserver(this, EventType.UPDATE_SUBJECTS_START, EventType.UPDATE_SUBJECTS_OK, EventType.UPDATE_SUBJECTS_KO)
        //Teacher.clearCache()
        //SubjectTeacher.clearCache()
    }

    inner class SearchAdapter : RecyclerView.Adapter<Holder>() {
        var data = mutableListOf<Any>()

        fun setLessons(data: List<Lesson>, query: String) {
            this.data.clear()
            val grouped = data.groupBy { it.mSubjectDescription }

            grouped.keys.forEach {
                this.data.add(it)
                this.data.addAll(grouped[it]?.map {
                    it.mArgument = it.mArgument.replace(Regex(query, RegexOption.IGNORE_CASE), "<b>$0</b>")
                    println(it.mArgument)
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