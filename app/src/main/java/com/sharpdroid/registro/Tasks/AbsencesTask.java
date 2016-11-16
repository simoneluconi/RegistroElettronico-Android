package com.sharpdroid.registro.Tasks;

import android.content.Context;
import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sharpdroid.registro.API.RESTFulAPI;
import com.sharpdroid.registro.Interfaces.Absences;
import com.sharpdroid.registro.Interfaces.Communication;

import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class AbsencesTask {
    private static final String TAG = AbsencesTask.class.getSimpleName();
    private final Context mContext;
    // TODO: 16/11/2016 Update with API
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
        RESTFulAPI.get(mContext, RESTFulAPI.ABSENCES_URL, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    done = new Gson().fromJson(responseString, Absences.class);
                } catch (JsonParseException exception) {
                    exception.printStackTrace();
                }
            }
        });
        return null;
    }
}
