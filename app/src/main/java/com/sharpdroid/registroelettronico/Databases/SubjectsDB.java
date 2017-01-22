package com.sharpdroid.registroelettronico.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sharpdroid.registroelettronico.Interfaces.API.LessonSubject;
import com.sharpdroid.registroelettronico.Interfaces.Client.Subject;

import java.util.ArrayList;
import java.util.List;

import static com.sharpdroid.registroelettronico.Databases.DatabaseInfo.DB_VERSION;

public class SubjectsDB extends SQLiteOpenHelper {
    private final static String DB_NAME = "SubjectsDB";
    private final static String columns[] = {"id", "code", "original_name", "name", "target", "professor", "classroom", "notes"};

    private SubjectsDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static SubjectsDB from(Context c) {
        return new SubjectsDB(c);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DB_NAME + " (" +
                columns[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                columns[1] + " INTEGER UNIQUE, " +
                columns[2] + " TEXT, " +
                columns[3] + " TEXT, " +
                columns[4] + " REAL, " +
                columns[5] + " TEXT, " +
                columns[6] + " TEXT, " +
                columns[7] + " TEXT" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(db);
    }

    public Subject getSubject(int code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Subject subject = null;

        Cursor c = db.rawQuery("SELECT * FROM " + DB_NAME + " WHERE " + columns[1] + " = ?", new String[]{String.valueOf(code)});
        if (c.moveToFirst())
            subject = new Subject(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3), c.getFloat(4), c.getString(5), c.getString(6), c.getString(7));

        c.close();
        return subject;
    }

    public Subject getSubject(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Subject subject = null;
        Cursor c;
        if (name.contains("...")) {
            name = name.replace("...", "%");
            c = db.rawQuery("SELECT * FROM " + DB_NAME + " WHERE " + columns[2] + " LIKE ? OR " + columns[3] + " LIKE ?", new String[]{name, name});
        } else {
            c = db.rawQuery("SELECT * FROM " + DB_NAME + " WHERE " + columns[2] + " = ? OR " + columns[3] + " = ?", new String[]{name, name});
        }

        if (c.moveToFirst())
            subject = new Subject(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3), c.getFloat(4), c.getString(5), c.getString(6), c.getString(7));

        c.close();
        return subject;
    }

    public SubjectsDB editSubject(int code, ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.update(DB_NAME, contentValues, columns[1] + " = ?", new String[]{String.valueOf(code)});
        db.setTransactionSuccessful();
        db.endTransaction();
        return this;
    }

    public SubjectsDB updateProfessorName(int code, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(columns[5], name);
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.update(DB_NAME, contentValues, columns[1] + " = ?", new String[]{String.valueOf(code)});
        db.setTransactionSuccessful();
        db.endTransaction();
        return this;
    }

    public void addSubject(LessonSubject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues;
        contentValues = new ContentValues();
        contentValues.put(columns[1], subject.getCode());
        contentValues.put(columns[2], subject.getName().toLowerCase());
        contentValues.put(columns[5], subject.getProfessor());
        db.insert(DB_NAME, null, contentValues);
        db.close();
    }

    public List<Subject> getSubjects() {
        List<Subject> subjects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + DB_NAME, null);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            subjects.add(new Subject(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3), c.getFloat(4), c.getString(5), c.getString(6), c.getString(7)));

        c.close();
        return subjects;
    }
}
