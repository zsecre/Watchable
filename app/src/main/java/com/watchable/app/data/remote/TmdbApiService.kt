package com.watchable.app.data.remote
  import com.watchable.app.data.model.*
  import okhttp3.OkHttpClient
  import okhttp3.logging.HttpLoggingInterceptor
  import retrofit2.Retrofit
  import retrofit2.converter.gson.GsonConverterFactory
  import retrofit2.http.*
  private const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
  const val TMDB_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIzNmY0N2U0NzAyZjBmZmJiMGM5Nzg4ZDA2OTk1ZWNkZSIsIm5iZiI6MTc3NjE0NDc3My4yNjgsInN1YiI6IjY5ZGRkMTg1ZTUzMmY2OTFkZWQ5NDEwOSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.dy8WanI7kFpTfCorNjBgEiHfx3nJVvBrpz9EZ6veHqo"
  interface TmdbApiService {
      @GET("movie/popular") suspend fun getPopularMovies(@Query("page") page: Int = 1): TmdbMovieResponse
      @GET("movie/top_rated") suspend fun getTopRatedMovies(@Query("page") page: Int = 1): TmdbMovieResponse
      @GET("movie/now_playing") suspend fun getNowPlayingMovies(@Query("page") page: Int = 1): TmdbMovieResponse
      @GET("movie/upcoming") suspend fun getUpcomingMovies(@Query("page") page: Int = 1): TmdbMovieResponse
      @GET("tv/popular") suspend fun getPopularTvShows(@Query("page") page: Int = 1): TmdbMovieResponse
      @GET("tv/top_rated") suspend fun getTopRatedTvShows(@Query("page") page: Int = 1): TmdbMovieResponse
      @GET("tv/on_the_air") suspend fun getOnTheAirTvShows(@Query("page") page: Int = 1): TmdbMovieResponse
      @GET("trending/all/week") suspend fun getTrending(@Query("page") page: Int = 1): TmdbMovieResponse
      @GET("trending/movie/week") suspend fun getTrendingMovies(@Query("page") page: Int = 1): TmdbMovieResponse
      @GET("trending/tv/week") suspend fun getTrendingTv(@Query("page") page: Int = 1): TmdbMovieResponse
      @GET("search/multi") suspend fun searchMulti(@Query("query") query: String, @Query("page") page: Int = 1): TmdbSearchResponse
  }
  object TmdbClient {
      val api: TmdbApiService = Retrofit.Builder().baseUrl(TMDB_BASE_URL)
          .client(OkHttpClient.Builder().addInterceptor { chain ->
              chain.proceed(chain.request().newBuilder()
                  .addHeader("Authorization", "Bearer ${TMDB_TOKEN}")
                  .addHeader("Accept", "application/json").build())
          }.build())
          .addConverterFactory(GsonConverterFactory.create()).build().create(TmdbApiService::class.java)
  }
  