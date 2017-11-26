package com.sharpdroid.registroelettronico.api.cloud.v1

import android.util.Log
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Cloud {
    private val okHttp by lazy {
        OkHttpClient.Builder().addInterceptor { chain: Interceptor.Chain ->
            val req = chain.request()
            val res = chain.proceed(req)
            if (res.isSuccessful) {
                Log.d("Cloud", "request successful")
            } else {
                Log.d("Cloud", "request not successful ${res.code()} ${req.method()} ${req.url()} ${req.body()}")
            }

            res
        }.connectTimeout(5000, TimeUnit.MILLISECONDS).build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .baseUrl("https://registrocloud.bortolan.ml/v1/")
                .client(okHttp)
                .build()
    }

    val api: CloudApi by lazy {
        retrofit.create(CloudApi::class.java)
    }
}