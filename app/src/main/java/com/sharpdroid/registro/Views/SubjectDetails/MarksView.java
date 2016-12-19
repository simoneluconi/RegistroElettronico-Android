package com.sharpdroid.registro.Views.SubjectDetails;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.sharpdroid.registro.Adapters.MarkAdapter;
import com.sharpdroid.registro.Interfaces.Mark;
import com.sharpdroid.registro.Interfaces.Subject;
import com.sharpdroid.registro.R;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MarksView extends CardView {
    Context mContext;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    MarkAdapter adapter;

    public MarksView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MarksView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public MarksView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    void init() {
        inflate(mContext, R.layout.view_marks, this);
        ButterKnife.bind(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext).marginResId(R.dimen.activity_vertical_margin, R.dimen.activity_vertical_margin).size(1).build());
    }

    public void setSubject(Subject subject) {
        adapter = new MarkAdapter(mContext, subject);
        adapter.setTarget(subject.getTarget());
        mRecyclerView.setAdapter(adapter);
    }

    public void addAll(List<Mark> markList) {
        adapter.addAll(markList);
    }

    public void clear() {
        adapter.clear();
    }
}
