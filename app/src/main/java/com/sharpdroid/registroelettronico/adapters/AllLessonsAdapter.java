package com.sharpdroid.registroelettronico.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.adapters.holders.HeaderHolder;
import com.sharpdroid.registroelettronico.adapters.holders.LessonHolder;
import com.sharpdroid.registroelettronico.database.entities.Lesson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach;
import static com.sharpdroid.registroelettronico.utils.Metodi.month_year;

public class AllLessonsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d", Locale.ITALIAN);

    private Context mContext;
    private int n_headers = 0, n_rows = 0;

    private List<Object> types = new ArrayList<>();

    public AllLessonsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == R.layout.adapter_header)
            return new HeaderHolder(LayoutInflater.from(mContext).inflate(viewType, parent, false));
        else
            return new LessonHolder(LayoutInflater.from(mContext).inflate(viewType, parent, false));
    }

    @Override
    public
    @LayoutRes
    int getItemViewType(int position) {
        return (types.get(position) instanceof HeaderEntry) ? R.layout.adapter_header : R.layout.adapter_lessons_1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).content.setText(((HeaderEntry) types.get(position)).getContent());
        } else {
            LessonHolder lessonHolder = (LessonHolder) holder;

            LessonEntry entry = (LessonEntry) types.get(position);

            lessonHolder.content.setText(entry.getLesson().getMArgument());
            lessonHolder.date.setText(capitalizeEach(dateFormat.format(entry.getLesson().getMDate())));
        }
    }

    public void addAll(List<Lesson> lessons) {
        elaborateList(lessons);
        notifyDataSetChanged();
    }

    private void elaborateList(List<Lesson> lessons) {
        HashMap<String, List<Lesson>> organizedLessons = new HashMap<>();

        for (Lesson lesson : lessons) {
            String date = month_year.format(lesson.getMDate());

            //organizza nella stessa lista se sono dello stesso mese
            if (organizedLessons.containsKey(date)) {
                List<Lesson> lessons1 = new ArrayList<>(organizedLessons.get(date));
                lessons1.add(lesson);
                organizedLessons.put(date, lessons1);
            } else {
                organizedLessons.put(date, Collections.singletonList(lesson));
                n_headers++;
                types.add(new HeaderEntry(date));
            }
            n_rows++;
            types.add(new LessonEntry(lesson));
        }
    }

    public void clear() {
        types.clear();
        n_headers = 0;
        n_rows = 0;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return n_headers + n_rows;
    }

    private class HeaderEntry {
        private String content;

        HeaderEntry(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }

    private class LessonEntry {
        private Lesson lesson;

        LessonEntry(Lesson lesson) {
            this.lesson = lesson;
        }

        Lesson getLesson() {
            return lesson;
        }
    }
}
