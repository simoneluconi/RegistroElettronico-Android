package com.sharpdroid.registro.Views.SubjectDetails;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.sharpdroid.registro.R;
import com.vstechlab.easyfonts.EasyFonts;

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
    TextView media;
    @BindView(R.id.obiettivo)
    TextView obiettivo;
    @BindView(R.id.progress)
    RoundCornerProgressBar progressBar;


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

        setTypeface(EasyFonts.robotoMedium(mContext), media, obiettivo);
        setTypeface(EasyFonts.robotoRegular(mContext), text1, text2);
        setProgress(8f);
    }

    public void setMedia(Float media) {
        this.media.setText(String.valueOf(media));
    }
    public void setProgress(Float media){
        progressBar.setProgress(media);
        setMedia(media);
    }
}
