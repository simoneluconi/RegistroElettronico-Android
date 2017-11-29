package com.sharpdroid.registroelettronico.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sharpdroid.registroelettronico.utils.LayoutHelper
import com.sharpdroid.registroelettronico.utils.LayoutHelper.MATCH_PARENT
import com.sharpdroid.registroelettronico.utils.LayoutHelper.WRAP_CONTENT
import com.sharpdroid.registroelettronico.views.timetable.TimetableLayout

class FragmentSchedule : Fragment() {
    lateinit var timetable: TimetableLayout

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        timetable = TimetableLayout(context)
        val ll = NestedScrollView(context)
        ll.layoutParams = LayoutHelper.createFrame(MATCH_PARENT, WRAP_CONTENT, 0, 0f, 0f, 0f, 0f)
        ll.addView(timetable)
        ll.setBackgroundColor(0xffffffff.toInt())
        return ll
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.title = "Orario"
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        timetable.saveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        timetable.restoreInstanceState(savedInstanceState)
    }

}
