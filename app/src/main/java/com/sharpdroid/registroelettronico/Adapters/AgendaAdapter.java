package com.sharpdroid.registroelettronico.Adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.Adapters.Holders.HeaderHolder;
import com.sharpdroid.registroelettronico.Databases.Entities.SuperAgenda;
import com.sharpdroid.registroelettronico.Interfaces.Client.AgendaEntry;
import com.sharpdroid.registroelettronico.Interfaces.Client.Entry;
import com.sharpdroid.registroelettronico.Interfaces.Client.HeaderEntry;
import com.sharpdroid.registroelettronico.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registroelettronico.Utils.Metodi.capitalizeEach;

public class AgendaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Entry> CVDataList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM", Locale.getDefault());
    private View place_holder;
    private AgendaClickListener mClickListener;

    public AgendaAdapter(View ph) {
        CVDataList = new ArrayList<>();
        place_holder = ph;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HeaderEntry.ID)
            return new HeaderHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
        else
            return new EventHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Entry entry = CVDataList.get(position);
        if (entry instanceof HeaderEntry) {
            ((HeaderHolder) holder).content.setText(((HeaderEntry) entry).getTitle());
        } else {
            EventHolder eventHolder = (EventHolder) holder;
            SuperAgenda event = ((AgendaEntry) entry).getEvent();
            Spannable title = new SpannableString(event.getAgenda().getNotes());
            if (event.getCompleted()) {
                title.setSpan(new StrikethroughSpan(), 0, event.getAgenda().getNotes().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            eventHolder.date.setText(dateFormat.format(event.getAgenda().getStart()));
            eventHolder.subject.setText(capitalizeEach(event.getAgenda().getAuthor(), true));
            eventHolder.title.setText(title);

            eventHolder.notes.setVisibility(View.GONE);
            eventHolder.divider.setVisibility((CVDataList.get(position - 1) instanceof HeaderEntry) ? View.INVISIBLE : View.VISIBLE);

            eventHolder.itemView.setOnClickListener((View v) -> {
                Log.d("CLICK", "CLICK");
                if (mClickListener != null)
                    mClickListener.onAgendaItemClicked(event);
            });
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

    public void setItemClickListener(AgendaClickListener longClickListener) {
        mClickListener = longClickListener;
    }

    public void addAll(List<SuperAgenda> events) {
        CVDataList.addAll(convert(events));
        if (CVDataList.isEmpty()) {
            place_holder.setVisibility(View.VISIBLE);
        } else {
            place_holder.setVisibility(View.GONE);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        CVDataList.clear();
        notifyDataSetChanged();
    }

    private List<Entry> convert(List<SuperAgenda> events) {
        LinkedHashMap<String, List<SuperAgenda>> organized = new LinkedHashMap<>();
        for (SuperAgenda e : events) {
            if (e.getTest()) {
                if (organized.containsKey("Verifiche")) {
                    List<SuperAgenda> verifiche = new ArrayList<>(organized.get("Verifiche"));
                    verifiche.add(e);
                    organized.put("Verifiche", verifiche);
                } else {
                    organized.put("Verifiche", Collections.singletonList(e));
                }
            } else {
                if (organized.containsKey("ALTRI EVENTI")) {
                    List<SuperAgenda> otherEvents = new ArrayList<>(organized.get("ALTRI EVENTI"));
                    otherEvents.add(e);
                    organized.put("ALTRI EVENTI", otherEvents);
                } else {
                    organized.put("ALTRI EVENTI", Collections.singletonList(e));
                }
            }
        }

        List<Entry> convert = new LinkedList<>();

        //Priorità alle verifiche
        if (organized.containsKey("Verifiche")) {
            convert.add(new HeaderEntry("Verifiche"));
            for (SuperAgenda e : organized.get("Verifiche")) {
                convert.add(new AgendaEntry(e));
            }
            organized.remove("Verifiche");
        }

        for (String k : organized.keySet()) {
            convert.add(new HeaderEntry(k));
            for (SuperAgenda e : organized.get(k)) {
                convert.add(new AgendaEntry(e));
            }
        }

        return convert;
    }

    public interface AgendaClickListener {
        void onAgendaItemClicked(SuperAgenda e);
    }

    class EventHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.divider)
        View divider;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.subject)
        TextView subject;
        @BindView(R.id.notes)
        TextView notes;
        @BindView(R.id.date)
        TextView date;

        EventHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
