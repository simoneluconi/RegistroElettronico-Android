package com.sharpdroid.registroelettronico.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.Databases.Entities.Note;
import com.sharpdroid.registroelettronico.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter per RecyclerView con Note disciplinari & Annotazioni
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    private final SimpleDateFormat formatter = new SimpleDateFormat("d MMM", Locale.ITALIAN);

    private final List<Note> CVDataList = new ArrayList<>();
    private final Context mContext;

    public NoteAdapter(Context context) {
        this.mContext = context;
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

        h.teacher.setText(nota.getMAuthor());
        h.date.setText(formatter.format(nota.getMDate()));
        h.content.setText(nota.getMText());

        if (nota.getMType().toLowerCase(Locale.getDefault()).contains("NTST")) {
            h.teacher.setTextColor(ContextCompat.getColor(mContext, R.color.deep_orange));
            h.icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_warning_orange));
        } else {
            h.teacher.setTextColor(ContextCompat.getColor(mContext, R.color.grey_middle));
            h.icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_error_grey));
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
        @BindView(R.id.icon)
        ImageView icon;

        NoteHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
