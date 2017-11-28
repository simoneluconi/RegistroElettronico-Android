package com.sharpdroid.registroelettronico.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sharpdroid.registroelettronico.utils.LayoutHelper
import com.sharpdroid.registroelettronico.views.timetable.TimetableLayout

class FragmentSchedule : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val l = TimetableLayout(context)
        l.layoutParams = LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 0, 0f, 0f, 0f, 0f)
        return l
    }

}
