package com.sharpdroid.registro.API;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;

public class RESTFulAPI {
    public static final String ORALE = "Orale";
    public static final String SCRITTO = "Scritto/Grafico";
    public static final String PRATICO = "Pratico";

    private static final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private static final SyncHttpClient syncHttpClient = new SyncHttpClient();

    static private final String BASE_URL = "https://api.daniele.ml/";
    public static final String LOGIN_URL = BASE_URL + "login";
    public static final String FILES_URL = BASE_URL + "files";
    public static final String ABSENCES_URL = BASE_URL + "absences";
    public static final String NOTES_URL = BASE_URL + "notes";
    public static final String SCRUTINIES_URL = BASE_URL + "scrutinies";
    public static final String MARKS_URL = BASE_URL + "marks";
    public static final String SUBJECTS_URL = BASE_URL + "subjects";
    public static final String COMMUNICATIONS_URL = BASE_URL + "communications";

    public RESTFulAPI() {

    }

    public static void get(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        syncHttpClient.setCookieStore(myCookieStore);
        syncHttpClient.get(url, params, responseHandler);
    }

    public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        asyncHttpClient.setCookieStore(myCookieStore);
        asyncHttpClient.post(url, params, responseHandler);
    }

    public String FILE_DOWNLOAD_URL(String id, String cksum) {
        return String.format("%s/%s/%s/%s/download", BASE_URL, "file", id, cksum);
    }

    public String SUBJECT_LESSONS_URL(String id) {
        return String.format("%s/%s/lessons", SUBJECTS_URL, id);
    }

    public String COMMUNICATION_DESC_URL(String id) {
        return String.format("%s/%s/desc", COMMUNICATIONS_URL, id);
    }

    public String COMMUNICATION_DOWNLOAD_URL(String id) {
        return String.format("%s/%s/download", BASE_URL, id);
    }
}
