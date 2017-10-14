package com.sharpdroid.registroelettronico.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orm.SugarRecord;
import com.sharpdroid.registroelettronico.Databases.Entities.Subject;
import com.sharpdroid.registroelettronico.Databases.Entities.Teacher;
import com.sharpdroid.registroelettronico.Fragments.FragmentSubjects;
import com.sharpdroid.registroelettronico.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registroelettronico.Utils.Metodi.capitalizeEach;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.SubjectHolder> {
    private List<Subject> CVDataList;
    private SubjectListener subjectListener = null;
    private Context c;

    public SubjectsAdapter(FragmentSubjects fragmentAgenda) {
        CVDataList = new LinkedList<>();
        if (fragmentAgenda != null) {
            this.subjectListener = fragmentAgenda;
            c = fragmentAgenda.getActivity();
        }
    }

    @Override
    public SubjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubjectHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_subject, parent, false));
    }

    @Override
    public void onBindViewHolder(SubjectHolder holder, int position) {
        Subject item = CVDataList.get(position);
        List<String> teachers = new ArrayList<>();
        for (Teacher t : item.getTeachers()) {
            teachers.add(t.getTeacherName());
        }
        holder.subject.setText(capitalizeEach(item.getDescription()));

        holder.prof.setVisibility(View.VISIBLE);
        holder.prof.setText(capitalizeEach(TextUtils.join(", ", teachers), true));

        holder.layout.setOnClickListener(view -> {
            if (subjectListener != null) {
                holder.layout.postDelayed(() -> subjectListener.onSubjectClick(item), ViewConfiguration.getTapTimeout());
            }
        });
    }

    @Override
    public int getItemCount() {
        return CVDataList.size();
    }

    public void addAll(List<Subject> subjects) {
        CVDataList = subjects;
        Collections.sort(CVDataList, (subject, t1) -> subject.getDescription().compareToIgnoreCase(t1.getDescription()));
        for (Subject s : subjects) {
            s.setTeachers(SugarRecord.findWithQuery(Teacher.class, "select * from TEACHER where TEACHER.ID IN (select SUBJECT_TEACHER.TEACHER from SUBJECT_TEACHER where SUBJECT_TEACHER.SUBJECT=?)", String.valueOf(s.getId())));
        }
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
