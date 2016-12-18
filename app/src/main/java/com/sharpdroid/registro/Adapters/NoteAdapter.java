package com.sharpdroid.registro.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registro.Interfaces.Note;
import com.sharpdroid.registro.R;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter per RecyclerView con Note disciplinari & Annotazioni
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    private final SimpleDateFormat format = new SimpleDateFormat("d MMM", Locale.getDefault());

    private final List<Note> CVDataList;
    private final Context mContext;

    public NoteAdapter(Context context, List<Note> CVDataList) {
        this.mContext = context;
        this.CVDataList = CVDataList;
    }

    public void addAll(Collection<Note> note) {
        CVDataList.addAll(note);
        notifyDataSetChanged();
    }

    public void clear() {
        CVDataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_note, parent, false);
        return new NoteHolder(v);
    }

    @Override
    public void onBindViewHolder(NoteHolder h, int position) {
        final Note nota = CVDataList.get(position);

        h.teacher.setText(nota.getTeacher());
        h.date.setText(format.format(nota.getDate()));
        h.content.setText(nota.getContent());
        h.type.setText(nota.getType());

        /**
         * Rosso: nota disciplinare
         * Dark: annotazione
         */
        if (nota.getType().toLowerCase(Locale.getDefault()).contains("disciplinare")) {
            h.type.setTextColor(ContextCompat.getColor(mContext, R.color.red_strong));
            h.teacher.setTextColor(ContextCompat.getColor(mContext, R.color.red_strong));
        } else {
            h.type.setTextColor(ContextCompat.getColor(mContext, android.R.color.primary_text_light));
            h.teacher.setTextColor(ContextCompat.getColor(mContext, android.R.color.primary_text_light));
        }
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.teacher)
        TextView teacher;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.type)
        TextView type;

        NoteHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
