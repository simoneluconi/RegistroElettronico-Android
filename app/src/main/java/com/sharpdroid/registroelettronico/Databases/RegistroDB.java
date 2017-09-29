package com.sharpdroid.registroelettronico.Databases;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.franmontiel.persistentcookiejar.persistence.SerializableCookie;
import com.github.mikephil.charting.data.Entry;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.sharpdroid.registroelettronico.Info;
import com.sharpdroid.registroelettronico.Interfaces.API.Absence;
import com.sharpdroid.registroelettronico.Interfaces.API.Absences;
import com.sharpdroid.registroelettronico.Interfaces.API.Communication;
import com.sharpdroid.registroelettronico.Interfaces.API.CommunicationDescription;
import com.sharpdroid.registroelettronico.Interfaces.API.Delay;
import com.sharpdroid.registroelettronico.Interfaces.API.Event;
import com.sharpdroid.registroelettronico.Interfaces.API.Exit;
import com.sharpdroid.registroelettronico.Interfaces.API.File;
import com.sharpdroid.registroelettronico.Interfaces.API.FileTeacher;
import com.sharpdroid.registroelettronico.Interfaces.API.Folder;
import com.sharpdroid.registroelettronico.Interfaces.API.Lesson;
import com.sharpdroid.registroelettronico.Interfaces.API.LessonSubject;
import com.sharpdroid.registroelettronico.Interfaces.API.Mark;
import com.sharpdroid.registroelettronico.Interfaces.API.MarkSubject;
import com.sharpdroid.registroelettronico.Interfaces.API.Note;
import com.sharpdroid.registroelettronico.Interfaces.Client.AdvancedEvent;
import com.sharpdroid.registroelettronico.Interfaces.Client.Average;
import com.sharpdroid.registroelettronico.Interfaces.Client.LocalEvent;
import com.sharpdroid.registroelettronico.Interfaces.Client.Subject;
import com.sharpdroid.registroelettronico.Interfaces.Client.SuperCommunication;
import com.sharpdroid.registroelettronico.Utils.Metodi;

import org.apache.commons.lang3.text.WordUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Cookie;

import static com.sharpdroid.registroelettronico.Utils.Metodi.AccountImage;
import static com.sharpdroid.registroelettronico.Utils.Metodi.createCookieKey;

public class RegistroDB extends SQLiteOpenHelper {
    private final static String DB_NAME = "RegistroDB";
    private final static String TABLE_API = "api_events";
    private final static String TABLE_LOCAL = "local_events";
    private final static String TABLE_SUBJECTS = "subject";
    private final static String TABLE_LESSONS = "lessons";
    private final static String TABLE_PROFESSORS = "teachers";
    private final static String TABLE_MARKS = "marks";
    private final static String TABLE_PROFILES = "profiles";
    private final static String TABLE_COOKIES = "cookies";

    public static RegistroDB instance;
    private static int DB_VERSION = 27;
    private Context mContext;

    public RegistroDB(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
        mContext = c;
    }

    public static synchronized RegistroDB getInstance(Context c) {
        if (instance == null)
            instance = new RegistroDB(c);
        return instance;
    }

    @Override
    public synchronized void close() {
        super.close();
        instance = null;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        db.execSQL("CREATE TABLE profiles(username TEXT NOT NULL, name TEXT, class TEXT, PRIMARY KEY (username));");
        db.execSQL("CREATE TABLE teachers(id INTEGER NOT NULL, name TEXT NOT NULL, PRIMARY KEY (id));");
        db.execSQL("CREATE TABLE cookies(username TEXT NOT NULL, `key` TEXT NOT NULL, value TEXT NOT NULL, FOREIGN KEY (username) REFERENCES profiles(username) ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE api_events(id INTEGER NOT NULL, title TEXT NOT NULL, content TEXT, teacher_id INTEGER NOT NULL, teacher_name TEXT NOT NULL, PRIMARY KEY (id), FOREIGN KEY (username) REFERENCES profiles(username) ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE folders(id TEXT PRIMARY KEY, name TEXT NOT NULL, date INTEGER NOT NULL);");
        db.execSQL("CREATE TABLE files(id INTEGER NOT NULL, name TEXT NOT NULL, type TEXT NOT NULL, date INTEGER NOT NULL, cksum TEXT, link TEXT, hidden INTEGER NOT NULL, filename TEXT, folder_id INTEGER NOT NULL, PRIMARY KEY (id), FOREIGN KEY (folder_id) REFERENCES folders(id) ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE communications(id INTEGER NOT NULL, title TEXT NOT NULL, content TEXT, date INTEGER NOT NULL, type TEXT NOT NULL, filename TEXT, attachment INTEGER, username TEXT NOT NULL, PRIMARY KEY (id), FOREIGN KEY (username) REFERENCES profiles(username) ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE notes(id TEXT PRIMARY KEY, content TEXT, date INTEGER NOT NULL, type TEXT NOT NULL, username TEXT NOT NULL, teacher_id INTEGER NOT NULL, FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE, FOREIGN KEY (username) REFERENCES profiles(username) ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE subjects(id INTEGER NOT NULL, original_name TEXT NOT NULL, name TEXT, target FLOAT, classroom TEXT, notes TEXT, username TEXT NOT NULL, PRIMARY KEY (id), FOREIGN KEY (username) REFERENCES profiles(username) ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE lessons (id TEXT NOT NULL, date INTEGER NOT NULL, content TEXT NOT NULL, subject_id INTEGER NOT NULL, teacher_id INTEGER NOT NULL, PRIMARY KEY(id), FOREIGN KEY(subject_id) REFERENCES subjects(id) ON DELETE CASCADE, FOREIGN KEY(teacher_id) REFERENCES teachers(id) ON DELETE CASCADE)");
        db.execSQL("CREATE TABLE marks (id TEXT NOT NULL, subject_id INTEGER NOT NULL, mark TEXT NOT NULL, description TEXT, date INTEGER NOT NULL, type TEXT NOT NULL, period TEXT NOT NULL, not_significant INTEGER NOT NULL, PRIMARY KEY(id), FOREIGN KEY(subject_id) REFERENCES subjects(id) ON DELETE CASCADE)");
        db.execSQL("CREATE TABLE subject_teacher(teacher_id INTEGER NOT NULL, subject_id INTEGER NOT NULL, FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE, FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE teacher_folder(teacher_id INTEGER, folder_id INTEGER NOT NULL, teacher_name TEXT NOT NULL, username TEXT NOT NULL, FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE, FOREIGN KEY (folder_id) REFERENCES folders(id) ON DELETE CASCADE, FOREIGN KEY (username) REFERENCES profiles(username) ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE local_events ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, title TENOT NULL, start INTEGER NOT NULL, end INTEGER NOT NULL, all_day INTEGER NOT NULL, type INTEGER NOT NULL, completed INTEGER, archived INTEGER, username TEXT NOT NULL, teXT NOT NULL, content TEXT NOT NULL, type TEXT NOT NULL, day INTEGER NOT NULL, teacher_id INTEGER, subject_id INTEGER, completed INTEGER, archived INTEGER, username TEXT NOT NULL, FOREIGN KEY(username) REFERENCES profiles(username) ON DELETE CASCADE, FOREIGN KEY(teacher_id) REFERENCES teachers(id) ON DELETE CASCADE, FOREIGN KEY(subject_id) REFERENCES subjects(id) ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE absences(id TEXT PRIMARY KEY,done INTEGER NOT NULL,_from INTEGER NOT NULL,_to INTEGER, days INTEGER, hours INTEGER, justification TEXT, type TEXT NOT NULL, username TEXT NOT NULL, FOREIGN KEY (username) REFERENCES profiles(username) ON DELETE CASCADE);");

        if (DB_VERSION != 27)
            onUpgrade(db, 1, DB_VERSION);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 27) {
            db.beginTransaction();
            db.execSQL("DROP TABLE IF EXISTS api");
            db.execSQL("DROP TABLE IF EXISTS archive");
            db.execSQL("DROP TABLE IF EXISTS completed");
            db.execSQL("DROP TABLE IF EXISTS cookies");
            db.execSQL("DROP TABLE IF EXISTS lessons");
            db.execSQL("DROP TABLE IF EXISTS local");
            db.execSQL("DROP TABLE IF EXISTS marks");
            db.execSQL("DROP TABLE IF EXISTS professors");
            db.execSQL("DROP TABLE IF EXISTS teachers");
            db.execSQL("DROP TABLE IF EXISTS subject_teacher");
            db.execSQL("DROP TABLE IF EXISTS profiles");
            db.execSQL("DROP TABLE IF EXISTS subjects");
            db.execSQL("DROP TABLE IF EXISTS communications");
            db.execSQL("DROP TABLE IF EXISTS folders");
            db.execSQL("DROP TABLE IF EXISTS files");
            db.execSQL("DROP TABLE IF EXISTS api_events");
            db.execSQL("DROP TABLE IF EXISTS notes");
            db.execSQL("DROP TABLE IF EXISTS local_events");
            db.execSQL("DROP TABLE IF EXISTS teacher_folder");
            db.setTransactionSuccessful();
            db.endTransaction();
            onCreate(db);
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean("first_run", true).apply();
        }
    }

    //region EVENTS
    //region API
    public void addEvents(List<Event> events) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        if (events.size() > 0) {
            setClassDescription(events.get(0).getClasse_desc());
        }
        String currentProfile = currentProfile();
        db.delete("api_events", null, null);
        for (Event e : events) {
            db.execSQL("INSERT OR IGNORE INTO api_events VALUES(?,?,?,?,?,?,?,(SELECT completed FROM api_events WHERE id = ?),(SELECT archived FROM api_events WHERE id = ?),?,?,?)", new Object[]{e.getId(), e.getTitle(), e.getNota_2(), e.getStart().getTime(), e.getEnd().getTime(), e.isAllDay() ? 1 : 0, e.getTipo(), e.getId(), e.getId(), currentProfile, e.getAutore_id(), e.getAutore_desc()});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<AdvancedEvent> getEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        String currentProfile = currentProfile();
        Cursor c = db.rawQuery("SELECT api_events.id, title, start,end,all_day, content,teachers.id AS teacher_id, coalesce(teachers.name, teacher_name),type,completed FROM api_events LEFT JOIN teachers ON teacher_id=teachers.id WHERE archived IS NULL AND username=?", new String[]{currentProfile});
        List<AdvancedEvent> list = new ArrayList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            list.add(new AdvancedEvent(c.getString(0), c.getString(1), new Date(c.getLong(2)), new Date(c.getLong(3)), c.getInt(4) == 1, null, c.getString(5), null, null, null, 0, c.getString(7), c.getString(6), c.getString(8), null, null, c.getLong(9)));
        }

        c.close();
        return list;
    }

    public List<AdvancedEvent> getEvents(long day) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT api_events.id, title, start,end,all_day, content,teachers.id AS teacher_id, teachers.name AS teacher_name,type,completed FROM api_events LEFT JOIN teachers ON teacher_id=teachers.id WHERE archived IS NULL AND (api_events.start BETWEEN ? AND ?) AND username=?", new String[]{String.valueOf(day), String.valueOf(day + 86399999), currentProfile()});
        List<AdvancedEvent> list = new ArrayList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            list.add(new AdvancedEvent(c.getString(0), c.getString(1), new Date(c.getLong(2)), new Date(c.getLong(3)), c.getInt(4) == 1, null, c.getString(5), null, null, null, 0, c.getString(7), c.getString(6), c.getString(8), null, null, c.getLong(9)));
        }
        c.close();
        return list;
    }

    //endregion
    //region LOCAL
    public void addLocalEvent(LocalEvent e) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values;
        values = new ContentValues();
        values.put("title", e.getTitle());
        values.put("content", e.getContent());
        values.put("type", e.getType());
        values.put("teacher_id", e.getProfId());
        values.put("subject_id", e.getSubjectId());
        values.put("day", e.getDay().getTime());
        values.put("username", currentProfile());
        db.insert("local_events", null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<AdvancedEvent> getLocalEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        String currentProfile = currentProfile();
        Cursor c = db.rawQuery("SELECT local_events.id,title,content,type,day,subject_id,teacher_id,completed,coalesce(subjects.name,subjects.original_name) AS subject_name, teachers.name AS teacher_name FROM local_events LEFT JOIN subjects ON subject_id = subjects.id LEFT JOIN teachers ON teacher_id = teachers.id WHERE local_events.archived IS NULL AND local_events.username=?", new String[]{currentProfile});
        List<AdvancedEvent> list = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            list.add(new AdvancedEvent(c.getString(0), c.getString(1), new Date(c.getLong(4)), null, true, null, c.getString(2), null, null, null, 0, c.getString(9), c.getString(6), c.getString(3), c.getString(8), c.getString(5), c.getLong(7)));
        }

        c.close();
        return list;
    }

    public List<AdvancedEvent> getLocalEvents(long day) {
        SQLiteDatabase db = this.getReadableDatabase();
        String currentProfile = currentProfile();
        Cursor c = db.rawQuery("SELECT local_events.id,title,content,type,day,subject_id,teacher_id,completed,coalesce(subjects.name,subjects.original_name) AS subject_name, teachers.name AS teacher_name FROM local_events LEFT JOIN subjects ON subject_id = subjects.id LEFT JOIN teachers ON teacher_id = teachers.id WHERE local_events.archived IS NULL AND day BETWEEN ? AND ? AND local_events.username=?", new String[]{String.valueOf(day), String.valueOf(day + 86399999), currentProfile});
        List<AdvancedEvent> list = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            list.add(new AdvancedEvent(c.getString(0), c.getString(1), new Date(c.getLong(4)), null, true, null, c.getString(2), null, null, null, 0, c.getString(9), c.getString(6), c.getString(3), c.getString(8), c.getString(5), c.getLong(7)));
        }
        c.close();
        return list;
    }
    //endregion

    public String getClassDescription() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT class FROM profiles WHERE username=?", new String[]{currentProfile()});
        String s = "";
        if (c.moveToFirst()) s = c.getString(0);
        c.close();
        return s;
    }

    public void setClassDescription(String c) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("class", c);
        db.update("profiles", cv, "username=?", new String[]{currentProfile()});
    }


    public List<AdvancedEvent> getAllEvents() {
        List<AdvancedEvent> list = new ArrayList<>();
        list.addAll(getLocalEvents());
        list.addAll(getEvents());
        return list;
    }

    public List<AdvancedEvent> getAllEvents(long day) {
        List<AdvancedEvent> list = new ArrayList<>();
        list.addAll(getLocalEvents(day));
        list.addAll(getEvents(day));
        return list;
    }
    //endregion

    //region COMPLETED
    public void setCompleted(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE api_events SET completed = ? WHERE id=?", new Object[]{System.currentTimeMillis(), id});
        db.execSQL("UPDATE local_events SET completed = ? WHERE id=?", new Object[]{System.currentTimeMillis(), id});
    }

    public void setUncompleted(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE api_events SET completed = NULL WHERE id=?", new Object[]{id});
        db.execSQL("UPDATE local_events SET completed = NULL WHERE id=?", new Object[]{id});
    }

    public boolean isCompleted(String id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT api_events.id, api_events.completed FROM api_events WHERE id=? AND completed IS NOT NULL UNION ALL SELECT local_events.id, local_events.completed FROM local_events WHERE id=? AND completed IS NOT NULL", new String[]{id, id});
        boolean completed = c.moveToFirst();
        c.close();
        return completed;
    }

    //endregion

    //region ARCHIVE
    public void archive(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE api_events SET archived = ? WHERE id=?", new Object[]{System.currentTimeMillis(), id});
        db.execSQL("UPDATE local_events SET archived = ? WHERE id=?", new Object[]{System.currentTimeMillis(), id});
    }

    public void clearArchive() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE api_events SET archived = NULL");
        db.execSQL("UPDATE local_events SET archived = NULL");
    }
    //endregion

    //region SUBJECT
    public Subject getSubject(int code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Subject subject = null;
        String currentProfile = currentProfile();
        Cursor c = db.rawQuery("SELECT subjects.id, coalesce(subjects.name, subjects.original_name) AS name, target,classroom,notes,GROUP_CONCAT(teacher_id),GROUP_CONCAT(teachers.name) FROM subjects LEFT JOIN subject_teacher ON subject_teacher.subject_id=subjects.id LEFT JOIN teachers ON teachers.id=teacher_id WHERE subject_id=? AND username=?", new String[]{String.valueOf(code), currentProfile});

        if (c.moveToFirst())
            subject = new Subject(c.getInt(0), WordUtils.capitalizeFully(c.getString(1), Metodi.Delimeters), c.getFloat(2), c.getString(6), c.getString(3), c.getString(4), c.getString(5).split(","));

        c.close();
        return subject;
    }

    public Subject getSubject(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Subject subject = null;
        Cursor c;
        if (name.contains("...")) {
            name = name.replace("...", "%").toLowerCase();
            c = db.rawQuery("SELECT subjects.id, coalesce(subjects.name, subjects.original_name) AS _name, target,classroom,notes,GROUP_CONCAT(teacher_id),GROUP_CONCAT(teachers.name) FROM subjects INNER JOIN subject_teacher ON subject_teacher.subject_id=subjects.id LEFT JOIN teachers ON teachers.id=teacher_id WHERE (lower(subjects.original_name) LIKE ? OR lower(subjects.name) LIKE ?) AND username=?", new String[]{name, name, currentProfile()});
        } else {
            c = db.rawQuery("SELECT subjects.id, coalesce(subjects.name, subjects.original_name) AS _name, target,classroom,notes,GROUP_CONCAT(teacher_id),GROUP_CONCAT(teachers.name) FROM subjects INNER JOIN subject_teacher ON subject_teacher.subject_id=subjects.id LEFT JOIN teachers ON teachers.id=teacher_id WHERE (lower(subjects.original_name) LIKE ? OR lower(subjects.name) LIKE ?) AND username=?", new String[]{name, name, currentProfile()});
        }
        if (c.moveToFirst())
            subject = new Subject(c.getInt(0), WordUtils.capitalizeFully(c.getString(1), Metodi.Delimeters), c.getFloat(2), c.getString(6), c.getString(3), c.getString(4), c.getString(5).split(","));


        c.close();
        return subject;
    }

    public List<Subject> getSubjects() {
        List<Subject> subjects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id FROM subjects WHERE username=?", new String[]{currentProfile()});

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            subjects.add(getSubject(c.getInt(0)));
        }

        c.close();
        return subjects;
    }

    public void editSubject(int code, ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.update(TABLE_SUBJECTS, contentValues, "id = ? AND username=?", new String[]{String.valueOf(code), currentProfile()});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void addSubject(LessonSubject subject) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        db.execSQL("INSERT OR IGNORE INTO subjects(id,original_name,username) VALUES(?,?,?)", new Object[]{subject.getCode(), subject.getName(), currentProfile()});
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    //endregion

    //region LESSONS
    public List<Lesson> getLessons(int code) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT teachers.name,lessons.date, lessons.content FROM lessons LEFT JOIN teachers ON teachers.id=lessons.teacher_id WHERE subject_id=? ORDER BY date DESC", new String[]{String.valueOf(code)});
        List<Lesson> lessons = new LinkedList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            lessons.add(new Lesson(c.getString(0), new Date(c.getLong(1)), c.getString(2)));
        }
        c.close();
        return lessons;
    }

    public List<Lesson> getLessons(int code, int limit) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT teachers.name,lessons.date, lessons.content FROM lessons LEFT JOIN teachers ON teachers.id=lessons.teacher_id WHERE subject_id=? ORDER BY date DESC LIMIT ?", new String[]{String.valueOf(code), String.valueOf(limit)});
        List<Lesson> lessons = new LinkedList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            lessons.add(new Lesson(c.getString(0), new Date(c.getLong(1)), c.getString(2)));
        }
        c.close();
        return lessons;
    }

    public void addLessons(int subject_id, int teacher_id, List<Lesson> lessons_list) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        for (Lesson lesson : lessons_list) {
            db.execSQL("INSERT OR IGNORE INTO lessons VALUES(?,?,?,?,?)", new Object[]{lesson.getHash(), lesson.getDate().getTime(), lesson.getContent(), subject_id, teacher_id});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    //endregion

    //region PROFESSORS
    public void addProfessor(int subject_id, int teacher_id, String teacher_name) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT OR IGNORE INTO teachers VALUES(?,?)", new Object[]{teacher_id, teacher_name});
        //inserisci se in subject_teacher non esiste un record (teacher_id, subject_id)
        db.execSQL("INSERT INTO subject_teacher SELECT * FROM (SELECT ?, ?) AS tmp WHERE NOT EXISTS (SELECT teacher_id FROM subject_teacher WHERE teacher_id = ? AND subject_id=?) LIMIT 1", new Object[]{teacher_id, subject_id, teacher_id, subject_id});
    }

    public List<Integer> getProfessorCodes(int subject_id) {
        List<Integer> p = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT teacher_id FROM subject_teacher WHERE subject_id = ? GROUP BY teacher_id", new String[]{String.valueOf(subject_id)});

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            p.add(c.getInt(0));
        }

        c.close();
        return p;
    }

    public List<Pair<Integer, String>> getProfessors() {
        List<Pair<Integer, String>> names = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT teachers.id, teachers.name FROM teachers LEFT JOIN subject_teacher ON teachers.id=subject_teacher.teacher_id LEFT JOIN subjects ON subject_teacher.subject_id=subjects.id WHERE username=? GROUP BY teachers.id ORDER BY teachers.name ASC", new String[]{currentProfile()});
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            names.add(Pair.create(c.getInt(0), c.getString(1)));
        }
        c.close();
        return names;
    }

    public String getSubjectOrProfessorName(String teacher_id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT coalesce(subjects.name,subjects.original_name), teachers.name FROM teachers LEFT JOIN subject_teacher ON teachers.id=subject_teacher.teacher_id LEFT JOIN subjects ON subject_teacher.subject_id=subjects.id WHERE teacher_id=?", new String[]{String.valueOf(teacher_id)});
        String s = "";

        if (c.moveToFirst()) {
            if (c.getCount() == 1) {
                s = c.getString(0);
            } else {
                s = c.getString(1);
            }
        }

        c.close();
        return s.toLowerCase();
    }

    public String getProfessorName(String teacher_id) {
        if (TextUtils.isEmpty(teacher_id)) return "";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM teachers WHERE id=?", new String[]{teacher_id});
        String s = c.moveToFirst() ? c.getString(0) : "";
        c.close();
        return s;
    }
    //endregion

    //region MARKS
    public void addMarks(List<MarkSubject> markSubjects) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete("marks", null, null);
        String name, query;
        String currentProfile = currentProfile();

        for (MarkSubject subject : markSubjects) {
            name = subject.getName();
            if (name.contains("...")) {
                name = name.replace("...", "%").toLowerCase();
                query = "SELECT subject.id FROM subject LEFT JOIN subject_teacher ON subject_teacher.subject_id=subject.id LEFT JOIN teachers ON teachers.id=teacher_id WHERE (lower(subject.original_name) LIKE ? OR lower(subject.name) LIKE ?) AND username=?";
            } else {
                query = "SELECT subject.id FROM subject LEFT JOIN subject_teacher ON subject_teacher.subject_id=subject.id LEFT JOIN teachers ON teachers.id=teacher_id WHERE (lower(subject.original_name) LIKE ? OR lower(subject.name) LIKE ?) AND username=?";
            }
            for (Mark mark : subject.getMarks()) {
                if (!mark.isNumeric() && !mark.isNs()) mark.setNs(true);
                db.execSQL("INSERT INTO marks VALUES(?,(" + query + "),?,?,?,?,?,?)", new Object[]{mark.getHash(), name, name, currentProfile, mark.getMark(), mark.getDesc(), mark.getDate().getTime(), mark.getType(), mark.getQ(), mark.isNs() ? 1 : 0});
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public MarkSubject getMarks(int subject_id) {
        return getMarks(subject_id, Period.ALL);
    }

    public MarkSubject getMarks(int subject_id, Period period) {
        List<Mark> marks = new ArrayList<>();
        MarkSubject markSubject = new MarkSubject("", marks);

        SQLiteDatabase db = getReadableDatabase();
        String[] args = new String[]{String.valueOf(subject_id)};
        if (period != Period.ALL)
            args = new String[]{String.valueOf(subject_id), period.getValue()};
        Cursor c = db.rawQuery("SELECT coalesce(subject.name,subject.original_name), marks.mark, marks.description, marks.date,marks.type,marks.period,marks.not_significant FROM marks LEFT JOIN subject ON subject.id=marks.subject_id WHERE marks.subject_id=? " + ((period != Period.ALL) ? "AND marks.period=?" : ""), args);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            marks.add(new Mark(c.getString(5), c.getInt(6) == 1, c.getString(4), new Date(c.getLong(3)), c.getString(1), c.getString(2)));
        }

        if (c.moveToFirst()) {
            markSubject.setName(c.getString(0));
            markSubject.setMarks(marks);
        }
        c.close();
        return markSubject;
    }

    public List<Entry> getMarksAsEntries(int id, Period p) {
        SQLiteDatabase db = getReadableDatabase();
        String val[] = {String.valueOf(id)};
        String query = "SELECT date, AVG(mark) FROM marks WHERE subject_id=? AND not_significant=0 GROUP BY date";
        if (p != Period.ALL) {
            query = "SELECT date, AVG(mark) FROM marks WHERE subject_id=? AND period=? AND not_significant=0 GROUP BY date";
            val = new String[]{String.valueOf(id), p.getValue()};
        }
        Cursor c = db.rawQuery(query, val);
        List<Entry> list = new ArrayList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            list.add(new Entry(c.getLong(0), c.getFloat(1)));
        }
        c.close();
        return list;
    }

    public boolean hasMarks(Period period) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id FROM marks WHERE period=? AND not_significant=0", new String[]{period.getValue()});
        boolean ex = c.moveToFirst();
        c.close();
        return ex;
    }


    public List<Average> getAverages(Period period, String sort_by) {
        List<Average> avg = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {currentProfile()};
        if (period != Period.ALL)
            args = new String[]{currentProfile(), period.getValue()};
        Cursor c = db.rawQuery("SELECT coalesce(subject.name,subject.original_name) as _name, AVG(marks.mark) as _avg, marks.subject_id, COUNT(marks.mark), subject.target FROM marks LEFT JOIN subject ON marks.subject_id=subject.id WHERE marks.not_significant=0 AND subject.username=? " + ((period != Period.ALL) ? "AND marks.period=?" : "") + " GROUP BY subject.id " + sort_by, args);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            avg.add(new Average(c.getString(0), c.getInt(2), c.getFloat(1), c.getInt(3), c.getFloat(4)));
        }
        c.close();
        return avg;
    }

    public double getAverage(Period p) {
        double avg = 0;
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {currentProfile()};
        if (p != Period.ALL)
            args = new String[]{currentProfile(), p.getValue()};
        Cursor c = db.rawQuery("SELECT AVG(_) FROM (SELECT AVG(marks.mark) as _ from marks LEFT JOIN subject ON marks.subject_id=subject.id WHERE subject.username=? AND marks.not_significant=0" + (p != Period.ALL ? " AND marks.period=?" : "") + " GROUP BY marks.subject_id)", args);
        if (c.moveToNext())
            avg = c.getDouble(0);
        c.close();
        return avg;
    }
    //endregion

    //region PROFILES
    public void addProfile(IProfile profile) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        if (profile.getName() != null) cv.put("name", profile.getName().getText());
        cv.put("username", profile.getEmail().getText());

        db.beginTransaction();
        db.insertWithOnConflict(TABLE_PROFILES, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void updateProfile(IProfile profile) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE profiles SET name=? WHERE username=?", new Object[]{profile.getName().getText(), profile.getEmail().getText()});
    }

    public List<IProfile> getProfiles() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT username, name FROM profiles", null);
        List<IProfile> profiles = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            try {
                profiles.add(new ProfileDrawerItem().withName(c.getString(1)).withEmail(c.getString(0)).withNameShown(true).withIcon(AccountImage(c.getString(1))).withIdentifier(new BigInteger(MessageDigest.getInstance("SHA-256").digest(c.getString(0).getBytes())).longValue()));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                ((Activity) mContext).finish();
            }
        }

        c.close();

        return profiles;
    }

    public void removeProfile(String user) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete("profiles", "username=?", new String[]{user});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public IProfile getProfile() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT username, name FROM profiles WHERE username=?", new String[]{currentProfile()});
        ProfileDrawerItem iProfile = new ProfileDrawerItem();
        if (c.moveToFirst()) {
            try {
                iProfile.withName(c.getString(1)).withEmail(c.getString(0)).withNameShown(true).withIcon(AccountImage(c.getString(1))).withIdentifier(new BigInteger(MessageDigest.getInstance("SHA-256").digest(c.getString(0).getBytes())).longValue());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                ((Activity) mContext).finish();
            }
        }
        c.close();
        return iProfile;
    }
    //endregion

    //region COOKIES
    public void addCookies(String username, Collection<Cookie> cookies) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        for (Cookie c : cookies) {
            db.execSQL("INSERT OR REPLACE INTO cookies (username, key, value)" +
                    "VALUES (?, ?, COALESCE((SELECT value FROM cookies WHERE username=? AND key=?),?))", new String[]{username, createCookieKey(c), username, createCookieKey(c), new SerializableCookie().encode(c)});
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<Cookie> getCookies(String username) {
        SQLiteDatabase db = getReadableDatabase();
        List<Cookie> cookies = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT value FROM " + TABLE_COOKIES + " WHERE username=?", new String[]{username});
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            cookies.add(new SerializableCookie().decode(c.getString(0)));
        }
        c.close();
        return cookies;
    }

    public void removeCookies(Collection<Cookie> cookies) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        for (Cookie c : cookies)
            db.delete(TABLE_COOKIES, "key = ? AND value = ?", new String[]{createCookieKey(c), new SerializableCookie().encode(c)});

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void removeCookies() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete(TABLE_COOKIES, null, null);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    //endregion

    //region FOLDERS
    public void addFileTeachers(List<FileTeacher> fileTeacherList) {
        SQLiteDatabase db = this.getWritableDatabase();
        String currentProfile = currentProfile();
        String folder_id;
        db.beginTransaction();
        for (FileTeacher teacher_folders : fileTeacherList) {
            for (Folder folder : teacher_folders.getFolders()) {
                folder.setProfName(teacher_folders.getName());
                folder_id = folder.getHash();
                db.execSQL("INSERT OR REPLACE INTO folders VALUES(?,?,?)", new Object[]{folder_id, folder.getName(), folder.getLast().getTime()});
                //Inserisci solamente se la cartella non è gia presente nel db
                db.execSQL("INSERT INTO teacher_folder SELECT * FROM (SELECT (SELECT id FROM teachers WHERE lower(name)=?), ?, ?, ?) AS tmp WHERE NOT EXISTS (SELECT folder_id FROM teacher_folder WHERE teacher_id = (SELECT id FROM teachers WHERE lower(name)=? LIMIT 1) AND folder_id=?) LIMIT 1", new Object[]{folder.getProfName().toLowerCase(), folder_id, folder.getProfName(), currentProfile, folder.getProfName().toLowerCase(), folder_id});
                for (File f : folder.getElements()) {
                    db.execSQL("INSERT OR REPLACE INTO files VALUES(?,?,?,?,?,?,?,NULL,?)", new Object[]{f.getId(), f.getName(), f.getType(), f.getDate().getTime(), f.getCksum(), f.getLink(), f.isHidden() ? 1 : 0, folder_id});
                }
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<FileTeacher> getFileTeachers() {
        SQLiteDatabase db = this.getReadableDatabase();
        //Select from the right user
        Cursor teachers = db.rawQuery("SELECT teacher_id,teacher_name FROM teacher_folder WHERE username=? GROUP BY teacher_id ", new String[]{currentProfile()});
        Cursor folders, files;

        List<FileTeacher> teachers_folders = new ArrayList<>();
        List<Folder> temp_folders = new ArrayList<>();
        List<File> temp_files = new ArrayList<>();
        int temp_teacher_id;
        String temp_teacher_name, temp_folder_id;

        for (teachers.moveToFirst(); !teachers.isAfterLast(); teachers.moveToNext()) {
            temp_teacher_id = teachers.getInt(0);
            temp_teacher_name = teachers.getString(1);
            folders = db.rawQuery("SELECT folders.* FROM teacher_folder LEFT JOIN folders ON folders.id = teacher_folder.folder_id WHERE teacher_id=? OR teacher_name=?", new String[]{String.valueOf(temp_teacher_id), temp_teacher_name});

            for (folders.moveToFirst(); !folders.isAfterLast(); folders.moveToNext()) {
                temp_folder_id = folders.getString(0);
                files = db.rawQuery("SELECT * FROM files WHERE folder_id=?", new String[]{temp_folder_id});

                for (files.moveToFirst(); !files.isAfterLast(); files.moveToNext()) {
                    temp_files.add(new File(files.getString(0), files.getString(1), files.getString(2), new Date(files.getLong(3)), files.getString(4), files.getString(5), files.getInt(6) == 1));
                }

                temp_folders.add(new Folder(folders.getString(1), new Date(folders.getLong(2)), new ArrayList<>(temp_files)));
                files.close();
                temp_files.clear();
            }

            teachers_folders.add(new FileTeacher(teachers.getString(1), new ArrayList<>(temp_folders)));

            folders.close();
            temp_folders.clear();
        }


        teachers.close();
        return teachers_folders;
    }

    public void setFileDownloaded(String cksum, String code, String filename) {
        ContentValues cv = new ContentValues();
        cv.put("filename", filename);
        getWritableDatabase().update("files", cv, "cksum=? AND id=?", new String[]{cksum, code});
    }
    //endregion

    //region FILES
    public boolean isFileDownloaded(String id, String cksum) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM files WHERE id=? AND cksum=? AND filename IS NOT NULL", new String[]{id, cksum});
        boolean bool = c.moveToFirst();
        c.close();
        return bool;
    }

    public String getFileName(String id, String cksum) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT filename FROM files WHERE id=? AND cksum=? AND filename IS NOT NULL", new String[]{id, cksum});
        String st = "";
        if (c.moveToFirst()) st = c.getString(0);
        c.close();
        return st;
    }
    //endregion

    //region COMMUNICATIONS
    public void addCommunications(List<Communication> communicationList) {
        SQLiteDatabase db = getWritableDatabase();
        String currentProfile = currentProfile();
        db.beginTransaction();
        for (Communication c : communicationList) {
            db.execSQL("INSERT OR REPLACE INTO communications VALUES(?,?,(select content from communications where id=?),?,?,(select filename from communications where id=?),(select attachment from communications where id=?),?)", new Object[]{c.getId(), c.getTitle(), c.getId(), c.getDate().getTime(), c.getType(), c.getId(), c.getId(), currentProfile});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void setCommunicationFilename(int id, String filename) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.execSQL("UPDATE communications SET filename=? WHERE id=?", new Object[]{filename, id});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void updateCommunication(int id, CommunicationDescription cd) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.execSQL("UPDATE communications SET content=?, attachment=? WHERE id=?", new Object[]{cd.getDesc().trim(), cd.isAttachment() ? 1 : 0, id});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<SuperCommunication> getCommunications() {
        SQLiteDatabase db = getReadableDatabase();
        List<SuperCommunication> list = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM communications WHERE username=? ORDER BY date DESC", new String[]{currentProfile()});
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            list.add(new SuperCommunication(c.getInt(0), c.getString(1), c.getString(2), new Date(c.getLong(3)), c.getString(4), c.getString(5), c.getInt(6) == 1));
        }
        c.close();
        return list;
    }
    //endregion

    //region NOTES
    public void addNotes(List<Note> noteList) {
        SQLiteDatabase db = getWritableDatabase();
        String currentProfile = currentProfile();
        db.beginTransaction();
        for (Note n : noteList) {
            db.execSQL("INSERT OR REPLACE INTO notes VALUES(?,?,?,?,?,(select id from teachers where lower(name)=? limit 1))", new Object[]{n.getHash(), n.getContent(), n.getDate().getTime(), n.getType(), currentProfile, n.getTeacher().toLowerCase()});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<Note> getNotes() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT teachers.name, notes.content, notes.date, notes.type FROM notes LEFT JOIN teachers ON teachers.id=notes.teacher_id WHERE notes.username=? ORDER BY notes.date DESC", new String[]{currentProfile()});
        List<Note> list = new ArrayList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            list.add(new Note(c.getString(0), c.getString(1), new Date(c.getLong(2)), c.getString(3)));
        }
        c.close();
        return list;
    }
    //endregion

    //region ABSENCES
    public void addAbsences(Absences absences) {
        SQLiteDatabase db = getWritableDatabase();
        String currentProfile = currentProfile();
        db.beginTransaction();
        for (Absence a : absences.getAbsences())
            db.execSQL("INSERT OR REPLACE INTO absences VALUES(?,?,?,?,?,NULL,?,'absence',?)", new Object[]{a.getId(), a.isDone() ? 1 : 0, a.getFrom().getTime(), a.getTo().getTime(), a.getDays(), a.getJustification(), currentProfile});
        for (Delay d : absences.getDelays())
            db.execSQL("INSERT OR REPLACE INTO absences VALUES(?,?,?,NULL,NULL,?,?,'delay',?)", new Object[]{d.getId(), d.isDone() ? 1 : 0, d.getDay().getTime(), d.getHour(), d.getJustification(), currentProfile});
        for (Exit e : absences.getExits())
            db.execSQL("INSERT OR REPLACE INTO absences VALUES(?,?,?,NULL,NULL,?,?,'exit',?)", new Object[]{e.getId(), e.isDone() ? 1 : 0, e.getDay().getTime(), e.getHour(), e.getJustification(), currentProfile});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public Absences getAbsences() {
        SQLiteDatabase db = getReadableDatabase();
        List<Absence> absences = new ArrayList<>();
        List<Delay> delays = new ArrayList<>();
        List<Exit> exits = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM absences WHERE username = ?", new String[]{currentProfile()});

        c.moveToFirst();
        while (!c.isAfterLast()) {
            switch (c.getString(7)) {
                case "absence":
                    absences.add(new Absence(0, c.getInt(1) == 1, new Date(c.getLong(2)), new Date(c.getLong(3)), c.getInt(4), c.getString(6)));
                    break;
                case "delay":
                    delays.add(new Delay(0, c.getInt(1) == 1, new Date(c.getLong(2)), c.getInt(5), c.getString(6)));
                    break;
                case "exit":
                    exits.add(new Exit(0, c.getInt(1) == 1, new Date(c.getLong(2)), c.getInt(5), c.getString(6)));
                    break;
            }
            c.moveToNext();
        }

        c.close();
        return new Absences(absences, delays, exits);
    }
    //endregion

    private String currentProfile() {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getString(Info.ACCOUNT, "");
    }


    public enum Period {
        FIRST("q1"),
        SECOND("q3"),
        ALL("");

        private final String id;

        Period(String id) {
            this.id = id;
        }

        public String getValue() {
            return id;
        }

    }
}
