package com.sharpdroid.registroelettronico.API.V2

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.google.android.gms.security.ProviderInstaller
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.API.V2.Deserializer.DateDeserializer
import com.sharpdroid.registroelettronico.API.V2.Deserializer.LongDeserializer
import com.sharpdroid.registroelettronico.Databases.Entities.LoginRequest
import com.sharpdroid.registroelettronico.Databases.Entities.LoginResponse
import com.sharpdroid.registroelettronico.Databases.Entities.Profile
import com.sharpdroid.registroelettronico.Info.API_URL
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class APIClient {

    companion object {


        fun with(context: Context): SpaggiariREST {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val profile = Profile.getProfile(context)

            try {
                //Installa il supporto al TSL se non è presente
                ProviderInstaller.installIfNeeded(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val loginInterceptor = Interceptor { chain: Interceptor.Chain ->
                val original = chain.request()
                //EXPIRED TOKEN && NOT LOGGIN IN
                //TODO: use token & expireDate of each profile
                if (original.url().toString() != API_URL + "/auth/login" && profile?.expire ?: 0 < System.currentTimeMillis()) {
                    Log.d("LOGIN INTERCEPTOR", "TOKEN EXPIRED, REQUESTING NEW TOKEN")

                    val loginRes = chain.proceed(original.newBuilder()
                            .url(API_URL + "/auth/login")
                            .method("POST",
                                    RequestBody.create(
                                            MediaType.parse("application/json"),
                                            LoginRequest(profile?.password.orEmpty(), profile?.username.orEmpty()).toString() //properly override to provide a json-like string
                                    )
                            )
                            .header("User-Agent", "zorro/1.0")
                            .header("Z-Dev-Apikey", "+zorro+")
                            .build())

                    if (loginRes.isSuccessful) {
                        val loginResponse = Gson().fromJson(loginRes.body()?.string(), LoginResponse::class.java)

                        Log.d("LOGIN INTERCEPTOR", "UPDATE TOKEN: " + loginResponse.token)


                        profile?.expire = loginResponse.expire.time
                        profile?.token = loginResponse.token
                        SugarRecord.update(profile)

                        sharedPref.edit()
                                .putBoolean("spaggiari-logged", false)
                                .apply()
                        chain.proceed(original)
                    } else {
                        sharedPref.edit().putBoolean("spaggiari-logged", false).apply()
                        chain.proceed(original)
                    }

                } else {
                    chain.proceed(original)
                }
            }

            val zorro = Interceptor { chain ->
                val original = chain.request()

                val request = original.newBuilder()
                        .header("User-Agent", "zorro/1.0")
                        .header("Z-Dev-Apikey", "+zorro+")
                        .header("Z-Auth-Token", profile?.token.orEmpty())
                        .method(original.method(), original.body())
                        .url(original.url().toString().replace("%7BstudentId%7D", profile?.id.toString()))
                        .build()
                val res = chain.proceed(request)

                if (!res.isSuccessful)
                    Log.d("REQUEST", request.method() + " " + request.url().toString() + " " + request.headers().toString() + " ==> " + res.body()?.string())
                else Log.d("REQUEST", request.method() + " " + request.url().toString() + " " + request.headers().toString())

                res
            }


            val okHttp = OkHttpClient.Builder()
                    .addInterceptor(loginInterceptor)
                    .addInterceptor(zorro)
                    .build()


            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .addConverterFactory(GsonConverterFactory.create(
                            GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                                    .registerTypeAdapter(Date::class.java, DateDeserializer())
                                    .registerTypeAdapter(Long::class.java, LongDeserializer())
                                    .create()))

                    .baseUrl(API_URL)
                    .client(okHttp)
                    .build()
            return retrofit.create(SpaggiariREST::class.java)
        }
    }
}