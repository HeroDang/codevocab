package com.group20.codevocab.data.remote

import android.content.Context
import com.group20.codevocab.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private var tokenManager: TokenManager? = null

    fun init(context: Context) {
        tokenManager = TokenManager(context)
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()

        // Lấy token từ TokenManager nếu đã được khởi tạo
        tokenManager?.getToken()?.let { token ->
            requestBuilder.addHeader(
                "Authorization",
                "Bearer $token"
            )
        }

        chain.proceed(requestBuilder.build())
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // Giữ lại hàm này nếu code cũ của bạn đang sử dụng nó
    fun getApiService(): ApiService = api
}
