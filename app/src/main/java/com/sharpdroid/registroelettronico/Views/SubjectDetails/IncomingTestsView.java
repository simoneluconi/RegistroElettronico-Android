package com.sharpdroid.registroelettronico.Views.SubjectDetails;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

public class IncomingTestsView extends CardView {
    Context mContext;

    public IncomingTestsView(Context context) {
        super(context);
        this.mContext = context;
    }

    public IncomingTestsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public IncomingTestsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }
}
