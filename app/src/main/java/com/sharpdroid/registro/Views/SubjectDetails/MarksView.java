package com.sharpdroid.registro.Views.SubjectDetails;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.sharpdroid.registro.Adapters.MarkAdapter;
import com.sharpdroid.registro.Interfaces.Mark;
import com.sharpdroid.registro.Interfaces.Subject;
import com.sharpdroid.registro.R;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MarksView extends CardView {
    Context mContext;
    SimpleDateFormat format = new SimpleDateFormat("d MMM", Locale.getDefault());

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.chart)
    LineChart lineChartView;

    MarkAdapter adapter;

    public MarksView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MarksView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public MarksView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    void init() {
        inflate(mContext, R.layout.view_marks, this);
        ButterKnife.bind(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext).marginResId(R.dimen.activity_vertical_margin, R.dimen.activity_vertical_margin).size(1).build());


        XAxis xAxis = lineChartView.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter((value, axis) -> format.format(new Date((long) value)));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = lineChartView.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        //leftAxis.setAxisMaximum(10f);

        YAxis rightAxis = lineChartView.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);
        //rightAxis.setAxisMaximum(10f);

        //not zoomable nor draggable
        lineChartView.setDragEnabled(false);
        lineChartView.setScaleEnabled(false);
        lineChartView.setPinchZoom(false);

        //do not show description nor legend
        lineChartView.getDescription().setEnabled(false);
        lineChartView.getLegend().setEnabled(false);
    }

    public void setSubject(Subject subject) {
        adapter = new MarkAdapter(mContext, subject);
        adapter.setTarget(subject.getTarget());
        mRecyclerView.setAdapter(adapter);
    }

    public void addAll(List<Mark> marks) {
        adapter.addAll(marks);
        setChart(marks);
    }

    public void clear() {
        adapter.clear();
    }

    void setChart(List<Mark> marks) {
        List<ILineDataSet> lines = new ArrayList<>();

        LineDataSet line = new LineDataSet(getEntriesFromMarks(marks), "");
        line.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        line.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        line.setDrawValues(false);
        line.setDrawFilled(true);
        line.setDrawCircles(true);
        line.setCircleRadius(1.5f);
        line.setCircleColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        line.setDrawCircleHole(false);
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

    List<Entry> getEntriesFromMarks(List<Mark> marks) {
        List<Entry> list = new ArrayList<>();
        for (Mark mark : marks) {
            if (!mark.isNs()) {
                Entry entry = new Entry(mark.getDate().getTime(), Float.parseFloat(mark.getMark()));
                list.add(entry);
            }
        }
        return list;
    }
}
