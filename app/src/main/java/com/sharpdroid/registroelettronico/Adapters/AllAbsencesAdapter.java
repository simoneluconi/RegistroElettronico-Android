package com.sharpdroid.registroelettronico.Adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.Adapters.Holders.AbsencesHolder;
import com.sharpdroid.registroelettronico.Adapters.Holders.HeaderHolder;
import com.sharpdroid.registroelettronico.Interfaces.API.Absence;
import com.sharpdroid.registroelettronico.Interfaces.API.Absences;
import com.sharpdroid.registroelettronico.Interfaces.API.Delay;
import com.sharpdroid.registroelettronico.Interfaces.API.Exit;
import com.sharpdroid.registroelettronico.Interfaces.Client.AbsenceEntry;
import com.sharpdroid.registroelettronico.Interfaces.Client.DelayEntry;
import com.sharpdroid.registroelettronico.Interfaces.Client.Entry;
import com.sharpdroid.registroelettronico.Interfaces.Client.ExitEntry;
import com.sharpdroid.registroelettronico.Interfaces.Client.HeaderEntry;
import com.sharpdroid.registroelettronico.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.sharpdroid.registroelettronico.Utils.Metodi.capitalizeFirst;
import static com.sharpdroid.registroelettronico.Utils.Metodi.convertAbsencesToHashmap;
import static com.sharpdroid.registroelettronico.Utils.Metodi.sortByDate;

public class AllAbsencesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private SimpleDateFormat long_date_format = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ITALIAN);

    private List<Entry> CVDataList;

    public AllAbsencesAdapter(Context mContext) {
        this.mContext = mContext;
        CVDataList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new HeaderHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_header, parent, false));
        else
            return new AbsencesHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_absence, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Entry entry = CVDataList.get(position);
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).content.setText(((HeaderEntry) entry).getTitle());
        } else {
            AbsencesHolder absencesHolder = (AbsencesHolder) holder;

            if (entry instanceof DelayEntry) {
                Delay delay = ((DelayEntry) entry).getDelay();

                absencesHolder.date.setText(capitalizeFirst(long_date_format.format(delay.getDay())));
                absencesHolder.hour.setText(delay.getHour() == 0 ? mContext.getResources().getString(R.string.short_delay) : mContext.getResources().getString(R.string.hours, "entrato", delay.getHour()));
                absencesHolder.done.setVisibility(delay.isDone() ? View.VISIBLE : View.INVISIBLE);
                absencesHolder.type_color.setImageDrawable(new ColorDrawable(ContextCompat.getColor(mContext, R.color.orangematerial)));
                absencesHolder.type_text.setText(delay.getHour() == 0 ? "RB" : "R");
            } else if (entry instanceof AbsenceEntry) {
                Absence absence = ((AbsenceEntry) entry).getAbsence();

                absencesHolder.date.setText(capitalizeFirst(long_date_format.format(absence.getFrom())));
                absencesHolder.hour.setText(mContext.getResources().getQuantityString(R.plurals.days, absence.getDays(), absence.getDays()));
                absencesHolder.done.setVisibility(absence.isDone() ? View.VISIBLE : View.INVISIBLE);
                absencesHolder.type_color.setImageDrawable(new ColorDrawable(ContextCompat.getColor(mContext, R.color.redmaterial)));
                absencesHolder.type_text.setText("A");
            } else {
                Exit exit = ((ExitEntry) entry).getExit();

                absencesHolder.date.setText(capitalizeFirst(long_date_format.format(exit.getDay())));
                absencesHolder.hour.setText(mContext.getResources().getString(R.string.hours, "uscito", exit.getHour()));
                absencesHolder.done.setVisibility(exit.isDone() ? View.VISIBLE : View.INVISIBLE);
                absencesHolder.type_color.setImageDrawable(new ColorDrawable(ContextCompat.getColor(mContext, R.color.bluematerial)));
                absencesHolder.type_text.setText("U");
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (CVDataList.get(position) instanceof HeaderEntry) ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
    }

    private List<Entry> convertToChronologic(Absences absences) {
        List<Entry> chronologic = new ArrayList<>();

        Map<String, List<Entry>> hashMap = sortByDate(convertAbsencesToHashmap(absences));

        for (String header : hashMap.keySet()) {
            chronologic.add(new HeaderEntry(header));
            chronologic.addAll(hashMap.get(header));
        }
        return chronologic;
    }

    public void addAll(Absences absences) {
        CVDataList = convertToChronologic(absences);
        notifyDataSetChanged();
    }

    public void clear() {
        CVDataList.clear();
        notifyDataSetChanged();
    }
}
