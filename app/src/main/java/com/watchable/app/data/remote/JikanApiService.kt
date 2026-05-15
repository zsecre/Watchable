package com.watchable.app.data.remote
  import com.watchable.app.data.model.JikanResponse
  import okhttp3.OkHttpClient
  import retrofit2.Retrofit
  import retrofit2.converter.gson.GsonConverterFactory
  import retrofit2.http.*
  interface JikanApiService {
      @GET("top/anime") suspend fun getTopAnime(@Query("page") page: Int = 1, @Query("limit") limit: Int = 20): JikanResponse
      @GET("seasons/now") suspend fun getCurrentSeasonAnime(@Query("page") page: Int = 1): JikanResponse
      @GET("anime") suspend fun searchAnime(@Query("q") query: String, @Query("page") page: Int = 1, @Query("limit") limit: Int = 20): JikanResponse
  }
  object JikanClient {
      val api: JikanApiService = Retrofit.Builder().baseUrl("https://api.jikan.moe/v4/")
          .client(OkHttpClient.Builder().build())
          .addConverterFactory(GsonConverterFactory.create()).build().create(JikanApiService::class.java)
  }
  