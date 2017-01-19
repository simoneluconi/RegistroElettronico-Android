package com.sharpdroid.registroelettronico.Adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.Interfaces.API.Event;
import com.sharpdroid.registroelettronico.Interfaces.API.Mark;
import com.sharpdroid.registroelettronico.Interfaces.Client.Subject;
import com.sharpdroid.registroelettronico.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.sharpdroid.registroelettronico.Utils.Metodi.getMarkColor;
import static com.sharpdroid.registroelettronico.Utils.Metodi.isEventTest;
import static com.sharpdroid.registroelettronico.Utils.Metodi.sortMarksByDate;

public class MarkAdapter extends RecyclerView.Adapter<MarkAdapter.MarkHolder> {
    float target;
    private Context mContext;
    private SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy", Locale.ITALIAN);
    private List<Mark> CVDataList;
    private Subject subject;
    private List<Event> events;

    public MarkAdapter(Context mContext, Subject subject, List<Event> events) {
        this.mContext = mContext;
        CVDataList = new ArrayList<>();
        this.subject = subject;
        this.events = events;
    }

    public void addAll(List<Mark> list) {
        CVDataList = sortMarksByDate(list);
        notifyDataSetChanged();
    }

    public void clear() {
        CVDataList.clear();
        notifyDataSetChanged();
    }

    public void setTarget(float t) {
        target = t;
        notifyDataSetChanged();
    }

    @Override
    public MarkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MarkHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_mark, parent, false));
    }

    @Override
    public void onBindViewHolder(MarkHolder holder, int position) {

        Mark mark = CVDataList.get(position);
        holder.color.setImageDrawable(new ColorDrawable(ContextCompat.getColor(mContext, getMarkColor(mark, target))));
        holder.mark.setText(mark.getMark());

        Event event = getPossibleMarkDescription(events, mark.getDate(), subject);
        if (TextUtils.isEmpty(mark.getDesc())) {
            if (event != null)
                holder.content.setText(event.getTitle());
        } else holder.content.setText(mark.getDesc());

        holder.type.setText(mark.getType());
        holder.date.setText(format.format(mark.getDate()));
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
    }

    class MarkHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.color)
        CircleImageView color;
        @BindView(R.id.mark)
        TextView mark;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.type)
        TextView type;

        MarkHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private Event getPossibleMarkDescription(List<Event> events, Date date, Subject subject) {

        for (Event e : events) {

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(date);
            cal2.setTime(e.getStart());
            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

            if (isEventTest(e) && sameDay && isThisEventOfThisSubject(e, subject))
                return e;
        }

        return null;
    }

    private boolean isThisEventOfThisSubject(Event event, Subject subject) {
        List<String> valuesToSearch = new ArrayList<>();
        valuesToSearch.add(subject.getOriginalName()); //L'evento deve contenere o il nome della materia
        Collections.addAll(valuesToSearch, subject.getProfessors()); //o deve contenere il nome del professore della materia

        boolean b = false;

        for (String s : valuesToSearch)
            if (event.getTitle().contains(s))
                b = true;

        for (String s : valuesToSearch) {
            for (String s1 : s.toLowerCase().split("\\s+"))
                for (String s2 : event.getAutore_desc().toLowerCase().split("\\s+"))
                    if (s1.equals(s2) && s1.length() > 2 && s2.length() > 2)
                        b = true;
        }
        return b;
    }


}
