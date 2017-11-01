package com.sharpdroid.registroelettronico.api.v2

import com.sharpdroid.registroelettronico.database.entities.*
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call
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

    @GET("students/{studentId}/agenda/all/{begin}/{end}")
    fun getAgendaBlocking(@Path("begin") begin: String, @Path("end") end: String): Call<AgendaAPI>

    /*
        @GET("students/{studentId}/agenda/{eventCode}/{begin}/{end}")
        fun getAgenda( @Path("eventCode") eventCode: String, @Path("begin") begin: String, @Path("end") end: String): Observable<List<Agenda>>
    */
    @GET("students/{studentId}/didactics/item/{fileId}")
    fun getAttachmentFile(@Path("fileId") fileId: Long): Call<ResponseBody>

    @GET("students/{studentId}/didactics/item/{fileId}")
    fun getAttachmentUrl(@Path("fileId") fileId: Long): Observable<DownloadUrlAPI>

    @GET("students/{studentId}/didactics/item/{fileId}")
    fun getAttachmentTxt(@Path("fileId") fileId: Long): Observable<DownloadTxtAPI>

    @GET("students/{studentId}/noticeboard")
    fun getBacheca(): Observable<CommunicationAPI>

    @GET("students/{studentId}/noticeboard")
    fun getBachecaBlocking(): Call<CommunicationAPI>

    @GET("students/{studentId}/noticeboard/attach/{eventCode}/{pubId}/101")
    fun getBachecaAttachment(@Path("eventCode") eventCode: String, @Path("pubId") pubId: Long): Call<ResponseBody>

    @POST("students/{studentId}/noticeboard/read/{eventCode}/{pubId}/101")
    fun readBacheca(@Path("eventCode") eventCode: String, @Path("pubId") pubId: Long): Observable<ReadResponse>

    /*
        @GET("students/{studentId}/schoolbooks")
        fun getBooks(): Observable<List<Course>>

    @GET("students/{studentId}/calendar/all")
    fun getCalendar(): Observable<Calendar>

    @GET("students/{studentId}/card")
    fun getCard(): Observable<Card>

    @GET("students/{studentId}/cards")
    fun getCards(): Observable<List<Card>>*/

    @GET("students/{studentId}/didactics")
    fun getDidactics(): Observable<DidacticAPI>

    @GET("students/{studentId}/grades")
    fun getGrades(): Observable<GradeAPI>

    @GET("students/{studentId}/grades")
    fun getGradesBlocking(): Call<GradeAPI>

    @GET("students/{studentId}/grades/subjects/{subjectInfo}")
    fun getGrades(@Path("subjectInfo") subject: Int): Observable<GradeAPI>

    @GET("students/{studentId}/lessons/today")
    fun getLessons(): Observable<LessonAPI>

    @GET("students/{studentId}/lessons/{day}")
    fun getLessons(@Path("day") day: String): Observable<LessonAPI>

    @GET("students/{studentId}/lessons/{start}/{end}")
    fun getLessons(@Path("start") start: String, @Path("end") end: String): Observable<LessonAPI>

    @GET("students/{studentId}/notes/all/")
    fun getNotes(): Observable<NoteAPI>

    @GET("students/{studentId}/notes/all/")
    fun getNotesBlocking(): Call<NoteAPI>

    @GET("students/{studentId}/periods")
    fun getPeriods(): Observable<PeriodAPI>

    @GET("students/{studentId}/subjects")
    fun getSubjects(): Observable<SubjectAPI>

    @POST("students/{studentId}/notes/{type}/read/{layout_note}")
    fun markNote(@Path("type") type: String, @Path("layout_note") note: Int): Observable<ResponseBody>

}