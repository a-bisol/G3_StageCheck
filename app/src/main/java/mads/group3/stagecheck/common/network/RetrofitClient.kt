package mads.group3.stagecheck.common.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface EventSearchApi {
    @POST("syncEvents")
    suspend fun searchEvents(@Body request: SearchRequest): SearchResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://us-central1-g3-stagecheck.cloudfunctions.net/"

    val api: EventSearchApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventSearchApi::class.java)
    }
}