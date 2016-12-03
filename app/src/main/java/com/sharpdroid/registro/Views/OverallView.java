package com.sharpdroid.registro.Views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sharpdroid.registro.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import devlight.io.library.ArcProgressStackView;


public class OverallView extends CardView {
    Context mContext;
    AttributeSet mAttributeSet;
    int defStyleAttr;

    @BindView(R.id.scritto)
    TextView mScritto;
    @BindView(R.id.orale)
    TextView mOrale;
    @BindView(R.id.media)
    TextView mMedia;
    @BindView(R.id.progressvoti)
    ArcProgressStackView arcProgressStackView;


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

    public void setScritto(String scritto) {
        mScritto.setText(scritto);
    }

    public void setOrale(String orale) {
        mOrale.setText(orale);
    }

    public void setMedia(String media) {
        mMedia.setText(media);
    }

    public void setProgress(float progress, int color) {
        List<ArcProgressStackView.Model> models = new ArrayList<>();
        models.add(new ArcProgressStackView.Model(null, progress, color));
        arcProgressStackView.setModels(models);
    }
}
