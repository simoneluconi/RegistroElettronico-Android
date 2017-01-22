package com.sharpdroid.registroelettronico.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.Adapters.MedieAdapter;
import com.sharpdroid.registroelettronico.Databases.SubjectsDB;
import com.sharpdroid.registroelettronico.Interfaces.API.MarkSubject;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Utils.ItemOffsetDecoration;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.ButterKnife;

// TODO: 19/01/2017 Divisione P1 e P2
// TODO: 19/01/2017 Visualizzare media generale e crediti scolastici in modo decente

public class FragmentMedie extends Fragment {
    final private String TAG = FragmentMedie.class.getSimpleName();

    SubjectsDB subjectsDB;
    int periodo;
    private MedieAdapter mRVAdapter;
    private Context mContext;

    public FragmentMedie() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        View layout = inflater.inflate(R.layout.coordinator_swipe_recycler_padding, container, false);

        ButterKnife.bind(this, layout);

        periodo = getArguments().getInt("q");
        subjectsDB = SubjectsDB.from(mContext);

        RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        mRecyclerView.addItemDecoration(new ItemOffsetDecoration(mContext, R.dimen.cards_margin));

        mRVAdapter = new MedieAdapter(mContext, new CopyOnWriteArrayList<>(), subjectsDB);
        mRecyclerView.setAdapter(mRVAdapter);

        return layout;
    }

    public void addSubjects(List<MarkSubject> markSubjects) {
        if (!markSubjects.isEmpty()) {
            mRVAdapter.clear();
            mRVAdapter.addAll(markSubjects);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subjectsDB.close();
    }
}
