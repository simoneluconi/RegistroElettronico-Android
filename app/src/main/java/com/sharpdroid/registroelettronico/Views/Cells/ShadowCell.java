package com.sharpdroid.registroelettronico.Views.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Utils.Metodi;

public class ShadowCell extends View {

    private int size = 12;

    public ShadowCell(Context context) {
        super(context);
        setBackgroundDrawable(getThemedDrawable(context, R.drawable.greydivider));
    }

    public static Drawable getThemedDrawable(Context context, int resId) {
        Drawable drawable = context.getResources().getDrawable(resId).mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(0xff000000, PorterDuff.Mode.MULTIPLY));
        return drawable;
    }

    public void setSize(int value) {
        size = value;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(Metodi.dp(size), MeasureSpec.EXACTLY));
    }
}

