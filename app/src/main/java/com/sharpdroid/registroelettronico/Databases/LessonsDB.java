package com.sharpdroid.registroelettronico.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sharpdroid.registroelettronico.Interfaces.API.Lesson;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.sharpdroid.registroelettronico.Databases.DatabaseInfo.DB_VERSION;

public class LessonsDB extends SQLiteOpenHelper {
    private final static String DB_NAME = "Lessons";
    private final static String[] columns = new String[]{"teacher", "date", "content"};

    private final static String DB_EXECUTE_NEW_TABLE = " (" + columns[0] + " TEXT, " + columns[1] + " INTEGER, " + columns[2] + " TEXT)";

    public LessonsDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addSubject(int code) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("CREATE TABLE " + code + DB_EXECUTE_NEW_TABLE);
    }

    public void removeSubject(int code) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + code);
    }

    public void addLessons(int code, List<Lesson> lessons) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values;
        db.beginTransaction();
        for (Lesson lesson : lessons) {
            values = new ContentValues();
            values.put(columns[0], lesson.getTeacher().toLowerCase().trim());
            values.put(columns[1], lesson.getDate().getTime());
            values.put(columns[2], lesson.getContent().toLowerCase().trim());
            db.insert(String.valueOf(code), null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void removeLessons(int code) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(String.valueOf(code), null, null);
    }

    public List<Lesson> getLessons(int code) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + code + " ORDER BY " + columns[1] + " DESC", null);
        List<Lesson> lessons = new LinkedList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            lessons.add(new Lesson(c.getString(0), new Date(c.getLong(1)), c.getString(2)));
        }
        c.close();
        return lessons;
    }

    public List<Lesson> getLessons(int code, int limit) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + code + " ORDER BY " + columns[1] + " DESC LIMIT " + limit, null);
        List<Lesson> lessons = new LinkedList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            lessons.add(new Lesson(c.getString(0), new Date(c.getLong(1)), c.getString(2)));
        }
        c.close();
        return lessons;
    }
}
