package com.sharpdroid.registro.Tasks;

import android.content.Context;
import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sharpdroid.registro.API.RESTFulAPI;
import com.sharpdroid.registro.Interfaces.MarkSubject;

import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MarkSubjectTask {
    private static final String TAG = MarkSubjectTask.class.getSimpleName();

    private List<MarkSubject> markSubjects = new LinkedList<>();
    private final Context mContext;

    public MarkSubjectTask(Context context) {
        this.mContext = context;
    }

    public List<MarkSubject> getMarkSubjects() {
        return markSubjects;
    }

    public void setMarkSubjects(List<MarkSubject> markSubjects) {
        this.markSubjects = markSubjects;
    }

    @WorkerThread
    public Void update() {
        RESTFulAPI.get(mContext, RESTFulAPI.MARKS_URL, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    markSubjects.clear();
                    markSubjects.addAll(new Gson().fromJson(responseString, new TypeToken<List<MarkSubject>>() {
                    }.getType()));
                } catch (JsonParseException exception) {
                    exception.printStackTrace();
                }
            }
        });
        return null;
    }
}
