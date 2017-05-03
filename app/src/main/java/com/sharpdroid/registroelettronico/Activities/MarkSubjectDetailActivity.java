package com.sharpdroid.registroelettronico.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sharpdroid.registroelettronico.API.SpiaggiariApiClient;
import com.sharpdroid.registroelettronico.Databases.RegistroDB;
import com.sharpdroid.registroelettronico.Interfaces.API.Mark;
import com.sharpdroid.registroelettronico.Interfaces.API.MarkSubject;
import com.sharpdroid.registroelettronico.Interfaces.Client.Media;
import com.sharpdroid.registroelettronico.Interfaces.Client.Subject;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Views.SubjectDetails.InfoView;
import com.sharpdroid.registroelettronico.Views.SubjectDetails.MarksView;
import com.sharpdroid.registroelettronico.Views.SubjectDetails.OverallView;
import com.sharpdroid.registroelettronico.Views.SubjectDetails.RecentLessonsView;
import com.sharpdroid.registroelettronico.Views.SubjectDetails.TargetView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.sharpdroid.registroelettronico.Utils.Metodi.MessaggioVoto;
import static com.sharpdroid.registroelettronico.Utils.Metodi.getProfessorOfThisSubject;
import static com.sharpdroid.registroelettronico.Utils.Metodi.getSubjectName;

// DONE: 03/12/2016 Dettagli (nome, aula, prof, ora, note, colore)
// DONE: 03/12/2016 Media (scritto, orale, totale)
// DONE: 03/12/2016 Obiettivo
// TODO: 03/12/2016 Orario settimanale (in quali giorni)
// TODO: 03/12/2016 Verifiche prossime
// TODO: 19/01/2016 Media Ipotetica
// DONE: 03/12/2016 Voti recenti
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
    @BindView(R.id.marks)
    MarksView marksView;

    MarkSubject data;
    RegistroDB db;
    Subject subject;
    Media media;
    int p;
    RegistroDB.Period period;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_subject_detail);

        ButterKnife.bind(this);

        //get DATA
        try {
            name = new Gson().getAdapter(MarkSubject.class).fromJson(getIntent().getStringExtra("data")).getName().toLowerCase();
            Log.d(TAG, getIntent().getStringExtra("data"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        p = getIntent().getIntExtra("period", 0);
        switch (p) {
            case 0:
                period = RegistroDB.Period.FIRST;
                break;
            case 1:
                period = RegistroDB.Period.SECOND;
                break;
            default:
                period = RegistroDB.Period.ALL;
                break;
        }

        //DATABASE
        db = RegistroDB.getInstance(this);


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

        subject = db.getSubject(name);
        data = db.getMarks(subject.getId(), period);
        setTitle(getSubjectName(subject));

        setInfo(subject);
        setOverall(data.getMarks());
        setTarget();
        setLessons(subject.getId());
        setMarks(data.getMarks());
    }

    private void setInfo(Subject subject) {
        infoView.setSubjectDetails(subject);
        infoView.setEditListener(view -> startActivity(new Intent(this, EditSubjectDetailsActivity.class).putExtra("code", subject.getId())));
    }

    void setOverall(List<Mark> marks) {
        media = new Media();
        media.addMarks(marks);
        if (media.containsValidMarks()) {
            overallView.setVisibility(View.VISIBLE);
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
        } else {
            overallView.setVisibility(View.GONE);
        }
    }

    private float getTarget() {
        float target = subject.getTarget();
        if (target <= 0) {
            target = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(this)
                    .getString("voto_obiettivo", "8"));
        }
        return target;
    }

    private void setTarget() {
        //set initial target

        targetView.setTarget(getTarget());

        //set progress
        if (media.containsValidMarks()) {
            targetView.setProgress(media.getMediaGenerale());
        } else {
            targetView.setVisibility(View.GONE);
        }

        //set listener for button

        targetView.setListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(getString(R.string.obiettivo_title));
            alert.setMessage(getString(R.string.obiettivo_summary));

            View v = getLayoutInflater().inflate(R.layout.fragment_imposta_obiettivo, null);
            SeekBar mSeekBar = (SeekBar) v.findViewById(R.id.seekbar);
            TextView mValueText = (TextView) v.findViewById(R.id.value);
            mSeekBar.setProgress((int) getTarget());
            mValueText.setText(String.format(Locale.getDefault(), "%.0f", getTarget()));

            alert.setView(v);

            alert.setPositiveButton(android.R.string.ok, (dialog, whichButton) ->
                    register(mSeekBar.getProgress())
            );
            alert.setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> {
                // Canceled. Do nothing;
            });

            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mValueText.setText(String.format(Locale.getDefault(), "%d", progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            alert.show();
        }, view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(getString(R.string.obiettivo_title));
            alert.setMessage(MessaggioVoto(targetView.getTarget(), media.getMediaGenerale(), media.getNumeroVoti()));

            alert.setNegativeButton(android.R.string.ok, (dialog, whichButton) -> {
                // Canceled. Do nothing;
            });
            alert.show();
        });
    }

    void register(float new_target) {
        targetView.setTarget(new_target);
        marksView.setTarget(new_target);

        ContentValues values = new ContentValues();
        values.put("target", String.valueOf((int) new_target));
        db.editSubject(subject.getId(), values);
        subject.setTarget(new_target);
        marksView.setLimitLines(new_target, media.getMediaGenerale());
    }

    private void setLessons(int code) {
        lessonsView.update(code);

        for (Integer prof : db.getProfessorCodes(code)) {
            new SpiaggiariApiClient(this)
                    .getLessons(code, String.valueOf(prof))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(lessons -> {
                        //db.removeLessons(code);
                        db.addLessons(code, prof, lessons);
                        lessonsView.update(code);
                        db.addProfessor(code, prof, getProfessorOfThisSubject(lessons));
                    }, Throwable::printStackTrace);
        }
    }

    private void setMarks(List<Mark> marks) {
        marksView.setSubject(subject, media.containsValidMarks() ? media.getMediaGenerale() : 0);
        marksView.addAll(marks);
        marksView.setChart(db.getMarksAsEntries(subject.getId(), period));
        marksView.setShowChart(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("show_chart", true) && marks.size() > 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        //CLOSE DATABASE

        super.onDestroy();
    }
}
