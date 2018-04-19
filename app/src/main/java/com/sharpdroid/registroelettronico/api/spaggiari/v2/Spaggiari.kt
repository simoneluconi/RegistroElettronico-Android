package com.sharpdroid.registroelettronico.api.spaggiari.v2

import android.util.Log
import android.util.SparseArray
import com.google.gson.GsonBuilder
import com.sharpdroid.registroelettronico.Info.API_URL
import com.sharpdroid.registroelettronico.api.spaggiari.v2.deserializer.DateDeserializer
import com.sharpdroid.registroelettronico.api.spaggiari.v2.deserializer.LongDeserializer
import com.sharpdroid.registroelettronico.database.entities.LoginRequest
import com.sharpdroid.registroelettronico.database.entities.Profile
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class Spaggiari(val profile: Profile?) {

    companion object {
        private val cache = SparseArray<SpaggiariAPI>()
    }

    private val loginInterceptor by lazy {
        Interceptor { chain ->
            val original = chain.request()
            if (profile != null && original.url().toString() != API_URL + "auth/login" && profile.expire.time < System.currentTimeMillis()) {
                Log.d("Spaggiari", "token expired, requesting new token")

                val ident = if (profile.isMulti) profile.ident else ""

                api().postLoginBlocking(LoginRequest(profile.password, profile.username, ident)).blockingSubscribe({
                    if (it?.isSuccessful == true) {
                        profile.token = it.body()?.token ?: throw IllegalStateException("token not in response body")
                        profile.expire = it.body()?.expire ?: throw IllegalStateException("expire not in response body")

                        Log.d("Spaggiari", "token updated: " + it.body()?.token)
                        DatabaseHelper.database.profilesDao().update(profile)
                    }
                }, {
                    Log.e("Spaggiari", it?.localizedMessage, it)
                })
            }
            try {
                return@Interceptor chain.proceed(original)
            } catch (err: Error) {
                return@Interceptor null
            }
        }
    }

    private val headers by lazy {
        Interceptor { chain ->
            val original = chain.request()

            val request = original.newBuilder()
                    .header("User-Agent", "CVVS/std/1.6.1 Android/1.6.1")
                    .header("Z-Dev-Apikey", "Tg1NWEwNGIgIC0K")
                    .header("Z-Auth-Token", profile?.token.orEmpty())
                    .method(original.method(), original.body())
                    .url(original.url().toString().replace("%7BstudentId%7D", profile?.id.toString()))
                    .build()

            try {
                val res = chain.proceed(request)
                if (!res.isSuccessful) {
                    Log.d("Spaggiari", "request not successful ${res.code()} ${request.method()} ${request.url()}")
                }
                return@Interceptor res
            } catch (err: Error) {
                return@Interceptor null
            }
        }
    }

    private val okHttp by lazy {
        OkHttpClient.Builder()
                .addInterceptor(loginInterceptor)
                .addInterceptor(headers)
                .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create(
                        GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                                .registerTypeAdapter(Date::class.java, DateDeserializer())
                                .registerTypeAdapter(Long::class.java, LongDeserializer())
                                .create()))

                .baseUrl(API_URL)
                .client(okHttp)
                .build()
    }

    fun api(): SpaggiariAPI {
        if (cache.get(profile?.id?.toInt() ?: 0, null) != null) return cache[profile?.id?.toInt()
                ?: 0]
        val retrofit = retrofit.create(SpaggiariAPI::class.java)
        cache.put(profile?.id?.toInt() ?: 0, retrofit)
        return retrofit
    }
}