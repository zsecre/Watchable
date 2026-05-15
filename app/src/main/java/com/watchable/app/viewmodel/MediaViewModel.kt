package com.watchable.app.viewmodel
  import android.app.Application
  import androidx.lifecycle.AndroidViewModel
  import androidx.lifecycle.viewModelScope
  import com.watchable.app.data.model.*
  import com.watchable.app.data.repository.LocalRepository
  import com.watchable.app.data.repository.MediaRepository
  import kotlinx.coroutines.async
  import kotlinx.coroutines.flow.*
  import kotlinx.coroutines.launch
  data class MoviesUiState(val popular: List<MediaItem> = emptyList(), val topRated: List<MediaItem> = emptyList(), val nowPlaying: List<MediaItem> = emptyList(), val upcoming: List<MediaItem> = emptyList(), val isLoading: Boolean = true)
  data class TvUiState(val popular: List<MediaItem> = emptyList(), val topRated: List<MediaItem> = emptyList(), val onTheAir: List<MediaItem> = emptyList(), val isLoading: Boolean = true)
  data class AnimeUiState(val top: List<MediaItem> = emptyList(), val currentSeason: List<MediaItem> = emptyList(), val isLoading: Boolean = true)
  data class SearchUiState(val results: List<MediaItem> = emptyList(), val isLoading: Boolean = false, val query: String = "")
  class MediaViewModel(application: Application) : AndroidViewModel(application) {
      private val mediaRepo = MediaRepository()
      val localRepo = LocalRepository(application)
      private val _moviesState = MutableStateFlow(MoviesUiState()); val moviesState: StateFlow<MoviesUiState> = _moviesState
      private val _tvState = MutableStateFlow(TvUiState()); val tvState: StateFlow<TvUiState> = _tvState
      private val _animeState = MutableStateFlow(AnimeUiState()); val animeState: StateFlow<AnimeUiState> = _animeState
      private val _searchState = MutableStateFlow(SearchUiState()); val searchState: StateFlow<SearchUiState> = _searchState
      val watchlist = localRepo.watchlist.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
      val history = localRepo.history.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
      val username = localRepo.username.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
      val darkTheme = localRepo.darkTheme.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
      val onboardingDone = localRepo.onboardingDone.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
      fun loadMovies() { if (_moviesState.value.popular.isNotEmpty()) return; viewModelScope.launch { _moviesState.value = MoviesUiState(isLoading = true); try { val p = async { mediaRepo.getPopularMovies() }; val t = async { mediaRepo.getTopRatedMovies() }; val n = async { mediaRepo.getNowPlayingMovies() }; val u = async { mediaRepo.getUpcomingMovies() }; _moviesState.value = MoviesUiState(popular = p.await(), topRated = t.await(), nowPlaying = n.await(), upcoming = u.await(), isLoading = false) } catch (e: Exception) { _moviesState.value = MoviesUiState(isLoading = false) } } }
      fun loadTv() { if (_tvState.value.popular.isNotEmpty()) return; viewModelScope.launch { _tvState.value = TvUiState(isLoading = true); try { val p = async { mediaRepo.getPopularTvShows() }; val t = async { mediaRepo.getTopRatedTvShows() }; val o = async { mediaRepo.getOnTheAirTvShows() }; _tvState.value = TvUiState(popular = p.await(), topRated = t.await(), onTheAir = o.await(), isLoading = false) } catch (e: Exception) { _tvState.value = TvUiState(isLoading = false) } } }
      fun loadAnime() { if (_animeState.value.top.isNotEmpty()) return; viewModelScope.launch { _animeState.value = AnimeUiState(isLoading = true); try { val t = async { mediaRepo.getTopAnime() }; val s = async { mediaRepo.getCurrentSeasonAnime() }; _animeState.value = AnimeUiState(top = t.await(), currentSeason = s.await(), isLoading = false) } catch (e: Exception) { _animeState.value = AnimeUiState(isLoading = false) } } }
      fun search(query: String) { _searchState.value = SearchUiState(query = query, isLoading = query.isNotBlank()); if (query.isBlank()) return; viewModelScope.launch { try { _searchState.value = SearchUiState(query = query, results = mediaRepo.searchMulti(query), isLoading = false) } catch (e: Exception) { _searchState.value = SearchUiState(query = query, isLoading = false) } } }
      fun toggleWatchlist(item: MediaItem) { viewModelScope.launch { if (watchlist.value.any { it.mediaId == item.id }) localRepo.removeFromWatchlist(item.id) else localRepo.addToWatchlist(item) } }
      fun addToHistory(item: MediaItem) { viewModelScope.launch { localRepo.addToHistory(item) } }
      fun clearHistory() { viewModelScope.launch { localRepo.clearHistory() } }
      fun setUsername(name: String) { viewModelScope.launch { localRepo.setUsername(name); localRepo.setOnboardingDone(true) } }
      fun setDarkTheme(dark: Boolean) { viewModelScope.launch { localRepo.setDarkTheme(dark) } }
  }
  