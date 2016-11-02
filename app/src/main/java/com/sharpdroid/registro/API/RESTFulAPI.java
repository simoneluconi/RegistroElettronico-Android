package com.sharpdroid.registro.API;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sharpdroid.registro.Library.Communication;
import com.sharpdroid.registro.Library.MarkSubject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class RESTFulAPI {
    static private final String BASE_URL = "https://api.daniele.ml/";

    public static final String LOGIN_URL = BASE_URL + "login";

    private static final String MARKS_URL = BASE_URL + "marks";

    public static final String FILES_URL = BASE_URL + "files";

    public static final String ABSENCES_URL = BASE_URL + "absences";

    private static final String SUBJECTS_URL = BASE_URL + "subjects";

    public static final String NOTES_URL = BASE_URL + "notes";

    private static final String COMMUNICATIONS_URL = BASE_URL + "communications";

    public static final String SCRUTINIES_URL = BASE_URL + "scrutinies";

    public static final String ORALE = "Orale";
    public static final String SCRITTO = "Scritto/Grafico";
    public static final String PRATICO = "Pratico";

    private static final AsyncHttpClient client = new AsyncHttpClient();

    public RESTFulAPI() {

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

    private static void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);
        client.get(url, params, responseHandler);
    }

    public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);
        client.post(url, params, responseHandler);
    }

    public static abstract class Marks implements Runnable {
        private final Context context;

        protected Marks(Context context) {
            this.context = context;
        }

        public abstract void then(List<MarkSubject> subjects);

        @Override
        public void run() {
            get(context, MARKS_URL, null, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        then(new Gson().fromJson(responseString, new TypeToken<List<MarkSubject>>() {
                        }.getType()));
                    } catch (JsonParseException exception) {
                        exception.printStackTrace();
                    }
                }
            });
        }
    }

    public static abstract class Communications implements Runnable {
        private final Context context;

        protected Communications(Context context) {
            this.context = context;
        }

        public abstract void then(List<Communication> communications);

        @Override
        public void run() {
            get(context, COMMUNICATIONS_URL, null, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        then(new Gson().fromJson(responseString, new TypeToken<List<Communication>>() {
                        }.getType()));
                    } catch (JsonParseException exception) {
                        exception.printStackTrace();
                    }
                }
            });
        }
    }
}
