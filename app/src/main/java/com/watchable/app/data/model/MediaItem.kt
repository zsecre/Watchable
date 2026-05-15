package com.watchable.app.data.model
  data class MediaItem(val id: String, val title: String, val posterPath: String, val backdropPath: String?, val voteAverage: Double, val overview: String, val type: MediaType, val releaseDate: String?, val genres: List<String> = emptyList())
  enum class MediaType { MOVIE, TV_SHOW, ANIME }
  data class WatchlistItem(val mediaId: String, val title: String, val posterPath: String, val type: MediaType, val addedAt: Long = System.currentTimeMillis())
  data class HistoryItem(val mediaId: String, val title: String, val posterPath: String, val type: MediaType, val watchedAt: Long = System.currentTimeMillis())
  data class Comment(val id: String = "", val mediaId: String = "", val userId: String = "", val userName: String = "", val text: String = "", val timestampMs: Long = System.currentTimeMillis())
  