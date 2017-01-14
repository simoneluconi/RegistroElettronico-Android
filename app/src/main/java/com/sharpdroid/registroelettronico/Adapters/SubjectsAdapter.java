package com.sharpdroid.registroelettronico.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharpdroid.registroelettronico.Activities.AllLessonsWithDownloadActivity;
import com.sharpdroid.registroelettronico.Adapters.Holders.AbsencesHolder;
import com.sharpdroid.registroelettronico.Interfaces.Client.Subject;
import com.sharpdroid.registroelettronico.R;

import org.apache.commons.lang3.text.WordUtils;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.sharpdroid.registroelettronico.Utils.Metodi.getSubjectName;
import static com.sharpdroid.registroelettronico.Utils.Metodi.material_colors;

public class SubjectsAdapter extends RecyclerView.Adapter<AbsencesHolder> {
    private Context mContext;

    private List<Subject> CVDataList;
    private Random random;

    public SubjectsAdapter(Context mContext) {
        this.mContext = mContext;
        CVDataList = new LinkedList<>();
        long time = new Date().getTime();
        random = new Random(time);
        Log.d("ADAPTER", "" + time);
    }

    @Override
    public AbsencesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AbsencesHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_absence, parent, false));
    }

    @Override
    public void onBindViewHolder(AbsencesHolder holder, int position) {
        Subject item = CVDataList.get(position);

        holder.done.setVisibility(View.GONE);
        holder.type_color.setImageDrawable(new ColorDrawable(material_colors[random.nextInt(material_colors.length - 1)]));

        holder.type_text.setText(getSubjectName(item).substring(0, 1).toUpperCase());
        holder.divider.setVisibility((position == 0) ? View.INVISIBLE : View.VISIBLE);
        holder.date.setText(getSubjectName(item));
        if (TextUtils.isEmpty(item.getProfessor())) {   //non visualizzare la textview se non serve
            holder.hour.setVisibility(View.GONE);
        } else {
            holder.hour.setVisibility(View.VISIBLE);
            holder.hour.setText(WordUtils.capitalizeFully(item.getProfessor()));
        }
        holder.layout.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, AllLessonsWithDownloadActivity.class);
            intent.putExtra("code", item.getCode());
            intent.putExtra("name", holder.date.getText());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
    }

    public void addAll(List<Subject> subjects) {
        CVDataList = subjects;
        Collections.sort(CVDataList, (subject, t1) -> getSubjectName(subject).compareToIgnoreCase(getSubjectName(t1)));
        notifyDataSetChanged();
    }

    public void clear() {
        CVDataList.clear();
        notifyDataSetChanged();
    }
}
