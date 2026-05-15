package com.watchable.app.data.repository
  import com.watchable.app.data.model.*
  import com.watchable.app.data.remote.JikanClient
  import com.watchable.app.data.remote.TmdbClient
  class MediaRepository {
      private val tmdb = TmdbClient.api; private val jikan = JikanClient.api
      suspend fun getTrendingAll() = runCatching { tmdb.getTrending().results.map { val t = if (it.name != null && it.title == null) MediaType.TV_SHOW else MediaType.MOVIE; it.toMediaItem(t) } }.getOrDefault(emptyList())
      suspend fun getPopularMovies() = runCatching { tmdb.getPopularMovies().results.map { it.toMediaItem(MediaType.MOVIE) } }.getOrDefault(emptyList())
      suspend fun getTopRatedMovies() = runCatching { tmdb.getTopRatedMovies().results.map { it.toMediaItem(MediaType.MOVIE) } }.getOrDefault(emptyList())
      suspend fun getNowPlayingMovies() = runCatching { tmdb.getNowPlayingMovies().results.map { it.toMediaItem(MediaType.MOVIE) } }.getOrDefault(emptyList())
      suspend fun getUpcomingMovies() = runCatching { tmdb.getUpcomingMovies().results.map { it.toMediaItem(MediaType.MOVIE) } }.getOrDefault(emptyList())
      suspend fun getPopularTvShows() = runCatching { tmdb.getPopularTvShows().results.map { it.toMediaItem(MediaType.TV_SHOW) } }.getOrDefault(emptyList())
      suspend fun getTopRatedTvShows() = runCatching { tmdb.getTopRatedTvShows().results.map { it.toMediaItem(MediaType.TV_SHOW) } }.getOrDefault(emptyList())
      suspend fun getOnTheAirTvShows() = runCatching { tmdb.getOnTheAirTvShows().results.map { it.toMediaItem(MediaType.TV_SHOW) } }.getOrDefault(emptyList())
      suspend fun getTopAnime() = runCatching { jikan.getTopAnime().data.map { it.toMediaItem() } }.getOrDefault(emptyList())
      suspend fun getCurrentSeasonAnime() = runCatching { jikan.getCurrentSeasonAnime().data.map { it.toMediaItem() } }.getOrDefault(emptyList())
      suspend fun getTrendingMovies() = runCatching { tmdb.getTrendingMovies().results.map { it.toMediaItem(MediaType.MOVIE) } }.getOrDefault(emptyList())
      suspend fun getTrendingTv() = runCatching { tmdb.getTrendingTv().results.map { it.toMediaItem(MediaType.TV_SHOW) } }.getOrDefault(emptyList())
      suspend fun searchMulti(query: String) = runCatching {
          val t = tmdb.searchMulti(query).results.filter { it.mediaType != "person" }.map { it.toMediaItem() }
          val a = if (query.length >= 2) jikan.searchAnime(query).data.take(5).map { it.toMediaItem() } else emptyList()
          t + a
      }.getOrDefault(emptyList())
  }
  