package com.sharpdroid.registroelettronico.Views.SubjectDetails;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.sharpdroid.registroelettronico.Adapters.MarkAdapter;
import com.sharpdroid.registroelettronico.Databases.Entities.Grade;
import com.sharpdroid.registroelettronico.Databases.Entities.Subject;
import com.sharpdroid.registroelettronico.R;
import com.transitionseverywhere.AutoTransition;
import com.transitionseverywhere.TransitionManager;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registroelettronico.Utils.Metodi.dpToPx;

public class MarksView extends CardView implements PopupMenu.OnMenuItemClickListener {
    Context mContext;
    SimpleDateFormat format = new SimpleDateFormat("d MMM", Locale.ITALIAN);

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.chart)
    LineChart lineChartView;
    @BindView(R.id.options)
    ImageButton optionButton;

    PopupMenu menu;
    MarkAdapter adapter;
    boolean showChart;

    public MarksView(Context context) {
        super(context);
        init(context);
    }

    public MarksView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MarksView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    void init(Context context) {
        mContext = context;


        inflate(mContext, R.layout.view_marks, this);
        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext).colorResId(R.color.divider).marginResId(R.dimen.padding_left_divider2, R.dimen.activity_vertical_margin).size(dpToPx(1)).build());
        mRecyclerView.setNestedScrollingEnabled(false);

        menu = new PopupMenu(mContext, optionButton);
        menu.getMenuInflater().inflate(R.menu.view_marks_menu, menu.getMenu());
        optionButton.setOnClickListener(view -> menu.show());
        menu.setOnMenuItemClickListener(this);

        XAxis xAxis = lineChartView.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter((value, axis) -> format.format(new Date((long) value)));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1314873000f);

        YAxis rightAxis = lineChartView.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxis = lineChartView.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setGridColor(Color.parseColor("#22000000"));
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(10f);

        //not zoomable nor draggable
        lineChartView.setDragEnabled(false);
        lineChartView.setScaleEnabled(false);
        lineChartView.setPinchZoom(false);

        //do not show description nor legend
        lineChartView.getDescription().setEnabled(false);
        lineChartView.getLegend().setEnabled(false);
    }

    public void setSubject(Subject subject, float media) {
        setLimitLines(subject.getTarget(), media);

        adapter = new MarkAdapter(mContext);
        setTarget(subject);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.invalidate();
    }

    public void setTarget(Float target) {
        adapter.setTarget(target.equals(0f) ? Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(mContext).getString("voto_obiettivo", "8")) : target);
    }

    public void setTarget(Subject subject) {
        float target = ((Float) subject.getTarget()).equals(0f) ? Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(mContext).getString("voto_obiettivo", "8")) : subject.getTarget();
        adapter.setTarget(target);
    }

    public void addAll(List<Grade> marks) {
        adapter.addAll(marks);
        showChart = marks.size() > 1;
    }

    public void setLimitLines(float target, float media) {
        Float t = target;
        if (t.equals(0f))
            t = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(mContext).getString("voto_obiettivo", "8"));

        LimitLine ll2 = new LimitLine(t, "Il tuo obiettivo");
        ll2.setLineWidth(1f);
        ll2.setLineColor(Color.parseColor("#22000000"));
        ll2.enableDashedLine(15f, 0f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setTextColor(Color.parseColor("#444444"));


        LimitLine ll1 = new LimitLine(media, "La tua media");
        ll1.setLineWidth(1f);
        ll1.setLineColor(Color.parseColor("#22000000"));
        ll1.enableDashedLine(15f, 0f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        ll1.setTextSize(10f);
        ll1.setTextColor(Color.parseColor("#444444"));

        lineChartView.getAxisLeft().getLimitLines().clear();
        lineChartView.getAxisLeft().addLimitLine(ll1);
        lineChartView.getAxisLeft().addLimitLine(ll2);
        invalidate();
    }

    public void clear() {
        adapter.clear();
    }

    public void setShowChart(boolean show) {
        menu.getMenu().findItem(R.id.show).setChecked(show);
        if (show) lineChartView.setVisibility(VISIBLE);
        else lineChartView.setVisibility(GONE);
    }

    public void setChart(List<Entry> marks) {
        List<ILineDataSet> lines = new ArrayList<>();

        LineDataSet line = new LineDataSet(marks, "");
        line.setMode(LineDataSet.Mode.LINEAR);
        line.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        line.setDrawFilled(true);
        line.setDrawCircles(true);
        line.setDrawCircleHole(false);
        line.setCircleRadius(2f);
        line.setDrawValues(false);
        line.setDrawHighlightIndicators(false);
        line.setCircleColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        line.setAxisDependency(YAxis.AxisDependency.LEFT);
        //drawable gradient
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.chart_fill);
            line.setFillDrawable(drawable);
        } else {
            line.setFillColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        }

        lines.add(line);

        LineData lineData = new LineData(lines);
        lineChartView.setData(lineData);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.show && showChart) {
            item.setChecked(!item.isChecked());
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean("show_chart", item.isChecked()).apply();
            TransitionManager.beginDelayedTransition((ViewGroup) getRootView(), new AutoTransition().setInterpolator(new DecelerateInterpolator(1.2f)).setDuration(300));
            lineChartView.setVisibility((item.isChecked()) ? VISIBLE : GONE);
        }
        return true;
    }

    public int getItemCount() {
        return adapter.getItemCount();
    }
}
