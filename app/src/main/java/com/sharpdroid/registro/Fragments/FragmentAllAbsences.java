package com.sharpdroid.registro.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.sharpdroid.registro.Adapters.AllAbsencesAdapter;
import com.sharpdroid.registro.Interfaces.Absences;
import com.sharpdroid.registro.R;
import com.sharpdroid.registro.Tasks.CacheTask;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentAllAbsences extends Fragment {
    final private String TAG = FragmentAllAbsences.class.getSimpleName();

    @BindView(R.id.expandable_list)
    ExpandableListView expandableListView;
    AllAbsencesAdapter adapter;

    Context mContext;

    public FragmentAllAbsences() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_all_absences, container, false);
        mContext = getContext();
        ButterKnife.bind(this, root);

        adapter = new AllAbsencesAdapter(mContext);


        expandableListView.setAdapter(adapter);

        return root;
    }

    void addAbsences(Absences absences, boolean docache) {
        adapter.clear();
        adapter.setAbsences(absences);

        if (docache) {
            // Update cache
            new CacheTask(mContext.getCacheDir(), TAG).execute((List) absences);
        }

    }

}
