package com.sharpdroid.registro.Views.SubjectDetails;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

public class TargetView extends CardView {
    Context mContext;

    public TargetView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public TargetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public TargetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    void init() {

    }
}
