package com.sharpdroid.registro.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registro.Interfaces.Note;
import com.sharpdroid.registro.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter per RecyclerView con Note disciplinari & Annotazioni
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    Context c;
    List<Note> noteList;
    SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM", Locale.getDefault());

    public void addAll(List<Note> note) {
        noteList = new ArrayList<>(note);
        notifyDataSetChanged();
    }

    public void clearAll() {
        noteList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public NoteAdapter(Context c) {
        this.c = c;
        noteList = new ArrayList<>();
    }

    @Override
    public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.adapter_note, parent, false);
        return new NoteHolder(v);
    }

    @Override
    public void onBindViewHolder(NoteHolder h, int position) {
        Note nota = noteList.get(position);

        String date = nota.getDate().split("T")[0];

        h.teacher.setText(nota.getTeacher());
        h.date.setText(date);
        h.content.setText(nota.getContent());
        h.type.setText(nota.getType());

        /**
         * Rosso: nota disciplinare
         * Dark: annotazione
         */
        h.type.setTextColor(
                nota.getType().toLowerCase().contains("disciplinare") ?
                        c.getResources().getColor(R.color.red_strong) :
                        c.getResources().getColor(android.R.color.primary_text_dark)
        );
        h.teacher.setTextColor(
                nota.getType().toLowerCase().contains("disciplinare") ?
                        c.getResources().getColor(R.color.red_strong) :
                        c.getResources().getColor(android.R.color.primary_text_dark)
        );

    }

    class NoteHolder extends RecyclerView.ViewHolder {
        TextView teacher, date, content, type;

        public NoteHolder(View v) {
            super(v);

            teacher = (TextView) v.findViewById(R.id.teacher);
            date = (TextView) v.findViewById(R.id.date);
            content = (TextView) v.findViewById(R.id.content);
            type = (TextView) v.findViewById(R.id.type);
        }
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
