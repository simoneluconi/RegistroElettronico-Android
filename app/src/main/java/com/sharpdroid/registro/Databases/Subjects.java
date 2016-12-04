package com.sharpdroid.registro.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sharpdroid.registro.Interfaces.Subject;

import static com.sharpdroid.registro.Databases.DatabaseInfo.DB_VERSION;

public class Subjects extends SQLiteOpenHelper {
    private final static String DB_NAME = "Subjects";
    private final static String columns[] = {"id", "code", "name", "target", "professor", "classroom", "notes"};

    public static Subjects from(Context c) {
        return new Subjects(c);
    }

    private Subjects(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DB_NAME + " (" +
                columns[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                columns[1] + " INTEGER UNIQUE, " +
                columns[2] + " TEXT, " +
                columns[3] + " REAL, " +
                columns[4] + " TEXT, " +
                columns[5] + " TEXT, " +
                columns[6] + " TEXT" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(db);
    }

    public boolean addSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(columns[0], subject.getId());
        contentValues.put(columns[1], subject.getCode());
        contentValues.put(columns[2], subject.getName());
        contentValues.put(columns[3], subject.getTarget());
        contentValues.put(columns[4], subject.getProfessor());
        contentValues.put(columns[5], subject.getClassroom());
        contentValues.put(columns[6], subject.getNotes());

        return db.insert(DB_NAME, null, contentValues) != -1;
    }

    public Subject getSubject(int code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Subject subject;
        Cursor c = db.rawQuery("SELECT * FROM " + DB_NAME + " WHERE " + columns[1] + " = ?", new String[]{String.valueOf(code)});
        c.moveToFirst();

        subject = new Subject(c.getInt(0), c.getInt(1), c.getString(2), c.getFloat(3), c.getString(4), c.getString(5), c.getString(6));

        c.close();
        return subject;
    }

    public boolean editSubject(int code, ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.update(DB_NAME, contentValues, columns[1] + " = ?", new String[]{String.valueOf(code)}) > 0;
    }

}
