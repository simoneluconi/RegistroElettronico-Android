package com.sharpdroid.registroelettronico.api

import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object Cloud {
    private val okHttp: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    private val retrofit: Retrofit by lazy {
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