package com.sharpdroid.registroelettronico.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.adapters.Holders.LessonHolder;
import com.sharpdroid.registroelettronico.database.entities.Lesson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class LessonsAdapter extends RecyclerView.Adapter<LessonHolder> {
    private final SimpleDateFormat formatter = new SimpleDateFormat("d MMM", Locale.ITALIAN);

    private Context mContext;

    private List<Lesson> lessons = new ArrayList<>();

    public LessonsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public LessonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LessonHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_lessons, parent, false));
    }

    @Override
    public void onBindViewHolder(LessonHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.content.setText(lesson.getMArgument().trim());
        holder.date.setText(formatter.format(lesson.getMDate()));
    }

    public void addAll(Collection<Lesson> list) {
        lessons.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        lessons.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }
}