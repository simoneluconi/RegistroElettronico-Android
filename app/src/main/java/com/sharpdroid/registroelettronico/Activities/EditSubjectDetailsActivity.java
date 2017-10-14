package com.sharpdroid.registroelettronico.Activities;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.orm.SugarRecord;
import com.sharpdroid.registroelettronico.Adapters.Holders.Holder;
import com.sharpdroid.registroelettronico.Databases.Entities.Subject;
import com.sharpdroid.registroelettronico.Databases.Entities.Teacher;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Utils.Metodi;
import com.sharpdroid.registroelettronico.Views.Cells.HeaderCell;
import com.sharpdroid.registroelettronico.Views.Cells.ShadowCell;
import com.sharpdroid.registroelettronico.Views.Cells.ValueDetailsCell;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditSubjectDetailsActivity extends AppCompatActivity {

    @BindView(R.id.recycler)
    RecyclerView recycler;
    Subject subject;
    EditSubjectAdapter adapter;
    private int rowCount;
    private int rowInfo;
    private int rowTitle;
    private int rowNotes;
    private int rowSeparator;
    private int rowTeachers;
    private int rowTeachers1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_subject_details);
        ButterKnife.bind(this);

        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        init(getIntent().getLongExtra("code", -1));

    }

    void init(long code) {
        if (code == -1) return;

        subject = SugarRecord.findById(Subject.class, code);
        subject.setTeachers(SugarRecord.findWithQuery(Teacher.class, "select * from TEACHER where TEACHER.ID IN (select SUBJECT_TEACHER.TEACHER from SUBJECT_TEACHER where SUBJECT_TEACHER.SUBJECT=?)", String.valueOf(subject.getId())));
        setTitle(Metodi.capitalizeEach(subject.getDescription()));

        rowCount = 0;
        rowInfo = rowCount++;
        rowTitle = rowCount++;
        rowNotes = rowCount++;
        rowSeparator = rowCount++;
        rowTeachers = rowCount++;

        rowCount += subject.getTeachers().size();

        adapter = new EditSubjectAdapter();
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    void apply() {
        SugarRecord.update(subject);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) super.onBackPressed();
        if (item.getItemId() == R.id.apply) apply();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_subject_menu, menu);
        return true;
    }

    private class EditSubjectAdapter extends RecyclerView.Adapter {
        View v = null;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    v = new HeaderCell(parent.getContext());
                    v.setBackgroundColor(Color.WHITE);
                    break;
                case 1:
                    v = new ValueDetailsCell(parent.getContext());
                    v.setBackgroundColor(Color.WHITE);
                    break;
                case 2:
                    v = new ShadowCell(parent.getContext());
                    break;
            }
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case 0:
                    ((HeaderCell) holder.itemView).setText((position == rowInfo) ? "Informazioni" : "Professori");
                    break;
                case 1:
                    if (position == rowTitle)
                        ((ValueDetailsCell) holder.itemView).setTextAndValue(Metodi.capitalizeEach(subject.getDescription()), "Nome", true);
                    else if (position == rowNotes)
                        ((ValueDetailsCell) holder.itemView).setTextAndValue(Metodi.capitalizeEach(subject.getDetails()), "Dettagli", true);
                    else
                        ((ValueDetailsCell) holder.itemView).setText(Metodi.capitalizeEach(subject.getTeachers().get(position - rowTeachers - 1).getTeacherName()), true);


                    break;
            }
            if (getItemViewType(position) == 1) {
                int[] attrs = new int[]{android.R.attr.selectableItemBackground};
                TypedArray ta = obtainStyledAttributes(attrs);
                Drawable drawableFromTheme = ta.getDrawable(0);
                LayerDrawable wrapper = new LayerDrawable(new Drawable[]{new ColorDrawable(Color.WHITE), drawableFromTheme});
                ta.recycle();
                holder.itemView.setBackgroundDrawable(wrapper);
                holder.itemView.setOnClickListener(view -> {

                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == rowInfo || position == rowTeachers)
                return 0;
            else if (position == rowSeparator)
                return 2;
            else return 1;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }
    }
}
