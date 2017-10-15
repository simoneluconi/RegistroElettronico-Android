package com.sharpdroid.registroelettronico.Views.SubjectDetails;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.R;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.Nullable;

public class OverallView extends CardView {
    Context mContext;
    AttributeSet mAttributeSet;
    int defStyleAttr;

    @BindView(R.id.scritto)
    TextView mScritto;
    @BindView(R.id.orale)
    TextView mOrale;
    @BindView(R.id.pratico)
    TextView mPratico;
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.text2)
    TextView text2;
    @BindView(R.id.text3)
    TextView text3;
    @BindView(R.id.header)
    TextView header;

    public OverallView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public OverallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mAttributeSet = attrs;
        init();
    }

    public OverallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mAttributeSet = attrs;
        this.defStyleAttr = defStyleAttr;
        init();
    }

    void init() {
        inflate(mContext, R.layout.view_overall_subject, this);
        ButterKnife.bind(this);
    }

    public void setScritto(@Nullable Float scritto) {
        if (scritto == null) mScritto.setText("-");
        else mScritto.setText(String.format(Locale.getDefault(), "%.2f", scritto));
    }

    public void setOrale(@Nullable Float orale) {
        if (orale == null) mOrale.setText("-");
        else mOrale.setText(String.format(Locale.getDefault(), "%.2f", orale));
    }

    public void setPratico(@Nullable Float pratico) {
        if (pratico == null) mPratico.setText("-");
        else mPratico.setText(String.format(Locale.getDefault(), "%.2f", pratico));
    }
}
