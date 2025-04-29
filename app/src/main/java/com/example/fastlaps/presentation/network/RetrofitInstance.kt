package com.example.fastlaps.presentation.network

import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectionSpecs(listOf(ConnectionSpec.CLEARTEXT))
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://ergast.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: ErgastApiService = retrofit.create(ErgastApiService::class.java)
}
