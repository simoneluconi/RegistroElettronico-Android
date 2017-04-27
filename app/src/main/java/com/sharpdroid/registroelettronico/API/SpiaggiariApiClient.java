package com.sharpdroid.registroelettronico.API;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.sharpdroid.registroelettronico.Activities.LoginActivity;
import com.sharpdroid.registroelettronico.Databases.RegistroDB;
import com.sharpdroid.registroelettronico.Interfaces.API.Absences;
import com.sharpdroid.registroelettronico.Interfaces.API.Communication;
import com.sharpdroid.registroelettronico.Interfaces.API.CommunicationDescription;
import com.sharpdroid.registroelettronico.Interfaces.API.Event;
import com.sharpdroid.registroelettronico.Interfaces.API.FileTeacher;
import com.sharpdroid.registroelettronico.Interfaces.API.Lesson;
import com.sharpdroid.registroelettronico.Interfaces.API.LessonSubject;
import com.sharpdroid.registroelettronico.Interfaces.API.Login;
import com.sharpdroid.registroelettronico.Interfaces.API.MarkSubject;
import com.sharpdroid.registroelettronico.Interfaces.API.Note;
import com.sharpdroid.registroelettronico.Interfaces.API.Scrutiny;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class SpiaggiariApiClient implements RESTfulAPIService {
    private final RESTfulAPIService mService;

    public SpiaggiariApiClient(Context context) {
        CookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SQLCookiePersistor(context));

        RegistroDB db = RegistroDB.getInstance(context);
        Interceptor CHECK_LOGIN = chain -> {
            Request request = chain.request();
            okhttp3.Response response = chain.proceed(request);
            if (response.code() == 403) {
                if (db.getOtherProfiles().size() > 0) {
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString("currentProfile", db.getOtherProfiles().get(0).getEmail().toString()).apply();
                    return response.newBuilder().build();
                } else {
                    context.startActivity(new Intent(context, LoginActivity.class).putExtra("user", PreferenceManager.getDefaultSharedPreferences(context).getString("currentProfile", null)));
                }
            }
            return response;
        };


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(CHECK_LOGIN)
                .build();

        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.daniele.ml/")
                .client(okHttpClient)
                .build();

        // Build the api
        mService = retrofit.create(RESTfulAPIService.class);
    }

    @Override
    public Observable<Login> postLogin(
            @NonNull @Field("login") String login,
            @NonNull @Field("password") String password,
            @NonNull @Field("key") String key) {
        return mService.postLogin(login, password, key);
    }

    @Override
    public Observable<List<FileTeacher>> getFiles() {
        return mService.getFiles();
    }

    @Override
    public Observable<Response<ResponseBody>> getDownload(
            @NonNull @Path("id") String id,
            @NonNull @Path("cksum") String cksum) {
        return mService.getDownload(id, cksum);
    }

    @Override
    public Observable<Absences> getAbsences() {
        return mService.getAbsences();
    }

    @Override
    public Observable<List<MarkSubject>> getMarks() {
        return mService.getMarks();
    }

    @Override
    public Observable<List<LessonSubject>> getSubjects() {
        return mService.getSubjects();
    }

    @Override
    public Observable<List<Lesson>> getLessons(@Path("id") int id, @Path("teacherCode") String teacherCode) {
        return mService.getLessons(id, teacherCode);
    }

    @Override
    public Observable<List<Note>> getNotes() {
        return mService.getNotes();
    }

    @Override
    public Observable<List<Communication>> getCommunications() {
        return mService.getCommunications();
    }

    @Override
    public Observable<CommunicationDescription> getCommunicationDesc(@Path("id") int id) {
        return mService.getCommunicationDesc(id);
    }

    @Override
    public Observable<Response<ResponseBody>> getCommunicationDownload(@Path("id") int id) {
        return mService.getCommunicationDownload(id);
    }

    @Override
    public Observable<List<Scrutiny>> getScrutines() {
        return mService.getScrutines();
    }

    @Override
    public Observable<List<Event>> getEvents(
            @Query(value = "start") long start,
            @Query(value = "end") long end) {
        return mService.getEvents(start, end);
    }
}
