package com.sharpdroid.registroelettronico.API.V1

import com.sharpdroid.registroelettronico.Interfaces.API.*
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

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

    @get:GET("subject")
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
