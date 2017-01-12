package com.sharpdroid.registroelettronico.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sharpdroid.registroelettronico.Interfaces.API.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.sharpdroid.registroelettronico.Databases.DatabaseInfo.DB_VERSION;
import static com.sharpdroid.registroelettronico.Utils.Metodi.toLowerCase;

public class AgendaDB extends SQLiteOpenHelper {

    private final static String DB_NAME = "AgendaDB";
    private final static String columns[] = {
            "id", "code", "title",
            "start", "end", "allDay",
            "data_inserimento", "nota_2", "master_id",
            "classe_id", "classe_desc", "gruppo",
            "autore_desc", "autore_id", "tipo",
            "materia_desc", "materia_id"
    };  //COUNT = 17

    private AgendaDB(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
    }

    public static AgendaDB from(Context c) {
        return new AgendaDB(c);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DB_NAME + " (" +
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(db);
    }

    public void addEvents(List<Event> events) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.delete(DB_NAME, null, null);
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
                db.insert(DB_NAME, null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<Event> getEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + DB_NAME, new String[]{});
        List<Event> list = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            list.add(new Event(c.getString(1), c.getString(2), new Date(c.getLong(3)), new Date(c.getLong(4)), c.getInt(5) == 1, new Date(c.getLong(6)), c.getString(7), c.getString(8), c.getString(9), c.getString(10), c.getInt(11), c.getString(12), c.getString(13), c.getString(14), c.getString(15), c.getString(16)));
        }

        c.close();
        return list;
    }

    public List<Event> getEventsFrom(long start) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + DB_NAME + " WHERE " + columns[3] + " >= ?", new String[]{String.valueOf(start)});
        List<Event> list = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            list.add(new Event(c.getString(1), c.getString(2), new Date(c.getLong(3)), new Date(c.getLong(4)), c.getInt(5) == 1, new Date(c.getLong(6)), c.getString(7), c.getString(8), c.getString(9), c.getString(10), c.getInt(11), c.getString(12), c.getString(13), c.getString(14), c.getString(15), c.getString(16)));
        }

        c.close();
        return list;
    }

    public List<Event> getEventsFromTo(long start, long end) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + DB_NAME + " WHERE " + columns[3] + " BETWEEN ? AND ?", new String[]{String.valueOf(start), String.valueOf(end)});
        List<Event> list = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            list.add(new Event(c.getString(1), c.getString(2), new Date(c.getLong(3)), new Date(c.getLong(4)), c.getInt(5) == 1, new Date(c.getLong(6)), c.getString(7), c.getString(8), c.getString(9), c.getString(10), c.getInt(11), c.getString(12), c.getString(13), c.getString(14), c.getString(15), c.getString(16)));
        }

        c.close();
        return list;
    }
}
