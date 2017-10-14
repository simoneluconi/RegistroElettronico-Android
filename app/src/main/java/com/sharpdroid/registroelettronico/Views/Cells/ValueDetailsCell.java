package com.sharpdroid.registroelettronico.Views.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.Utils.LayoutHelper;
import com.sharpdroid.registroelettronico.Utils.Metodi;


public class ValueDetailsCell extends FrameLayout {
    TextView textView, valueTextView;
    Paint dividerPaint;
    private boolean needDivider;

    public ValueDetailsCell(@NonNull Context context) {
        super(context);
        dividerPaint = new Paint();
        dividerPaint.setStrokeWidth(1);
        dividerPaint.setColor(0xffd9d9d9);

        textView = new TextView(context);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        addView(textView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 17, 10, 17, 0));

        valueTextView = new TextView(context);
        valueTextView.setTextColor(0xff8a8a8a);
        valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        valueTextView.setGravity(Gravity.LEFT);
        valueTextView.setLines(1);
        valueTextView.setMaxLines(1);
        valueTextView.setSingleLine(true);
        valueTextView.setPadding(0, 0, 0, 0);
        addView(valueTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 17, 35, 17, 0));
    }

    public void setTextAndValue(String text, String value, boolean divider) {
        textView.setVisibility(View.VISIBLE);
        valueTextView.setVisibility(View.VISIBLE);
        textView.setText(text);
        valueTextView.setText(value);
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setText(String text, boolean divider) {
        textView.setVisibility(View.VISIBLE);
        valueTextView.setVisibility(View.GONE);
        textView.setText(text);
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, dividerPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(Metodi.dp(64) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));

    }
}
