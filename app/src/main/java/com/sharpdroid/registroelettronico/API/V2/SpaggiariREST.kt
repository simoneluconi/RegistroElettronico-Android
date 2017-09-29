package com.sharpdroid.registroelettronico.API.V2

import com.sharpdroid.registroelettronico.Databases.Entities.*
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface SpaggiariREST {
    @POST("rest/v1/auth/login")
    fun postLogin(@Body user: LoginRequest): Observable<LoginResponse>

    @GET("rest/v1/students/{studentId}/absences/details")
    fun getAbsences(): Observable<AbsenceAPI>

    @GET("rest/v1/students/{studentId}/absences/details/{begin}")
    fun getAbsences(@Path("begin") begin: String): Observable<AbsenceAPI>

    @GET("rest/v1/students/{studentId}/absences/details/{begin}/{end}")
    fun getAbsences(@Path("begin") begin: String, @Path("end") end: String): Observable<AbsenceAPI>

    @GET("rest/v1/students/{studentId}/agenda/all/{begin}/{end}")
    fun getAgenda(@Path("begin") begin: String, @Path("end") end: String): Observable<AgendaAPI>

    /*
        @GET("rest/v1/students/{studentId}/agenda/{eventCode}/{begin}/{end}")
        fun getAgenda( @Path("eventCode") eventCode: String, @Path("begin") begin: String, @Path("end") end: String): Observable<List<Agenda>>
    */
    @GET("rest/v1/students/{studentId}/didactics/item/{contentId}")
    fun getAttachmentFile(@Path("contentId") contentId: Int): Observable<ResponseBody>

    @GET("rest/v1/students/{studentId}/noticeboard")
    fun getBacheca(): Observable<CommunicationAPI>

    @GET("rest/v1/students/{studentId}/noticeboard/attach/{eventCode}/{pubId}/101")
    fun getBachecaAttachment(@Path("eventCode") eventCode: String, @Path("pubId") pubId: Int): Observable<ResponseBody>
/*
    @GET("rest/v1/students/{studentId}/schoolbooks")
    fun getBooks(): Observable<List<Course>>

    @GET("rest/v1/students/{studentId}/calendar/all")
    fun getCalendar(): Observable<List<Day>>

    @GET("rest/v1/students/{studentId}/card")
    fun getCard(): Observable<Card>

    @GET("rest/v1/students/{studentId}/cards")
    fun getCards(): Observable<List<Card>>*/

    @GET("rest/v1/students/{studentId}/didactics")
    fun getDidactics(): Observable<DidacticAPI>

    @GET("rest/v1/students/{studentId}/grades")
    fun getGrades(): Observable<GradeAPI>

    @GET("rest/v1/students/{studentId}/grades/subjects/{subject}")
    fun getGrades(@Path("subject") subject: Int): Observable<GradeAPI>

    @GET("rest/v1/students/{studentId}/lessons/today")
    fun getLessons(): Observable<LessonAPI>

    @GET("rest/v1/students/{studentId}/lessons/{day}")
    fun getLessons(@Path("day") day: String): Observable<LessonAPI>

    @GET("rest/v1/students/{studentId}/lessons/{start}/{end}")
    fun getLessons(@Path("start") start: String, @Path("end") end: String): Observable<LessonAPI>

    @GET("rest/v1/students/{studentId}/notes/all/")
    fun getNotes(): Observable<NoteAPI>

    @GET("rest/v1/students/{studentId}/periods")
    fun getPeriods(): Observable<List<Period>>

    @GET("rest/v1/students/{studentId}/subjects")
    fun getSubjects(): Observable<SubjectAPI>

    @POST("rest/v1/students/{studentId}/notes/{type}/read/{note}")
    fun markNote(@Path("type") type: String, @Path("note") note: Int): Observable<ResponseBody>

}