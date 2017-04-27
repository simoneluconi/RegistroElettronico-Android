package com.sharpdroid.registroelettronico.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.Adapters.SubjectsAdapter;
import com.sharpdroid.registroelettronico.Databases.RegistroDB;
import com.sharpdroid.registroelettronico.Interfaces.Client.Subject;
import com.sharpdroid.registroelettronico.R;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registroelettronico.Utils.Metodi.dpToPx;

public class FragmentSubjects extends Fragment implements SubjectsAdapter.SubjectListener {
    @BindView(R.id.recycler)
    RecyclerView recycler;

    SubjectsAdapter adapter;
    RegistroDB db;

    public FragmentSubjects() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lessons, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        db = RegistroDB.getInstance(getContext());

        getActivity().setTitle(getString(R.string.lessons));

        adapter = new SubjectsAdapter(this);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).colorResId(R.color.divider).size(dpToPx(1)).build());
        recycler.setAdapter(adapter);
        adapter.addAll(db.getSubjects());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }

    @Override
    public void onSubjectClick(Subject subject) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.fragment_container, FragmentLessons.newInstance(subject.getCode())).addToBackStack(null);

        transaction.commit();
    }
}