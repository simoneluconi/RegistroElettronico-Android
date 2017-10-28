package com.sharpdroid.registroelettronico.adapters.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class AbsencesHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.date)
    public TextView date;
    @BindView(R.id.hour)
    public TextView hour;
    @BindView(R.id.type)
    public TextView type_text;
    @BindView(R.id.done)
    public ImageView done;
    @BindView(R.id.type_color)
    public CircleImageView type_color;
    @BindView(R.id.layout)
    public View layout;

    public AbsencesHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}