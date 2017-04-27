package com.sharpdroid.registroelettronico.Activities;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sharpdroid.registroelettronico.Databases.RegistroDB;
import com.sharpdroid.registroelettronico.Interfaces.Client.LocalEvent;
import com.sharpdroid.registroelettronico.Interfaces.Client.Subject;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Views.LocalEvent.OptionView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registroelettronico.Utils.Metodi.capitalizeFirst;
import static com.sharpdroid.registroelettronico.Utils.Metodi.capitalizeList;
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
    @BindView(R.id.layout_note)
    TextInputLayout note;
    @BindView(R.id.options)
    LinearLayout options;
    @BindView(R.id.confirm)
    FloatingActionButton confirm;

    SimpleDateFormat format = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.ITALIAN);
    RegistroDB registroDB;
    Animation animShake;

    int selectedSubject = -1;
    int selectedProfessor = -1;
    int selectedSubjectCode = -1;
    int selectedProfessorCode = -1;
    Date selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        ButterKnife.bind(this);
        registroDB = RegistroDB.getInstance(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        selectedDay = new Date(getIntent().getLongExtra("time", 0));
        init(getIntent().getStringExtra("type"));
        animShake = AnimationUtils.loadAnimation(this, R.anim.shake);

    }

    private void init(String type) {
        setTitle(type);
        confirm.setOnClickListener(v -> handleConfirm(type));
        initDefault();
    }

    private void handleConfirm(String type) {
        switch (type.toLowerCase()) {
            case "verifica":
                if (handleTitle() && handleSubtitle() && handleSubject() && handleProfessor() && handleDate()) {
                    selectedDay = betterDate(selectedDay);
                    registroDB.addLocalEvent(new LocalEvent(UUID.randomUUID().toString(), title.getEditText().getText().toString(), note.getEditText().getText().toString(), type, selectedDay, selectedSubjectCode, selectedProfessorCode, null));
                    finish();
                } else {
                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(40);
                }
                break;
            case "compiti":
                if (handleTitle() && handleSubtitle() && handleSubject() && handleProfessor() && handleDate()) {
                    selectedDay = betterDate(selectedDay);
                    registroDB.addLocalEvent(new LocalEvent(UUID.randomUUID().toString(), title.getEditText().getText().toString(), note.getEditText().getText().toString(), type, selectedDay, selectedSubjectCode, selectedProfessorCode, null));
                    finish();
                } else {
                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(40);
                }
                break;
            default:
                if (handleTitle() && handleSubtitle() && handleDate()) {
                    selectedDay = betterDate(selectedDay);
                    registroDB.addLocalEvent(new LocalEvent(UUID.randomUUID().toString(), title.getEditText().getText().toString(), note.getEditText().getText().toString(), type, selectedDay, selectedSubjectCode, selectedProfessorCode, null));
                    finish();
                } else {
                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(40);
                }
                break;
        }
    }

    private Date betterDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);


        return cal.getTime();
    }

    private void initDefault() {
        options.addView(new OptionView.Builder(this).title("Materia").content("Non impostata").image(R.drawable.event_subject).onClick(this::subjectDialog).build());
        options.addView(new OptionView.Builder(this).title("Professore").content("Non impostato").image(R.drawable.event_professor).onClick(this::professorDialog).build());
        options.addView(new OptionView.Builder(this).title("Data").content(capitalizeFirst(format.format(selectedDay))).image(R.drawable.event_date).onClick(this::datePicker).build());
    }

    private void subjectDialog(View v) {
        List<Subject> subjectList;

        subjectList = registroDB.getSubjects();

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

    private void professorDialog(View v) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(this)
                .title("Seleziona un professore");

        List<Pair<Integer, String>> professors = registroDB.getProfessors();
        List<String> professorsNames = capitalizeList(pairToSecond(professors));
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

    private void datePicker(View view) {
        Calendar now = Calendar.getInstance();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                null,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setOnDateSetListener((view1, year, monthOfYear, dayOfMonth) -> {
            if (selectedDay == null) selectedDay = new Date();
            Calendar cal = Calendar.getInstance();
            cal.set(year, monthOfYear, dayOfMonth);
            selectedDay.setTime(cal.getTimeInMillis());
            ((TextView) view.findViewById(R.id.content)).setText(capitalizeFirst(format.format(selectedDay)));
        });
        dpd.show(getFragmentManager(), "Datepickerdialog");

    }

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

    }

    boolean handleTitle() {
        boolean ok = title.getEditText() != null && !title.getEditText().getText().toString().isEmpty();
        if (!ok) {
            title.startAnimation(animShake);
            title.requestFocus();
        }
        return ok;
    }

    boolean handleSubtitle() {
        boolean ok = note.getEditText() != null && !note.getEditText().getText().toString().isEmpty();
        if (!ok) {
            note.startAnimation(animShake);
            note.requestFocus();
        }
        return ok;
    }

    boolean handleSubject() {
        return selectedSubject != -1 && selectedSubjectCode != -1;
    }

    boolean handleProfessor() {
        return selectedProfessor != -1 && selectedProfessorCode != -1;
    }

    boolean handleDate() {
        return selectedDay != null;
    }
}
