package com.example.prova1_api_catdog.core

import com.example.prova1_api_catdog.data.service.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    // URLs das APIs
    private const val CAT_BASE_URL = "https://api.thecatapi.com/"
    private const val DOG_BASE_URL = "https://api.thedogapi.com/"

    // Suas API Keys
    private const val CAT_API_KEY = "live_Ip1vjimKxE4wZllptMsgfDi0P1ZJ5qRCjS8jFfrtdt6TyVYNIjzg4MgGCQbuLloH"
    private const val DOG_API_KEY = "live_scBFtITqJSBZpc59ol44egFqm7wM2LZm6QYrDasz2z9Chd0JnPsZBhdFcvjlBZHo"

    // Interceptor para Gatos
    private val authInterceptorCat = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("x-api-key", CAT_API_KEY)
            .build()
        chain.proceed(request)
    }

    // Interceptor para Cachorros
    private val authInterceptorDog = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("x-api-key", DOG_API_KEY)
            .build()
        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private fun createOkHttpClient(authInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val catClient = createOkHttpClient(authInterceptorCat)
    private val dogClient = createOkHttpClient(authInterceptorDog)

    private val catRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(CAT_BASE_URL)
            .client(catClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val dogRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(DOG_BASE_URL)
            .client(dogClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val catApiService: ApiService by lazy {
        catRetrofit.create(ApiService::class.java)
    }

    val dogApiService: ApiService by lazy {
        dogRetrofit.create(ApiService::class.java)
    }
}