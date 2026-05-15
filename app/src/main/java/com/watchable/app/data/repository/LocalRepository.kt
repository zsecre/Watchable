package com.watchable.app.data.repository
  import android.content.Context
  import androidx.datastore.preferences.core.edit
  import androidx.datastore.preferences.core.stringPreferencesKey
  import com.google.gson.Gson
  import com.google.gson.reflect.TypeToken
  import com.watchable.app.PREF_DARK_THEME
  import com.watchable.app.PREF_ONBOARDING_DONE
  import com.watchable.app.PREF_USERNAME
  import com.watchable.app.data.model.*
  import com.watchable.app.dataStore
  import kotlinx.coroutines.flow.Flow
  import kotlinx.coroutines.flow.map
  private val PREF_WATCHLIST = stringPreferencesKey("watchlist")
  private val PREF_HISTORY = stringPreferencesKey("history")
  class LocalRepository(private val context: Context) {
      private val gson = Gson()
      val username: Flow<String?> = context.dataStore.data.map { it[PREF_USERNAME] }
      val darkTheme: Flow<Boolean> = context.dataStore.data.map { it[PREF_DARK_THEME] ?: true }
      val onboardingDone: Flow<Boolean> = context.dataStore.data.map { it[PREF_ONBOARDING_DONE] ?: false }
      val watchlist: Flow<List<WatchlistItem>> = context.dataStore.data.map { prefs ->
          val json = prefs[PREF_WATCHLIST] ?: return@map emptyList()
          runCatching { gson.fromJson<List<WatchlistItem>>(json, object : TypeToken<List<WatchlistItem>>() {}.type) }.getOrDefault(emptyList())
      }
      val history: Flow<List<HistoryItem>> = context.dataStore.data.map { prefs ->
          val json = prefs[PREF_HISTORY] ?: return@map emptyList()
          runCatching { gson.fromJson<List<HistoryItem>>(json, object : TypeToken<List<HistoryItem>>() {}.type) }.getOrDefault(emptyList())
      }
      suspend fun setUsername(name: String) = context.dataStore.edit { it[PREF_USERNAME] = name }
      suspend fun setDarkTheme(dark: Boolean) = context.dataStore.edit { it[PREF_DARK_THEME] = dark }
      suspend fun setOnboardingDone(done: Boolean) = context.dataStore.edit { it[PREF_ONBOARDING_DONE] = done }
      suspend fun addToWatchlist(item: MediaItem) = context.dataStore.edit { prefs ->
          val list = runCatching { gson.fromJson<MutableList<WatchlistItem>>(prefs[PREF_WATCHLIST] ?: "[]", object : TypeToken<MutableList<WatchlistItem>>() {}.type) }.getOrDefault(mutableListOf())
          if (list.none { it.mediaId == item.id }) list.add(0, WatchlistItem(item.id, item.title, item.posterPath, item.type))
          prefs[PREF_WATCHLIST] = gson.toJson(list)
      }
      suspend fun removeFromWatchlist(mediaId: String) = context.dataStore.edit { prefs ->
          val list = runCatching { gson.fromJson<MutableList<WatchlistItem>>(prefs[PREF_WATCHLIST] ?: "[]", object : TypeToken<MutableList<WatchlistItem>>() {}.type) }.getOrDefault(mutableListOf())
          list.removeAll { it.mediaId == mediaId }
          prefs[PREF_WATCHLIST] = gson.toJson(list)
      }
      suspend fun addToHistory(item: MediaItem) = context.dataStore.edit { prefs ->
          val list = runCatching { gson.fromJson<MutableList<HistoryItem>>(prefs[PREF_HISTORY] ?: "[]", object : TypeToken<MutableList<HistoryItem>>() {}.type) }.getOrDefault(mutableListOf())
          list.removeAll { it.mediaId == item.id }
          list.add(0, HistoryItem(item.id, item.title, item.posterPath, item.type))
          if (list.size > 50) list.subList(50, list.size).clear()
          prefs[PREF_HISTORY] = gson.toJson(list)
      }
      suspend fun clearHistory() = context.dataStore.edit { it[PREF_HISTORY] = "[]" }
  }
  