package com.sharpdroid.registroelettronico.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
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
import com.sharpdroid.registroelettronico.utils.Metodi.dp
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

    val data = arrayOf("subject", "subject_color", "day", "start", "end")

    val viewModel by lazy { ViewModelProviders.of(this)[AddTimetableItemViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_timetable_item)
        setSupportActionBar(toolbar)

        title = "Aggiungi lezione"
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = Adapter()
        recycler.addItemDecoration(HorizontalDividerItemDecoration.Builder(this).colorResId(R.color.divider).build())

        viewModel.subject.observe(this, Observer {
            recycler.adapter.notifyItemChanged(0)

            viewModel.subject.value?.let { viewModel.color.value = DatabaseHelper.database.timetableDao().colors(it.subject.id) }
        })
        viewModel.color.observe(this, Observer {
            recycler.adapter.notifyItemChanged(1)
        })
        viewModel.day.observe(this, Observer {
            recycler.adapter.notifyItemChanged(2)
        })
        viewModel.start.observe(this, Observer {
            recycler.adapter.notifyItemChanged(3)
            recycler.adapter.notifyItemChanged(4)
        })
        viewModel.end.observe(this, Observer {
            recycler.adapter.notifyItemChanged(3)
            recycler.adapter.notifyItemChanged(4)
        })

        if (viewModel.subjects.isEmpty()) {
            viewModel.subjects.addAll(DatabaseHelper.database.subjectsDao().getSubjectsWithInfoBlocking(Account.with(this@AddTimetableItemActivity).user))
        }

        //IF EDITING EXISTING LESSON
        if (intent.extras["id"] != null) {
            DatabaseHelper.database.timetableDao().findById(intent.extras["id"] as Long)?.let {
                title = "Modifica lezione"

                //remember to update current
                if (viewModel.id.value == null)
                    viewModel.id.value = it.id

                //query subject
                if (viewModel.subject.value == null)
                    viewModel.subject.value = DatabaseHelper.database.subjectsDao().getSubjectInfoBlocking(it.subject)

                //convert start value to h:mm format
                var h: Int
                var m: Int
                if (viewModel.start.value == null) {
                    h = Math.floor(it.start.toDouble()).toInt()
                    m = ((it.start * 60f) % 60).toInt()
                    viewModel.start.value = "$h:${if (m >= 10) m.toString() else "0$m"}"
                }
                //convert end value to h:mm format
                if (viewModel.end.value == null) {
                    h = Math.floor(it.end.toDouble()).toInt()
                    m = ((it.end * 60f) % 60).toInt()
                    viewModel.end.value = "$h:${if (m >= 10) m.toString() else "0$m"}"
                }
                if (viewModel.day.value == null)
                    viewModel.day.value = it.dayOfWeek
                if (viewModel.color.value == null)
                    viewModel.color.value = it.color
            } ?: finish()
            return
        }

        //IF CREATING NEW LESSON
        if (viewModel.id.value == null)
            viewModel.id.value = 0
        if (viewModel.day.value == null)
            viewModel.day.value = intent.extras["day"] as Int?
        if (viewModel.start.value == null)
            viewModel.start.value = intent.extras["start"]?.let { "$it:00" }
        if (viewModel.end.value == null)
            viewModel.end.value = intent.extras["start"]?.let { "${(it as Int) + 1}:00" }
    }

    private fun save() {
        if (viewModel.subject.value == null || start() >= end()) return

        DatabaseHelper.database.timetableDao().insert(TimetableItem(viewModel.id.value ?: 0, Account.with(this).user.toInt(), start(), end(), viewModel.day.value!!, viewModel.subject.value!!.subject.id, viewModel.color.value ?: ContextCompat.getColor(this@AddTimetableItemActivity, R.color.primary)))
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
                    drawable.setColorFilter(0xff636363.toInt(), PorterDuff.Mode.SRC_ATOP)
                    view.setup(viewModel.subject.value?.getSubjectName() ?: "Seleziona una materia", "Materia", drawable, "Materia non selezionata", viewModel.subject.value == null) { _ ->
                        MaterialDialog.Builder(this@AddTimetableItemActivity)
                                .title("Seleziona una materia")
                                .items(viewModel.subjects.map { it.getSubjectName() }.sortedBy { it })
                                .itemsCallbackSingleChoice(viewModel.subjects.indexOf(viewModel.subject.value), { _, _, which, _ ->
                                    viewModel.subject.value = viewModel.subjects[which]
                                    true
                                })
                                .show()
                    }
                }
                "subject_color" -> {
                    val oval = ShapeDrawable(OvalShape())
                    oval.intrinsicWidth = dp(16)
                    oval.intrinsicHeight = dp(16)
                    oval.setColorFilter(
                            viewModel.color.value ?: ContextCompat.getColor(this@AddTimetableItemActivity, R.color.primary),
                            PorterDuff.Mode.SRC_ATOP
                    )
                    view.setup("Seleziona un colore", oval) { _ ->
                        ColorChooserDialog.Builder(this@AddTimetableItemActivity, R.string.choose_color)
                                .preselect(viewModel.color.value ?: ContextCompat.getColor(this@AddTimetableItemActivity, R.color.primary))
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
                    drawable.setColorFilter(0xff636363.toInt(), PorterDuff.Mode.SRC_ATOP)
                    view.setup(viewModel.day.value?.let { capitalizeFirst(resources.getStringArray(R.array.days_of_week)[it]) } ?: "Seleziona un giorno", "Giorno", drawable) { _ ->
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
                    drawable.setColorFilter(0xff636363.toInt(), PorterDuff.Mode.SRC_ATOP)
                    view.setup(viewModel.start.value ?: "Seleziona un'orario", "Inizio", drawable) { _ ->
                        val current = viewModel.start.value?.split(":")!!.map { it.toInt(10) }

                        TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                            viewModel.start.value = "$hourOfDay:${if (minute >= 10) minute.toString() else "0$minute"}"
                        }, current[0], current[1], true).show(fragmentManager, "")
                    }
                }
                "end" -> {
                    val oval = ShapeDrawable(OvalShape())
                    oval.intrinsicWidth = dp(24)
                    oval.intrinsicHeight = dp(24)
                    oval.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                    view.setup(viewModel.end.value ?: "Seleziona un'orario", "Fine", oval, "Orario non valido", start() >= end()) { _ ->
                        val current = viewModel.end.value?.split(":")!!.map { it.toInt(10) }

                        TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                            viewModel.end.value = "$hourOfDay:${if (minute >= 10) minute.toString() else "0$minute"}"
                        }, current[0], current[1], true).show(fragmentManager, "")

                    }
                }
            }
        }
    }

    private fun start(): Float {
        val start = viewModel.start.value?.split(":")!!.map { it.toInt(10) }
        return start[0] + start[1] / 60f
    }

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
