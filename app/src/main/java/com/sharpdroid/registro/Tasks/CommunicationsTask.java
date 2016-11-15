package com.sharpdroid.registro.Tasks;

import android.content.Context;
import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sharpdroid.registro.API.RESTFulAPI;
import com.sharpdroid.registro.Interfaces.Communication;

import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class CommunicationsTask {
    private static final String TAG = CommunicationsTask.class.getSimpleName();

    private List<Communication> communications = new LinkedList<>();
    private final Context mContext;

    public CommunicationsTask(Context context) {
        this.mContext = context;
    }

    public List<Communication> getCommunications() {
        return communications;
    }

    public void setCommunications(List<Communication> communications) {
        this.communications = communications;
    }

    @WorkerThread
    public Void update() {
        RESTFulAPI.get(mContext, RESTFulAPI.COMMUNICATIONS_URL, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    communications.clear();
                    communications.addAll(new Gson().fromJson(responseString, new TypeToken<List<Communication>>() {
                    }.getType()));
                } catch (JsonParseException exception) {
                    exception.printStackTrace();
                }
            }
        });
        return null;
    }
}
