package com.sharpdroid.registro.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.sharpdroid.registro.Interfaces.Absences;
import com.sharpdroid.registro.R;

import java.util.List;

public class AllAbsencesAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private Absences absences;
    private LayoutInflater mInflater;

    public AllAbsencesAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setAbsences(Absences absences) {
        this.absences = absences;
        notifyDataSetChanged();
    }

    public void clear() {
        this.absences = null;
    }

    @Override
    public int getGroupCount() {
        return 3;
    }

    @Override
    public int getChildrenCount(int i) {
        switch (i) {
            case 0:
                return absences.getAbsences().size();
            case 1:
                return absences.getDelays().size();
            case 2:
                return absences.getExits().size();
        }
        return 0;
    }

    @Override
    public List getGroup(int i) {
        switch (i) {
            case 0:
                return absences.getAbsences();
            case 1:
                return absences.getDelays();
            case 2:
                return absences.getExits();
            default:
                return null;
        }
    }

    @Override
    public List getChild(int i, int i1) {
        return (List) getGroup(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int p_parent, int p_child) {
        return p_child;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int group_pos, boolean expanded, View view, ViewGroup viewGroup) {
        if (view == null)
            view = mInflater.inflate(R.layout.adapter_expandable_group, viewGroup, false);

        List groupData = getGroup(group_pos);

        return view;
    }

    @Override
    public View getChildView(int group_pos, int child_pos, boolean last, View view, ViewGroup viewGroup) {
        if (view == null)
            view = mInflater.inflate(R.layout.adapter_expandable_child, viewGroup, false);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

}
