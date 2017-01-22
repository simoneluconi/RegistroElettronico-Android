package com.sharpdroid.registroelettronico.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.Adapters.MedieAdapter;
import com.sharpdroid.registroelettronico.Databases.SubjectsDB;
import com.sharpdroid.registroelettronico.Interfaces.API.Mark;
import com.sharpdroid.registroelettronico.Interfaces.API.MarkSubject;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Tasks.CacheListObservable;
import com.sharpdroid.registroelettronico.Utils.ItemOffsetDecoration;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sharpdroid.registroelettronico.Utils.Metodi.getMarksOfThisPeriod;

// TODO: 19/01/2017 Divisione P1 e P2
// TODO: 19/01/2017 Visualizzare media generale e crediti scolastici in modo decente

public class FragmentMedie extends Fragment {
    final private String TAG = FragmentMedie.class.getSimpleName();

    SubjectsDB subjectsDB;
    int periodo;
    boolean cached;
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

        cached = false;
        periodo = getArguments().getInt("q");
        subjectsDB = SubjectsDB.from(mContext);

        RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        mRecyclerView.addItemDecoration(new ItemOffsetDecoration(mContext, R.dimen.cards_margin));

        mRVAdapter = new MedieAdapter(mContext, new CopyOnWriteArrayList<>(), subjectsDB);
        mRecyclerView.setAdapter(mRVAdapter);

        bindMarksSubjectsCache();

        return layout;
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
    public void onResume() {
        super.onResume();
        bindMarksSubjectsCache();
        Log.d(TAG, "RESUME " + periodo);
    }


    private void bindMarksSubjectsCache() {
        if (!cached) {
            new CacheListObservable(new File(mContext.getCacheDir(), TAG))
                    .getCachedList(MarkSubject.class)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(marksSubjects -> {
                        addSubjects(marksSubjects);
                        Log.d(TAG, "Restored cache");
                    });
            cached = true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subjectsDB.close();
    }
}
