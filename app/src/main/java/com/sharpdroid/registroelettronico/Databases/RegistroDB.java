package com.sharpdroid.registroelettronico.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import okhttp3.Cookie;

import static com.sharpdroid.registroelettronico.Utils.Metodi.AccountImage;
import static com.sharpdroid.registroelettronico.Utils.Metodi.createCookieKey;
import static com.sharpdroid.registroelettronico.Utils.Metodi.toLowerCase;

public class RegistroDB extends SQLiteOpenHelper {
    private final static String DB_NAME = "RegistroDB";
    private final static String TABLE_API = "api";
    private final static String TABLE_LOCAL = "local";
    private final static String TABLE_COMPLETED = "completed";
    private final static String TABLE_ARCHIVE = "archive";
    private final static String TABLE_SUBJECTS = "subjects";
    private final static String TABLE_LESSONS = "lessons";
    private final static String TABLE_PROFESSORS = "professors";
    private final static String TABLE_MARKS = "marks";
    private final static String TABLE_PROFILES = "profiles";
    private final static String TABLE_COOKIES = "cookies";
    private final static String subjects[] = {"id", "code", "original_name", "name", "target", "classroom", "notes"};
    private final static String lessons[] = {subjects[1], "teacher_code", "date", "content"};
    private final static String columns[] = {
            "id", "code", "title",
            "start", "end", "allDay",
            "data_inserimento", "nota_2", "master_id",
            "classe_id", "classe_desc", "gruppo",
            "autore_desc", "autore_id", "tipo",
            "materia_desc", "materia_id"
    };  //COUNT = 17
    private final static String l_columns[] = {
            "uuid", "title", "content", "type", "day", "subject_id", "prof_id"
    };  //COUNT = 7
    private final static String marks[] = {
            "subject_code", "mark", "description", "date", "type", "period", "not_significant"
    };
    private static int DB_VERSION = 4;
    private Context mContext;

    public RegistroDB(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
        mContext = c;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_API + " (" +
                columns[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                columns[1] + " INTEGER, " +
                columns[2] + " TEXT," +
                columns[3] + " INTEGER," +  //START
                columns[4] + " INTEGER," +  //END
                columns[5] + " INTEGER," +
                columns[6] + " INTEGER," +  //INSERIMENTO
                columns[7] + " TEXT," +
                columns[8] + " TEXT," +
                columns[9] + " TEXT," +
                columns[10] + " TEXT," +
                columns[11] + " INTEGER," +
                columns[12] + " TEXT," +
                columns[13] + " TEXT," +
                columns[14] + " TEXT," +
                columns[15] + " TEXT," +
                columns[16] + " TEXT" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_LOCAL + " (" +
                l_columns[0] + " TEXT UNIQUE PRIMARY KEY, " +
                l_columns[1] + " TEXT, " +
                l_columns[2] + " TEXT, " +
                l_columns[3] + " TEXT, " +
                l_columns[4] + " INTEGER, " +
                l_columns[5] + " INTEGER, " +
                l_columns[6] + " INTEGER" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_COMPLETED + "(" +
                "id TEXT UNIQUE PRIMARY KEY, " +
                "date INTEGER);");

        db.execSQL("CREATE TABLE " + TABLE_ARCHIVE + "(" +
                "id TEXT UNIQUE PRIMARY KEY);");


        db.execSQL("CREATE TABLE " + TABLE_SUBJECTS + " (" +
                subjects[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                subjects[1] + " INTEGER UNIQUE, " +
                subjects[2] + " TEXT, " +
                subjects[3] + " TEXT, " +
                subjects[4] + " REAL, " +
                subjects[5] + " TEXT, " +
                subjects[6] + " TEXT);");

        db.execSQL("CREATE TABLE " + TABLE_LESSONS + " (" +
                lessons[0] + " INTEGER, " +
                lessons[1] + " INTEGER, " +
                lessons[2] + " INTEGER, " +
                lessons[3] + " TEXT" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_PROFESSORS + "( subject_code INTEGER, teacher_code INTEGER, teacher_name TEXT);");
        onUpgrade(db, 1, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE " + TABLE_MARKS + "(" +
                    "subject_code INTEGER," +
                    "mark TEXT," +
                    "description TEXT," +
                    "date INTEGER, " +
                    "type TEXT," +
                    "period TEXT," +
                    "not_significant INTEGER);");
        }

        if (oldVersion < 3) {
            db.execSQL("CREATE TABLE " + TABLE_PROFILES + "(" +
                    "uuid TEXT," +
                    "name TEXT," +
                    "username TEXT," +
                    "cookie TEXT)");
        }
        if (oldVersion < 4) {
            db.execSQL("CREATE TABLE " + TABLE_COOKIES + "(" +
                    "username TEXT," +
                    "key TEXT," +
                    "value TEXT)");
        }
    }

    //region EVENTS
    //region API
    public void addEvents(List<Event> events) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.delete(TABLE_API, null, null);
        ContentValues values;
        for (Event e : events) {
            values = new ContentValues();
            values.put("code", e.getId());
            values.put("title", e.getTitle());
            values.put("start", e.getStart().getTime());
            values.put("end", e.getEnd().getTime());
            values.put("allDay", e.isAllDay() ? 1 : 0);
            values.put("data_inserimento", e.getData_inserimento().getTime());
            values.put("nota_2", toLowerCase(e.getNota_2()));
            values.put("master_id", e.getMaster_id());
            values.put("classe_id", e.getClasse_id());
            values.put("classe_desc", e.getClasse_desc());
            values.put("gruppo", e.getGruppo());
            values.put("autore_desc", toLowerCase(e.getAutore_desc()));
            values.put("autore_id", e.getAutore_id());
            values.put("tipo", toLowerCase(e.getTipo()));
            values.put("materia_desc", toLowerCase(e.getMateria_desc()));
            values.put("materia_id", e.getMateria_id());
            db.insert(TABLE_API, null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<AdvancedEvent> getEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT api.code, api.title, api.start, api.end, api.allDay, api.data_inserimento, api.nota_2, api.autore_id AS teacher_code,coalesce(professors.teacher_name,api.autore_desc), api.tipo, completed.date AS completed FROM api " +
                "LEFT JOIN completed ON api.code=completed.id " +
                "LEFT JOIN professors ON api.autore_id=professors.teacher_code " +
                "WHERE NOT EXISTS (SELECT * FROM archive WHERE archive.id = api.code) GROUP BY api.code", null);
        List<AdvancedEvent> list = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            list.add(new AdvancedEvent(c.getString(0), c.getString(1), new Date(c.getLong(2)), new Date(c.getLong(3)), c.getInt(4) == 1, new Date(c.getLong(5)), c.getString(6), null, null, null, 0, c.getString(8), c.getString(7), c.getString(9), null, null, c.getLong(10)));
        }

        c.close();
        return list;
    }

    public List<AdvancedEvent> getEvents(long day) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT api.code, api.title, api.start, api.end, api.allDay, api.data_inserimento, api.nota_2, api.autore_id AS teacher_code,coalesce(professors.teacher_name,api.autore_desc), api.tipo, completed.date AS completed FROM api " +
                "LEFT JOIN completed ON api.code=completed.id " +
                "LEFT JOIN professors ON api.autore_id=professors.teacher_code " +
                "WHERE (api.start BETWEEN ? AND ?) AND NOT EXISTS (SELECT * FROM archive WHERE archive.id=api.code) GROUP BY api.code", new String[]{String.valueOf(day), String.valueOf(day + 86399999)});
        List<AdvancedEvent> list = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            list.add(new AdvancedEvent(c.getString(0), c.getString(1), new Date(c.getLong(2)), new Date(c.getLong(3)), c.getInt(4) == 1, new Date(c.getLong(5)), c.getString(6), null, null, null, 0, c.getString(8), c.getString(7), c.getString(9), null, null, c.getLong(10)));
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
        values.put(l_columns[0], e.getUuid());
        values.put(l_columns[1], e.getTitle());
        values.put(l_columns[2], e.getContent());
        values.put(l_columns[3], e.getType());
        values.put(l_columns[4], e.getDay().getTime());
        values.put(l_columns[5], e.getSubjectId());
        values.put(l_columns[6], e.getProfId());
        db.insert(TABLE_LOCAL, null, values);

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<AdvancedEvent> getLocalEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT local.*, completed.date AS completed, coalesce(subjects.name, subjects.original_name), professors.teacher_name AS prof_name FROM local " +
                "LEFT JOIN completed ON local.uuid=completed.id " +
                "LEFT JOIN subjects ON local.subject_id=subjects.code " +
                "LEFT JOIN professors ON local.prof_id=professors.teacher_code AND local.subject_id=professors.subject_code " +
                "WHERE NOT EXISTS(SELECT * FROM archive WHERE archive.id = local.uuid) GROUP BY local.uuid", null);
        List<AdvancedEvent> list = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            list.add(new AdvancedEvent(c.getString(0), c.getString(1), new Date(c.getLong(4)), null, true, null, c.getString(2), null, null, null, 0, c.getString(9), c.getString(6), c.getString(3), c.getString(8), c.getString(5), c.getLong(7)));
        }

        c.close();
        return list;
    }

    public List<AdvancedEvent> getLocalEvents(long day) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT local.*, completed.date AS completed, coalesce(subjects.name, subjects.original_name), professors.teacher_name AS prof_name FROM local " +
                "LEFT JOIN completed ON local.uuid=completed.id " +
                "LEFT JOIN subjects ON local.subject_id=subjects.code " +
                "LEFT JOIN professors ON local.prof_id=professors.teacher_code AND local.subject_id=professors.subject_code " +
                "WHERE (local.day BETWEEN ? AND ?) AND NOT EXISTS (SELECT * FROM archive WHERE archive.id = local.uuid) GROUP BY local.uuid", new String[]{String.valueOf(day), String.valueOf(day + 86399999)});
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
        Cursor c = db.rawQuery("SELECT classe_desc FROM api GROUP BY classe_desc", null);
        String s = "";
        if (c.moveToFirst()) s = c.getString(0);
        c.close();
        return s;
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
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("date", System.currentTimeMillis());
        db.insert(TABLE_COMPLETED, null, contentValues);
    }

    public void setUncompleted(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_COMPLETED, "id=?", new String[]{id});
    }

    public boolean isCompleted(String id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_COMPLETED + " WHERE id=?", new String[]{id});
        boolean completed = c.moveToFirst();
        c.close();
        return completed;
    }

    //endregion

    //region ARCHIVE
    public void archive(String id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        db.insert(TABLE_ARCHIVE, null, cv);
    }

    public void clearArchive() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_ARCHIVE, null, null);
    }
    //endregion

    //region SUBJECT
    public Subject getSubject(int code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Subject subject = null;

        Cursor c = db.rawQuery("SELECT subjects.id, subjects.code, coalesce(subjects.name, subjects.original_name) AS name, subjects.target, subjects.classroom, subjects.notes, professors.teacher_code, professors.teacher_name FROM subjects" +
                " LEFT JOIN professors ON subjects.code=professors.subject_code WHERE subjects.code=? GROUP BY professors.teacher_code", new String[]{String.valueOf(code)});
        List<Integer> codes = new ArrayList<>();
        List<String> names = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            codes.add(c.getInt(6));
            names.add(c.getString(7));
        }

        if (c.moveToFirst())
            subject = new Subject(c.getInt(0), c.getInt(1), c.getString(2), c.getFloat(3), TextUtils.join(",", names), c.getString(4), c.getString(5), codes);


        c.close();
        return subject;
    }

    public Subject getSubject(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Subject subject = null;
        Cursor c;
        if (name.contains("...")) {
            name = name.replace("...", "%");
            c = db.rawQuery("SELECT subjects.id, subjects.code, coalesce(subjects.name, subjects.original_name) AS name, subjects.target, subjects.classroom, subjects.notes, professors.teacher_code, professors.teacher_name FROM subjects " +
                    "LEFT JOIN lessons ON subjects.code=lessons.code " +
                    "LEFT JOIN professors ON subjects.code = professors.subject_code WHERE " + subjects[2] + " LIKE ? OR " + subjects[3] + " LIKE ? GROUP BY professors.teacher_code", new String[]{name, name});
        } else {
            c = db.rawQuery("SELECT subjects.id, subjects.code, coalesce(subjects.name, subjects.original_name) AS name, subjects.target, subjects.classroom, subjects.notes, professors.teacher_code, professors.teacher_name FROM subjects " +
                    "LEFT JOIN lessons ON subjects.code=lessons.code " +
                    "LEFT JOIN professors ON subjects.code = professors.subject_code WHERE " + subjects[2] + " = ? OR " + subjects[3] + " = ? GROUP BY professors.teacher_code", new String[]{name, name});
        }

        List<Integer> codes = new ArrayList<>();
        List<String> names = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            codes.add(c.getInt(6));
            names.add(c.getString(7));
        }

        if (c.moveToFirst())
            subject = new Subject(c.getInt(0), c.getInt(1), c.getString(2), c.getFloat(3), TextUtils.join(",", names), c.getString(4), c.getString(5), codes);


        c.close();
        return subject;
    }

    public List<Subject> getSubjects() {
        List<Subject> subjects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT code FROM subjects", null);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            subjects.add(getSubject(c.getInt(0)));
        }

        c.close();
        return subjects;
    }

    public void editSubject(int code, ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.update(TABLE_SUBJECTS, contentValues, subjects[1] + " = ?", new String[]{String.valueOf(code)});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void addSubject(LessonSubject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_SUBJECTS + " WHERE " + subjects[1] + " = ?", new String[]{String.valueOf(subject.getCode())});
        if (!c.moveToFirst()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(subjects[1], subject.getCode());
            contentValues.put(subjects[2], subject.getName().toLowerCase());
            db.insert(TABLE_SUBJECTS, null, contentValues);
        }
        c.close();
    }
    //endregion

    //region LESSONS
    public List<Lesson> getLessons(int code) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT professors.teacher_name, lessons.date, lessons.content FROM lessons " +
                "LEFT JOIN professors ON lessons.teacher_code=professors.teacher_code AND lessons.code=professors.subject_code " +
                "WHERE lessons.code = ? " +
                "ORDER BY date DESC", new String[]{String.valueOf(code)});
        List<Lesson> lessons = new LinkedList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            lessons.add(new Lesson(c.getString(0), new Date(c.getLong(1)), c.getString(2)));
        }
        c.close();
        return lessons;
    }

    public List<Lesson> getLessons(int code, int limit) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT professors.teacher_name, lessons.date, lessons.content FROM lessons " +
                "LEFT JOIN professors ON lessons.teacher_code=professors.teacher_code AND lessons.code=professors.subject_code " +
                "WHERE lessons.code = ? " +
                "ORDER BY date DESC " +
                "LIMIT " + limit, new String[]{String.valueOf(code)});

        List<Lesson> lessons = new LinkedList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            lessons.add(new Lesson(c.getString(0), new Date(c.getLong(1)), c.getString(2)));
        }
        c.close();
        return lessons;
    }

    public void addLessons(int subject, int professor_code, List<Lesson> lessons_list) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values;
        db.beginTransaction();
        db.delete(TABLE_LESSONS, lessons[1] + "=? AND " + lessons[0] + "=?", new String[]{String.valueOf(professor_code), String.valueOf(subject)});
        for (Lesson lesson : lessons_list) {
            values = new ContentValues();
            values.put(lessons[0], subject);
            values.put(lessons[1], professor_code);
            values.put(lessons[2], lesson.getDate().getTime());
            values.put(lessons[3], lesson.getContent().trim());
            db.insert(TABLE_LESSONS, null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void removeLessons(int code) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete(TABLE_LESSONS, lessons[1] + "=?", new String[]{String.valueOf(code)});
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    //endregion

    //region PROFESSORS
    public void addProfessor(int subject_code, int teacher_code, String teacher_name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values;
        Cursor c = db.rawQuery("SELECT * FROM professors WHERE subject_code = ? AND teacher_code = ?", new String[]{String.valueOf(subject_code), String.valueOf(teacher_code)});
        if (!c.moveToFirst()) {
            db.beginTransaction();
            values = new ContentValues();
            values.put("subject_code", subject_code);
            values.put("teacher_code", teacher_code);
            values.put("teacher_name", teacher_name);
            db.insert(TABLE_PROFESSORS, null, values);

            db.setTransactionSuccessful();
            db.endTransaction();
        }
        c.close();
    }

    public List<Integer> getProfessorCodes(int subject_code) {
        List<Integer> p = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT teacher_code FROM professors WHERE subject_code = ? GROUP BY teacher_code", new String[]{String.valueOf(subject_code)});

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            p.add(c.getInt(0));
        }

        c.close();
        return p;
    }

    public List<Pair<Integer, String>> getProfessors() {
        List<Pair<Integer, String>> names = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT professors.teacher_code, professors.teacher_name FROM professors ORDER BY professors.teacher_name ASC", null);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            names.add(Pair.create(c.getInt(0), c.getString(1)));
        }
        c.close();
        return names;
    }

    public String getSubjectOrProfessorName(String teacher_id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT coalesce(subjects.name,subjects.original_name), professors.teacher_name FROM subjects LEFT JOIN professors ON subjects.code=professors.subject_code WHERE professors.teacher_code=?", new String[]{String.valueOf(teacher_id)});
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

    public boolean isProfessorOfSubject(String subject, String prof) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM professors WHERE professors.subject_code=? AND professors.teacher_code=?", new String[]{subject, prof});
        boolean b = c.moveToFirst();
        c.close();
        return b;
    }

    public String getProfessorName(String teacher_id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT professors.teacher_name FROM professors WHERE professors.teacher_code=?", new String[]{teacher_id});
        String s = c.moveToFirst() ? c.getString(0) : "";
        c.close();
        return s;
    }
    //endregion

    //region MARKS
    public void addMarks(List<MarkSubject> markSubjects) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        int code;
        db.beginTransaction();
        db.delete(TABLE_MARKS, null, null);
        for (MarkSubject subject : markSubjects) {
            code = getSubject(subject.getName()).getCode();
            for (Mark mark : subject.getMarks()) {

                contentValues.put(marks[0], code);
                contentValues.put(marks[1], mark.getMark());
                contentValues.put(marks[2], mark.getDesc());
                contentValues.put(marks[3], mark.getDate().getTime());
                contentValues.put(marks[4], mark.getType());
                contentValues.put(marks[5], mark.getQ());
                contentValues.put(marks[6], mark.isNs() ? 1 : 0);

                db.insert(TABLE_MARKS, null, contentValues);
                contentValues.clear();
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public MarkSubject getMarks(int subject_code) {
        List<Mark> marks = new ArrayList<>();
        MarkSubject markSubject = new MarkSubject("", marks);

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT coalesce(subjects.name,subjects.original_name), marks.mark, marks.description, marks.date, marks.type, marks.period, marks.not_significant FROM marks LEFT JOIN subjects ON marks.subject_code=subjects.code WHERE marks.subject_code=?", new String[]{String.valueOf(subject_code)});

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

    public MarkSubject getMarks(int subject_code, Period period) {
        List<Mark> marks = new ArrayList<>();
        MarkSubject markSubject = new MarkSubject("", marks);

        SQLiteDatabase db = getReadableDatabase();
        String[] args = new String[]{String.valueOf(subject_code)};
        if (period != Period.ALL)
            args = new String[]{String.valueOf(subject_code), period.getValue()};
        Cursor c = db.rawQuery("SELECT coalesce(subjects.name,subjects.original_name), marks.mark, marks.description, marks.date, marks.type, marks.period, marks.not_significant FROM marks LEFT JOIN subjects ON marks.subject_code=subjects.code WHERE marks.subject_code=? " + ((period != Period.ALL) ? "AND marks.period=?" : ""), args);

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
        Cursor c = db.rawQuery("SELECT subjects.original_name,subjects.name, marks.mark, marks.description, marks.date, marks.type, marks.period, marks.not_significant FROM marks LEFT JOIN subjects ON marks.subject_code=subjects.code WHERE marks.period=? AND marks.not_significant=0", new String[]{period.getValue()});
        boolean ex = c.moveToFirst();
        c.close();
        return ex;
    }


    public List<Average> getAverages(Period period, String sort_by) {
        List<Average> avg = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] args = null;
        if (period != Period.ALL)
            args = new String[]{period.getValue()};
        Cursor c = db.rawQuery("SELECT coalesce(subjects.name,subjects.original_name) as _name, AVG(marks.mark) as _avg, marks.subject_code, COUNT(marks.mark), subjects.target FROM marks LEFT JOIN subjects ON marks.subject_code=subjects.code WHERE marks.not_significant!=1 " + ((period != Period.ALL) ? "AND marks.period=?" : "") +
                "GROUP BY marks.subject_code " + sort_by, args);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            avg.add(new Average(c.getString(0), c.getInt(2), c.getFloat(1), c.getInt(3), c.getFloat(4)));
        }
        c.close();
        return avg;
    }

    public double getAverage(Period p) {
        double avg = 0;
        SQLiteDatabase db = getReadableDatabase();
        String[] args = null;
        if (p != Period.ALL)
            args = new String[]{p.getValue()};
        Cursor c = db.rawQuery("SELECT AVG(marks.mark) FROM marks WHERE marks.not_significant=0 " + ((p != Period.ALL) ? "AND marks.period=?" : ""), args);
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

        cv.put("uuid", UUID.randomUUID().toString());
        cv.put("name", profile.getName().getText());
        cv.put("username", profile.getEmail().getText());

        db.insert(TABLE_PROFILES, null, cv);

    }

    public List<IProfile> getProfiles() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM profiles", null);
        List<IProfile> profiles = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            profiles.add(new ProfileDrawerItem().withName(c.getString(1)).withEmail(c.getString(2)).withNameShown(true).withIcon(AccountImage(c.getString(1))));
        }

        c.close();

        return profiles;
    }

    public List<IProfile> getOtherProfiles() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM profiles WHERE username!=?", new String[]{PreferenceManager.getDefaultSharedPreferences(mContext).getString("currentProfile", "")});
        List<IProfile> profiles = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            profiles.add(new ProfileDrawerItem().withName(c.getString(1)).withEmail(c.getString(2)).withNameShown(true).withIcon(AccountImage(c.getString(1))));
        }

        c.close();

        return profiles;
    }

    public void removeProfile(String user) {
        Log.d("RegistroDB", "Remove " + user);
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PROFILES, "username=?", new String[]{user});
        db.delete(TABLE_COOKIES, "username=?", new String[]{user});
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
        Cursor c = db.rawQuery("SELECT * FROM profiles WHERE username=?", new String[]{PreferenceManager.getDefaultSharedPreferences(mContext).getString("currentProfile", "")});
        ProfileDrawerItem iProfile = new ProfileDrawerItem();
        if (c.moveToFirst()) {
            iProfile.withName(c.getString(1)).withEmail(c.getString(2)).withNameShown(true).withIcon(AccountImage(c.getString(1)));
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
