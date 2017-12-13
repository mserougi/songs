package com.tooploox.song

import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import tooploox.com.song.Songs

/**
 * Created by mohammed on 12/13/17.
 */
interface ApiService {

    @GET("search")
    fun getSongs(@Query("term") term: String): Observable<Songs>

    /**
     * Companion object to create the API service
     */
    companion object Factory {
        fun create(): ApiService {

            val builder = OkHttpClient().newBuilder()

            // Skip SSL for now
            builder.hostnameVerifier { _, _ -> true }

            val baseUrl = getBaseUrl()
            
            // Headers that apply to all requests
            val headerInterceptor = Interceptor { chain ->
                val request = chain.request()?.newBuilder()?.addHeader("Content-Type", "application/json")?.build()
                chain.proceed(request)
            }
            builder.addInterceptor(headerInterceptor)

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(logging)

            val okHttpClient = builder.build()

            val retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(baseUrl)
                    .build()

            return retrofit.create(ApiService::class.java)
        }

        private fun getBaseUrl(): String
        {
            return "https://itunes.apple.com/"
        }
    }
}
