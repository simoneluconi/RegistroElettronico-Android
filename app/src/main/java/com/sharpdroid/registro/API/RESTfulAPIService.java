package com.sharpdroid.registro.API;

import android.support.annotation.NonNull;

import com.sharpdroid.registro.Interfaces.Absences;
import com.sharpdroid.registro.Interfaces.Communication;
import com.sharpdroid.registro.Interfaces.CommunicationDescription;
import com.sharpdroid.registro.Interfaces.FileTeacher;
import com.sharpdroid.registro.Interfaces.Lesson;
import com.sharpdroid.registro.Interfaces.LessonSubject;
import com.sharpdroid.registro.Interfaces.Login;
import com.sharpdroid.registro.Interfaces.MarkSubject;
import com.sharpdroid.registro.Interfaces.Note;
import com.sharpdroid.registro.Interfaces.Scrutiny;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface RESTfulAPIService {

    @POST("login")
    @FormUrlEncoded
    Observable<Login> postLogin(
            @NonNull @Field("login") String login,
            @NonNull @Field("password") String password,
            @NonNull @Field("key") String key);

    @GET("files")
    Observable<List<FileTeacher>> getFiles();

    @GET("file/{id}/{cksum}/download")
    Observable<ResponseBody> getDownload(
            @NonNull @Path("id") String id,
            @NonNull @Path("cksum") String cksum);

    @GET("absences")
    Observable<Absences> getAbsences();

    @GET("marks")
    Observable<List<MarkSubject>> getMarks();

    @GET("subjects")
    Observable<LessonSubject> getSubjects();

    @GET("subjects/{id}/lessons")
    Observable<Lesson> getLessons(
            @Path("id") int id);

    @GET("notes")
    Observable<List<Note>> getNotes();

    @GET("communications")
    Observable<List<Communication>> getCommunications();

    @GET("communication/{id}/desc")
    Observable<CommunicationDescription> getcommunicationDesc(
            @Path("id") int id);

    @GET("communication/{id}/download")
    Observable<ResponseBody> getcommunicationDownload(
            @Path("id") int id);

    @GET("scrutinies")
    Observable<List<Scrutiny>> getScrutines();
}
