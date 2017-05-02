package com.sharpdroid.registroelettronico.Databases

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.preference.PreferenceManager
import android.support.v4.util.Pair
import android.text.TextUtils
import com.franmontiel.persistentcookiejar.persistence.SerializableCookie
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.sharpdroid.registroelettronico.Interfaces.API.*
import com.sharpdroid.registroelettronico.Interfaces.Client.*
import com.sharpdroid.registroelettronico.Utils.Metodi
import com.sharpdroid.registroelettronico.Utils.Metodi.AccountImage
import com.sharpdroid.registroelettronico.Utils.Metodi.createCookieKey
import okhttp3.Cookie
import org.apache.commons.lang3.text.WordUtils
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class RegistroDB(private val mContext: Context) : SQLiteOpenHelper(mContext, RegistroDB.DB_NAME, null, RegistroDB.DB_VERSION) {

    override fun close() {
        super.close()
        instance = null
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("PRAGMA foreign_keys=ON")
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE profiles(username TEXT NOT NULL, name TEXT, class TEXT, PRIMARY KEY (username));")
        db.execSQL("CREATE TABLE teachers(id INTEGER NOT NULL, name TEXT NOT NULL, PRIMARY KEY (id));")
        db.execSQL("CREATE TABLE cookies(username TEXT NOT NULL, key TEXT NOT NULL, value TEXT NOT NULL, FOREIGN KEY (username) REFERENCES profiles(username) ON DELETE CASCADE);")
        db.execSQL("CREATE TABLE api_events(id INTEGER NOT NULL, title TEXT NOT NULL, content TEXT NOT NULL, start INTEGER NOT NULL, end INTEGER NOT NULL, all_day INTEGER NOT NULL, type INTEGER NOT NULL, completed INTEGER, archived INTEGER, username TEXT NOT NULL, teacher_id INTEGER NOT NULL, teacher_name TEXT NOT NULL, PRIMARY KEY (id), FOREIGN KEY (username) REFERENCES profiles(username) ON DELETE CASCADE);")
        db.execSQL("CREATE TABLE folders(id TEXT PRIMARY KEY, name TEXT NOT NULL, date INTEGER NOT NULL);")
        db.execSQL("CREATE TABLE files(id INTEGER NOT NULL, name TEXT NOT NULL, type TEXT NOT NULL, date INTEGER NOT NULL, cksum TEXT, link TEXT, hidden INTEGER NOT NULL, filename TEXT, folder_id INTEGER NOT NULL, PRIMARY KEY (id), FOREIGN KEY (folder_id) REFERENCES folders(id) ON DELETE CASCADE);")
        db.execSQL("CREATE TABLE communications(id INTEGER NOT NULL, title TEXT NOT NULL, content TEXT, date INTEGER NOT NULL, type TEXT NOT NULL, filename TEXT, attachment INTEGER, username TEXT NOT NULL, PRIMARY KEY (id), FOREIGN KEY (username) REFERENCES profiles(username) ON DELETE CASCADE);")
        db.execSQL("CREATE TABLE notes(id TEXT PRIMARY KEY, content TEXT, date INTEGER NOT NULL, type TEXT NOT NULL, username TEXT NOT NULL, teacher_id INTEGER NOT NULL, FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE, FOREIGN KEY (username) REFERENCES profiles(username) ON DELETE CASCADE);")
        db.execSQL("CREATE TABLE subjects(id INTEGER NOT NULL, original_name TEXT NOT NULL, name TEXT, target FLOAT, classroom TEXT, notes TEXT, username TEXT NOT NULL, PRIMARY KEY (id), FOREIGN KEY (username) REFERENCES profiles(username) ON DELETE CASCADE);")
        db.execSQL("CREATE TABLE lessons (id TEXT NOT NULL, date INTEGER NOT NULL, content TEXT NOT NULL, subject_id INTEGER NOT NULL, teacher_id INTEGER NOT NULL, PRIMARY KEY(id), FOREIGN KEY(subject_id) REFERENCES subjects(id) ON DELETE CASCADE, FOREIGN KEY(teacher_id) REFERENCES teachers(id) ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE marks (id TEXT NOT NULL, subject_id INTEGER NOT NULL, mark TEXT NOT NULL, description TEXT, date INTEGER NOT NULL, type TEXT NOT NULL, period TEXT NOT NULL, not_significant INTEGER NOT NULL, PRIMARY KEY(id), FOREIGN KEY(subject_id) REFERENCES subjects(id) ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE subject_teacher(teacher_id INTEGER NOT NULL, subject_id INTEGER NOT NULL, FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE, FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE);")
        db.execSQL("CREATE TABLE teacher_folder(teacher_id INTEGER, folder_id INTEGER NOT NULL, teacher_name TEXT NOT NULL, username TEXT NOT NULL, FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE, FOREIGN KEY (folder_id) REFERENCES folders(id) ON DELETE CASCADE, FOREIGN KEY (username) REFERENCES profiles(username) ON DELETE CASCADE);")
        db.execSQL("CREATE TABLE local_events ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT NOT NULL, type TEXT NOT NULL, day INTEGER NOT NULL, teacher_id INTEGER, subject_id INTEGER, completed INTEGER, archived INTEGER, username TEXT NOT NULL, FOREIGN KEY(username) REFERENCES profiles(username) ON DELETE CASCADE, FOREIGN KEY(teacher_id) REFERENCES teachers(id) ON DELETE CASCADE, FOREIGN KEY(subject_id) REFERENCES subjects(id) ON DELETE CASCADE);")

        if (DB_VERSION != 26)
            onUpgrade(db, 1, DB_VERSION)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 26) {
            db.execSQL("DROP TABLE IF EXISTS api")
            db.execSQL("DROP TABLE IF EXISTS archive")
            db.execSQL("DROP TABLE IF EXISTS completed")
            db.execSQL("DROP TABLE IF EXISTS cookies")
            db.execSQL("DROP TABLE IF EXISTS lessons")
            db.execSQL("DROP TABLE IF EXISTS local")
            db.execSQL("DROP TABLE IF EXISTS marks")
            db.execSQL("DROP TABLE IF EXISTS professors")
            db.execSQL("DROP TABLE IF EXISTS teachers")
            db.execSQL("DROP TABLE IF EXISTS subject_teacher")
            db.execSQL("DROP TABLE IF EXISTS profiles")
            db.execSQL("DROP TABLE IF EXISTS subjects")
            db.execSQL("DROP TABLE IF EXISTS communications")
            db.execSQL("DROP TABLE IF EXISTS folders")
            db.execSQL("DROP TABLE IF EXISTS files")
            db.execSQL("DROP TABLE IF EXISTS api_events")
            db.execSQL("DROP TABLE IF EXISTS notes")
            db.execSQL("DROP TABLE IF EXISTS local_events")
            db.execSQL("DROP TABLE IF EXISTS teacher_folder")
            onCreate(db)
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean("first_run", true).apply()
        }
    }

    //region EVENTS
    //region API
    fun addEvents(events: List<Event>) {
        val db = this.writableDatabase
        db.beginTransaction()
        if (events.isNotEmpty()) {
            classDescription = events[0].classe_desc
        }
        for (e in events) {
            db.execSQL("INSERT OR REPLACE INTO api_events VALUES(?,?,?,?,?,?,?,(SELECT completed FROM api_events WHERE id = ?),(SELECT archived FROM api_events WHERE id = ?),?,?,?)", arrayOf(e.id, e.title, e.nota_2, e.start.time, e.end.time, if (e.isAllDay) 1 else 0, e.tipo, e.id, e.id, currentProfile(), e.autore_id, e.autore_desc))
        }
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    val events: List<AdvancedEvent>
        get() {
            val db = this.readableDatabase
            val c = db.rawQuery("SELECT api_events.id, title, start,end,all_day, content,teachers.id AS teacher_id, coalesce(teachers.name, teacher_name),type,completed FROM api_events LEFT JOIN teachers ON teacher_id=teachers.id WHERE archived IS NULL AND username=?", arrayOf(currentProfile()))
            val list = ArrayList<AdvancedEvent>()
            c.moveToFirst()
            while (!c.isAfterLast) {
                list.add(AdvancedEvent(c.getString(0), c.getString(1), Date(c.getLong(2)), Date(c.getLong(3)), c.getInt(4) == 1, null, c.getString(5), null, null, null, 0, c.getString(7), c.getString(6), c.getString(8), null, null, c.getLong(9)))
                c.moveToNext()
            }

            c.close()
            return list
        }

    fun getEvents(day: Long): List<AdvancedEvent> {
        val db = this.readableDatabase
        val c = db.rawQuery("SELECT api_events.id, title, start,end,all_day, content,teachers.id AS teacher_id, teachers.name AS teacher_name,type,completed FROM api_events LEFT JOIN teachers ON teacher_id=teachers.id WHERE archived IS NULL AND (api_events.start BETWEEN ? AND ?) AND username=?", arrayOf(day.toString(), (day + 86399999).toString(), currentProfile()))
        val list = ArrayList<AdvancedEvent>()
        c.moveToFirst()
        while (!c.isAfterLast) {
            list.add(AdvancedEvent(c.getString(0), c.getString(1), Date(c.getLong(2)), Date(c.getLong(3)), c.getInt(4) == 1, null, c.getString(5), null, null, null, 0, c.getString(7), c.getString(6), c.getString(8), null, null, c.getLong(9)))
            c.moveToNext()
        }
        c.close()
        return list
    }

    //endregion
    //region LOCAL
    fun addLocalEvent(e: LocalEvent) {
        val db = this.writableDatabase
        db.beginTransaction()
        val values: ContentValues = ContentValues()
        values.put("title", e.title)
        values.put("content", e.content)
        values.put("type", e.type)
        values.put("teacher_id", e.profId)
        values.put("subject_id", e.subjectId)
        values.put("day", e.day.time)
        values.put("username", currentProfile())
        db.insert("local_events", null, values)
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    val localEvents: List<AdvancedEvent>
        get() {
            val db = this.readableDatabase
            val c = db.rawQuery("SELECT local_events.id,title,content,type,day,subject_id,teacher_id,completed,coalesce(subjects.name,subjects.original_name) AS subject_name, teachers.name AS teacher_name FROM local_events LEFT JOIN subjects ON subject_id = subjects.id LEFT JOIN teachers ON teacher_id = teachers.id WHERE local_events.archived IS NULL AND local_events.username=?", arrayOf(currentProfile()))
            val list = ArrayList<AdvancedEvent>()

            c.moveToFirst()
            while (!c.isAfterLast) {
                list.add(AdvancedEvent(c.getString(0), c.getString(1), Date(c.getLong(4)), null, true, null, c.getString(2), null, null, null, 0, c.getString(9), c.getString(6), c.getString(3), c.getString(8), c.getString(5), c.getLong(7)))
                c.moveToNext()
            }

            c.close()
            return list
        }

    fun getLocalEvents(day: Long): List<AdvancedEvent> {
        val db = this.readableDatabase
        val c = db.rawQuery("SELECT local_events.id,title,content,type,day,subject_id,teacher_id,completed,coalesce(subjects.name,subjects.original_name) AS subject_name, teachers.name AS teacher_name FROM local_events LEFT JOIN subjects ON subject_id = subjects.id LEFT JOIN teachers ON teacher_id = teachers.id WHERE local_events.archived IS NULL AND day BETWEEN ? AND ? AND local_events.username=?", arrayOf(day.toString(), (day + 86399999).toString(), currentProfile()))
        val list = ArrayList<AdvancedEvent>()

        c.moveToFirst()
        while (!c.isAfterLast) {
            list.add(AdvancedEvent(c.getString(0), c.getString(1), Date(c.getLong(4)), null, true, null, c.getString(2), null, null, null, 0, c.getString(9), c.getString(6), c.getString(3), c.getString(8), c.getString(5), c.getLong(7)))
            c.moveToNext()
        }
        c.close()
        return list
    }
    //endregion

    var classDescription: String
        get() {
            val db = this.readableDatabase
            val c = db.rawQuery("SELECT class FROM profiles WHERE username=?", arrayOf(currentProfile()))
            var s = ""
            if (c.moveToFirst()) s = c.getString(0)
            c.close()
            return s
        }
        set(c) {
            val db = this.writableDatabase
            val cv = ContentValues()
            cv.put("class", c)
            db.update("profiles", cv, "username=?", arrayOf(currentProfile()))
        }


    val allEvents: List<AdvancedEvent>
        get() {
            val list = ArrayList<AdvancedEvent>()
            list.addAll(localEvents)
            list.addAll(events)
            return list
        }

    fun getAllEvents(day: Long): List<AdvancedEvent> {
        val list = ArrayList<AdvancedEvent>()
        list.addAll(getLocalEvents(day))
        list.addAll(getEvents(day))
        return list
    }
    //endregion

    //region COMPLETED
    fun setCompleted(id: String) {
        val db = writableDatabase
        db.execSQL("UPDATE api_events SET completed = ? WHERE id=?", arrayOf(System.currentTimeMillis(), id))
        db.execSQL("UPDATE local_events SET completed = ? WHERE id=?", arrayOf(System.currentTimeMillis(), id))
    }

    fun setUncompleted(id: String) {
        val db = writableDatabase
        db.execSQL("UPDATE api_events SET completed = NULL WHERE id=?", arrayOf<Any>(id))
        db.execSQL("UPDATE local_events SET completed = NULL WHERE id=?", arrayOf<Any>(id))
    }

    fun isCompleted(id: String): Boolean {
        val db = readableDatabase
        val c = db.rawQuery("SELECT api_events.id, api_events.completed FROM api_events WHERE id=? AND completed IS NOT NULL UNION ALL SELECT local_events.id, local_events.completed FROM local_events WHERE id=? AND completed IS NOT NULL", arrayOf(id, id))
        val completed = c.moveToFirst()
        c.close()
        return completed
    }

    //endregion

    //region ARCHIVE
    fun archive(id: String) {
        val db = writableDatabase
        db.execSQL("UPDATE api_events SET archived = ? WHERE id=?", arrayOf(System.currentTimeMillis(), id))
        db.execSQL("UPDATE local_events SET archived = ? WHERE id=?", arrayOf(System.currentTimeMillis(), id))
    }

    fun clearArchive() {
        val db = writableDatabase
        db.execSQL("UPDATE api_events SET archived = NULL")
        db.execSQL("UPDATE local_events SET archived = NULL")
    }
    //endregion

    //region SUBJECT
    fun getSubject(code: Int): Subject? {
        val db = this.readableDatabase
        var subject: Subject? = null

        val c = db.rawQuery("SELECT subjects.id, coalesce(subjects.name, subjects.original_name) AS name, target,classroom,notes,teacher_id,teachers.name AS teacher_name FROM subjects LEFT JOIN subject_teacher ON subject_teacher.subject_id=subjects.id LEFT JOIN teachers ON teachers.id=teacher_id WHERE subject_id=? AND username=?", arrayOf(code.toString(), currentProfile()))
        val codes = ArrayList<Int>()
        val names = ArrayList<String>()

        c.moveToFirst()
        while (!c.isAfterLast) {
            codes.add(c.getInt(5))
            names.add(c.getString(6))
            c.moveToNext()
        }
        if (c.moveToFirst())
            subject = Subject(c.getInt(0), WordUtils.capitalizeFully(c.getString(1), *Metodi.Delimeters), c.getFloat(2), TextUtils.join(",", names), c.getString(3), c.getString(4), codes)

        c.close()
        return subject
    }

    fun getSubject(name: String): Subject? {
        var name = name
        val db = this.readableDatabase
        var subject: Subject? = null
        val c: Cursor
        if (name.contains("...")) {
            name = name.replace("...", "%").toLowerCase()
            c = db.rawQuery("SELECT subjects.id, coalesce(subjects.name, subjects.original_name) AS _name, target,classroom,notes,teacher_id,teachers.name AS teacher_name FROM subjects INNER JOIN subject_teacher ON subject_teacher.subject_id=subjects.id LEFT JOIN teachers ON teachers.id=teacher_id WHERE (lower(subjects.original_name) LIKE ? OR lower(subjects.name) LIKE ?) AND username=?", arrayOf(name, name, currentProfile()))
        } else {
            c = db.rawQuery("SELECT subjects.id, coalesce(subjects.name, subjects.original_name) AS _name, target,classroom,notes,teacher_id,teachers.name AS teacher_name FROM subjects INNER JOIN subject_teacher ON subject_teacher.subject_id=subjects.id LEFT JOIN teachers ON teachers.id=teacher_id WHERE (lower(subjects.original_name) LIKE ? OR lower(subjects.name) LIKE ?) AND username=?", arrayOf(name, name, currentProfile()))
        }

        val codes = ArrayList<Int>()
        val names = ArrayList<String>()

        if (!c.moveToFirst()) return null
        while (!c.isAfterLast) {
            codes.add(c.getInt(5))
            names.add(c.getString(6))
            c.moveToNext()
        }

        if (c.moveToFirst())
            subject = Subject(c.getInt(0), WordUtils.capitalizeFully(c.getString(1), *Metodi.Delimeters), c.getFloat(2), TextUtils.join(",", names), c.getString(3), c.getString(4), codes)


        c.close()
        return subject
    }

    val subjects: List<Subject?>
        get() {
            val subjects = ArrayList<Subject?>()
            val db = this.readableDatabase
            val c = db.rawQuery("SELECT id FROM subjects WHERE username=?", arrayOf(currentProfile()))

            c.moveToFirst()
            while (!c.isAfterLast) {
                subjects.add(getSubject(c.getInt(0)))
                c.moveToNext()
            }

            c.close()
            return subjects
        }

    fun editSubject(code: Int, contentValues: ContentValues) {
        val db = this.writableDatabase
        db.beginTransaction()
        db.update(TABLE_SUBJECTS, contentValues, "id = ? AND username=?", arrayOf(code.toString(), currentProfile()))
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    fun addSubject(subject: LessonSubject) {
        val db = this.writableDatabase

        db.beginTransaction()
        db.execSQL("INSERT OR IGNORE INTO subjects(id,original_name,username) VALUES(?,?,?)", arrayOf(subject.code, subject.name, currentProfile()))
        db.setTransactionSuccessful()
        db.endTransaction()
    }
    //endregion

    //region LESSONS
    fun getLessons(code: Int): List<Lesson> {
        val db = readableDatabase
        val c = db.rawQuery("SELECT teachers.name,lessons.date, lessons.content FROM lessons LEFT JOIN teachers ON teachers.id=lessons.teacher_id WHERE subject_id=? ORDER BY date DESC", arrayOf(code.toString()))
        val lessons = LinkedList<Lesson>()
        c.moveToFirst()
        while (!c.isAfterLast) {
            lessons.add(Lesson(c.getString(0), Date(c.getLong(1)), c.getString(2)))
            c.moveToNext()
        }
        c.close()
        return lessons
    }

    fun getLessons(code: Int, limit: Int): List<Lesson> {
        val db = readableDatabase
        val c = db.rawQuery("SELECT teachers.name,lessons.date, lessons.content FROM lessons LEFT JOIN teachers ON teachers.id=lessons.teacher_id WHERE subject_id=? ORDER BY date DESC LIMIT ?", arrayOf(code.toString(), limit.toString()))
        val lessons = LinkedList<Lesson>()
        c.moveToFirst()
        while (!c.isAfterLast) {
            lessons.add(Lesson(c.getString(0), Date(c.getLong(1)), c.getString(2)))
            c.moveToNext()
        }
        c.close()
        return lessons
    }

    fun addLessons(subject_id: Int, teacher_id: Int, lessons_list: List<Lesson>) {
        val db = writableDatabase
        db.beginTransaction()
        for (lesson in lessons_list) {
            db.execSQL("INSERT OR IGNORE INTO lessons VALUES(?,?,?,?,?)", arrayOf(lesson.hash, lesson.date.time, lesson.content, subject_id, teacher_id))
        }
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    //endregion

    //region PROFESSORS
    fun addProfessor(subject_id: Int, teacher_id: Int, teacher_name: String) {
        val db = writableDatabase
        db.execSQL("INSERT OR IGNORE INTO teachers VALUES(?,?)", arrayOf(teacher_id, teacher_name))
        //inserisci se in subject_teacher non esiste un record (teacher_id, subject_id)
        db.execSQL("INSERT INTO subject_teacher SELECT * FROM (SELECT ?, ?) AS tmp WHERE NOT EXISTS (SELECT teacher_id FROM subject_teacher WHERE teacher_id = ? AND subject_id=?) LIMIT 1", arrayOf<Any>(teacher_id, subject_id, teacher_id, subject_id))
    }

    fun getProfessorCodes(subject_id: Int): List<Int> {
        val p = ArrayList<Int>()
        val db = readableDatabase
        val c = db.rawQuery("SELECT teacher_id FROM subject_teacher WHERE subject_id = ? GROUP BY teacher_id", arrayOf(subject_id.toString()))

        c.moveToFirst()
        while (!c.isAfterLast) {
            p.add(c.getInt(0))
            c.moveToNext()
        }

        c.close()
        return p
    }

    val professors: List<Pair<Int, String>>
        get() {
            val names = ArrayList<Pair<Int, String>>()
            val db = readableDatabase

            val c = db.rawQuery("SELECT teachers.id, teachers.name FROM teachers LEFT JOIN subject_teacher ON teachers.id=subject_teacher.teacher_id LEFT JOIN subjects ON subject_teacher.subject_id=subjects.id WHERE username=? GROUP BY teachers.id ORDER BY teachers.name ASC", arrayOf(currentProfile()))
            c.moveToFirst()
            while (!c.isAfterLast) {
                names.add(Pair.create(c.getInt(0), c.getString(1)))
                c.moveToNext()
            }
            c.close()
            return names
        }

    fun getSubjectOrProfessorName(teacher_id: String): String {
        val db = readableDatabase
        val c = db.rawQuery("SELECT coalesce(subjects.name,subjects.original_name), teachers.name FROM teachers LEFT JOIN subject_teacher ON teachers.id=subject_teacher.teacher_id LEFT JOIN subjects ON subject_teacher.subject_id=subjects.id WHERE teacher_id=?", arrayOf(teacher_id.toString()))
        var s = ""

        if (c.moveToFirst()) {
            if (c.count == 1) {
                s = c.getString(0)
            } else {
                s = c.getString(1)
            }
        }

        c.close()
        return s.toLowerCase()
    }

    fun getProfessorName(teacher_id: String): String {
        if (TextUtils.isEmpty(teacher_id)) return ""
        val db = readableDatabase
        val c = db.rawQuery("SELECT name FROM teachers WHERE id=?", arrayOf(teacher_id))
        val s = if (c.moveToFirst()) c.getString(0) else ""
        c.close()
        return s
    }
    //endregion

    //region MARKS
    fun addMarks(markSubjects: List<MarkSubject>) {
        val db = writableDatabase
        db.beginTransaction()
        var name: String
        var query: String
        for (subject in markSubjects) {
            name = subject.name
            if (name.contains("...")) {
                name = name.replace("...", "%").toLowerCase()
                query = "SELECT subjects.id FROM subjects LEFT JOIN subject_teacher ON subject_teacher.subject_id=subjects.id LEFT JOIN teachers ON teachers.id=teacher_id WHERE (lower(subjects.original_name) LIKE ? OR lower(subjects.name) LIKE ?) AND username=?"
            } else {
                query = "SELECT subjects.id FROM subjects LEFT JOIN subject_teacher ON subject_teacher.subject_id=subjects.id LEFT JOIN teachers ON teachers.id=teacher_id WHERE (lower(subjects.original_name) LIKE ? OR lower(subjects.name) LIKE ?) AND username=?"
            }
            for (mark in subject.marks) {
                db.execSQL("INSERT OR REPLACE INTO marks VALUES(?,($query),?,?,?,?,?,?)", arrayOf(mark.hash, name, name, currentProfile(), mark.mark, mark.desc, mark.date.time, mark.type, mark.q, if (mark.isNs) 1 else 0))
            }
        }
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    fun getMarks(subject_id: Int): MarkSubject {
        val marks = ArrayList<Mark>()
        val markSubject = MarkSubject("", marks)

        val db = readableDatabase
        val c = db.rawQuery("SELECT coalesce(subjects.name,subjects.original_name), marks.mark, marks.description, marks.date,marks.type,marks.period,marks.not_significant FROM marks LEFT JOIN subjects ON subjects.id=marks.subject_id WHERE marks.subject_id=?", arrayOf(subject_id.toString()))

        c.moveToFirst()
        while (!c.isAfterLast) {
            marks.add(Mark(c.getString(5), c.getInt(6) == 1, c.getString(4), Date(c.getLong(3)), c.getString(1), c.getString(2)))
            c.moveToNext()
        }

        if (c.moveToFirst()) {
            markSubject.name = c.getString(0)
            markSubject.marks = marks
        }
        c.close()
        return markSubject
    }

    fun getMarks(subject_id: Int, period: Period): MarkSubject {
        val marks = ArrayList<Mark>()
        val markSubject = MarkSubject("", marks)

        val db = readableDatabase
        var args = arrayOf(subject_id.toString())
        if (period != Period.ALL)
            args = arrayOf(subject_id.toString(), period.value)
        val c = db.rawQuery("SELECT coalesce(subjects.name,subjects.original_name), marks.mark, marks.description, marks.date,marks.type,marks.period,marks.not_significant FROM marks LEFT JOIN subjects ON subjects.id=marks.subject_id WHERE marks.subject_id=? " + if (period != Period.ALL) "AND marks.period=?" else "", args)

        c.moveToFirst()
        while (!c.isAfterLast) {
            marks.add(Mark(c.getString(5), c.getInt(6) == 1, c.getString(4), Date(c.getLong(3)), c.getString(1), c.getString(2)))
            c.moveToNext()
        }

        if (c.moveToFirst()) {
            markSubject.name = c.getString(0)
            markSubject.marks = marks
        }
        c.close()
        return markSubject
    }

    fun hasMarks(period: Period): Boolean {
        val db = readableDatabase
        val c = db.rawQuery("SELECT id FROM marks WHERE period=? AND not_significant=0", arrayOf(period.value))
        val ex = c.moveToFirst()
        c.close()
        return ex
    }


    fun getAverages(period: Period, sort_by: String): List<Average> {
        val avg = ArrayList<Average>()
        val db = readableDatabase
        var args = arrayOf(currentProfile())
        if (period != Period.ALL)
            args = arrayOf(currentProfile(), period.value)
        val c = db.rawQuery("SELECT coalesce(subjects.name,subjects.original_name) as _name, AVG(marks.mark) as _avg, marks.subject_id, COUNT(marks.mark), subjects.target FROM marks LEFT JOIN subjects ON marks.subject_id=subjects.id WHERE marks.not_significant=0 AND subjects.username=? " + (if (period != Period.ALL) "AND marks.period=?" else "") + " GROUP BY subjects.id " + sort_by, args)

        c.moveToFirst()
        while (!c.isAfterLast) {
            avg.add(Average(c.getString(0), c.getInt(2), c.getFloat(1), c.getInt(3), c.getFloat(4)))
            c.moveToNext()
        }
        c.close()
        return avg
    }

    fun getAverage(p: Period): Double {
        var avg = 0.0
        val db = readableDatabase
        var args = arrayOf(currentProfile())
        if (p != Period.ALL)
            args = arrayOf(currentProfile(), p.value)
        val c = db.rawQuery("SELECT AVG(marks.mark) FROM marks LEFT JOIN subjects ON marks.subject_id=subjects.id WHERE marks.not_significant=0 AND subjects.username=?  " + if (p != Period.ALL) "AND marks.period=?" else "", args)
        if (c.moveToNext())
            avg = c.getDouble(0)
        c.close()
        return avg
    }
    //endregion

    //region PROFILES
    fun addProfile(profile: IProfile<*>) {
        val db = writableDatabase
        val cv = ContentValues()

        if (profile.name != null) cv.put("name", profile.name.text)
        cv.put("username", profile.email.text)

        db.beginTransaction()
        db.insertWithOnConflict(TABLE_PROFILES, null, cv, SQLiteDatabase.CONFLICT_IGNORE)
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    fun updateProfile(profile: IProfile<*>) {
        val db = writableDatabase
        val cv = ContentValues()

        cv.put("name", profile.name.text)

        db.update("profiles", cv, "username=?", arrayOf(profile.email.text))
    }

    val profiles: List<IProfile<*>>
        get() {
            val db = readableDatabase
            val c = db.rawQuery("SELECT username, name FROM profiles", null)
            val profiles = ArrayList<IProfile<*>>()

            c.moveToFirst()
            while (!c.isAfterLast) {
                try {
                    profiles.add(ProfileDrawerItem().withName(c.getString(1)).withEmail(c.getString(0)).withNameShown(true).withIcon(AccountImage(c.getString(1))).withIdentifier(BigInteger(MessageDigest.getInstance("SHA-256").digest(c.getString(0).toByteArray())).toLong()))
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                    (mContext as Activity).finish()
                }

                c.moveToNext()
            }

            c.close()

            return profiles
        }

    fun removeProfile(user: String) {
        val db = writableDatabase
        db.beginTransaction()
        db.delete("profiles", "username=?", arrayOf(user))
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    val profile: IProfile<*>
        get() {
            val db = readableDatabase
            val c = db.rawQuery("SELECT username, name FROM profiles WHERE username=?", arrayOf(currentProfile()))
            val iProfile = ProfileDrawerItem()
            if (c.moveToFirst()) {
                try {
                    iProfile.withName(c.getString(1)).withEmail(c.getString(0)).withNameShown(true).withIcon(AccountImage(c.getString(1))).withIdentifier(BigInteger(MessageDigest.getInstance("SHA-256").digest(c.getString(0).toByteArray())).toLong())
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                    (mContext as Activity).finish()
                }

            }
            c.close()
            return iProfile
        }
    //endregion

    //region COOKIES
    fun addCookies(username: String, cookies: Collection<Cookie>) {
        val db = writableDatabase
        db.beginTransaction()
        for (c in cookies) {
            db.execSQL("INSERT OR REPLACE INTO cookies (username, key, value)" + "VALUES (?, ?, COALESCE((SELECT value FROM cookies WHERE username=? AND key=?),?))", arrayOf(username, createCookieKey(c), username, createCookieKey(c), SerializableCookie().encode(c)))
        }

        db.setTransactionSuccessful()
        db.endTransaction()
    }

    fun getCookies(username: String): List<Cookie> {
        val db = readableDatabase
        val cookies = ArrayList<Cookie>()
        val c = db.rawQuery("SELECT value FROM $TABLE_COOKIES WHERE username=?", arrayOf(username))
        c.moveToFirst()
        while (!c.isAfterLast) {
            cookies.add(SerializableCookie().decode(c.getString(0)))
            c.moveToNext()
        }
        c.close()
        return cookies
    }

    fun removeCookies(cookies: Collection<Cookie>) {
        val db = writableDatabase
        db.beginTransaction()

        for (c in cookies)
            db.delete(TABLE_COOKIES, "key = ? AND value = ?", arrayOf(createCookieKey(c), SerializableCookie().encode(c)))

        db.setTransactionSuccessful()
        db.endTransaction()
    }

    fun removeCookies() {
        val db = writableDatabase
        db.beginTransaction()
        db.delete(TABLE_COOKIES, null, null)
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    //endregion

    //region FOLDERS
    fun addFileTeachers(fileTeacherList: List<FileTeacher>) {
        val db = this.writableDatabase
        var folder_id: String
        db.beginTransaction()
        for (teacher_folders in fileTeacherList) {
            for (folder in teacher_folders.folders) {
                folder.profName = teacher_folders.name
                folder_id = folder.hash
                db.execSQL("INSERT OR REPLACE INTO folders VALUES(?,?,?)", arrayOf(folder_id, folder.name, folder.last.time))
                //Inserisci solamente se la cartella non è gia presente nel db
                db.execSQL("INSERT INTO teacher_folder SELECT * FROM (SELECT (SELECT id FROM teachers WHERE lower(name)=?), ?, ?, ?) AS tmp WHERE NOT EXISTS (SELECT folder_id FROM teacher_folder WHERE teacher_id = (SELECT id FROM teachers WHERE lower(name)=? LIMIT 1) AND folder_id=?) LIMIT 1", arrayOf<Any>(folder.profName.toLowerCase(), folder_id, folder.profName, currentProfile(), folder.profName.toLowerCase(), folder_id))
                for (f in folder.elements) {
                    db.execSQL("INSERT OR REPLACE INTO files VALUES(?,?,?,?,?,?,?,NULL,?)", arrayOf(f.id, f.name, f.type, f.date.time, f.cksum, f.link, if (f.isHidden) 1 else 0, folder_id))
                }
            }
        }
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    //Select from the right user
    val fileTeachers: List<FileTeacher>
        get() {
            val db = this.readableDatabase
            val teachers = db.rawQuery("SELECT teacher_id,teacher_name FROM teacher_folder WHERE username=? GROUP BY teacher_id ", arrayOf(currentProfile()))
            var folders: Cursor
            var files: Cursor

            val teachers_folders = ArrayList<FileTeacher>()
            val temp_folders = ArrayList<Folder>()
            val temp_files = ArrayList<File>()
            var temp_teacher_id: Int
            var temp_teacher_name: String
            var temp_folder_id: String

            teachers.moveToFirst()
            while (!teachers.isAfterLast) {
                temp_teacher_id = teachers.getInt(0)
                temp_teacher_name = teachers.getString(1)
                folders = db.rawQuery("SELECT folders.* FROM teacher_folder LEFT JOIN folders ON folders.id = teacher_folder.folder_id WHERE teacher_id=? OR teacher_name=?", arrayOf(temp_teacher_id.toString(), temp_teacher_name))

                folders.moveToFirst()
                while (!folders.isAfterLast) {
                    temp_folder_id = folders.getString(0)
                    files = db.rawQuery("SELECT * FROM files WHERE folder_id=?", arrayOf(temp_folder_id))

                    files.moveToFirst()
                    while (!files.isAfterLast) {
                        temp_files.add(File(files.getString(0), files.getString(1), files.getString(2), Date(files.getLong(3)), files.getString(4), files.getString(5), files.getInt(6) == 1))
                        files.moveToNext()
                    }

                    temp_folders.add(Folder(folders.getString(1), Date(folders.getLong(2)), ArrayList(temp_files)))
                    files.close()
                    temp_files.clear()
                    folders.moveToNext()
                }

                teachers_folders.add(FileTeacher(teachers.getString(1), ArrayList(temp_folders)))

                folders.close()
                temp_folders.clear()
                teachers.moveToNext()
            }


            teachers.close()
            return teachers_folders
        }

    fun setFileDownloaded(cksum: String, code: String, filename: String) {
        val cv = ContentValues()
        cv.put("filename", filename)
        writableDatabase.update("files", cv, "cksum=? AND id=?", arrayOf(cksum, code))
    }
    //endregion

    //region FILES
    fun isFileDownloaded(id: String, cksum: String): Boolean {
        val db = readableDatabase
        val c = db.rawQuery("SELECT * FROM files WHERE id=? AND cksum=? AND filename IS NOT NULL", arrayOf(id, cksum))
        val bool = c.moveToFirst()
        c.close()
        return bool
    }

    fun getFileName(id: String, cksum: String): String {
        val db = readableDatabase
        val c = db.rawQuery("SELECT filename FROM files WHERE id=? AND cksum=? AND filename IS NOT NULL", arrayOf(id, cksum))
        var st = ""
        if (c.moveToFirst()) st = c.getString(0)
        c.close()
        return st
    }
    //endregion

    //region COMMUNICATIONS
    fun addCommunications(communicationList: List<Communication>) {
        val db = writableDatabase
        db.beginTransaction()
        for (c in communicationList) {
            db.execSQL("INSERT OR IGNORE INTO communications VALUES(?,?,(select content from communications where id=?),?,?,(select filename from communications where id=?),(select attachment from communications where id=?),?)", arrayOf(c.id, c.title, c.id, c.date.time, c.type, c.id, c.id, currentProfile()))
        }
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    fun setCommunicationFilename(id: Int, filename: String) {
        val db = writableDatabase
        db.beginTransaction()
        db.execSQL("UPDATE communications SET filename=? WHERE id=?", arrayOf(filename, id))
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    fun updateCommunication(id: Int, cd: CommunicationDescription) {
        val db = writableDatabase
        db.beginTransaction()
        db.execSQL("UPDATE communications SET content=?, attachment=? WHERE id=?", arrayOf<Any>(cd.desc.trim { it <= ' ' }, if (cd.isAttachment) 1 else 0, id))
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    val communications: List<SuperCommunication>
        get() {
            val db = readableDatabase
            val list = ArrayList<SuperCommunication>()
            val c = db.rawQuery("SELECT * FROM communications WHERE username=? ORDER BY date DESC", arrayOf(currentProfile()))
            c.moveToFirst()
            while (!c.isAfterLast) {
                list.add(SuperCommunication(c.getInt(0), c.getString(1), c.getString(2), Date(c.getLong(3)), c.getString(4), c.getString(5), c.getInt(6) == 1))
                c.moveToNext()
            }
            c.close()
            return list
        }
    //endregion

    //region NOTES
    fun addNotes(noteList: List<Note>) {
        val db = writableDatabase
        db.beginTransaction()
        for (n in noteList) {
            db.execSQL("INSERT OR IGNORE INTO notes VALUES(?,?,?,?,?,(select id from teachers where lower(name)=? limit 1))", arrayOf(n.hash, n.content, n.date.time, n.type, currentProfile(), n.teacher.toLowerCase()))
        }
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    val notes: List<Note>
        get() {
            val db = readableDatabase
            val c = db.rawQuery("SELECT teachers.name, notes.content, notes.date, notes.type FROM notes LEFT JOIN teachers ON teachers.id=notes.teacher_id WHERE notes.username=? ORDER BY notes.date DESC", arrayOf(currentProfile()))
            val list = ArrayList<Note>()
            c.moveToFirst()
            while (!c.isAfterLast) {
                list.add(Note(c.getString(0), c.getString(1), Date(c.getLong(2)), c.getString(3)))
                c.moveToNext()
            }
            c.close()
            return list
        }
    //endregion

    private fun currentProfile(): String {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getString("currentProfile", "")
    }


    enum class Period constructor(val value: String) {
        FIRST("q1"),
        SECOND("q3"),
        ALL("")

    }

    companion object {
        private val DB_NAME = "RegistroDB"
        private val TABLE_API = "api_events"
        private val TABLE_LOCAL = "local_events"
        private val TABLE_SUBJECTS = "subjects"
        private val TABLE_LESSONS = "lessons"
        private val TABLE_PROFESSORS = "teachers"
        private val TABLE_MARKS = "marks"
        private val TABLE_PROFILES = "profiles"
        private val TABLE_COOKIES = "cookies"

        var instance: RegistroDB? = null
        private val DB_VERSION = 26

        fun getInstance(c: Context): RegistroDB? {
            if (instance == null)
                instance = RegistroDB(c)
            return instance
        }
    }
}
