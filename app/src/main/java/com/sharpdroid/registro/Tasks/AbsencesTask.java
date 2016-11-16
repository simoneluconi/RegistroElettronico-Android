package com.sharpdroid.registro.Tasks;

import android.content.Context;
import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sharpdroid.registro.API.RESTFulAPI;
import com.sharpdroid.registro.Interfaces.Absence;
import com.sharpdroid.registro.Interfaces.Absences;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class AbsencesTask {
    private static final String TAG = AbsencesTask.class.getSimpleName();
    private final Context mContext;
    private Absences done;

    public AbsencesTask(Context context) {
        this.mContext = context;
    }

    public Absences getAbsences() {
        return done;
    }

    public void setAbsences(Absences absences) {
        this.done = absences;
    }

    @WorkerThread
    public Void update() {
        done = new Gson().fromJson("{\"absences\":[{\"id\":0,\"from\":\"21 ott\",\"to\":\"21 ott\",\"days\":1,\"justification\":\"A - Motivi di salute\"}],\"delays\":[]}",Absences.class);

        //done = new Gson().fromJson("\"done\":{\"absences\":[{\"id\":0,\"from\":\"15 nov\",\"to\":\"15 nov\",\"days\":1},{\"id\":0,\"from\":\"04 ott\",\"to\":\"07 ott\",\"days\":4}],\"delays\":[]}", Absences.class);
        /*
        RESTFulAPI.get(mContext, RESTFulAPI.ABSENCES_URL, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    absences = new Gson().fromJson("\"done\":{\"absences\":[{\"id\":0,\"from\":\"15 nov\",\"to\":\"15 nov\",\"days\":1},{\"id\":0,\"from\":\"04 ott\",\"to\":\"07 ott\",\"days\":4}],\"delays\":[]}", Absences.class);
                } catch (JsonParseException exception) {
                    exception.printStackTrace();
                }
            }
        });*/
        return null;
    }
}
