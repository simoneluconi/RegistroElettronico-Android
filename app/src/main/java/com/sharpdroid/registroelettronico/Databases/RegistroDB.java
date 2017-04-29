package com.sharpdroid.registroelettronico.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.franmontiel.persistentcookiejar.persistence.SerializableCookie;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.sharpdroid.registroelettronico.Interfaces.API.Event;
import com.sharpdroid.registroelettronico.Interfaces.API.Lesson;
import com.sharpdroid.registroelettronico.Interfaces.API.LessonSubject;
import com.sharpdroid.registroelettronico.Interfaces.API.Mark;
import com.sharpdroid.registroelettronico.Interfaces.API.MarkSubject;
import com.sharpdroid.registroelettronico.Interfaces.Client.AdvancedEvent;
import com.sharpdroid.registroelettronico.Interfaces.Client.Average;
import com.sharpdroid.registroelettronico.Interfaces.Client.LocalEvent;
import com.sharpdroid.registroelettronico.Interfaces.Client.Subject;
import com.sharpdroid.registroelettronico.Utils.Metodi;

import org.apache.commons.lang3.text.WordUtils;

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
    private final static String TABLE_SUBJECTS = "subjects";
    private final static String TABLE_LESSONS = "lessons";
    private final static String TABLE_PROFESSORS = "teachers";
    private final static String TABLE_MARKS = "marks";
    private final static String TABLE_PROFILES = "profiles";
    private final static String TABLE_COOKIES = "cookies";

    public static RegistroDB instance;
    private static int DB_VERSION = 14;
    private String current_profile;
    private Context mContext;

    public RegistroDB(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
        mContext = c;
    }

    public static synchronized RegistroDB getInstance(Context c) {
        if (instance == null) instance = new RegistroDB(c);
        return instance;
    }

    @Override
    public synchronized void close() {
        super.close();
        instance = null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE profiles(username TEXT NOT NULL, name TEXT NOT NULL, class TEXT, PRIMARY KEY (username));");
        db.execSQL("CREATE TABLE teachers(id INTEGER NOT NULL, name TEXT NOT NULL, PRIMARY KEY (id));");
        db.execSQL("CREATE TABLE cookies(username TEXT NOT NULL, key TEXT NOT NULL, value TEXT NOT NULL, FOREIGN KEY (username) REFERENCES profiles(username) ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE api_events(id INTEGER NOT NULL, title TEXT NOT NULL, content TEXT NOT NULL, start INTEGER NOT NULL, end INTEGER NOT NULL, all_day INTEGER NOT NULL, type INTEGER NOT NULL, completed INTEGER, archived INTEGER, username TEXT NOT NULL, teacher_id INTEGER NOT NULL, teacher_name TEXT NOT NULL, PRIMARY KEY (id), FOREIGN KEY (username) REFERENCES profiles(username) ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE folders(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, date INTEGER NOT NULL, teacher_id INTEGER NOT NULL, FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE files(id INTEGER NOT NULL, name TEXT NOT NULL, type TEXT NOT NULL, date INTEGER NOT NULL, cksum TEXT, link TEXT, hidden INTEGER NOT NULL, folder_id INTEGER NOT NULL, PRIMARY KEY (id), FOREIGN KEY (folder_id) REFERENCES folders(id) ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE communications(id INTEGER NOT NULL, title TEXT NOT NULL, date INTEGER NOT NULL, type TEXT NOT NULL, username TEXT NOT NULL, PRIMARY KEY (id), FOREIGN KEY (username) REFERENCES profiles(username));");
        db.execSQL("CREATE TABLE notes(content TEXT NOT NULL, date INTEGER NOT NULL, type TEXT NOT NULL, username TEXT NOT NULL, teacher_id INTEGER NOT NULL, FOREIGN KEY (teacher_id) REFERENCES teachers(id), FOREIGN KEY (username) REFERENCES profiles(username));");
        db.execSQL("CREATE TABLE subjects(id INTEGER NOT NULL, original_name TEXT NOT NULL, name TEXT, target FLOAT, classroom TEXT, notes TEXT, username TEXT NOT NULL, PRIMARY KEY (id), FOREIGN KEY (username) REFERENCES profiles(username) ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE lessons (id TEXT NOT NULL, date INTEGER NOT NULL, content TEXT NOT NULL, subject_id INTEGER NOT NULL, teacher_id INTEGER NOT NULL, PRIMARY KEY(id), FOREIGN KEY(subject_id) REFERENCES subjects(id), FOREIGN KEY(teacher_id) REFERENCES teachers(id))");
        db.execSQL("CREATE TABLE marks (id TEXT NOT NULL, subject_id INTEGER NOT NULL, mark TEXT NOT NULL, description TEXT, date INTEGER NOT NULL, type TEXT NOT NULL, period TEXT NOT NULL, not_significant INTEGER NOT NULL, PRIMARY KEY(id), FOREIGN KEY(subject_id) REFERENCES subjects(id) ON DELETE CASCADE)");
        db.execSQL("CREATE TABLE subject_teacher(teacher_id INTEGER NOT NULL, subject_id INTEGER NOT NULL, FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE, FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE local_events ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT NOT NULL, type TEXT NOT NULL, day INTEGER NOT NULL, teacher_id INTEGER, subject_id INTEGER, completed INTEGER, archived INTEGER, username TEXT NOT NULL, FOREIGN KEY(username) REFERENCES profiles(username) ON DELETE CASCADE, FOREIGN KEY(teacher_id) REFERENCES teachers(id) ON DELETE CASCADE, FOREIGN KEY(subject_id) REFERENCES subjects(id) ON DELETE CASCADE);");

        if (DB_VERSION != 14)
            onUpgrade(db, 1, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 14) {
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
            setClass(events.get(0).getClasse_desc());
        }
        for (Event e : events) {
            db.execSQL("INSERT OR REPLACE INTO api_events VALUES(?,?,?,?,?,?,?,(SELECT completed FROM api_events WHERE id = ?),(SELECT archived FROM api_events WHERE id = ?),?,?,?)", new Object[]{e.getId(), e.getTitle(), e.getNota_2(), e.getStart().getTime(), e.getEnd().getTime(), e.isAllDay() ? 1 : 0, e.getTipo(), e.getId(), e.getId(), currentProfile(), e.getAutore_id(), e.getAutore_desc()});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<AdvancedEvent> getEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT api_events.id, title, start,end,all_day, content,teachers.id AS teacher_id, coalesce(teachers.name, teacher_name),type,completed FROM api_events LEFT JOIN teachers ON teacher_id=teachers.id WHERE archived IS NULL AND username=?", new String[]{currentProfile()});
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
        values.put("day", e.getDay().getTime());
        values.put("username", currentProfile());
        db.insert("local_events", null, values);

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<AdvancedEvent> getLocalEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT local_events.id,title,content,type,day,subject_id,teacher_id,completed,coalesce(subjects.name,subjects.original_name) AS subject_name, teachers.name AS teacher_name FROM local_events LEFT JOIN subjects ON subject_id = subjects.id LEFT JOIN teachers ON teacher_id = teachers.id WHERE local_events.archived IS NULL AND local_events.username=?", new String[]{currentProfile()});
        List<AdvancedEvent> list = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            list.add(new AdvancedEvent(c.getString(0), c.getString(1), new Date(c.getLong(4)), null, true, null, c.getString(2), null, null, null, 0, c.getString(9), c.getString(6), c.getString(3), c.getString(8), c.getString(5), c.getLong(7)));
        }

        c.close();
        return list;
    }

    public List<AdvancedEvent> getLocalEvents(long day) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT local_events.id,title,content,type,day,subject_id,teacher_id,completed,coalesce(subjects.name,subjects.original_name) AS subject_name, teachers.name AS teacher_name FROM local_events LEFT JOIN subjects ON subject_id = subjects.id LEFT JOIN teachers ON teacher_id = teachers.id WHERE local_events.archived IS NULL AND day BETWEEN ? AND ? AND local_events.username=?", new String[]{String.valueOf(day), String.valueOf(day + 86399999), currentProfile()});
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

    public void setClass(String c) {
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
        Cursor c = db.rawQuery("SELECT api_events.id, api_events.completed FROM api_events WHERE id=? AND completed IS NULL UNION ALL SELECT local_events.id, local_events.completed FROM local_events WHERE id=? AND completed IS NULL", new String[]{id, id});
        boolean completed = c.moveToFirst();
        c.close();
        return completed;
    }

    //endregion

    //region ARCHIVE
    public void archive(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE api_events SET archive = ? WHERE id=?", new Object[]{System.currentTimeMillis(), id});
        db.execSQL("UPDATE local_events SET archive = ? WHERE id=?", new Object[]{System.currentTimeMillis(), id});
    }

    public void clearArchive() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE api_events SET archive = NULL");
        db.execSQL("UPDATE local_events SET archive = NULL");
    }
    //endregion

    //region SUBJECT
    public Subject getSubject(int code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Subject subject = null;

        Cursor c = db.rawQuery("SELECT subjects.id, coalesce(subjects.name, subjects.original_name) AS name, target,classroom,notes,teacher_id,teachers.name AS teacher_name FROM subjects LEFT JOIN subject_teacher ON subject_teacher.subject_id=subjects.id LEFT JOIN teachers ON teachers.id=teacher_id WHERE subject_id=? AND username=?", new String[]{String.valueOf(code), currentProfile()});
        List<Integer> codes = new ArrayList<>();
        List<String> names = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            codes.add(c.getInt(5));
            names.add(c.getString(6));
        }
        if (c.moveToFirst())
            subject = new Subject(c.getInt(0), WordUtils.capitalizeFully(c.getString(1), Metodi.Delimeters), c.getFloat(2), TextUtils.join(",", names), c.getString(3), c.getString(4), codes);


        c.close();
        return subject;
    }

    public Subject getSubject(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Subject subject = null;
        Cursor c;
        if (name.contains("...")) {
            name = name.replace("...", "%").toLowerCase();
            c = db.rawQuery("SELECT subjects.id, coalesce(subjects.name, subjects.original_name) AS _name, target,classroom,notes,teacher_id,teachers.name AS teacher_name FROM subjects INNER JOIN subject_teacher ON subject_teacher.subject_id=subjects.id LEFT JOIN teachers ON teachers.id=teacher_id WHERE (lower(subjects.original_name) LIKE ? OR lower(subjects.name) LIKE ?) AND username=?", new String[]{name, name, currentProfile()});
        } else {
            c = db.rawQuery("SELECT subjects.id, coalesce(subjects.name, subjects.original_name) AS _name, target,classroom,notes,teacher_id,teachers.name AS teacher_name FROM subjects INNER JOIN subject_teacher ON subject_teacher.subject_id=subjects.id LEFT JOIN teachers ON teachers.id=teacher_id WHERE (lower(subjects.original_name) LIKE ? OR lower(subjects.name) LIKE ?) AND username=?", new String[]{name, name, currentProfile()});
        }

        List<Integer> codes = new ArrayList<>();
        List<String> names = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            codes.add(c.getInt(5));
            names.add(c.getString(6));
        }

        if (c.moveToFirst())
            subject = new Subject(c.getInt(0), WordUtils.capitalizeFully(c.getString(1), Metodi.Delimeters), c.getFloat(2), TextUtils.join(",", names), c.getString(3), c.getString(4), codes);


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
        db.execSQL("INSERT OR IGNORE INTO subjects(id,original_name,username) VALUES(?,?,?)", new Object[]{subject.getCode(), subject.getName(), currentProfile()});
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

    /*public void removeLessons(int code) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete(TABLE_LESSONS, lessons[1] + "=?", new String[]{String.valueOf(code)});
        db.setTransactionSuccessful();
        db.endTransaction();
    }*/
    //endregion

    //region PROFESSORS
    public void addProfessor(int subject_id, int teacher_id, String teacher_name) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT OR IGNORE INTO teachers VALUES(?,?)", new Object[]{teacher_id, teacher_name});
        //inserisci se in subject_teacher non esiste un record teacher_id-subject_id
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
        String name, query;
        for (MarkSubject subject : markSubjects) {
            name = subject.getName();
            if (name.contains("...")) {
                name = name.replace("...", "%").toLowerCase();
                query = "SELECT subjects.id FROM subjects LEFT JOIN subject_teacher ON subject_teacher.subject_id=subjects.id LEFT JOIN teachers ON teachers.id=teacher_id WHERE (lower(subjects.original_name) LIKE ? OR lower(subjects.name) LIKE ?) AND username=?";
            } else {
                query = "SELECT subjects.id FROM subjects LEFT JOIN subject_teacher ON subject_teacher.subject_id=subjects.id LEFT JOIN teachers ON teachers.id=teacher_id WHERE (lower(subjects.original_name) LIKE ? OR lower(subjects.name) LIKE ?) AND username=?";
            }
            for (Mark mark : subject.getMarks()) {
                db.execSQL("INSERT OR REPLACE INTO marks VALUES(?,(" + query + "),?,?,?,?,?,?)", new Object[]{mark.getHash(), name, name, currentProfile(), mark.getMark(), mark.getDesc(), mark.getDate().getTime(), mark.getType(), mark.getQ(), mark.isNs() ? 1 : 0});
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public MarkSubject getMarks(int subject_id) {
        List<Mark> marks = new ArrayList<>();
        MarkSubject markSubject = new MarkSubject("", marks);

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT coalesce(subjects.name,subjects.original_name), marks.mark, marks.description, marks.date,marks.type,marks.period,marks.not_significant FROM marks LEFT JOIN subjects ON subjects.id=marks.subject_id WHERE marks.subject_id=?", new String[]{String.valueOf(subject_id)});

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

    public MarkSubject getMarks(int subject_id, Period period) {
        List<Mark> marks = new ArrayList<>();
        MarkSubject markSubject = new MarkSubject("", marks);

        SQLiteDatabase db = getReadableDatabase();
        String[] args = new String[]{String.valueOf(subject_id)};
        if (period != Period.ALL)
            args = new String[]{String.valueOf(subject_id), period.getValue()};
        Cursor c = db.rawQuery("SELECT coalesce(subjects.name,subjects.original_name), marks.mark, marks.description, marks.date,marks.type,marks.period,marks.not_significant FROM marks LEFT JOIN subjects ON subjects.id=marks.subject_id WHERE marks.subject_id=? " + ((period != Period.ALL) ? "AND marks.period=?" : ""), args);

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
        Cursor c = db.rawQuery("SELECT coalesce(subjects.name,subjects.original_name) as _name, AVG(marks.mark) as _avg, marks.subject_id, COUNT(marks.mark), subjects.target FROM marks LEFT JOIN subjects ON marks.subject_id=subjects.id WHERE marks.not_significant=0 AND subjects.username=? " + ((period != Period.ALL) ? "AND marks.period=?" : "") + " GROUP BY subjects.id " + sort_by, args);

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
        Cursor c = db.rawQuery("SELECT AVG(marks.mark) FROM marks LEFT JOIN subjects ON marks.subject_id=subjects.id WHERE marks.not_significant=0 AND subjects.username=?  " + ((p != Period.ALL) ? "AND marks.period=?" : ""), args);
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

        cv.put("name", profile.getName().getText());
        cv.put("username", profile.getEmail().getText());

        db.insert(TABLE_PROFILES, null, cv);

    }

    public List<IProfile> getProfiles() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM profiles", null);
        List<IProfile> profiles = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            profiles.add(new ProfileDrawerItem().withName(c.getString(1)).withEmail(c.getString(0)).withNameShown(true).withIcon(AccountImage(c.getString(1))));
        }

        c.close();

        return profiles;
    }

    public void removeProfile(String user) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("profiles", "username=?", new String[]{user});
    }

    public boolean isUserLogged(String user) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM profiles WHERE username=?", new String[]{user});
        boolean logged = c.moveToFirst();
        c.close();
        return logged;
    }

    public IProfile getProfile() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM profiles WHERE username=?", new String[]{currentProfile()});
        ProfileDrawerItem iProfile = new ProfileDrawerItem();
        if (c.moveToFirst()) {
            iProfile.withName(c.getString(1)).withEmail(c.getString(0)).withNameShown(true).withIcon(AccountImage(c.getString(1)));
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
        db.delete(TABLE_COOKIES, null, null);
    }

    //endregion

    private String currentProfile() {
        if (current_profile == null) updateProfile();
        return current_profile;
    }

    public void updateProfile() {
        current_profile = PreferenceManager.getDefaultSharedPreferences(mContext).getString("currentProfile", "");
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
