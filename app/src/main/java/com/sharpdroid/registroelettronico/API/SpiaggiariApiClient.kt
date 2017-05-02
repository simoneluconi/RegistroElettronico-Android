package com.sharpdroid.registroelettronico.API

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.sharpdroid.registroelettronico.Activities.LoginActivity
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
import io.reactivex.schedulers.Schedulers
import okhttp3.CookieJar
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.Path
import retrofit2.http.Query

class SpiaggiariApiClient(context: Context) : RESTfulAPIService {
    private val mService: RESTfulAPIService

    init {
        val cookieJar = PersistentCookieJar(SetCookieCache(), SQLCookiePersistor(context))


        val CHECK_LOGIN = Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            if (response.code() == 403) {
                context.startActivity(Intent(context, LoginActivity::class.java).putExtra("user", PreferenceManager.getDefaultSharedPreferences(context).getString("currentProfile", null)))
            }
            response
        }

        val okHttpClient = OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(CHECK_LOGIN)
                .build()

        // Retrofit
        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.daniele.ml/")
                .client(okHttpClient)
                .build()

        // Build the api
        mService = retrofit.create(RESTfulAPIService::class.java)
    }

    override fun postLogin(
            @Field("login") login: String,
            @Field("password") password: String,
            @Field("key") key: String): Observable<Login> {
        return mService.postLogin(login, password, key)
    }

    override val files: Observable<List<FileTeacher>>
        get() = mService.files

    override fun getDownload(
            @Path("id") id: String,
            @Path("cksum") cksum: String): Observable<Response<ResponseBody>> {
        return mService.getDownload(id, cksum)
    }

    override val absences: Observable<Absences>
        get() = mService.absences

    override val marks: Observable<List<MarkSubject>>
        get() = mService.marks

    override val subjects: Observable<List<LessonSubject>>
        get() = mService.subjects

    override fun getLessons(@Path("id") id: Int, @Path("teacherCode") teacherCode: String): Observable<List<Lesson>> {
        return mService.getLessons(id, teacherCode)
    }

    override val notes: Observable<List<Note>>
        get() = mService.notes

    override val communications: Observable<List<Communication>>
        get() = mService.communications

    override fun getCommunicationDesc(@Path("id") id: Int): Observable<CommunicationDescription> {
        return mService.getCommunicationDesc(id)
    }

    override fun getCommunicationDownload(@Path("id") id: Int): Observable<Response<ResponseBody>> {
        return mService.getCommunicationDownload(id)
    }

    override val scrutines: Observable<List<Scrutiny>>
        get() = mService.scrutines

    override fun getEvents(
            @Query(value = "start") start: Long,
            @Query(value = "end") end: Long): Observable<List<Event>> {
        return mService.getEvents(start, end)
    }
}
