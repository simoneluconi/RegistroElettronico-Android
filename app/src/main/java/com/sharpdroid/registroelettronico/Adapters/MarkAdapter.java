package com.sharpdroid.registroelettronico.Adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.Databases.Entities.Grade;
import com.sharpdroid.registroelettronico.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.sharpdroid.registroelettronico.Utils.Metodi.getMarkColor;
import static com.sharpdroid.registroelettronico.Utils.Metodi.sortMarksByDate;

public class MarkAdapter extends RecyclerView.Adapter<MarkAdapter.MarkHolder> {
    float target;
    private Context mContext;
    private SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy", Locale.ITALIAN);
    private List<Grade> CVDataList;

    public MarkAdapter(Context mContext) {
        this.mContext = mContext;
        CVDataList = new ArrayList<>();
    }

    public void addAll(List<Grade> list) {
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

        Grade mark = CVDataList.get(position);
        holder.color.setImageDrawable(new ColorDrawable(ContextCompat.getColor(mContext, getMarkColor(mark.getMValue(), target))));
        holder.mark.setText(mark.getMStringValue());

        holder.content.setText(mark.getMNotes().trim());

        holder.content.setVisibility((TextUtils.isEmpty(holder.content.getText()) ? View.GONE : View.VISIBLE));

        holder.type.setText(mark.getMType());
        holder.date.setText(format.format(mark.getMDate()));
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
    }


    class MarkHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.color)
        CircleImageView color;
        @BindView(R.id.mark)
        TextView mark;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.type)
        TextView type;

        MarkHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
