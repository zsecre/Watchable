package com.watchable.app.ui.screens
  import androidx.compose.foundation.background
  import androidx.compose.foundation.layout.*
  import androidx.compose.foundation.lazy.LazyColumn
  import androidx.compose.material3.*
  import androidx.compose.runtime.*
  import androidx.compose.ui.Alignment
  import androidx.compose.ui.Modifier
  import androidx.compose.ui.unit.dp
  import com.watchable.app.data.model.*
  import com.watchable.app.ui.components.*
  import com.watchable.app.ui.theme.BrandCyan
  import com.watchable.app.viewmodel.HomeViewModel
  @Composable
  fun HomeScreen(viewModel: HomeViewModel, onNavigateToDetail: (String, String) -> Unit) {
      val state by viewModel.uiState.collectAsState()
      fun nav(item: MediaItem) = onNavigateToDetail(item.id, when (item.type) { MediaType.MOVIE -> "movie"; MediaType.TV_SHOW -> "tv"; MediaType.ANIME -> "anime" })
      if (state.isLoading) { Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BrandCyan) }; return }
      LazyColumn(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
          item { FeaturedCarousel(state.featured, { nav(it) }, Modifier.fillMaxWidth().height(400.dp)) }
          item { Spacer(Modifier.height(24.dp)) }
          if (state.trendingMovies.isNotEmpty()) item { MediaRow("Trending Movies", state.trendingMovies, { nav(it) }); Spacer(Modifier.height(20.dp)) }
          if (state.trendingTv.isNotEmpty()) item { MediaRow("Trending TV Shows", state.trendingTv, { nav(it) }); Spacer(Modifier.height(20.dp)) }
          if (state.topAnime.isNotEmpty()) item { MediaRow("Top Anime", state.topAnime, { nav(it) }); Spacer(Modifier.height(24.dp)) }
      }
  }
  