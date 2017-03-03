package com.sharpdroid.registroelettronico.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sharpdroid.registroelettronico.Interfaces.API.Lesson;
import com.sharpdroid.registroelettronico.Interfaces.API.LessonSubject;
import com.sharpdroid.registroelettronico.Interfaces.Client.Subject;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.sharpdroid.registroelettronico.Databases.DatabaseInfo.DB_VERSION;

public class SubjectsDB extends SQLiteOpenHelper {
    private final static String DB_NAME = "Subjects";
    private final static String TABLE_SUBJECTS = "subjects";
    private final static String TABLE_LESSONS = "lessons";
    private final static String TABLE_PROFESSORS = "professors";
    private final static String subjects[] = {"id", "code", "original_name", "name", "target", "professor", "classroom", "notes"};
    private final static String lessons[] = {subjects[1], "teacher", "date", "content"};
    private final static String professors[] = {"subject_code", "code", "name"};

    private SubjectsDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static SubjectsDB from(Context c) {
        return new SubjectsDB(c);
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
                subjects[6] + " TEXT, " +
                subjects[7] + " TEXT);");

        db.execSQL("CREATE TABLE " + TABLE_LESSONS + " (" +
                lessons[0] + " INTEGER, " +
                lessons[1] + " TEXT, " +
                lessons[2] + " INTEGER, " +
                lessons[3] + " TEXT);");

        db.execSQL("CREATE TABLE " + TABLE_PROFESSORS + " (" +
                professors[0] + " INTEGER, " +
                professors[1] + " INTEGER UNIQUE, " +
                professors[2] + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LESSONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);
        onCreate(db);
    }


    //region GETTER
    public Subject getSubject(int code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Subject subject = null;

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_SUBJECTS + " WHERE " + subjects[1] + " = ?", new String[]{String.valueOf(code)});
        if (c.moveToFirst())
            subject = new Subject(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3), c.getFloat(4), c.getString(5), c.getString(6), c.getString(7), getProfessorCodes(code));

        c.close();
        return subject;
    }

    public Subject getSubject(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Subject subject = null;
        Cursor c;
        if (name.contains("...")) {
            name = name.replace("...", "%");
            c = db.rawQuery("SELECT * FROM " + TABLE_SUBJECTS + " WHERE " + subjects[2] + " LIKE ? OR " + subjects[3] + " LIKE ?", new String[]{name, name});
        } else {
            c = db.rawQuery("SELECT * FROM " + TABLE_SUBJECTS + " WHERE " + subjects[2] + " = ? OR " + subjects[3] + " = ?", new String[]{name, name});
        }

        if (c.moveToFirst())
            subject = new Subject(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3), c.getFloat(4), c.getString(5), c.getString(6), c.getString(7), getProfessorCodes(c.getInt(1)));

        c.close();
        return subject;
    }

    public List<Subject> getSubjects() {
        List<Subject> subjects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_SUBJECTS, null);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            subjects.add(new Subject(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3), c.getFloat(4), c.getString(5), c.getString(6), c.getString(7), getProfessorCodes(c.getInt(1))));

        c.close();
        return subjects;
    }

    public List<Lesson> getLessons(int code) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_LESSONS + " WHERE " + lessons[0] + "=? ORDER BY " + lessons[2] + " DESC", new String[]{String.valueOf(code)});
        List<Lesson> lessons = new LinkedList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            lessons.add(new Lesson(c.getString(1), new Date(c.getLong(2)), c.getString(3)));
        }
        c.close();
        return lessons;
    }

    public List<Lesson> getLessons(int code, int limit) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_LESSONS + " WHERE " + lessons[0] + "=? ORDER BY " + lessons[2] + " DESC LIMIT " + limit, new String[]{String.valueOf(code)});
        List<Lesson> lessons = new LinkedList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            lessons.add(new Lesson(c.getString(1), new Date(c.getLong(2)), c.getString(3)));
        }
        c.close();
        return lessons;
    }

    public List<Integer> getProfessorCodes() {
        List<Integer> p = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + professors[1] + " FROM " + TABLE_PROFESSORS, null);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            p.add(c.getInt(0));
        }
        c.close();
        return p;
    }

    public List<Integer> getProfessorCodes(int subject_code) {
        List<Integer> p = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + professors[1] + " FROM " + TABLE_PROFESSORS + " WHERE " + professors[0] + "=?", new String[]{String.valueOf(subject_code)});
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            p.add(c.getInt(0));
        }
        c.close();
        return p;
    }
    //endregion

    //region SETTER
    public SubjectsDB editSubject(int code, ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.update(TABLE_SUBJECTS, contentValues, subjects[1] + " = ?", new String[]{String.valueOf(code)});
        db.setTransactionSuccessful();
        db.endTransaction();
        return this;
    }

    public SubjectsDB updateProfessorName(int code, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(subjects[5], name);
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.update(TABLE_SUBJECTS, contentValues, subjects[1] + " = ?", new String[]{String.valueOf(code)});
        db.setTransactionSuccessful();
        db.endTransaction();
        return this;
    }

    public void addSubject(LessonSubject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues;
        contentValues = new ContentValues();
        contentValues.put(subjects[1], subject.getCode());
        contentValues.put(subjects[2], subject.getName().toLowerCase());
        db.insert(TABLE_SUBJECTS, null, contentValues);
        db.close();
    }

    public void addSubject(LessonSubject subject, String prof) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues;
        contentValues = new ContentValues();
        contentValues.put(subjects[1], subject.getCode());
        contentValues.put(subjects[2], subject.getName().toLowerCase());
        contentValues.put(subjects[5], prof);
        db.insert(TABLE_SUBJECTS, null, contentValues);
        db.close();
    }

    public void addLessons(int code, List<Lesson> lessons_list) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values;
        db.beginTransaction();
        for (Lesson lesson : lessons_list) {
            values = new ContentValues();
            values.put(lessons[0], code);
            values.put(lessons[1], lesson.getTeacher().toLowerCase().trim());
            values.put(lessons[2], lesson.getDate().getTime());
            values.put(lessons[3], lesson.getContent().trim());
            db.insert(TABLE_LESSONS, null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void removeLessons(int code) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_LESSONS, lessons[0] + "=?", new String[]{String.valueOf(code)});
    }

    public void addProfessors(LessonSubject subject) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values;
        db.beginTransaction();
        for (int prof_code : subject.getTeacherCodes()) {
            values = new ContentValues();
            values.put(professors[0], subject.getCode());
            values.put(professors[1], prof_code);
            db.insert(TABLE_PROFESSORS, null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    //endregion
}
