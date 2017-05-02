package com.sharpdroid.registroelettronico.API

import com.sharpdroid.registroelettronico.Interfaces.API.Absences
import com.sharpdroid.registroelettronico.Interfaces.API.Communication
import com.sharpdroid.registroelettronico.Interfaces.API.CommunicationDescription
import com.sharpdroid.registroelettronico.Interfaces.API.Event
import com.sharpdroid.registroelettronico.Interfaces.API.FileTeacher
import com.sharpdroid.registroelettronico.Interfaces.API.Lesson
import com.sharpdroid.registroelettronico.Interfaces.API.LessonSubject
import com.sharpdroid.registroelettronico.Interfaces.API.Login
import com.sharpdroid.registroelettronico.Interfaces.API.MarkSubject
import com.sharpdroid.registroelettronico.Interfaces.API.Note
import com.sharpdroid.registroelettronico.Interfaces.API.Scrutiny

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface RESTfulAPIService {

    @POST("login")
    @FormUrlEncoded
    fun postLogin(
            @Field("login") login: String,
            @Field("password") password: String,
            @Field("key") key: String): Observable<Login>

    @get:GET("files")
    val files: Observable<List<FileTeacher>>

    @GET("file/{id}/{cksum}/download")
    fun getDownload(
            @Path("id") id: String,
            @Path("cksum") cksum: String): Observable<Response<ResponseBody>>

    @get:GET("absences")
    val absences: Observable<Absences>

    @get:GET("marks")
    val marks: Observable<List<MarkSubject>>

    @get:GET("subjects")
    val subjects: Observable<List<LessonSubject>>

    @GET("subject/{id}/lessons")
    fun getLessons(
            @Path("id") id: Int,
            @Query("teacherCode") teacherCode: String): Observable<List<Lesson>>

    @get:GET("notes")
    val notes: Observable<List<Note>>

    @get:GET("communications")
    val communications: Observable<List<Communication>>

    @GET("communication/{id}/desc")
    fun getCommunicationDesc(
            @Path("id") id: Int): Observable<CommunicationDescription>

    @GET("communication/{id}/download")
    fun getCommunicationDownload(
            @Path("id") id: Int): Observable<Response<ResponseBody>>

    @get:GET("scrutinies")
    val scrutines: Observable<List<Scrutiny>>

    @GET("events")
    fun getEvents(
            @Query(value = "start") start: Long,
            @Query(value = "end") end: Long): Observable<List<Event>>
}
