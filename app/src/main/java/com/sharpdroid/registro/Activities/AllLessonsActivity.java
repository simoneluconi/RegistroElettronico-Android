package com.sharpdroid.registro.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sharpdroid.registro.Adapters.AllLessonsAdapter;
import com.sharpdroid.registro.Interfaces.Lesson;
import com.sharpdroid.registro.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllLessonsActivity extends AppCompatActivity {

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    AllLessonsAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_lessons);
        ButterKnife.bind(this);

        ArrayList<Lesson> lessons = (ArrayList<Lesson>) getIntent().getSerializableExtra("data");

        adapter = new AllLessonsAdapter(this);
        adapter.addAll(lessons);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        //mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).marginResId(R.dimen.activity_vertical_margin, R.dimen.activity_vertical_margin).size(1).build());
        mRecyclerView.setAdapter(adapter);
    }
}
