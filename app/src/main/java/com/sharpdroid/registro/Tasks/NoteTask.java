package com.sharpdroid.registro.Tasks;

import android.content.Context;
import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sharpdroid.registro.API.RESTFulAPI;
import com.sharpdroid.registro.Interfaces.Note;

import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class NoteTask {
    private static final String TAG = NoteTask.class.getSimpleName();
    private final Context mContext;
    private List<Note> notes = new LinkedList<>();

    public NoteTask(Context context) {
        this.mContext = context;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    @WorkerThread
    public Void update() {
        RESTFulAPI.get(mContext, RESTFulAPI.NOTES_URL, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    notes.clear();
                    notes.addAll(new Gson().fromJson(responseString, new TypeToken<List<Note>>() {
                    }.getType()));
                } catch (JsonParseException exception) {
                    exception.printStackTrace();
                }
            }
        });
        return null;
    }
}
