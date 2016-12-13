package com.sharpdroid.registro.Views.SubjectDetails;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.sharpdroid.registro.R;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registro.Utils.Metodi.setTypeface;

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

    float media, target;


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

        setTypeface(EasyFonts.robotoMedium(mContext), mediaView, targetView);
        setTypeface(EasyFonts.robotoLight(mContext), text1, text2);
    }

    private void setMedia(float media) {
        this.mediaView.setText(String.format(Locale.getDefault(), "%.2f", media));
        this.media = media;
    }

    public void setTarget(float target) {
        targetView.setText(String.format(Locale.getDefault(), "%.2f", target));
        this.target = target;
        progressBar.setMax(target);
    }

    public void setProgress(Float media) {
        progressBar.setProgress(media);
        setMedia(media);
    }
}
