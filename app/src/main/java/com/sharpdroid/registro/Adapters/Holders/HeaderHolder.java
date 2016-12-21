package com.sharpdroid.registro.Adapters.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sharpdroid.registro.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HeaderHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.content)
    public TextView content;

    public HeaderHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
