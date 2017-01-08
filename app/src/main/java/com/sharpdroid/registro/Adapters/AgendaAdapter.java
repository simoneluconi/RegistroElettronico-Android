package com.sharpdroid.registro.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registro.Adapters.Holders.HeaderHolder;
import com.sharpdroid.registro.Interfaces.API.Event;
import com.sharpdroid.registro.Interfaces.Client.AgendaEntry;
import com.sharpdroid.registro.Interfaces.Client.Entry;
import com.sharpdroid.registro.Interfaces.Client.HeaderEntry;
import com.sharpdroid.registro.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registro.Utils.Metodi.isEventTest;

public class AgendaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Entry> CVDataList;
    private final Context mContext;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM", Locale.getDefault());

    public AgendaAdapter(Context mContext) {
        this.mContext = mContext;
        CVDataList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HeaderEntry.ID)
            return new HeaderHolder(LayoutInflater.from(mContext).inflate(viewType, parent, false));
        else
            return new EventHolder(LayoutInflater.from(mContext).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Entry entry = CVDataList.get(position);
        if (entry instanceof HeaderEntry) {
            ((HeaderHolder) holder).content.setText(((HeaderEntry) entry).getTitle());
        } else {
            EventHolder eventHolder = (EventHolder) holder;
            Event event = ((AgendaEntry) entry).getEvent();

            if (CVDataList.get(position - 1) instanceof HeaderEntry) {
                eventHolder.divider.setVisibility(View.INVISIBLE);
            } else {
                eventHolder.divider.setVisibility(View.VISIBLE);
            }

            eventHolder.date.setText(dateFormat.format(event.getStart()));
            eventHolder.subject.setText(event.getClasse_desc());
            eventHolder.title.setText(event.getTitle());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return CVDataList.get(position).getID();
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
    }


    public void addAll(List<Event> events) {
        CVDataList = convert(events);
        notifyDataSetChanged();
    }

    public void clear() {
        CVDataList.clear();
        notifyDataSetChanged();
    }

    private List<Entry> convert(List<Event> events) {
        HashMap<String, List<Event>> organized = new HashMap<>();
        for (Event e : events) {
            if (isEventTest(e)) {
                if (organized.containsKey("Verifiche")) {
                    List<Event> verifiche = new ArrayList<>(organized.get("Verifiche"));
                    verifiche.add(e);
                    organized.put("Verifiche", verifiche);
                } else {
                    organized.put("Verifiche", Collections.singletonList(e));
                }
            } else {
                if (organized.containsKey("Alti Eventi")) {
                    List<Event> verifiche = new ArrayList<>(organized.get("Alti Eventi"));
                    verifiche.add(e);
                    organized.put("Alti Eventi", verifiche);
                } else {
                    organized.put("Alti Eventi", Collections.singletonList(e));
                }
            }
        }

        List<Entry> convert = new LinkedList<>();

        for (String k : organized.keySet()) {
            convert.add(new HeaderEntry(k));
            for (Event e : organized.get(k)) {
                convert.add(new AgendaEntry(e));
            }
        }

        return convert;
    }

    protected class EventHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.divider)
        View divider;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.subject)
        TextView subject;
        @BindView(R.id.date)
        TextView date;

        EventHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
