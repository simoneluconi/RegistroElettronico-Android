package com.sharpdroid.registroelettronico.Views.SubjectDetails;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.Button;

import com.orm.SugarRecord;
import com.sharpdroid.registroelettronico.Activities.AllLessonsWithDownloadActivity;
import com.sharpdroid.registroelettronico.Adapters.LessonsAdapter;
import com.sharpdroid.registroelettronico.Databases.Entities.Lesson;
import com.sharpdroid.registroelettronico.R;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registroelettronico.Utils.Metodi.dpToPx;

public class RecentLessonsView extends CardView {
    Context mContext;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.load_more)
    Button showAllButton;

    LessonsAdapter adapter;

    public RecentLessonsView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public RecentLessonsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public RecentLessonsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        inflate(mContext, R.layout.view_recent_lessons, this);
        ButterKnife.bind(this);

        adapter = new LessonsAdapter(mContext);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext).colorResId(R.color.divider).marginResId(R.dimen.activity_vertical_margin, R.dimen.activity_vertical_margin).size(dpToPx(1)).build());
        mRecyclerView.setAdapter(adapter);
    }

    public void update(int code) {
        adapter.clear();
        adapter.addAll(SugarRecord.find(Lesson.class, "M_SUBJECT_CODE=?", String.valueOf(code)));
        showAllButton.setOnClickListener(view -> mContext.startActivity(new Intent(mContext, AllLessonsWithDownloadActivity.class).putExtra("code", code)));
    }

    public void clear() {
        adapter.clear();
    }
}
