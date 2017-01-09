package com.sharpdroid.registro.Activities;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.sharpdroid.registro.API.SpiaggiariApiClient;
import com.sharpdroid.registro.Adapters.AllLessonsAdapter;
import com.sharpdroid.registro.Interfaces.API.Lesson;
import com.sharpdroid.registro.Listeners.OnScrollLessonsListener;
import com.sharpdroid.registro.R;
import com.sharpdroid.registro.Tasks.CacheListTask;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sharpdroid.registro.Utils.Metodi.isNetworkAvailable;

public class AllLessonsWithDownloadActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {
    private final static String TAG = "Lessons";
    static int code;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    AllLessonsAdapter mRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_refresh);
        ButterKnife.bind(this);

        code = getIntent().getIntExtra("code", -1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle(getIntent().getStringExtra("name"));
        mRVAdapter = new AllLessonsAdapter(this);
        mRecyclerView.addOnScrollListener(new OnScrollLessonsListener());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRVAdapter);

        bindLessonsCache();

        UpdateLessons();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    private void addLessons(List<Lesson> lessons, boolean docache) {
        if (!lessons.isEmpty()) {
            mRVAdapter.clear();
            mRVAdapter.addAll(lessons);

            if (docache) {
                // Update cache

                new CacheListTask(getCacheDir(), TAG + File.pathSeparator + code).execute((List) lessons);
            }
        }
    }

    private void bindLessonsCache() {
        ObjectInputStream objectInputStream = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(getCacheDir(), TAG + File.pathSeparator + code));
            objectInputStream = new ObjectInputStream(fileInputStream);
            List<Lesson> cachedData = new LinkedList<>();
            Lesson temp;
            while ((temp = (Lesson) objectInputStream.readObject()) != null) {
                cachedData.add(temp);
            }
            addLessons(cachedData, false);
            Log.d(TAG, "Restored cache");
        } catch (FileNotFoundException e) {
            Log.w(TAG, "Cache not found.");
        } catch (EOFException e) {
            Log.e(TAG, "Error while reading cache! (EOF) ");
        } catch (StreamCorruptedException e) {
            Log.e(TAG, "Corrupted cache!");
        } catch (IOException e) {
            Log.e(TAG, "Error while reading cache!");
        } catch (ClassCastException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onRefresh() {
        UpdateLessons();
    }

    private void UpdateLessons() {
        if (isNetworkAvailable(this)) {
            mSwipeRefreshLayout.setRefreshing(true);
            new SpiaggiariApiClient(this).getLessons(code)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(lessons -> {
                        addLessons(lessons, true);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }, error -> mSwipeRefreshLayout.setRefreshing(false));
        } else {
            Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
