package com.sharpdroid.registroelettronico.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.Adapters.Holders.HeaderHolder;
import com.sharpdroid.registroelettronico.Databases.RegistroDB;
import com.sharpdroid.registroelettronico.Interfaces.Client.AdvancedEvent;
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

import static com.sharpdroid.registroelettronico.Utils.Metodi.getSubjectNameOrProfessorName;
import static com.sharpdroid.registroelettronico.Utils.Metodi.isEventTest;

public class AgendaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private List<Entry> CVDataList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM", Locale.getDefault());
    private View place_holder;
    private AgendaClickListener mClickListener;
    private RegistroDB db;

    public AgendaAdapter(Context mContext, View ph, RegistroDB db) {
        this.mContext = mContext;
        CVDataList = new ArrayList<>();
        place_holder = ph;
        this.db = db;
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
            AdvancedEvent event = ((AgendaEntry) entry).getEvent();

            eventHolder.divider.setVisibility((CVDataList.get(position - 1) instanceof HeaderEntry) ? View.INVISIBLE : View.VISIBLE);

            eventHolder.date.setText(dateFormat.format(event.getStart()));
            eventHolder.subject.setText(getSubjectNameOrProfessorName(event, db));
            Spannable title = new SpannableString(event.getTitle());
            if (event.isCompleted()) {
                title.setSpan(new StrikethroughSpan(), 0, event.getTitle().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            eventHolder.title.setText(title);
            eventHolder.notes.setText(event.getNota_2());

            eventHolder.subject.setVisibility(TextUtils.isEmpty(eventHolder.subject.getText()) ? View.GONE : View.VISIBLE);
            eventHolder.notes.setVisibility((!event.getNota_2().trim().equalsIgnoreCase(event.getTitle().trim()) && !TextUtils.isEmpty(event.getNota_2())) ? View.VISIBLE : View.GONE);

            eventHolder.itemView.setOnClickListener((View v) -> {
                if (mClickListener != null)
                    mClickListener.onAgendaItemClicked(event, position);
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

    public void addAll(List<AdvancedEvent> events) {
        CVDataList = convert(events);
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

    private List<Entry> convert(List<AdvancedEvent> events) {
        LinkedHashMap<String, List<AdvancedEvent>> organized = new LinkedHashMap<>();
        for (AdvancedEvent e : events) {
            if (isEventTest(e)) {
                if (organized.containsKey(mContext.getString(R.string.verifiche))) {
                    List<AdvancedEvent> verifiche = new ArrayList<>(organized.get(mContext.getString(R.string.verifiche)));
                    verifiche.add(e);
                    organized.put(mContext.getString(R.string.verifiche), verifiche);
                } else {
                    organized.put(mContext.getString(R.string.verifiche), Collections.singletonList(e));
                }
            } else {
                if (organized.containsKey(mContext.getString(R.string.altri_eventi))) {
                    List<AdvancedEvent> otherEvents = new ArrayList<>(organized.get(mContext.getString(R.string.altri_eventi)));
                    otherEvents.add(e);
                    organized.put(mContext.getString(R.string.altri_eventi), otherEvents);
                } else {
                    organized.put(mContext.getString(R.string.altri_eventi), Collections.singletonList(e));
                }
            }
        }

        List<Entry> convert = new LinkedList<>();

        //Priorità alle verifiche
        if (organized.containsKey(mContext.getString(R.string.verifiche))) {
            convert.add(new HeaderEntry(mContext.getString(R.string.verifiche)));
            for (AdvancedEvent e : organized.get(mContext.getString(R.string.verifiche))) {
                convert.add(new AgendaEntry(e));
            }
            organized.remove(mContext.getString(R.string.verifiche));
        }

        for (String k : organized.keySet()) {
            convert.add(new HeaderEntry(k));
            for (AdvancedEvent e : organized.get(k)) {
                convert.add(new AgendaEntry(e));
            }
        }

        return convert;
    }

    public interface AgendaClickListener {
        void onAgendaItemClicked(AdvancedEvent e, int position);
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
