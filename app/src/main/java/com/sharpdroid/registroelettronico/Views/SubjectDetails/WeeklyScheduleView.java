package com.sharpdroid.registroelettronico.Views.SubjectDetails;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

public class WeeklyScheduleView extends CardView {
    Context mContext;

    public WeeklyScheduleView(Context context) {
        super(context);
        this.mContext = context;
    }

    public WeeklyScheduleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public WeeklyScheduleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }
}
