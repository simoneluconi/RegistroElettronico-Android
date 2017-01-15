package com.sharpdroid.registroelettronico.Adapters.Holders;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LessonHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.divider)
    @Nullable
    public View divider;
    @BindView(R.id.content)
    public TextView content;
    @BindView(R.id.date)
    public TextView date;

    public LessonHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}