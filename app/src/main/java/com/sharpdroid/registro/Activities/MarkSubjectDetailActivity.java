package com.sharpdroid.registro.Activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.sharpdroid.registro.Interfaces.Media;
import com.sharpdroid.registro.R;
import com.sharpdroid.registro.Views.OverallView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registro.Utils.Metodi.getMediaColor;

// TODO: 03/12/2016 Dettagli (nome, aula, prof, ora, note, colore)
// TODO: 03/12/2016 Media (scritto, orale, totale)
// TODO: 03/12/2016 Obiettivo
// TODO: 03/12/2016 Orario settimanale (in quali giorni)
// TODO: 03/12/2016 Verifiche prossime
// TODO: 03/12/2016 Voti recenti

public class MarkSubjectDetailActivity extends AppCompatActivity {

    @BindView(R.id.overall)
    OverallView overallView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_subject_detail);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setOverall();
    }

    void setOverall() {
        overallView.setMedia("5.5");
        overallView.setProgress(0.8f, ContextCompat.getColor(this,R.color.bluematerial));
    }
}
