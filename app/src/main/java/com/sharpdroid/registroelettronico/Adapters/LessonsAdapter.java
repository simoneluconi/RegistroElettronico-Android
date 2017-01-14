package com.sharpdroid.registroelettronico.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.Adapters.Holders.LessonHolder;
import com.sharpdroid.registroelettronico.Interfaces.API.Lesson;
import com.sharpdroid.registroelettronico.R;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.sharpdroid.registroelettronico.Utils.Metodi.Delimeters;


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
        holder.content.setText(lesson.getContent().trim());
        holder.date.setText(formatter.format(lesson.getDate()));
    }

    public void addAll(Collection<Lesson> list) {
        lessons.addAll(list);
        Collections.reverse(lessons);
        notifyDataSetChanged();
    }

    public void clear() {
        lessons.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (lessons.size() > 5) ? 5 : lessons.size();
    }

}