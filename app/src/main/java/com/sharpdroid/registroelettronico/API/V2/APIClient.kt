package com.sharpdroid.registroelettronico.API.V2

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.google.android.gms.security.ProviderInstaller
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sharpdroid.registroelettronico.API.V2.Deserializer.DateDeserializer
import com.sharpdroid.registroelettronico.Databases.Entities.LoginRequest
import com.sharpdroid.registroelettronico.Databases.Entities.LoginResponse
import com.sharpdroid.registroelettronico.Info
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

            try {
                //Installa il supporto al TSL se non è presente
                ProviderInstaller.installIfNeeded(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val studentId = Interceptor { chain: Interceptor.Chain ->
                var request = chain.request()

                if (request.url().toString().contains("{studentId}"))
                    request = request.newBuilder()
                            .method(request.method(), request.body())
                            .url(request.url().toString().replace("{studentId}", sharedPref.getString(Info.Spaggiari.IDENT, ""))).build()

                chain.proceed(request)
            }

            val loginInterceptor = Interceptor { chain: Interceptor.Chain ->
                val original = chain.request()

                //EXPIRED TOKEN && NOT LOGGIN IN
                if (original.url().toString() != "https://web.spaggiari.eu/rest/v1/auth/login" && sharedPref.getLong("spaggiari-expireDate", 0L) < System.currentTimeMillis()) {
                    Log.d("LOGIN INTERCEPTOR", "TOKEN EXPIRED, REQUESTING NEW TOKEN")

                    val loginRes = chain.proceed(original.newBuilder()
                            .url("https://web.spaggiari.eu/rest/v1/auth/login")
                            .method("POST",
                                    RequestBody.create(
                                            MediaType.parse("application/json"),
                                            LoginRequest(sharedPref.getString("spaggiari-pass", ""), sharedPref.getString("spaggiari-user", "")).toString() //properly override to provide a json-like string
                                    )
                            )
                            .header("User-Agent", "zorro/1.0")
                            .header("Z-Dev-Apikey", "+zorro+")
                            .build())

                    if (loginRes.isSuccessful) {
                        val loginResponse = Gson().fromJson(loginRes.body()?.string(), LoginResponse::class.java)

                        Log.d("LOGIN INTERCEPTOR", "UPDATE TOKEN: " + loginResponse.token)

                        sharedPref.edit()
                                .putString("spaggiari-token", loginResponse.token)
                                .putLong("spaggiari-expireDate", loginResponse.expire.time)
                                .putBoolean("spaggiari-logged", false)
                                .apply()
                        chain.proceed(original)
                    } else {
                        Log.d("LOGIN INTERCEPTOR", loginRes.body().toString())
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
                        .header("Z-Auth-Token", sharedPref.getString(Info.Spaggiari.TOKEN, ""))
                        .method(original.method(), original.body())
                        .build()

                chain.proceed(request)
            }


            val okHttp = OkHttpClient.Builder()
                    .addInterceptor(studentId)
                    .addInterceptor(loginInterceptor)
                    .addInterceptor(zorro)
                    .build()


            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .addConverterFactory(GsonConverterFactory.create(
                            GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                                    .registerTypeAdapter(Date::class.java, DateDeserializer())
                                    .create()))

                    .baseUrl("https://web.spaggiari.eu/")
                    .client(okHttp)
                    .build()
            return retrofit.create(SpaggiariREST::class.java)
        }
    }
}