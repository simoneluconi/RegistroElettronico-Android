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
    private Absences data;
    private LayoutInflater mInflater;

    public AllAbsencesAdapter(Context mContext) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
    }

    public Absences getData() {
        return data;
    }

    public void setData(Absences data) {
        //this.data.clear();
        this.data.setData(data);
        notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return 3;
    }

    @Override
    public int getChildrenCount(int i) {
        return data.getSize(i);
    }

    @Override
    public Object getGroup(int i) {
        return data.getGroup(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return data.getGroup(i).get(i1);
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

        // TODO: 16/11/2016 views
        List groupData = (List) getGroup(group_pos);

        return view;
    }

    @Override
    public View getChildView(int group_pos, int child_pos, boolean last, View view, ViewGroup viewGroup) {
        if (view == null)
            view = mInflater.inflate(R.layout.adapter_expandable_child, viewGroup, false);

        // TODO: 16/11/2016 views

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

}
