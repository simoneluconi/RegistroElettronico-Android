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
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RESTfulAPIService {

    @POST("login")
    @FormUrlEncoded
    Call<Login> postLogin(
            @NonNull @Field("login") String login,
            @NonNull @Field("password") String password,
            @NonNull @Field("key") String key);

    @GET("files")
    Call<List<FileTeacher>> getFiles();

    @GET("file/{id}/{cksum}/download")
    Call<ResponseBody> getDownload(
            @NonNull @Path("id") String id,
            @NonNull @Path("cksum") String cksum);

    @GET("https://gist.githubusercontent.com/luca020400/55f65db6a685dc2413f9ba7252c20cbf/raw/absences.json")
    Call<Absences> getAbsences();

    @GET("marks")
    Call<List<MarkSubject>> getMarks();

    @GET("subjects")
    Call<LessonSubject> getSubjects();

    @GET("subjects/{id}/lessons")
    Call<Lesson> getLessons(
            @Path("id") int id);

    @GET("notes")
    Call<List<Note>> getNotes();

    @GET("communications")
    Call<List<Communication>> getCommunications();

    @GET("communication/{id}/desc")
    Call<CommunicationDescription> getcommunicationDesc(
            @Path("id") int id);

    @GET("communication/{id}/download")
    Call<ResponseBody> getcommunicationDownload(
            @Path("id") int id);

    @GET("scrutinies")
    Call<List<Scrutiny>> getScrutines();
}
