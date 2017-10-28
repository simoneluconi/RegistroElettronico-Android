package com.sharpdroid.registroelettronico.views.localEvent;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OptionView extends RelativeLayout {
    OnClickListener onClickListener;

    @BindView(R.id.layout)
    View layout;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.image)
    ImageView image;

    public OptionView(Context context) {
        super(context);
        init();
    }

    public OptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.OptionView,
                0, 0);

        try {
            setTitle(a.getString(R.styleable.OptionView_title));
            setContent(a.getString(R.styleable.OptionView_content));
            setImage(a.getDrawable(R.styleable.OptionView_image));
        } finally {
            a.recycle();
        }
    }

    public Builder builder() {
        return new Builder(getContext());
    }

    private void init() {
        inflate(getContext(), R.layout.view_event_option, this);
        ButterKnife.bind(this);

    }


    public void setTitle(String t) {
        if (title != null)
            title.setText(t);
    }

    public void setContent(String c) {
        if (content != null) {
            if (TextUtils.isEmpty(c)) {
                content.setText(getContext().getString(R.string.not_set));
            } else {
                content.setText(c);
            }
        }
    }

    public void setImage(@DrawableRes int drawable) {
        if (image != null)
            image.setImageResource(drawable);
    }

    public void setImage(Drawable drawable) {
        if (image != null)
            image.setImageDrawable(drawable);
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        if (layout != null)
            layout.setOnClickListener(onClickListener);
    }

    public static class Builder {
        Context c;
        private String title, content;
        @DrawableRes
        private int drawable;
        private OnClickListener listener;

        public Builder(Context c) {
            this.c = c;
        }

        public Builder title(String t) {
            title = t;
            return this;
        }

        public Builder content(String c) {
            content = c;
            return this;
        }

        public Builder image(@DrawableRes int i) {
            drawable = i;
            return this;
        }

        public Builder onClick(OnClickListener l) {
            listener = l;
            return this;
        }

        public OptionView build() {
            OptionView v = new OptionView(c);
            v.setImage(drawable);
            v.setContent(content);
            v.setTitle(title);
            v.setOnClickListener(listener);
            return v;
        }

    }
}
