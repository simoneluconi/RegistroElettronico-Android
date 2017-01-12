package com.sharpdroid.registroelettronico.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.sharpdroid.registroelettronico.Databases.DatabaseInfo.DB_VERSION;

public class FilesDB extends SQLiteOpenHelper {
    private final static String DB_NAME = "FilesDB";
    private final static String columns[] = {"id", "code", "cksum", "filename"};

    public FilesDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static FilesDB from(Context c) {
        return new FilesDB(c);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DB_NAME + " (" +
                columns[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                columns[1] + " INTEGER UNIQUE, " +
                columns[2] + " TEXT," +
                columns[3] + " TEXT" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(db);
    }

    public boolean isPresent(String code, String cksum) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + columns[0] + " FROM " + DB_NAME + " WHERE " + columns[1] + " = ? AND " + columns[2] + " = ?", new String[]{code, cksum});
        boolean exists = c.moveToFirst();
        c.close();

        return exists;
    }


    public String getFileName(String code, String cksum) {
        SQLiteDatabase db = this.getReadableDatabase();
        String name = null;
        Cursor c = db.rawQuery("SELECT * FROM " + DB_NAME + " WHERE " + columns[1] + " = ? AND " + columns[2] + " = ?", new String[]{code, cksum});
        if (c.moveToFirst())
            name = c.getString(3);

        c.close();
        return name;
    }

    public void addRecord(String filename, String code, String cksum) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(columns[1], code);
        contentValues.put(columns[2], cksum);
        contentValues.put(columns[3], filename);

        db.beginTransaction();
        db.insert(DB_NAME, null, contentValues);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

}
