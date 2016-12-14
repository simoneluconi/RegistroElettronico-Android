package com.sharpdroid.registro.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.sharpdroid.registro.Databases.SubjectsDB;
import com.sharpdroid.registro.Interfaces.Mark;
import com.sharpdroid.registro.Interfaces.MarkSubject;
import com.sharpdroid.registro.Interfaces.Media;
import com.sharpdroid.registro.Interfaces.Subject;
import com.sharpdroid.registro.R;
import com.sharpdroid.registro.Views.SubjectDetails.InfoView;
import com.sharpdroid.registro.Views.SubjectDetails.OverallView;
import com.sharpdroid.registro.Views.SubjectDetails.TargetView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registro.Utils.Metodi.getSubjectName;

// TODO: 03/12/2016 Dettagli (nome, aula, prof, ora, note, colore)
// DONE: 03/12/2016 Media (scritto, orale, totale)
// TODO: 03/12/2016 Obiettivo
// TODO: 03/12/2016 Orario settimanale (in quali giorni)
// TODO: 03/12/2016 Verifiche prossime
// TODO: 03/12/2016 Voti recenti
// TODO: 14/12/2016 Lezioni recenti

public class MarkSubjectDetailActivity extends AppCompatActivity {

    private static final String TAG = MarkSubjectDetailActivity.class.getSimpleName();
    @BindView(R.id.info)
    InfoView infoView;
    @BindView(R.id.overall)
    OverallView overallView;
    @BindView(R.id.target)
    TargetView targetView;

    MarkSubject data;
    SubjectsDB db;
    Subject subject;
    Media media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_subject_detail);

        ButterKnife.bind(this);

        //get DATA
        data = (MarkSubject) getIntent().getSerializableExtra("data");

        //DATABASE
        db = SubjectsDB.from(this);

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        subject = db.getSubject(data.getName());
        setTitle(getSubjectName(subject));

        setInfo(subject);
        setOverall(data.getMarks());
        setTarget(subject);
    }

    private void setInfo(Subject subject) {
        infoView.setSubjectDetails(subject);
        infoView.setEditListener(view -> startActivity(new Intent(this, EditSubjectDetailsActivity.class).putExtra("code", subject.getCode())));
    }

    private void setTarget(Subject subject) {
        targetView.setProgress(media.getMediaGenerale());
        try {
            targetView.setTarget(subject.getTarget());
        } catch (NullPointerException e) {
            float target = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(this)
                    .getString("voto_obiettivo", "8"));
            targetView.setTarget(target);
        }
        // TODO: 13/12/2016 OPEN DIALOG
        targetView.setClickListener(view -> targetView.setTarget(8f));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    void setOverall(List<Mark> marks) {
        media = new Media();
        media.addMarks(marks);
        try {
            overallView.setMedia(media.getMediaGenerale());
        } catch (Exception ignored) {
        }
        try {
            overallView.setOrale(String.format(Locale.getDefault(), "%.2f", media.getMediaOrale()));
        } catch (Exception ignored) {
        }
        try {
            overallView.setScritto(String.format(Locale.getDefault(), "%.2f", media.getMediaScritto()));
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onStop() {
        //CLOSE DATABASE
        db.close();
        super.onStop();
    }
}
