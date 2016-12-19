package com.sharpdroid.registro.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.sharpdroid.registro.API.SpiaggiariApiClient;
import com.sharpdroid.registro.Databases.SubjectsDB;
import com.sharpdroid.registro.Interfaces.Mark;
import com.sharpdroid.registro.Interfaces.MarkSubject;
import com.sharpdroid.registro.Interfaces.Media;
import com.sharpdroid.registro.Interfaces.Subject;
import com.sharpdroid.registro.R;
import com.sharpdroid.registro.Views.SubjectDetails.InfoView;
import com.sharpdroid.registro.Views.SubjectDetails.OverallView;
import com.sharpdroid.registro.Views.SubjectDetails.RecentLessonsView;
import com.sharpdroid.registro.Views.SubjectDetails.TargetView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sharpdroid.registro.Utils.Metodi.getSubjectName;

// DONE: 03/12/2016 Dettagli (nome, aula, prof, ora, note, colore)
// DONE: 03/12/2016 Media (scritto, orale, totale)
// DONE: 03/12/2016 Obiettivo
// TODO: 03/12/2016 Orario settimanale (in quali giorni)
// TODO: 03/12/2016 Verifiche prossime
// TODO: 03/12/2016 Voti recenti
// DONE: 14/12/2016 Lezioni recenti

public class MarkSubjectDetailActivity extends AppCompatActivity {
    private static final String TAG = MarkSubjectDetailActivity.class.getSimpleName();

    @BindView(R.id.info)
    InfoView infoView;
    @BindView(R.id.overall)
    OverallView overallView;
    @BindView(R.id.target)
    TargetView targetView;
    @BindView(R.id.lessons)
    RecentLessonsView lessonsView;

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
        setLessons(subject.getCode());
    }

    private void setLessons(int id) {
        new SpiaggiariApiClient(this).mService.getLessons(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lessons -> lessonsView.addAll(lessons), error -> {
                    // TODO: 16/12/2016 something went wrong
                });
    }

    private void setInfo(Subject subject) {
        infoView.setSubjectDetails(subject);
        infoView.setEditListener(view -> startActivity(new Intent(this, EditSubjectDetailsActivity.class).putExtra("code", subject.getCode())));
    }

    private void setTarget(Subject subject) {
        //set initial target
        float target = subject.getTarget();
        if (target <= 0) {
            target = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(this)
                    .getString("voto_obiettivo", "8"));
        }

        targetView.setTarget(target);

        //set progress
        targetView.setProgress(media.getMediaGenerale());

        //set listener for button
        targetView.setClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(getString(R.string.obiettivo_title));
            alert.setMessage(getString(R.string.obiettivo_summary));

            // Set an EditText view to get user input
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);

            alert.setView(input);
            alert.setPositiveButton("Ok", (dialog, whichButton) -> {
                try {
                    float new_target = Float.parseFloat(input.getText().toString());
                    if (new_target <= 10 && new_target >= 0) {
                        targetView.setTarget(new_target);

                        ContentValues values = new ContentValues();
                        values.put("target", input.getText().toString());
                        db.editTarget(subject.getCode(), values);
                    }
                } catch (NumberFormatException e) {
                    Log.d(TAG, "Could not parse " + e);
                }
            });

            alert.setNegativeButton("Cancel", (dialog, whichButton) -> {
                // Canceled. Do nothing;
            });
            alert.show();
        });


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
            overallView.setPratico(String.format(Locale.getDefault(), "%.2f", media.getMediaPratico()));
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
