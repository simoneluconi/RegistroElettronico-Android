package com.sharpdroid.registroelettronico.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.sharpdroid.registroelettronico.Interfaces.API.Lesson;
import com.sharpdroid.registroelettronico.Interfaces.API.LessonSubject;
import com.sharpdroid.registroelettronico.Interfaces.Client.Subject;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class SubjectsDB extends SQLiteOpenHelper {
    private final static String DB_NAME = "Subjects";
    private final static String TABLE_SUBJECTS = "subjects";
    private final static String TABLE_LESSONS = "lessons";
    private final static String TABLE_PROFESSORS = "professors";
    private final static String subjects[] = {"id", "code", "original_name", "name", "target", "classroom", "notes"};
    private final static String lessons[] = {subjects[1], "teacher_code", "date", "content"};
    private static int DB_VERSION = 17;

    public SubjectsDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int n) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LESSONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFESSORS);
        onCreate(db);
    }


    //region SUBJECT
    public Subject getSubject(int code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Subject subject = null;

        Cursor c = db.rawQuery("SELECT subjects.*, professors.teacher_code, professors.teacher_name FROM subjects LEFT JOIN professors ON subjects.code=professors.subject_code WHERE subjects.code=? GROUP BY professors.teacher_code", new String[]{String.valueOf(code)});
        List<Integer> codes = new ArrayList<>();
        List<String> names = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            codes.add(c.getInt(7));
            names.add(c.getString(8));
        }

        if (c.moveToFirst())
            subject = new Subject(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3), c.getFloat(4), TextUtils.join(",", names), c.getString(5), c.getString(6), codes);


        c.close();
        return subject;
    }

    public Subject getSubject(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Subject subject = null;
        Cursor c;
        if (name.contains("...")) {
            name = name.replace("...", "%");
            c = db.rawQuery("SELECT subjects.*, professors.teacher_code, professors.teacher_name FROM subjects " +
                    "LEFT JOIN lessons ON subjects.code=lessons.code " +
                    "LEFT JOIN professors ON subjects.code = professors.subject_code WHERE " + subjects[2] + " LIKE ? OR " + subjects[3] + " LIKE ? GROUP BY professors.teacher_code", new String[]{name, name});
        } else {
            c = db.rawQuery("SELECT subjects.*, professors.teacher_code, professors.teacher_name FROM subjects " +
                    "LEFT JOIN lessons ON subjects.code=lessons.code " +
                    "LEFT JOIN professors ON subjects.code = professors.subject_code WHERE " + subjects[2] + " = ? OR " + subjects[3] + " = ?", new String[]{name, name});
        }

        List<Integer> codes = new ArrayList<>();
        List<String> names = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            codes.add(c.getInt(7));
            names.add(c.getString(8));
        }

        if (c.moveToFirst())
            subject = new Subject(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3), c.getFloat(4), TextUtils.join(",", names), c.getString(5), c.getString(6), codes);


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

    public SubjectsDB editSubject(int code, ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.update(TABLE_SUBJECTS, contentValues, subjects[1] + " = ?", new String[]{String.valueOf(code)});
        db.setTransactionSuccessful();
        db.endTransaction();
        return this;
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

        Cursor c = db.rawQuery("SELECT lessons.teacher_code, professors.teacher_name FROM lessons LEFT JOIN professors ON lessons.teacher_code = professors.teacher_code GROUP BY lessons.teacher_code", null);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            names.add(Pair.create(c.getInt(0), c.getString(1)));
        }
        c.close();
        return names;
    }
    //endregion
}
