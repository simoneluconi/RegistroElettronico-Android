package com.sharpdroid.registroelettronico.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.sharpdroid.registroelettronico.Adapters.FileAdapter;
import com.sharpdroid.registroelettronico.Databases.FilesDB;
import com.sharpdroid.registroelettronico.Interfaces.API.Folder;
import com.sharpdroid.registroelettronico.R;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;

import butterknife.ButterKnife;

import static com.sharpdroid.registroelettronico.Utils.Metodi.dpToPx;

public class ActivityFiles extends AppCompatActivity {
    final static String TAG = ActivityFiles.class.getSimpleName();

    FileAdapter mRVAdapter;
    FilesDB db;
    Folder data;

    public ActivityFiles() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        ButterKnife.bind(this);
        db = new FilesDB(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        CoordinatorLayout mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        try {
            //GET DATA VIA JSON
            data = new Gson().getAdapter(Folder.class).fromJson(getIntent().getStringExtra("folder"));
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (data != null) {
                setTitle(WordUtils.capitalizeFully(data.getProfName()));
                getSupportActionBar().setSubtitle(WordUtils.capitalize(data.getName()));
                mRVAdapter = new FileAdapter(this, mCoordinatorLayout, db);
                addSubjects(data);
            }
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).margin(dpToPx(72), dpToPx(16)).size(1).build());
        mRecyclerView.setItemAnimator(null);

        mRecyclerView.setAdapter(mRVAdapter);
    }

    private void addSubjects(Folder folder) {
        mRVAdapter.clear();
        mRVAdapter.addAll(folder.getElements());
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }
}
