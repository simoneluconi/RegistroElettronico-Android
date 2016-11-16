package com.sharpdroid.registro.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.sharpdroid.registro.Interfaces.Absences;
import com.sharpdroid.registro.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AllAbsencesAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private Absences data;
    private LayoutInflater mInflater;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());

    public AllAbsencesAdapter(Context mContext) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
    }

    public Absences getData() {
        return data;
    }

    public void setData(Absences data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void clear() {
        data = null;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return 3;
    }

    @Override
    public int getChildrenCount(int i) {
        switch (i) {
            case 0:
                return data.getAbsences().size();
            case 1:
                return data.getDelays().size();
            case 2:
                return data.getExits().size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int i) {
        switch (i) {
            case 0:
                return data.getAbsences();
            case 1:
                return data.getDelays();
            case 2:
                return data.getExits();
            default:
                return null;
        }
    }

    @Override
    public Object getChild(int i, int i1) {
        return ((List) getGroup(i)).get(i1);
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
