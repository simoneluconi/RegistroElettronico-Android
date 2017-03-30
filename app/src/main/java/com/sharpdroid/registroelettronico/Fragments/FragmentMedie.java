package com.sharpdroid.registroelettronico.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.Adapters.MedieAdapter;
import com.sharpdroid.registroelettronico.Databases.RegistroDB;
import com.sharpdroid.registroelettronico.Interfaces.API.Mark;
import com.sharpdroid.registroelettronico.Interfaces.API.MarkSubject;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Utils.ItemOffsetDecoration;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.ButterKnife;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static com.sharpdroid.registroelettronico.Utils.Metodi.getMarksOfThisPeriod;

// TODO: 19/01/2017 Visualizzare media generale e crediti scolastici in modo decente

public class FragmentMedie extends Fragment {
    final private String TAG = FragmentMedie.class.getSimpleName();

    Context mContext;
    RegistroDB subjectsDB;
    int periodo;
    private MedieAdapter mRVAdapter;

    public FragmentMedie() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        return inflater.inflate(R.layout.coordinator_swipe_recycler_padding, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        periodo = getArguments().getInt("q");
        subjectsDB = new RegistroDB(mContext);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        mRecyclerView.setBackgroundColor(Color.parseColor("#F1F1F1"));
        mRecyclerView.setHasFixedSize(true);
        if (getResources().getBoolean(R.bool.md_is_tablet) || getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        }
        mRecyclerView.addItemDecoration(new ItemOffsetDecoration(mContext, R.dimen.cards_margin));

        mRVAdapter = new MedieAdapter(mContext, new CopyOnWriteArrayList<>(), subjectsDB);
        mRecyclerView.setAdapter(mRVAdapter);
    }

    public void addSubjects(List<MarkSubject> markSubjects) {
        if (!markSubjects.isEmpty()) {
            mRVAdapter.clear();

            if (periodo == 0)
                mRVAdapter.addAll(getMarksOfThisPeriod(markSubjects, Mark.PRIMO_PERIODO));
            else if (periodo == 1)
                mRVAdapter.addAll(getMarksOfThisPeriod(markSubjects, Mark.SECONDO_PERIODO));
            else
                mRVAdapter.addAll(markSubjects);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subjectsDB.close();
    }
}
