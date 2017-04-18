package com.sharpdroid.registroelettronico.Adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.Interfaces.Client.Subject;
import com.sharpdroid.registroelettronico.R;

import org.apache.commons.lang3.text.WordUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registroelettronico.Utils.Metodi.Delimeters;
import static com.sharpdroid.registroelettronico.Utils.Metodi.getSubjectName;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.SubjectHolder> {
    private List<Subject> CVDataList;
    private SubjectListener subjectListener = null;

    public SubjectsAdapter(SubjectListener subjectListener) {
        CVDataList = new LinkedList<>();
        this.subjectListener = subjectListener;
    }

    @Override
    public SubjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubjectHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_subject, parent, false));
    }

    @Override
    public void onBindViewHolder(SubjectHolder holder, int position) {
        Subject item = CVDataList.get(position);

        holder.subject.setText(getSubjectName(item));
        if (TextUtils.isEmpty(item.getProfessor())) {   //non visualizzare la textview se non serve
            holder.prof.setVisibility(View.GONE);
        } else {
            holder.prof.setVisibility(View.VISIBLE);
            holder.prof.setText(WordUtils.capitalizeFully(TextUtils.join(" - ", item.getProfessors()), Delimeters));
        }
        holder.layout.setOnClickListener(view -> {
            if (subjectListener != null) subjectListener.onSubjectClick(item);
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

    public interface SubjectListener {
        void onSubjectClick(Subject subject);
    }

    protected class SubjectHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.subject)
        TextView subject;
        @BindView(R.id.professor)
        TextView prof;
        @BindView(R.id.layout)
        View layout;

        public SubjectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
