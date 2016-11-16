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
    private List<Absences> absences = new LinkedList<>();

    public AbsencesTask(Context context) {
        this.mContext = context;
    }

    public List<Absences> getAbsences() {
        return absences;
    }

    public void setAbsences(List<Absences> absences) {
        this.absences = absences;
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
                    absences.clear();
                    absences.addAll(new Gson().fromJson(responseString, new TypeToken<List<Communication>>() {
                    }.getType()));
                } catch (JsonParseException exception) {
                    exception.printStackTrace();
                }
            }
        });
        return null;
    }
}
