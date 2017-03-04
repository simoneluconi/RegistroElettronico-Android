package com.sharpdroid.registroelettronico.Activities;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import com.sharpdroid.registroelettronico.Databases.SubjectsDB;
import com.sharpdroid.registroelettronico.Interfaces.Client.Subject;
import com.sharpdroid.registroelettronico.R;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registroelettronico.Utils.Metodi.Delimeters;
import static com.sharpdroid.registroelettronico.Utils.Metodi.getSubjectName;

public class EditSubjectDetailsActivity extends AppCompatActivity {

    @BindView(R.id.name)
    TextInputEditText name;
    @BindView(R.id.professor)
    TextInputEditText prof;
    @BindView(R.id.professor2)
    TextInputEditText prof2;
    @BindView(R.id.classroom)
    TextInputEditText classroom;
    @BindView(R.id.notes)
    TextInputEditText notes;

    Subject subject;
    SubjectsDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_subject_details);
        ButterKnife.bind(this);

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        db = new SubjectsDB(this);

        name.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_title), null, null, null);
        classroom.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_room), null, null, null);
        notes.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_description), null, null, null);
        prof.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_person_black), null, null, null);
        prof2.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_person_black), null, null, null);

        notes.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                apply();
                return true;
            }
            return false;
        });

        init(getIntent().getIntExtra("code", -1));
    }

    void init(int code) {
        if (code != -1) {
            subject = db.getSubject(code);

            name.setText(getSubjectName(subject));
            if (subject.getProfessor() != null) {
                String[] p = subject.getProfessors();
                if (p.length > 0)
                    prof.setText(WordUtils.capitalizeFully(p[0].trim(), Delimeters));
                if (p.length > 1)
                    prof2.setText(WordUtils.capitalizeFully(p[1].trim(), Delimeters));
            }
            if (subject.getClassroom() != null)
                classroom.setText(subject.getClassroom());
            if (subject.getNotes() != null)
                notes.setText(subject.getNotes());
        }
    }

    void apply() {
        ContentValues values = new ContentValues();

        String name, prof, classroom, notes;

        name = this.name.getText().toString().trim();
        prof = getProfsNames();
        classroom = this.classroom.getText().toString().trim();
        notes = this.notes.getText().toString().trim();

        values.put("name", name);
        values.put("professor", prof);
        values.put("classroom", classroom);
        values.put("notes", notes);

        db.editSubject(subject.getCode(), values);

        finish();
    }

    private String getProfsNames() {
        List<String> pr = new ArrayList<>();
        if (!TextUtils.isEmpty(prof.getText().toString()))
            pr.add(prof.getText().toString());

        if (!TextUtils.isEmpty(prof2.getText().toString()))
            pr.add(prof2.getText().toString());

        if (!pr.isEmpty())
            return StringUtils.join(pr, " ~ ");
        else return "";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        if (item.getItemId() == R.id.apply) apply();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_subject_menu, menu);
        return true;
    }

    @Override
    protected void onStop() {
        db.close();
        super.onStop();
    }
}
