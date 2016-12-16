package com.sharpdroid.registro.Adapters;

import android.content.Context;
import android.support.v7.view.CollapsibleActionView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registro.Interfaces.Lesson;
import com.sharpdroid.registro.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registro.Utils.Metodi.beautifyName;

public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.LessonHolder> {
    private final SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM", Locale.getDefault());

    private Context mContext;

    private List<Lesson> lessons;

    public LessonsAdapter(Context mContext) {
        this.mContext = mContext;
        lessons = new ArrayList<>();
    }

    @Override
    public LessonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LessonHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_lessons, parent, false));
    }

    @Override
    public void onBindViewHolder(LessonHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.content.setText(beautifyName(lesson.getContent().trim()));
        try {
            holder.date.setText(
                    dateFormat.format(
                            apiFormat.parse(
                                    lesson.getDate().split("T")[0]
                            )
                    ));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void addAll(List<Lesson> list) {
        lessons = list;
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

    class LessonHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.date)
        TextView date;

        LessonHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}