package com.sharpdroid.registro.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class AbsencesAdapter extends RecyclerView.Adapter<AbsencesAdapter.AbsencesHolder> {

    @Override
    public AbsencesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(AbsencesHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class AbsencesHolder extends RecyclerView.ViewHolder {

        public AbsencesHolder(View itemView) {
            super(itemView);
        }
    }
}
