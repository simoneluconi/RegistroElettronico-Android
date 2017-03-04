package com.sharpdroid.registroelettronico.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.sharpdroid.registroelettronico.Adapters.AllLessonsAdapter;
import com.sharpdroid.registroelettronico.Databases.SubjectsDB;
import com.sharpdroid.registroelettronico.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllLessonsActivity extends AppCompatActivity {
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    AllLessonsAdapter adapter;
    SubjectsDB db;
    int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_lessons);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        code = getIntent().getIntExtra("code", 0);
        db = new SubjectsDB(this);

        setTitle("Lezioni");
        adapter = new AllLessonsAdapter(this);
        adapter.addAll(db.getLessons(code));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
