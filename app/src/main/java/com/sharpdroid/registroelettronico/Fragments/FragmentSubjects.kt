package com.sharpdroid.registroelettronico.Fragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.Activities.EditSubjectDetailsActivity
import com.sharpdroid.registroelettronico.Adapters.SubjectsAdapter
import com.sharpdroid.registroelettronico.Databases.Entities.Subject
import com.sharpdroid.registroelettronico.Databases.Entities.SubjectInfo
import com.sharpdroid.registroelettronico.Databases.Entities.SubjectTeacher
import com.sharpdroid.registroelettronico.Databases.Entities.Teacher
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.Account
import com.sharpdroid.registroelettronico.Utils.EventType
import com.sharpdroid.registroelettronico.Utils.Metodi.dp
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_lessons.*

class FragmentSubjects : Fragment(), SubjectsAdapter.SubjectListener, NotificationManager.NotificationReceiver {
    lateinit var adapter: SubjectsAdapter

    var selectedSubject: Subject? = null

    override fun didReceiveNotification(code: Int, args: Array<in Any>) {
        when (code) {
            EventType.UPDATE_SUBJECTS_START -> {

            }
            EventType.UPDATE_SUBJECTS_OK,
            EventType.UPDATE_SUBJECTS_KO -> {
                setAdapterData(fetch())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_lessons, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.instance.addObserver(this, EventType.UPDATE_SUBJECTS_START, EventType.UPDATE_SUBJECTS_OK, EventType.UPDATE_SUBJECTS_KO)

        Log.d("FragmentSubjects", "onViewCreated")

        if (arguments != null && arguments.getInt("lessons", -1) != -1) {
            onSubjectClick(SugarRecord.findById(Subject::class.java, arguments?.getInt("lessons")))
        }

        if (savedInstanceState != null) {
            selectedSubject = savedInstanceState["subject"] as Subject?
            //if (selectedSubject != null) onSubjectClick(selectedSubject!!)
        }

        //updateSubjects(activity) //This will fire didReceiveNotification(...)
        activity.title = getString(R.string.lessons)
        adapter = SubjectsAdapter(this)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.addItemDecoration(HorizontalDividerItemDecoration.Builder(context).colorResId(R.color.divider).size(dp(1)).build())
        recycler.adapter = adapter
        setAdapterData(fetch())
    }

    override fun onResume() {
        super.onResume()
        selectedSubject = null
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (selectedSubject != null) outState?.putSerializable("subject", selectedSubject)
    }

    private fun setAdapterData(data: List<SubjectInfo>) {
        Teacher.clearCache()
        SubjectTeacher.clearCache()
        SubjectTeacher.setupCache(Account.with(context).user)
        Teacher.setupCache(Account.with(context).user)
        adapter.clear()
        adapter.addAll(data)
    }

    private fun fetch(): List<SubjectInfo> {
        return SugarRecord.findWithQuery(Subject::class.java, "select * from SUBJECT where SUBJECT.ID IN (SELECT  SUBJECT_TEACHER.SUBJECT from SUBJECT_TEACHER WHERE SUBJECT_TEACHER.PROFILE=?) ORDER BY DESCRIPTION ASC", Account.with(activity).user.toString()).map { it.getInfo(activity) }
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
        Teacher.clearCache()
        SubjectTeacher.clearCache()
    }
}