package com.sharpdroid.registroelettronico.API.V2

import com.sharpdroid.registroelettronico.Databases.Entities.*
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface SpaggiariREST {
    @GET("rest/v1/students/{studentId}/absences/details")
    fun getAbsences(@Path("studentId") studentId: String): Observable<List<Absence>>

    @GET("rest/v1/students/{studentId}/absences/details/{begin}")
    fun getAbsences(@Path("studentId") studentId: String, @Path("begin") begin: String): Observable<List<Absence>>

    @GET("rest/v1/students/{studentId}/absences/details/{begin}/{end}")
    fun getAbsences(@Path("studentId") studentId: String, @Path("begin") begin: String, @Path("end") end: String): Observable<List<Absence>>

    @GET("rest/v1/students/{studentId}/agenda/all/{begin}/{end}")
    fun getAgenda(@Path("studentId") studentId: String, @Path("begin") begin: String, @Path("end") end: String): Observable<List<Agenda>>

    @GET("rest/v1/students/{studentId}/agenda/{eventCode}/{begin}/{end}")
    fun getAgenda(@Path("studentId") studentId: String, @Path("eventCode") eventCode: String, @Path("begin") begin: String, @Path("end") end: String): Observable<List<Agenda>>

    @GET("rest/v1/students/{studentId}/didactics/item/{contentId}")
    fun getAttachmentFile(@Path("studentId") studentId: String, @Path("contentId") contentId: Int): Observable<ResponseBody>

    @GET("rest/v1/students/{studentId}/noticeboard")
    fun getBacheca(@Path("studentId") studentId: String): Observable<List<Communication>>

    @GET("rest/v1/students/{studentId}/noticeboard/attach/{eventCode}/{pubId}/101")
    fun getBachecaAttachment(@Path("studentId") studentId: String, @Path("eventCode") eventCode: String, @Path("pubId") pubId: Int): Observable<ResponseBody>
/*
    @GET("rest/v1/students/{studentId}/schoolbooks")
    fun getBooks(@Path("studentId") studentId: String): Observable<List<Course>>

    @GET("rest/v1/students/{studentId}/calendar/all")
    fun getCalendar(@Path("studentId") studentId: String): Observable<List<Day>>

    @GET("rest/v1/students/{studentId}/card")
    fun getCard(@Path("studentId") studentId: String): Observable<Card>

    @GET("rest/v1/students/{studentId}/cards")
    fun getCards(@Path("studentId") studentId: String): Observable<List<Card>>*/

    @GET("rest/v1/students/{studentId}/didactics")
    fun getDidactics(@Path("studentId") studentId: String): Observable<List<Teacher>>

    @GET("rest/v1/students/{studentId}/grades")
    fun getGrades(@Path("studentId") studentId: String): Observable<List<Grade>>

    @GET("rest/v1/students/{studentId}/grades/subjects/{subject}")
    fun getGrades(@Path("studentId") studentId: String, @Path("subject") subject: Int): Observable<List<Grade>>

    @GET("rest/v1/students/{studentId}/lessons/today")
    fun getLessons(@Path("studentId") studentId: String): Observable<List<Lesson>>

    @GET("rest/v1/students/{studentId}/lessons/{day}")
    fun getLessons(@Path("studentId") studentId: String, @Path("day") day: String): Observable<List<Lesson>>

    @GET("rest/v1/students/{studentId}/lessons/{start}/{end}")
    fun getLessons(@Path("studentId") studentId: String, @Path("start") start: String, @Path("end") end: String): Observable<List<Lesson>>

    @GET("rest/v1/students/{studentId}/notes/all/")
    fun getNotes(@Path("studentId") studentId: String): Observable<List<Note>>

    @GET("rest/v1/students/{studentId}/periods")
    fun getPeriods(@Path("studentId") studentId: String): Observable<List<Period>>

    @GET("rest/v1/students/{studentId}/subjects")
    fun getSubjects(@Path("studentId") studentId: String): Observable<List<Subject>>

    @POST("rest/v1/students/{studentId}/notes/{type}/read/{note}")
    fun markNote(@Path("studentId") studentId: String, @Path("type") type: String, @Path("note") note: Int): Observable<ResponseBody>

}