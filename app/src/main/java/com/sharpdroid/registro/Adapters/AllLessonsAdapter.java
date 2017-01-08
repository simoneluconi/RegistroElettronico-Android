package com.sharpdroid.registro.Adapters;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharpdroid.registro.Adapters.Holders.HeaderHolder;
import com.sharpdroid.registro.Adapters.Holders.LessonHolder;
import com.sharpdroid.registro.Interfaces.API.Lesson;
import com.sharpdroid.registro.Interfaces.Client.Entry;
import com.sharpdroid.registro.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.sharpdroid.registro.Utils.Metodi.beautifyName;
import static com.sharpdroid.registro.Utils.Metodi.month_year;

public class AllLessonsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d", Locale.ITALIAN);

    private Context mContext;
    private int n_headers = 0, n_rows = 0;

    private List<Lesson> data;
    private List<Entry> types;

    public AllLessonsAdapter(Context mContext) {
        this.mContext = mContext;
        data = new ArrayList<>();
        types = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HeaderEntry.ID)
            return new HeaderHolder(LayoutInflater.from(mContext).inflate(HeaderEntry.ID, parent, false));
        else
            return new LessonHolder(LayoutInflater.from(mContext).inflate(LessonEntry.ID, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return types.get(position).getID();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).content.setText(beautifyName(((HeaderEntry) types.get(position)).getContent()));
        } else {
            LessonHolder lessonHolder = (LessonHolder) holder;

            if (getItemViewType(position - 1) == HeaderEntry.ID)
                lessonHolder.divider.setVisibility(View.INVISIBLE);
            else
                lessonHolder.divider.setVisibility(View.VISIBLE);

            LessonEntry entry = (LessonEntry) types.get(position);

            lessonHolder.content.setText(entry.getLesson().getContent());
            lessonHolder.date.setText(beautifyName(dateFormat.format(entry.getLesson().getDate())));
        }
    }

    public void addAll(List<Lesson> lessons) {
        data = lessons;
        elaborateList(lessons);
        notifyDataSetChanged();
    }

    private void elaborateList(List<Lesson> lessons) {
        HashMap<String, List<Lesson>> organizedLessons = new HashMap<>();

        for (Lesson lesson : lessons) {
            String date = month_year.format(lesson.getDate());

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
        data.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return n_headers + n_rows;
    }

    private class HeaderEntry extends Entry {
        @IdRes
        final static int ID = R.layout.adapter_header;
        private String content;

        HeaderEntry(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        @Override
        public int getID() {
            return ID;
        }
    }

    private class LessonEntry extends Entry {
        @IdRes
        final static int ID = R.layout.adapter_lessons_1;
        private Lesson lesson;

        LessonEntry(Lesson lesson) {
            this.lesson = lesson;
        }

        Lesson getLesson() {
            return lesson;
        }

        @Override
        public int getID() {
            return ID;
        }
    }
}