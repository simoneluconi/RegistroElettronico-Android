package com.sharpdroid.registro.Views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sharpdroid.registro.R;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import devlight.io.library.ArcProgressStackView;

import static com.sharpdroid.registro.Utils.Metodi.setTypeface;

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
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.text2)
    TextView text2;

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
        setTypeface(EasyFonts.robotoMedium(mContext), mScritto, mOrale, mMedia);
        setTypeface(EasyFonts.robotoRegular(mContext), text1, text2);
    }

    public void setScritto(String scritto) {
        mScritto.setText(scritto);
    }

    public void setOrale(String orale) {
        mOrale.setText(orale);
    }

    public void setMedia(String media) {
        mMedia.setText(media);
        setProgress(Float.parseFloat(media) * 10);
    }

    public void setProgress(float progress) {
        arcProgressStackView.setModels(Collections.singletonList(new ArcProgressStackView.Model("Voto", progress, ContextCompat.getColor(mContext, R.color.greenmaterial))));
    }
}
