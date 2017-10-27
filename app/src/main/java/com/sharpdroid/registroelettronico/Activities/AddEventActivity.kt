package com.sharpdroid.registroelettronico.Activities

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.Databases.Entities.LocalAgenda
import com.sharpdroid.registroelettronico.Databases.Entities.Subject
import com.sharpdroid.registroelettronico.Databases.Entities.SubjectInfo
import com.sharpdroid.registroelettronico.Databases.Entities.Teacher
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.Account
import com.sharpdroid.registroelettronico.Utils.Metodi.capitalizeEach
import com.sharpdroid.registroelettronico.Utils.Metodi.capitalizeFirst
import com.sharpdroid.registroelettronico.Views.LocalEvent.OptionView
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.activity_add_event.*
import java.text.SimpleDateFormat
import java.util.*

class AddEventActivity : AppCompatActivity() {
    internal var format = SimpleDateFormat("EEEE d MMMM yyyy", Locale.ITALIAN)
    lateinit private var animShake: Animation

    private var selectedSubject: SubjectInfo? = null
    private var selectedProfessor: Teacher? = null
    private var selectedDay: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)
        setSupportActionBar(toolbar)

        supportActionBar?.title = ""
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        selectedDay = Date(intent.getLongExtra("time", 0))
        init(intent.getStringExtra("type"))
        animShake = AnimationUtils.loadAnimation(this, R.anim.shake)
    }

    private fun init(type: String) {
        title = type
        confirm.setOnClickListener { _ -> handleConfirm(type) }
        initDefault()
    }

    private fun handleConfirm(type: String) {
        when (type.toLowerCase()) {
            "verifica" -> if (handleTitle() && handleSubject() && handleProfessor() && handleDate()) {
                selectedDay = betterDate(selectedDay)
                SugarRecord.save(LocalAgenda(layout_verifica.editText!!.text.toString(), layout_note.editText!!.text.toString(), type, selectedDay!!, selectedSubject!!.subject, selectedProfessor!!, null, Account.with(this).user, false))
                finish()
            } else {
                (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(40)
            }
            "compiti" -> if (handleTitle() && handleSubject() && handleProfessor() && handleDate()) {
                selectedDay = betterDate(selectedDay)
                SugarRecord.save(LocalAgenda(layout_verifica.editText!!.text.toString(), layout_note.editText!!.text.toString(), type, selectedDay!!, selectedSubject!!.subject, selectedProfessor!!, null, Account.with(this).user, false))
                finish()
            } else {
                (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(40)
            }
            else -> if (handleTitle() && handleDate()) {
                selectedDay = betterDate(selectedDay)
                SugarRecord.save(LocalAgenda(layout_verifica.editText!!.text.toString(), layout_note.editText!!.text.toString(), type, selectedDay!!, selectedSubject?.subject ?: Subject(), selectedProfessor ?: Teacher(), null, Account.with(this).user, false))
                finish()
            } else {
                (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(40)
            }
        }
    }

    private fun betterDate(date: Date?): Date {
        val cal = Calendar.getInstance()
        cal.time = date

        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)


        return cal.time
    }

    private fun initDefault() {
        options!!.addView(OptionView.Builder(this).title("Materia").content("Non impostata").image(R.drawable.event_subject).onClick { this.subjectDialog(it) }.build())
        options!!.addView(OptionView.Builder(this).title("Professore").content("Non impostato").image(R.drawable.event_professor).onClick { this.professorDialog(it) }.build())
        options!!.addView(OptionView.Builder(this).title("Data").content(capitalizeFirst(format.format(selectedDay))).image(R.drawable.event_date).onClick { this.datePicker(it) }.build())
    }

    private fun subjectDialog(v: View) {
        val subjectList: List<SubjectInfo> = SugarRecord.findWithQuery(Subject::class.java, "SELECT * FROM SUBJECT WHERE ID IN (SELECT SUBJECT_TEACHER.SUBJECT FROM SUBJECT_TEACHER WHERE SUBJECT_TEACHER.PROFILE=?) ORDER BY DESCRIPTION ASC", Account.with(this).user.toString()).map { it.getInfo(this) }

        MaterialDialog.Builder(this)
                .title("Seleziona una materia")
                .items(subjectList.map { capitalizeEach(it.description.or(it.subject.description), false) })
                .itemsCallbackSingleChoice(subjectList.indexOf(selectedSubject)) { _, _, which, text ->
                    selectedSubject = subjectList[which]
                    v.findViewById<TextView>(R.id.content).text = text
                    true
                }
                .show()
    }

    private fun professorDialog(v: View) {
        val professors = SugarRecord.findWithQuery(Teacher::class.java, "SELECT * FROM TEACHER WHERE ID IN (SELECT SUBJECT_TEACHER.TEACHER FROM SUBJECT_TEACHER WHERE SUBJECT_TEACHER.PROFILE=?) ORDER BY TEACHER_NAME ASC", Account.with(this).user.toString())

        MaterialDialog.Builder(this)
                .title("Seleziona un professore")
                .items(professors.map { capitalizeEach(it.teacherName, true) })
                .itemsCallbackSingleChoice(professors.indexOf(selectedProfessor)) { _, _, which, text ->
                    selectedProfessor = professors[which]
                    v.findViewById<TextView>(R.id.content).text = text
                    true
                }.show()
    }

    private fun datePicker(view: View) {
        val now = Calendar.getInstance()
        now.time = selectedDay

        val dpd = DatePickerDialog.newInstance(null,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        )
        dpd.setOnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            if (selectedDay == null) selectedDay = Date()
            val cal = Calendar.getInstance()
            cal.set(year, monthOfYear, dayOfMonth)
            selectedDay!!.time = cal.timeInMillis
            (view.findViewById<View>(R.id.content) as TextView).text = capitalizeFirst(format.format(selectedDay))
        }
        dpd.show(fragmentManager, "Datepickerdialog")

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun setTitle(title: CharSequence) {
        this.layout_verifica.hint = title
    }

    private fun handleTitle(): Boolean {
        val ok = layout_verifica.editText != null && !layout_verifica.editText!!.text.toString().isEmpty()
        if (!ok) {
            layout_verifica.startAnimation(animShake)
            layout_verifica.requestFocus()
        }
        return ok
    }

    private fun handleSubtitle(): Boolean {
        val ok = layout_note.editText != null && !layout_note.editText!!.text.toString().isEmpty()
        if (!ok) {
            layout_note.startAnimation(animShake)
            layout_note.requestFocus()
        }
        return ok
    }

    private fun handleSubject(): Boolean {
        return selectedSubject != null
    }

    private fun handleProfessor(): Boolean {
        return selectedProfessor != null
    }

    private fun handleDate(): Boolean {
        return selectedDay != null
    }
}
