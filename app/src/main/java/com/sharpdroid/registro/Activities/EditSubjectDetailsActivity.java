package com.sharpdroid.registro.Activities;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import com.sharpdroid.registro.Databases.SubjectsDB;
import com.sharpdroid.registro.Interfaces.Subject;
import com.sharpdroid.registro.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registro.Utils.Metodi.NomeDecente;
import static com.sharpdroid.registro.Utils.Metodi.beautifyName;

public class EditSubjectDetailsActivity extends AppCompatActivity {

    @BindView(R.id.name)
    TextInputEditText name;
    @BindView(R.id.professor)
    TextInputEditText prof;
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

        db = SubjectsDB.from(this);

        name.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_title), null, null, null);
        prof.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_person_black), null, null, null);
        classroom.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_room), null, null, null);
        notes.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_description), null, null, null);

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

            if (subject.getName() != null)
                name.setText(subject.getName());
            else
                name.setText(subject.getOriginalName());
            if (subject.getProfessor() != null)
                prof.setText(subject.getProfessor());
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
        prof = this.prof.getText().toString().trim();
        classroom = this.classroom.getText().toString().trim();
        notes = this.notes.getText().toString().trim();

        values.put("name", beautifyName(name));
        values.put("professor", NomeDecente(prof));
        values.put("classroom", classroom);
        values.put("notes", beautifyName(notes));

        db.editSubject(subject.getCode(), values);

        finish();
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