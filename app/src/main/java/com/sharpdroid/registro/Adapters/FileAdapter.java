package com.sharpdroid.registro.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

public class FileAdapter {

    class FileHolder extends RecyclerView.ViewHolder {


        public FileHolder(View layout) {
            super(layout);
            ButterKnife.bind(this, layout);
        }
    }
}
