package com.sharpdroid.registroelettronico.API.V2

import com.sharpdroid.registroelettronico.Databases.Entities.*
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface SpaggiariREST {
    @POST("auth/login")
    fun postLogin(@Body user: LoginRequest): Observable<LoginResponse>

    @GET("students/{studentId}/absences/details")
    fun getAbsences(): Observable<AbsenceAPI>

    @GET("students/{studentId}/absences/details/{begin}")
    fun getAbsences(@Path("begin") begin: String): Observable<AbsenceAPI>

    @GET("students/{studentId}/absences/details/{begin}/{end}")
    fun getAbsences(@Path("begin") begin: String, @Path("end") end: String): Observable<AbsenceAPI>

    @GET("students/{studentId}/agenda/all/{begin}/{end}")
    fun getAgenda(@Path("begin") begin: String, @Path("end") end: String): Observable<AgendaAPI>

    /*
        @GET("students/{studentId}/agenda/{eventCode}/{begin}/{end}")
        fun getAgenda( @Path("eventCode") eventCode: String, @Path("begin") begin: String, @Path("end") end: String): Observable<List<Agenda>>
    */
    @GET("students/{studentId}/didactics/item/{contentId}")
    fun getAttachmentFile(@Path("contentId") contentId: Int): Observable<ResponseBody>

    @GET("students/{studentId}/noticeboard")
    fun getBacheca(): Observable<CommunicationAPI>

    @GET("students/{studentId}/noticeboard/attach/{eventCode}/{pubId}/101")
    fun getBachecaAttachment(@Path("eventCode") eventCode: String, @Path("pubId") pubId: Int): Observable<ResponseBody>
/*
    @GET("students/{studentId}/schoolbooks")
    fun getBooks(): Observable<List<Course>>

    @GET("students/{studentId}/calendar/all")
    fun getCalendar(): Observable<List<Day>>

    @GET("students/{studentId}/card")
    fun getCard(): Observable<Card>

    @GET("students/{studentId}/cards")
    fun getCards(): Observable<List<Card>>*/

    @GET("students/{studentId}/didactics")
    fun getDidactics(): Observable<DidacticAPI>

    @GET("students/{studentId}/grades")
    fun getGrades(): Observable<GradeAPI>

    @GET("students/{studentId}/grades/subjects/{subject}")
    fun getGrades(@Path("subject") subject: Int): Observable<GradeAPI>

    @GET("students/{studentId}/lessons/today")
    fun getLessons(): Observable<LessonAPI>

    @GET("students/{studentId}/lessons/{day}")
    fun getLessons(@Path("day") day: String): Observable<LessonAPI>

    @GET("students/{studentId}/lessons/{start}/{end}")
    fun getLessons(@Path("start") start: String, @Path("end") end: String): Observable<LessonAPI>

    @GET("students/{studentId}/notes/all/")
    fun getNotes(): Observable<NoteAPI>

    @GET("students/{studentId}/periods")
    fun getPeriods(): Observable<PeriodAPI>

    @GET("students/{studentId}/subjects")
    fun getSubjects(): Observable<SubjectAPI>

    @POST("students/{studentId}/notes/{type}/read/{note}")
    fun markNote(@Path("type") type: String, @Path("note") note: Int): Observable<ResponseBody>

}