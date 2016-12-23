package com.sharpdroid.registro.Adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registro.Interfaces.API.Mark;
import com.sharpdroid.registro.Interfaces.Client.Subject;
import com.sharpdroid.registro.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.sharpdroid.registro.Utils.Metodi.getMarkColor;
import static com.sharpdroid.registro.Utils.Metodi.sortMarksByDate;

public class MarkAdapter extends RecyclerView.Adapter<MarkAdapter.MarkHolder> {
    float target;
    private Context mContext;
    private SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
    private List<Mark> CVDataList;
    private Subject subject;

    public MarkAdapter(Context mContext, Subject subject) {
        this.mContext = mContext;
        CVDataList = new ArrayList<>();
        this.subject = subject;
    }

    public void addAll(List<Mark> list) {
        CVDataList = sortMarksByDate(list);
        notifyDataSetChanged();
    }

    public void clear() {
        CVDataList.clear();
        notifyDataSetChanged();
    }

    public void setTarget(float t) {
        target = t;
        notifyDataSetChanged();
    }

    @Override
    public MarkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MarkHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_mark, parent, false));
    }

    @Override
    public void onBindViewHolder(MarkHolder holder, int position) {
        Mark mark = CVDataList.get(position);
        holder.color.setImageDrawable(new ColorDrawable(ContextCompat.getColor(mContext, getMarkColor(mark, target))));
        holder.mark.setText(mark.getMark());
        holder.content.setText(mark.getDesc());
        holder.date.setText(format.format(mark.getDate()));
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
    }

    protected class MarkHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.color)
        CircleImageView color;
        @BindView(R.id.mark)
        TextView mark;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.date)
        TextView date;

        MarkHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
