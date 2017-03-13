package com.sharpdroid.registroelettronico.Activities;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sharpdroid.registroelettronico.Databases.SubjectsDB;
import com.sharpdroid.registroelettronico.Interfaces.Client.Subject;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Views.LocalEvent.OptionView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registroelettronico.Utils.Metodi.getCodesFromSubjects;
import static com.sharpdroid.registroelettronico.Utils.Metodi.getNamesFromSubjects;
import static com.sharpdroid.registroelettronico.Utils.Metodi.pairToFirst;
import static com.sharpdroid.registroelettronico.Utils.Metodi.pairToSecond;
import static com.sharpdroid.registroelettronico.Utils.Metodi.toIntArray;

public class AddEventActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.layout_verifica)
    TextInputLayout title;
    @BindView(R.id.options)
    LinearLayout options;

    SubjectsDB subjectsDB;

    int selectedSubject = -1;
    int selectedProfessor = -1;
    int selectedSubjectCode = -1;
    int selectedProfessorCode = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        ButterKnife.bind(this);
        subjectsDB = new SubjectsDB(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init(getIntent().getStringExtra("type"));

    }

    private void init(String type) {
        setTitle(type);

        switch (type.toLowerCase()) {
            case "verifica":
                Log.v("TYPE", type);
                initVerifica();
                break;
            case "compiti":
                initCompiti();
                break;
            default:
                initDefault();
                break;
        }
    }

    private void initDefault() {
    }

    private void initCompiti() {

    }

    private void initVerifica() {
        options.addView(new OptionView.Builder(this).title("Materia").image(R.drawable.event_subject).onClick(this::subjectDialog).build());
        options.addView(new OptionView.Builder(this).title("Professore").image(R.drawable.event_professor).onClick(this::professorDialog).build());

    }


    /**
     * click listener for option 'Subject'
     *
     * @param v the layout clicked
     */
    public void subjectDialog(View v) {
        List<Subject> subjectList;

        if (selectedProfessorCode != -1 && selectedSubjectCode == -1) {      //Se è stato selezionato un professore ma non la materia

        }

        subjectList = subjectsDB.getSubjects();

        new MaterialDialog.Builder(this)
                .title("Seleziona una materia")
                .items(getNamesFromSubjects(subjectList))
                .itemsIds(getCodesFromSubjects(subjectList))
                .itemsCallbackSingleChoice(selectedSubject, (dialog, view, which, text) -> {
                    selectedSubject = which;
                    selectedSubjectCode = view.getId();
                    ((TextView) v.findViewById(R.id.content)).setText(text);
                    return true;
                })
                .show();
    }

    public void professorDialog(View v) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(this)
                .title("Seleziona un professore");

        List<Pair<Integer, String>> professors = subjectsDB.getProfessors();
        List<String> professorsNames = pairToSecond(professors);
        List<Integer> professorsCodes = pairToFirst(professors);

        dialog
                .items(professorsNames)
                .itemsIds(toIntArray(professorsCodes))
                .itemsCallbackSingleChoice(selectedProfessor, (dialog1, view, which, text) -> {
                    selectedProfessor = which;
                    selectedProfessorCode = view.getId();
                    ((TextView) v.findViewById(R.id.content)).setText(text);
                    return true;
                }).show();
    }
    //}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title.setHint(title);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subjectsDB.close();
    }
}
