package com.sharpdroid.registro.Activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.sharpdroid.registro.R;
import com.sharpdroid.registro.Views.OverallView;

import butterknife.BindView;
import butterknife.ButterKnife;

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

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setOverall();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    void setOverall() {
        overallView.setMedia("5.5");
        overallView.setOrale("7.5");
    }
}
