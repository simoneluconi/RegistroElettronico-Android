package com.sharpdroid.registroelettronico.API;

import android.support.annotation.NonNull;

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
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface RESTfulAPIService {

    @POST("login")
    @FormUrlEncoded
    Observable<Login> postLogin(
            @NonNull @Field("login") String login,
            @NonNull @Field("password") String password,
            @NonNull @Field("key") String key);

    @GET("files")
    Observable<List<FileTeacher>> getFiles();

    @GET("file/{id}/{cksum}/download")
    Observable<Response<ResponseBody>> getDownload(
            @NonNull @Path("id") String id,
            @NonNull @Path("cksum") String cksum);

    @GET("absences")
    Observable<Absences> getAbsences();

    @GET("marks")
    Observable<List<MarkSubject>> getMarks();

    @GET("subjects")
    Observable<List<LessonSubject>> getSubjects();

    @GET("subject/{id}/lessons")
    Observable<List<Lesson>> getLessons(
            @Path("id") int id);

    @GET("notes")
    Observable<List<Note>> getNotes();

    @GET("communications")
    Observable<List<Communication>> getCommunications();

    @GET("communication/{id}/desc")
    Observable<CommunicationDescription> getCommunicationDesc(
            @Path("id") int id);

    @GET("communication/{id}/download")
    Observable<Response<ResponseBody>> getCommunicationDownload(
            @Path("id") int id);

    @GET("scrutinies")
    Observable<List<Scrutiny>> getScrutines();

    @GET("events")
    Observable<List<Event>> getEvents(
            @Query(value = "start") long start,
            @Query(value = "end") long end);
}
