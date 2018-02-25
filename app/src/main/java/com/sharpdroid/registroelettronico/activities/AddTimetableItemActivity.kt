package com.sharpdroid.registroelettronico.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.PorterDuff
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.holders.Holder
import com.sharpdroid.registroelettronico.database.entities.TimetableItem
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeFirst
import com.sharpdroid.registroelettronico.utils.Metodi.convertFloatTimeToString
import com.sharpdroid.registroelettronico.utils.Metodi.dp
import com.sharpdroid.registroelettronico.utils.or
import com.sharpdroid.registroelettronico.viewModels.AddTimetableItemViewModel
import com.sharpdroid.registroelettronico.views.cells.ComplexCell
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.activity_add_timetable_item.*

class AddTimetableItemActivity : AppCompatActivity(), ColorChooserDialog.ColorCallback {

    override fun onColorSelection(dialog: ColorChooserDialog, selectedColor: Int) {
        viewModel.color.value = selectedColor
    }

    override fun onColorChooserDismissed(dialog: ColorChooserDialog) {
    }

    /**
     * Structure of the recyclerView
     */
    val data = arrayOf("subject", "subject_color", "day", "start", "end", "where", "notes")

    val viewModel by lazy { ViewModelProviders.of(this)[AddTimetableItemViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_timetable_item)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = Adapter()
        recycler.addItemDecoration(HorizontalDividerItemDecoration.Builder(this).colorResId(R.color.divider).build())

        //Setup observers
        with(viewModel) {
            subject.observe(this@AddTimetableItemActivity, Observer {
                recycler.adapter.notifyItemChanged(0)

                //Choose the last color inserted for this subject
                subject.value?.let { color.value = DatabaseHelper.database.timetableDao().colors(it.subject.id) }
            })
            color.observe(this@AddTimetableItemActivity, Observer {
                recycler.adapter.notifyItemChanged(1)
            })
            day.observe(this@AddTimetableItemActivity, Observer {
                recycler.adapter.notifyItemChanged(2)
            })
            start.observe(this@AddTimetableItemActivity, Observer {
                recycler.adapter.notifyItemChanged(3)
                recycler.adapter.notifyItemChanged(4)
            })
            end.observe(this@AddTimetableItemActivity, Observer {
                recycler.adapter.notifyItemChanged(3)
                recycler.adapter.notifyItemChanged(4)
            })
            where.observe(this@AddTimetableItemActivity, Observer {
                recycler.adapter.notifyItemChanged(5)
            })
            notes.observe(this@AddTimetableItemActivity, Observer {
                recycler.adapter.notifyItemChanged(6)
            })
            if (viewModel.subjects.isEmpty()) {
                viewModel.subjects.addAll(DatabaseHelper.database.subjectsDao().getAllSubjectsPOJOBlocking(Account.with(this@AddTimetableItemActivity).user).sortedBy { it.getSubjectName() })
            }
        }

        //IF EDITING EXISTING LESSON
        if (intent.extras != null && intent.extras["id"] != null) {
            DatabaseHelper.database.timetableDao().findById(intent.extras["id"] as Long)?.let {
                title = "Modifica lezione"

                //remember to update current
                if (viewModel.id.value == null)
                    viewModel.id.value = it.id

                //query subject
                if (viewModel.subject.value == null)
                    viewModel.subject.value = DatabaseHelper.database.subjectsDao().getSubjectPOJOBlocking(it.subject, Account.with(this).user)

                //convert start value to h:mm format
                if (viewModel.start.value == null) {
                    viewModel.start.value = convertFloatTimeToString(it.start)
                }
                //convert end value to h:mm format
                if (viewModel.end.value == null) {
                    viewModel.end.value = convertFloatTimeToString(it.end)
                }
                if (viewModel.day.value == null)
                    viewModel.day.value = it.dayOfWeek
                if (viewModel.color.value == null)
                    viewModel.color.value = it.color
                if (viewModel.where.value == null)
                    viewModel.where.value = it.where
                if (viewModel.notes.value == null)
                    viewModel.notes.value = it.notes
            } ?: finish()
        }

        //IF CREATING NEW LESSON
        else {
            title = "Aggiungi lezione"

            if (viewModel.id.value == null)
                viewModel.id.value = 0
            if (viewModel.day.value == null)
                viewModel.day.value = intent.extras["day"] as Int?
            if (viewModel.start.value == null)
                viewModel.start.value = intent.extras["start"]?.let { "$it:00" }
            if (viewModel.end.value == null)
                viewModel.end.value = intent.extras["start"]?.let { "${(it as Int) + 1}:00" }
        }
    }

    /**
     * This is gonna save the new TimetableItem ONLY IF all conditions are OK
     */
    private fun save() {
        if (viewModel.subject.value == null || start() >= end()) return

        DatabaseHelper.database.timetableDao().insert(TimetableItem(
                viewModel.id.value ?: 0,
                Account.with(this).user.toInt(),
                start(),
                end(),
                viewModel.day.value!!,
                viewModel.subject.value!!.subject.id,
                viewModel.color.value
                        ?: ContextCompat.getColor(this@AddTimetableItemActivity, R.color.primary),
                viewModel.where.value,
                viewModel.notes.value
        ))
        super.onBackPressed()
    }

    inner class Adapter : RecyclerView.Adapter<Holder>() {
        override fun getItemCount() = data.count()

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
            return Holder(ComplexCell(parent!!.context))
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val view = holder.itemView as ComplexCell
            when (data[position]) {
                "subject" -> {
                    val drawable = ContextCompat.getDrawable(this@AddTimetableItemActivity, R.drawable.ic_school_black_24dp)
                    view.setup(viewModel.subject.value?.getSubjectName()
                            ?: "Seleziona una materia", "Materia", drawable, "Materia non selezionata", viewModel.subject.value == null) { _ ->
                        MaterialDialog.Builder(this@AddTimetableItemActivity)
                                .title("Seleziona una materia")
                                .items(viewModel.subjects.map { it.getSubjectName() })
                                .itemsCallbackSingleChoice(viewModel.subjects.indexOf(viewModel.subject.value), { _, _, which, _ ->
                                    viewModel.subject.value = viewModel.subjects[which]
                                    true
                                })
                                .show()
                    }
                }
                "subject_color" -> {
                    //Update color of the shape
                    val oval = ShapeDrawable(OvalShape())
                    oval.intrinsicWidth = dp(16)
                    oval.intrinsicHeight = dp(16)
                    oval.setColorFilter(
                            viewModel.color.value
                                    ?: ContextCompat.getColor(this@AddTimetableItemActivity, R.color.primary),
                            PorterDuff.Mode.SRC_ATOP
                    )
                    view.setup("Seleziona un colore", oval, false) { _ ->
                        ColorChooserDialog.Builder(this@AddTimetableItemActivity, R.string.choose_color)
                                .preselect(viewModel.color.value
                                        ?: ContextCompat.getColor(this@AddTimetableItemActivity, R.color.primary))
                                .doneButton(R.string.ok)
                                .allowUserColorInputAlpha(false)
                                .cancelButton(android.R.string.cancel)
                                .backButton(R.string.back)
                                .customButton(R.string.edit)
                                .presetsButton(R.string.presets)
                                .show(supportFragmentManager)
                    }
                }
                "day" -> {
                    val drawable = ContextCompat.getDrawable(this@AddTimetableItemActivity, R.drawable.agenda_bsheet_calendar)
                    view.setup(viewModel.day.value?.let { capitalizeFirst(resources.getStringArray(R.array.days_of_week)[it]) }
                            ?: "Seleziona un giorno", "Giorno", drawable) { _ ->
                        MaterialDialog.Builder(this@AddTimetableItemActivity)
                                .title("Seleziona un giorno")
                                .items(resources.getStringArray(R.array.days_of_week).map { capitalizeFirst(it) })
                                .itemsCallback { _, _, position, _ ->
                                    viewModel.day.value = position
                                }.show()
                    }
                }
                "start" -> {
                    val drawable = ContextCompat.getDrawable(this@AddTimetableItemActivity, R.drawable.ic_access_time_black_24dp)
                    view.setup(viewModel.start.value
                            ?: "Seleziona un'orario", "Inizio", drawable) { _ ->
                        val current = viewModel.start.value?.split(":")!!.map { it.toInt(10) }

                        TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                            viewModel.start.value = "$hourOfDay:${if (minute >= 10) minute.toString() else "0$minute"}"
                        }, current[0], current[1], true).show(fragmentManager, "")
                    }
                }
                "end" -> {
                    view.setup(viewModel.end.value
                            ?: "Seleziona un'orario", "Fine", null, "Orario non valido", start() >= end()) { _ ->
                        val current = viewModel.end.value?.split(":")!!.map { it.toInt(10) }

                        TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                            viewModel.end.value = "$hourOfDay:${if (minute >= 10) minute.toString() else "0$minute"}"
                        }, current[0], current[1], true).show(fragmentManager, "")

                    }
                }
                "where" -> {
                    view.setup(viewModel.where.value.or("Aula"), ContextCompat.getDrawable(this@AddTimetableItemActivity, R.drawable.ic_place_black_24dp), true) {
                        MaterialDialog.Builder(this@AddTimetableItemActivity)
                                .title("Aggiungi l'aula")
                                .input("Aula", viewModel.where.value, true) { _, input ->
                                    viewModel.where.value = capitalizeFirst(input.toString())
                                }.show()
                    }
                }
                "notes" -> {
                    view.setup(viewModel.notes.value.or("Note"), ContextCompat.getDrawable(this@AddTimetableItemActivity, R.drawable.ic_filter_list_black_24dp), true) {
                        MaterialDialog.Builder(this@AddTimetableItemActivity)
                                .title("Aggiungi note")
                                .input("Note", viewModel.notes.value, true) { _, input ->
                                    viewModel.notes.value = capitalizeFirst(input.toString())
                                }
                                .show()
                    }

                }
            }
        }
    }

    /**
     * Converts hh:mm into float
     */
    private fun start(): Float {
        val start = viewModel.start.value?.split(":")!!.map { it.toInt(10) }
        return start[0] + start[1] / 60f
    }

    /**
     * Converts hh:mm into float
     */
    private fun end(): Float {
        val end = viewModel.end.value?.split(":")!!.map { it.toInt(10) }
        return end[0] + end[1] / 60f
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_subject_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) super.onBackPressed()
        if (item.itemId == R.id.apply) save()
        return super.onOptionsItemSelected(item)
    }
}
