package com.sharpdroid.registro.Databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.sharpdroid.registro.Databases.DatabaseInfo.DB_VERSION;

public class AgendaDB extends SQLiteOpenHelper {

    private final static String DB_NAME = "AgendaDB";
    private final static String columns[] = {"id", "code", "filename"};

    public AgendaDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*db.execSQL("CREATE TABLE " + DB_NAME + " (" +
                columns[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                columns[1] + " INTEGER UNIQUE, " +
                columns[2] + " TEXT" +
                ");");*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        /*db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(db);*/
    }
}
