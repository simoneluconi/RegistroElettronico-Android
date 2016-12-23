package com.sharpdroid.registro.API;

import android.support.annotation.NonNull;

import com.sharpdroid.registro.Interfaces.API.Absences;
import com.sharpdroid.registro.Interfaces.API.Communication;
import com.sharpdroid.registro.Interfaces.API.CommunicationDescription;
import com.sharpdroid.registro.Interfaces.API.FileTeacher;
import com.sharpdroid.registro.Interfaces.API.Lesson;
import com.sharpdroid.registro.Interfaces.API.LessonSubject;
import com.sharpdroid.registro.Interfaces.API.Login;
import com.sharpdroid.registro.Interfaces.API.MarkSubject;
import com.sharpdroid.registro.Interfaces.API.Note;
import com.sharpdroid.registro.Interfaces.API.Scrutiny;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

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
    Observable<List<LessonSubject>> getSubjects();

    @GET("subject/{id}/lessons")
    Observable<List<Lesson>> getLessons(
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
