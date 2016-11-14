package com.sharpdroid.registro.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registro.Interfaces.Note;
import com.sharpdroid.registro.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter per RecyclerView con Note disciplinari & Annotazioni
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    private Context mContext;
    private List<Note> CVDataList;

    public NoteAdapter(Context context, List<Note> CVDataList) {
        this.mContext = context;
        this.CVDataList = CVDataList;
    }

    public void addAll(List<Note> note) {
        CVDataList = new ArrayList<>(note);
        notifyDataSetChanged();
    }

    public void clear() {
        CVDataList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public NoteAdapter(Context c) {
        this.mContext = c;
        CVDataList = new ArrayList<>();
    }

    @Override
    public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_note, parent, false);
        return new NoteHolder(v);
    }

    @Override
    public void onBindViewHolder(NoteHolder h, int position) {
        Note nota = CVDataList.get(position);

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
                        mContext.getResources().getColor(R.color.red_strong) :
                        mContext.getResources().getColor(android.R.color.primary_text_dark)
        );
        h.teacher.setTextColor(
                nota.getType().toLowerCase().contains("disciplinare") ?
                        mContext.getResources().getColor(R.color.red_strong) :
                        mContext.getResources().getColor(android.R.color.primary_text_dark)
        );

    }

    class NoteHolder extends RecyclerView.ViewHolder {
        TextView teacher, date, content, type;

        NoteHolder(View v) {
            super(v);

            teacher = (TextView) v.findViewById(R.id.teacher);
            date = (TextView) v.findViewById(R.id.date);
            content = (TextView) v.findViewById(R.id.content);
            type = (TextView) v.findViewById(R.id.type);
        }
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
    }
}
