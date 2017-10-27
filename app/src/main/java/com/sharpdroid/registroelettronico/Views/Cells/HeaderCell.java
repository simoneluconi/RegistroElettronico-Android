package com.sharpdroid.registroelettronico.Views.Cells;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Utils.LayoutHelper;
import com.sharpdroid.registroelettronico.Utils.Metodi;

public class HeaderCell extends FrameLayout {
    private TextView textView;

    public HeaderCell(Context context) {
        super(context);

        textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        textView.setTextColor(ContextCompat.getColor(context, R.color.primary));
        textView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        textView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.START | Gravity.TOP, 17, 15, 17, 0));
    }

    public TextView getTextView() {
        return textView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(Metodi.dp(38), MeasureSpec.EXACTLY));
    }

    public void setText(String text) {
        textView.setText(text);
    }
}
