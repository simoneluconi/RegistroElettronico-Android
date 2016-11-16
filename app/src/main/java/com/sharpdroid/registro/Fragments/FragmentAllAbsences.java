package com.sharpdroid.registro.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.sharpdroid.registro.Adapters.AllAbsencesAdapter;
import com.sharpdroid.registro.R;

public class FragmentAllAbsences extends Fragment {
    ExpandableListView expandableListView;
    AllAbsencesAdapter adapter;

    Context mContext;

    public FragmentAllAbsences() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_all_absences, container, false);
        mContext = getContext();

        expandableListView = (ExpandableListView) layout.findViewById(R.id.expandable_list);
        adapter = new AllAbsencesAdapter(mContext);

        //// TODO: 16/11/2016 adapter.setData();

        expandableListView.setAdapter(adapter);

        return layout;
    }

}
