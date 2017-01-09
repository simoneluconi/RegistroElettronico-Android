package com.sharpdroid.registro.API;

import android.content.Context;
import android.support.annotation.NonNull;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.sharpdroid.registro.Interfaces.API.Absences;
import com.sharpdroid.registro.Interfaces.API.Communication;
import com.sharpdroid.registro.Interfaces.API.CommunicationDescription;
import com.sharpdroid.registro.Interfaces.API.Event;
import com.sharpdroid.registro.Interfaces.API.FileTeacher;
import com.sharpdroid.registro.Interfaces.API.Lesson;
import com.sharpdroid.registro.Interfaces.API.LessonSubject;
import com.sharpdroid.registro.Interfaces.API.Login;
import com.sharpdroid.registro.Interfaces.API.MarkSubject;
import com.sharpdroid.registro.Interfaces.API.Note;
import com.sharpdroid.registro.Interfaces.API.Scrutiny;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class SpiaggiariApiClient implements RESTfulAPIService {
    private final RESTfulAPIService mService;

    public SpiaggiariApiClient(Context context) {
        CookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://staging.api.daniele.ml/")
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
    public Observable<List<Lesson>> getLessons(@Path("id") int id) {
        return mService.getLessons(id);
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
