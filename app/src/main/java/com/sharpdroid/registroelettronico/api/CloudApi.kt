package com.sharpdroid.registroelettronico.api

import com.sharpdroid.registroelettronico.database.entities.LocalAgenda
import com.sharpdroid.registroelettronico.database.entities.RemoteAgendaInfo
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CloudApi {
    @GET("user/{profile}/local")
    fun pullLocal(@Path("profile") profile: Long): Observable<List<LocalAgenda>>

    @GET("user/{profile}/remote")
    fun pullRemoteInfo(@Path("profile") profile: Long): Observable<List<RemoteAgendaInfo>>

    @POST("user/{profile}/local")
    fun pushLocal(@Path("profile") profile: Long, @Body data: List<LocalAgenda>): Observable<ResponseBody>

    @POST("user/{profile}/remote")
    fun pushRemoteInfo(@Path("profile") profile: Long, @Body data: List<RemoteAgendaInfo>): Observable<ResponseBody>
}