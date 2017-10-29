package com.sharpdroid.registroelettronico.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.adapters.Holders.HeaderHolder;
import com.sharpdroid.registroelettronico.database.entities.LocalAgenda;
import com.sharpdroid.registroelettronico.database.entities.SuperAgenda;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach;

public class AgendaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> CVDataList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM", Locale.getDefault());
    private View place_holder;
    private AgendaClickListener mClickListener;

    public AgendaAdapter(View ph) {
        CVDataList = new ArrayList<>();
        place_holder = ph;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == R.layout.adapter_header)
            return new HeaderHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
        else
            return new EventHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object entry = CVDataList.get(position);
        if (entry instanceof String) {
            ((HeaderHolder) holder).content.setText(((String) entry));
        } else if (entry instanceof SuperAgenda) {
            EventHolder eventHolder = (EventHolder) holder;
            SuperAgenda event = ((SuperAgenda) entry);
            Spannable title = new SpannableString(event.getAgenda().getNotes());
            if (event.getCompleted()) {
                title.setSpan(new StrikethroughSpan(), 0, event.getAgenda().getNotes().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            eventHolder.date.setText(dateFormat.format(event.getAgenda().getStart()));
            eventHolder.subject.setText(capitalizeEach(event.getAgenda().getAuthor(), true));
            eventHolder.title.setText(title);

            eventHolder.notes.setVisibility(View.GONE);
            eventHolder.divider.setVisibility((CVDataList.get(position - 1) instanceof String) ? View.INVISIBLE : View.VISIBLE);

            eventHolder.itemView.setOnClickListener((View v) -> {
                if (mClickListener != null)
                    mClickListener.onAgendaItemClicked(event);
            });
        } else if (entry instanceof LocalAgenda) {
            EventHolder eventHolder = (EventHolder) holder;
            LocalAgenda event = ((LocalAgenda) entry);

            Spannable title = new SpannableString(event.getTitle());
            if (event.getCompleted_date() != null) {
                title.setSpan(new StrikethroughSpan(), 0, event.getTitle().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            eventHolder.date.setText(dateFormat.format(event.getDay()));
            eventHolder.subject.setText(capitalizeEach(event.getTeacher().getTeacherName(), true));
            eventHolder.title.setText(title);
            eventHolder.notes.setText(event.getContent().trim());

            eventHolder.notes.setVisibility(event.getContent().isEmpty() ? View.GONE : View.VISIBLE);
            eventHolder.divider.setVisibility(getItemViewType(position - 1) == R.layout.adapter_header ? View.INVISIBLE : View.VISIBLE);

            eventHolder.itemView.setOnClickListener((View v) -> {
                if (mClickListener != null)
                    mClickListener.onAgendaItemClicked(event);
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = CVDataList.get(position);
        if (item instanceof String) return R.layout.adapter_header;
        else return R.layout.adapter_event;
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
    }

    public void setItemClickListener(AgendaClickListener longClickListener) {
        mClickListener = longClickListener;
    }

    public void addAll(List<Object> events) {
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

    private List<Object> convert(List<Object> events) {
        LinkedHashMap<String, List<Object>> organized = new LinkedHashMap<>();
        for (Object e : events) {
            if (e instanceof SuperAgenda) {
                if (((SuperAgenda) e).getTest()) {
                    if (organized.containsKey("Verifiche")) {
                        List<Object> verifiche = new ArrayList<>(organized.get("Verifiche"));
                        verifiche.add(e);
                        organized.put("Verifiche", verifiche);
                    } else {
                        organized.put("Verifiche", Collections.singletonList(e));
                    }
                } else {
                    if (organized.containsKey("ALTRI EVENTI")) {
                        List<Object> otherEvents = new ArrayList<>(organized.get("ALTRI EVENTI"));
                        otherEvents.add(e);
                        organized.put("ALTRI EVENTI", otherEvents);
                    } else {
                        organized.put("ALTRI EVENTI", Collections.singletonList(e));
                    }
                }
            } else if (e instanceof LocalAgenda) {
                if (((LocalAgenda) e).getType().equalsIgnoreCase("verifica")) {
                    if (organized.containsKey("Verifiche")) {
                        List<Object> verifiche = new ArrayList<>(organized.get("Verifiche"));
                        verifiche.add(e);
                        organized.put("Verifiche", verifiche);
                    } else {
                        organized.put("Verifiche", Collections.singletonList(e));
                    }
                } else {
                    if (organized.containsKey("ALTRI EVENTI")) {
                        List<Object> otherEvents = new ArrayList<>(organized.get("ALTRI EVENTI"));
                        otherEvents.add(e);
                        organized.put("ALTRI EVENTI", otherEvents);
                    } else {
                        organized.put("ALTRI EVENTI", Collections.singletonList(e));
                    }
                }
            }
        }

        List<Object> convert = new LinkedList<>();

        //Priorità alle verifiche
        if (organized.containsKey("Verifiche")) {
            convert.add("Verifiche");
            convert.addAll(organized.get("Verifiche"));
            organized.remove("Verifiche");
        }

        for (String k : organized.keySet()) {
            convert.add(k);
            convert.addAll(organized.get(k));
        }

        return convert;
    }

    public interface AgendaClickListener {
        void onAgendaItemClicked(Object e);
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
