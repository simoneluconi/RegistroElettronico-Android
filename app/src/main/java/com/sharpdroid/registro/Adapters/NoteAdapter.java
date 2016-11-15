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

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Adapter per RecyclerView con Note disciplinari & Annotazioni
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
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

        String date = nota.getDate().split("T")[0];

        h.teacher.setText(nota.getTeacher());
        h.date.setText(date);
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
            h.type.setTextColor(ContextCompat.getColor(mContext, android.R.color.primary_text_dark));
            h.teacher.setTextColor(ContextCompat.getColor(mContext, android.R.color.primary_text_dark));
        }
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        final TextView teacher;
        final TextView date;
        final TextView content;
        final TextView type;

        NoteHolder(View v) {
            super(v);

            teacher = (TextView) v.findViewById(R.id.teacher);
            date = (TextView) v.findViewById(R.id.date);
            content = (TextView) v.findViewById(R.id.content);
            type = (TextView) v.findViewById(R.id.type);
        }
    }
}
