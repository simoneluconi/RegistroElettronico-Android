package com.sharpdroid.registro.Adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharpdroid.registro.Adapters.Holders.HeaderHolder;
import com.sharpdroid.registro.Interfaces.API.Absence;
import com.sharpdroid.registro.Interfaces.API.Absences;
import com.sharpdroid.registro.Interfaces.API.Delay;
import com.sharpdroid.registro.Interfaces.API.Exit;
import com.sharpdroid.registro.Interfaces.Client.AbsenceEntry;
import com.sharpdroid.registro.Interfaces.Client.DelayEntry;
import com.sharpdroid.registro.Interfaces.Client.Entry;
import com.sharpdroid.registro.Interfaces.Client.ExitEntry;
import com.sharpdroid.registro.Interfaces.Client.HeaderEntry;
import com.sharpdroid.registro.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.sharpdroid.registro.Utils.Metodi.convertAbsencesToHashmap;
import static com.sharpdroid.registro.Utils.Metodi.sortByDate;

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
            absencesHolder.divider.setVisibility((getItemViewType(position - 1) == 0) ? View.INVISIBLE : View.VISIBLE);

            if (entry instanceof DelayEntry) {
                Delay delay = ((DelayEntry) entry).getDelay();

                absencesHolder.date.setText(long_date_format.format(delay.getDay()));
                absencesHolder.hour.setText(mContext.getString(R.string.hour, "entrato", delay.getHour()));
                absencesHolder.hour.setText(mContext.getResources().getQuantityString(R.plurals.hours, delay.getHour(), "entrato", delay.getHour()));
                absencesHolder.done.setVisibility(delay.isDone() ? View.VISIBLE : View.INVISIBLE);
                absencesHolder.type_color.setImageDrawable(new ColorDrawable(ContextCompat.getColor(mContext, R.color.orangematerial)));
                absencesHolder.type_text.setText("R");
            } else if (entry instanceof AbsenceEntry) {
                Absence absence = ((AbsenceEntry) entry).getAbsence();

                absencesHolder.date.setText(long_date_format.format(absence.getFrom()));
                absencesHolder.hour.setText(mContext.getResources().getQuantityString(R.plurals.days, absence.getDays(), absence.getDays()));
                absencesHolder.done.setVisibility(absence.isDone() ? View.VISIBLE : View.INVISIBLE);
                absencesHolder.type_color.setImageDrawable(new ColorDrawable(ContextCompat.getColor(mContext, R.color.redmaterial)));
                absencesHolder.type_text.setText("A");
            } else {
                Exit exit = ((ExitEntry) entry).getExit();

                absencesHolder.date.setText(long_date_format.format(exit.getDay()));
                absencesHolder.hour.setText(mContext.getResources().getQuantityString(R.plurals.hours, exit.getHour(), "entrato", exit.getHour()));
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

    class AbsencesHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.hour)
        TextView hour;
        @BindView(R.id.type)
        TextView type_text;
        @BindView(R.id.done)
        ImageView done;
        @BindView(R.id.type_color)
        CircleImageView type_color;
        @BindView(R.id.divider)
        View divider;

        AbsencesHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
