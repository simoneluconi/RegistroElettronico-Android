package com.sharpdroid.registroelettronico.Views.SubjectDetails;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.sharpdroid.registroelettronico.R;
import com.transitionseverywhere.AutoTransition;
import com.transitionseverywhere.Recolor;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registroelettronico.Utils.Metodi.getMarkColor;

public class TargetView extends CardView {
    Context mContext;

    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.text2)
    TextView text2;
    @BindView(R.id.media)
    TextView mediaView;
    @BindView(R.id.obiettivo)
    TextView targetView;
    @BindView(R.id.progress)
    RoundCornerProgressBar progressBar;
    @BindView(R.id.imposta)
    Button set;
    @BindView(R.id.dettagli)
    Button details;

    float media, target = 1;

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
        inflate(mContext, R.layout.view_target, this);
        ButterKnife.bind(this);
    }

    public void setProgress(Float media) {
        setMedia(media);
    }

    public float getMedia() {
        return media;
    }

    private void setMedia(float media) {
        if (media == -1) {
            this.media = -1;
            progressBar.setProgressColor(getColor(R.color.intro_blue));
            progressBar.setMax(10);
            progressBar.setProgress(11);
            mediaView.setText("-");
        } else {
            this.media = media;
            this.mediaView.setText(String.format(Locale.getDefault(), "%.2f", media));

            TransitionManager.beginDelayedTransition((ViewGroup) progressBar.getRootView(), new TransitionSet().addTransition(new Recolor()).addTransition(new AutoTransition()).excludeTarget(TextView.class, true));
            progressBar.setProgress(media);
            progressBar.setProgressColor(getColor(getMarkColor(media, target)));
        }
    }

    public float getTarget() {
        return target;
    }

    public void setTarget(float target) {
        targetView.setText(String.format(Locale.getDefault(), "%.2f", target));

        TransitionManager.beginDelayedTransition((ViewGroup) progressBar.getRootView(), new TransitionSet().addTransition(new Recolor()).addTransition(new AutoTransition()).excludeTarget(TextView.class, true));
        progressBar.setMax(target);
        progressBar.setProgress(media);
        progressBar.setProgressColor(getColor(getMarkColor(media, target)));

        setMedia(media);
        this.target = target;
    }

    public void clear() {
        target = 0f;
        targetView.setText("-");

        //bar
        progressBar.setProgress(0f);
        progressBar.setMax(0f);
    }

    public void setListener(OnClickListener target, OnClickListener details) {
        set.setOnClickListener(target);
        this.details.setOnClickListener(details);
    }

    private int getColor(int color) {
        return ContextCompat.getColor(mContext, color);
    }
}
