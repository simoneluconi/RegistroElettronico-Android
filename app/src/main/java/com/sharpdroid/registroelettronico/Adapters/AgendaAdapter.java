package com.sharpdroid.registroelettronico.Adapters;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.Adapters.Holders.HeaderHolder;
import com.sharpdroid.registroelettronico.Interfaces.API.Event;
import com.sharpdroid.registroelettronico.Interfaces.Client.AgendaEntry;
import com.sharpdroid.registroelettronico.Interfaces.Client.Entry;
import com.sharpdroid.registroelettronico.Interfaces.Client.HeaderEntry;
import com.sharpdroid.registroelettronico.R;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registroelettronico.Utils.Metodi.Delimeters;
import static com.sharpdroid.registroelettronico.Utils.Metodi.convertCalendarEvents;
import static com.sharpdroid.registroelettronico.Utils.Metodi.isEventTest;

public class AgendaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private List<Entry> CVDataList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM", Locale.getDefault());
    private View place_holder;

    public AgendaAdapter(Context mContext, View ph) {
        this.mContext = mContext;
        CVDataList = new ArrayList<>();
        place_holder = ph;
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
            eventHolder.subject.setText(WordUtils.capitalizeFully(event.getAutore_desc().toLowerCase(), Delimeters));
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
        if (CVDataList.size() == 0) {
            place_holder.setVisibility(View.VISIBLE);
        } else {
            place_holder.setVisibility(View.GONE);
        }
        notifyDataSetChanged();
    }

    public void addAllCalendarEvents(List<com.github.sundeepk.compactcalendarview.domain.Event> events) {
        CVDataList = convert(convertCalendarEvents(events));
        if (CVDataList.size() == 0) {
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

    private List<Entry> convert(List<Event> events) {
        HashMap<String, List<Event>> organized = new HashMap<>();
        for (Event e : events) {
            if (isEventTest(e)) {
                if (organized.containsKey(mContext.getString(R.string.verifiche))) {
                    List<Event> verifiche = new ArrayList<>(organized.get(mContext.getString(R.string.verifiche)));
                    verifiche.add(e);
                    organized.put(mContext.getString(R.string.verifiche), verifiche);
                } else {
                    organized.put(mContext.getString(R.string.verifiche), Collections.singletonList(e));
                }
            } else {
                if (organized.containsKey(mContext.getString(R.string.altri_eventi))) {
                    List<Event> otherEvents = new ArrayList<>(organized.get(mContext.getString(R.string.altri_eventi)));
                    otherEvents.add(e);
                    organized.put(mContext.getString(R.string.altri_eventi), otherEvents);
                } else {
                    organized.put(mContext.getString(R.string.altri_eventi), Collections.singletonList(e));
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

    private void addEventToCalendar(Event event) {
        Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setType("vnd.android.cursor.item/event");
        calIntent.putExtra(CalendarContract.Events.TITLE, event.getAutore_desc());
        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, event.getTitle());
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, event.isAllDay());
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getStart().getTime());
        if (!event.isAllDay())
            calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getEnd().getTime());
        mContext.startActivity(calIntent);
    }

    class EventHolder extends RecyclerView.ViewHolder {
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

            itemView.setOnLongClickListener((View v) -> {
                Entry e = CVDataList.get(getAdapterPosition());
                Event event = ((AgendaEntry) e).getEvent();
                addEventToCalendar(event);
                return false;
            });
        }
    }
}
