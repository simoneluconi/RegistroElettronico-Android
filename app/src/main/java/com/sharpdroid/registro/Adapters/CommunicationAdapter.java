package com.sharpdroid.registro.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registro.Interfaces.Communication;
import com.sharpdroid.registro.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommunicationAdapter extends RecyclerView.Adapter<CommunicationAdapter.CommunicationHolder> {
    private final List<Communication> CVDataList;

    public CommunicationAdapter(List<Communication> CVDataList) {
        this.CVDataList = CVDataList;
    }

    public void addAll(Collection<Communication> list) {
        CVDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        CVDataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public CommunicationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_communications, parent, false);
        return new CommunicationHolder(v);
    }

    @Override
    public void onBindViewHolder(CommunicationHolder ViewHolder, int i) {
        final Communication communication = CVDataList.get(ViewHolder.getAdapterPosition());
        ViewHolder.Title.setText(communication.getTitle());
        String datestring = communication.getDate().split("T")[0];
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALIAN);
            Date convertedCommitDate = formatter.parse(datestring);
            //formatter = new SimpleDateFormat("dd/MM/YYYY", Locale.ITALIAN);
            formatter = new SimpleDateFormat("d MMMM", Locale.ITALIAN);
            ViewHolder.Date.setText(formatter.format(convertedCommitDate));
        } catch (ParseException e) {
            ViewHolder.Date.setText(datestring);
        }
        ViewHolder.Type.setText(communication.getType());
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
    }

    class CommunicationHolder extends RecyclerView.ViewHolder {
        final TextView Title;
        final TextView Date;
        final TextView Type;

        CommunicationHolder(View itemView) {
            super(itemView);
            Title = (TextView) itemView.findViewById(R.id.title);
            Date = (TextView) itemView.findViewById(R.id.date);
            Type = (TextView) itemView.findViewById(R.id.type);
        }
    }
}
