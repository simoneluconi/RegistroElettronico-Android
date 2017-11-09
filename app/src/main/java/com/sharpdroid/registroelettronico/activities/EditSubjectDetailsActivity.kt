package com.sharpdroid.registroelettronico.activities

import android.arch.lifecycle.Observer
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.*
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.holders.Holder
import com.sharpdroid.registroelettronico.database.entities.SubjectInfo
import com.sharpdroid.registroelettronico.database.pojos.SubjectPOJO
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi
import com.sharpdroid.registroelettronico.utils.or
import com.sharpdroid.registroelettronico.views.cells.HeaderCell
import com.sharpdroid.registroelettronico.views.cells.ShadowCell
import com.sharpdroid.registroelettronico.views.cells.ValueDetailsCell
import kotlinx.android.synthetic.main.activity_edit_subject_details.*

class EditSubjectDetailsActivity : AppCompatActivity() {

    private lateinit var adapter: EditSubjectAdapter
    private var rowCount: Int = 0
    private var rowInfo: Int = 0
    private var rowTitle: Int = 0
    private var rowNotes: Int = 0
    private var rowClassroom: Int = 0
    private var rowSeparator: Int = 0
    private var rowTeachers: Int = 0

    private var subject: SubjectPOJO? = null
    private val teachers = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_subject_details)

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        title = "Modifica"

        teachers.clear()
        teachers.addAll(DatabaseHelper.database.subjectsDao().getTeachersOfSubject(intent.getLongExtra("code", 0L)).map { it.teacherName })

        DatabaseHelper.database.subjectsDao().getSubjectInfo(intent.getLongExtra("code", 0L)).observe(this, Observer {
            init(it!!, teachers)
        })


        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Materia").putContentType("Modifica"))

    }

    internal fun init(subject: SubjectPOJO, teachers: List<String>) {
        rowCount = 0
        rowInfo = rowCount++
        rowTitle = rowCount++
        rowNotes = rowCount++
        rowClassroom = rowCount++
        rowSeparator = rowCount++
        rowTeachers = rowCount++

        rowCount += teachers.size

        if (subject.subjectInfo.isEmpty()) {
            subject.subjectInfo = listOfNotNull(SubjectInfo(0L, 0f, "", "", "", subject.subject.id, Account.with(this).user))
        }

        this.subject = subject

        adapter = EditSubjectAdapter(subjectInfo = subject, teachers = teachers) {
            val builder = MaterialDialog.Builder(this)

            when (it) {
                rowTitle -> {
                    builder.title("Nome")
                    builder.content("Modifica il nome della materia")
                    builder.input("Inserisci un nuovo nome", Metodi.capitalizeEach(subject.subjectInfo[0].description.or(subject.subject.description)), true, { _, _ -> })
                    builder.onPositive { dialog, _ -> subject.subjectInfo[0].description = dialog.inputEditText!!.text.toString(); adapter.notifyDataSetChanged() }
                }
                rowNotes -> {
                    builder.title("Dettagli")
                    builder.content("Modifica i dettagli della materia")
                    builder.input("Inserisci i dettagli", subject.subjectInfo[0].details, true, { _, _ -> })
                    builder.onPositive { dialog, _ -> subject.subjectInfo[0].details = dialog.inputEditText!!.text.toString(); adapter.notifyDataSetChanged() }

                }
                rowClassroom -> {
                    builder.title("Classe")
                    builder.content("Modifica la classe della materia")
                    builder.input("Inserisci una nuova classe", subject.subjectInfo[0].classroom, true, { _, _ -> })
                    builder.onPositive { dialog, _ -> subject.subjectInfo[0].classroom = dialog.inputEditText!!.text.toString(); adapter.notifyDataSetChanged() }
                }
            }

            builder.positiveText("Salva")
            builder.neutralText("Annulla")
            builder.show()
        }
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    }

    internal fun apply() {
        subject?.subjectInfo?.getOrNull(0)?.let { DatabaseHelper.database.subjectsDao().insert(it) }
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) super.onBackPressed()
        if (item.itemId == R.id.apply) apply()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_subject_menu, menu)
        return true
    }

    private inner class EditSubjectAdapter(var subjectInfo: SubjectPOJO, var teachers: List<String>, var listener: (position: Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val v: View
            when (viewType) {
                0 -> {
                    v = HeaderCell(parent.context)
                    v.setBackgroundColor(Color.WHITE)
                }
                1 -> {
                    v = ValueDetailsCell(parent.context)
                    v.setBackgroundColor(Color.WHITE)
                }
                else -> v = ShadowCell(parent.context)
            }
            return Holder(v)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (getItemViewType(position)) {
                0 -> (holder.itemView as HeaderCell).setText(if (position == rowInfo) "Informazioni" else "Professori")
                1 -> when (position) {
                    rowTitle -> (holder.itemView as ValueDetailsCell).setTextAndValue(Metodi.capitalizeEach(subjectInfo.subjectInfo.getOrNull(0)?.description.or(subjectInfo.subject.description).or("Senza nome")), "Nome", true)
                    rowNotes -> (holder.itemView as ValueDetailsCell).setTextAndValue(Metodi.capitalizeEach(subjectInfo.subjectInfo.getOrNull(0)?.details).or("Aggiungi dettagli"), "Dettagli", true)
                    rowClassroom -> (holder.itemView as ValueDetailsCell).setTextAndValue(Metodi.capitalizeEach(subjectInfo.subjectInfo.getOrNull(0)?.classroom).or("Aggiungi aula"), "Aula", true)
                    else -> (holder.itemView as ValueDetailsCell).setText(Metodi.capitalizeEach(teachers[position - rowTeachers - 1]), true)
                }
            }
            if (getItemViewType(position) == 1 && position in rowTitle..rowClassroom) {
                val attrs = intArrayOf(android.R.attr.selectableItemBackground)
                val ta = obtainStyledAttributes(attrs)
                val drawableFromTheme = ta.getDrawable(0)
                val wrapper = LayerDrawable(arrayOf(ColorDrawable(Color.WHITE), drawableFromTheme))
                ta.recycle()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.itemView.background = wrapper
                } else {
                    @Suppress("DEPRECATION")
                    holder.itemView.setBackgroundDrawable(wrapper)
                }
                holder.itemView.setOnClickListener { view ->
                    view.postDelayed({ listener.invoke(position) }, ViewConfiguration.getTapTimeout().toLong())
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if (position == rowInfo || position == rowTeachers) 0
            else if (position == rowSeparator) 2
            else 1
        }

        override fun getItemCount(): Int {
            return rowCount
        }
    }
}
