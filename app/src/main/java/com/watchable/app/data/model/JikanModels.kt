package com.watchable.app.data.model
  import com.google.gson.annotations.SerializedName
  data class JikanResponse(@SerializedName("data") val data: List<JikanAnime>)
  data class JikanAnime(@SerializedName("mal_id") val malId: Int, @SerializedName("title") val title: String?, @SerializedName("title_english") val titleEnglish: String?, @SerializedName("images") val images: JikanImages?, @SerializedName("score") val score: Double?, @SerializedName("synopsis") val synopsis: String?, @SerializedName("aired") val aired: JikanAired?, @SerializedName("genres") val genres: List<JikanGenre>?)
  data class JikanImages(@SerializedName("jpg") val jpg: JikanImageUrls?)
  data class JikanImageUrls(@SerializedName("image_url") val imageUrl: String?, @SerializedName("large_image_url") val largeImageUrl: String?)
  data class JikanAired(@SerializedName("string") val string: String?)
  data class JikanGenre(@SerializedName("name") val name: String)
  fun JikanAnime.toMediaItem() = MediaItem(id = "jikan_${malId}", title = titleEnglish ?: title ?: "Unknown", posterPath = images?.jpg?.largeImageUrl ?: images?.jpg?.imageUrl ?: "", backdropPath = null, voteAverage = score ?: 0.0, overview = synopsis ?: "", type = MediaType.ANIME, releaseDate = aired?.string, genres = genres?.map { it.name } ?: emptyList())
  