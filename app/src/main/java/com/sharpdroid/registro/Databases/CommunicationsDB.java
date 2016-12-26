package com.sharpdroid.registro.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.sharpdroid.registro.Databases.DatabaseInfo.DB_VERSION;

public class CommunicationsDB extends SQLiteOpenHelper {
    private final static String DB_NAME = "CommunicationsDB";
    private final static String columns[] = {"id", "code", "filename"};

    public CommunicationsDB(Context context) {
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
        return getFileName(code) != null;
    }


    public String getFileName(int code) {
        SQLiteDatabase db = this.getReadableDatabase();
        String name = null;
        Cursor c = db.rawQuery("SELECT * FROM " + DB_NAME + " WHERE " + columns[1] + " = ?", new String[]{String.valueOf(code)});
        if (c.moveToFirst())
            name = c.getString(2);

        c.close();
        return name;
    }

    public void addRecord(String filename, int code) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(columns[1], code);
        contentValues.put(columns[2], filename);

        long newRowId = db.insert(
                DB_NAME,
                null,
                contentValues);
        db.close();
        Log.v("RecordInserito", "Riga: " + newRowId);
    }

}
