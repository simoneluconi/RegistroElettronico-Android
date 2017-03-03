package com.sharpdroid.registroelettronico.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.sharpdroid.registroelettronico.Databases.DatabaseInfo.DB_VERSION;

public class CommunicationsDB extends SQLiteOpenHelper {
    private final static String DB_NAME = "CommunicationsDB";
    private final static String columns[] = {"id", "code", "filename", "title", "content"};

    private CommunicationsDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static CommunicationsDB from(Context c) {
        return new CommunicationsDB(c);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DB_NAME + " (" +
                columns[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                columns[1] + " INTEGER UNIQUE, " +
                columns[2] + " TEXT" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(db);
    }

    public boolean isPresent(int code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + columns[2] + " FROM " + DB_NAME + " WHERE " + columns[1] + " = ?", new String[]{String.valueOf(code)});
        boolean exists = c.moveToFirst();
        c.close();

        return exists;
    }


    public String getFileName(int code) {
        SQLiteDatabase db = this.getReadableDatabase();
        String name = null;
        Cursor c = db.rawQuery("SELECT " + columns[2] + " FROM " + DB_NAME + " WHERE " + columns[1] + " = ?", new String[]{String.valueOf(code)});
        if (c.moveToFirst())
            name = c.getString(0);

        c.close();
        return name;
    }

    public void addRecord(String filename, int code) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(columns[1], code);
        contentValues.put(columns[2], filename);

        db.beginTransaction();
        db.insert(DB_NAME, null, contentValues);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

}
