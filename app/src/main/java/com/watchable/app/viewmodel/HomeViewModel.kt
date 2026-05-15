package com.watchable.app.viewmodel
  import androidx.lifecycle.ViewModel
  import androidx.lifecycle.viewModelScope
  import com.watchable.app.data.model.MediaItem
  import com.watchable.app.data.repository.MediaRepository
  import kotlinx.coroutines.async
  import kotlinx.coroutines.flow.*
  import kotlinx.coroutines.launch
  data class HomeUiState(val featured: List<MediaItem> = emptyList(), val trendingMovies: List<MediaItem> = emptyList(), val trendingTv: List<MediaItem> = emptyList(), val topAnime: List<MediaItem> = emptyList(), val isLoading: Boolean = true, val error: String? = null)
  class HomeViewModel : ViewModel() {
      private val repository = MediaRepository()
      private val _uiState = MutableStateFlow(HomeUiState())
      val uiState: StateFlow<HomeUiState> = _uiState
      init { loadHome() }
      fun loadHome() { viewModelScope.launch {
          _uiState.value = HomeUiState(isLoading = true)
          try {
              val f = async { repository.getTrendingAll() }
              val m = async { repository.getTrendingMovies() }
              val t = async { repository.getTrendingTv() }
              val a = async { repository.getTopAnime() }
              _uiState.value = HomeUiState(featured = f.await().take(8), trendingMovies = m.await().take(10), trendingTv = t.await().take(10), topAnime = a.await().take(10), isLoading = false)
          } catch (e: Exception) { _uiState.value = HomeUiState(isLoading = false, error = e.message) }
      } }
  }
  