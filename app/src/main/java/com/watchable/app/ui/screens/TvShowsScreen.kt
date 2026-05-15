package com.watchable.app.ui.screens
  import androidx.compose.foundation.background
  import androidx.compose.foundation.layout.*
  import androidx.compose.foundation.lazy.LazyColumn
  import androidx.compose.material3.*
  import androidx.compose.runtime.*
  import androidx.compose.ui.Alignment
  import androidx.compose.ui.Modifier
  import androidx.compose.ui.unit.dp
  import com.watchable.app.ui.components.MediaRow
  import com.watchable.app.ui.theme.BrandCyan
  import com.watchable.app.viewmodel.MediaViewModel
  @Composable
  fun TvShowsScreen(viewModel: MediaViewModel, onNavigateToDetail: (String, String) -> Unit) {
      val state by viewModel.tvState.collectAsState()
      LaunchedEffect(Unit) { viewModel.loadTv() }
      if (state.isLoading) { Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BrandCyan) }; return }
      LazyColumn(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentPadding = PaddingValues(vertical = 16.dp)) {
          item { Text("TV Shows", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }
          if (state.popular.isNotEmpty()) item { MediaRow("Popular", state.popular, { onNavigateToDetail(it.id, "tv") }); Spacer(Modifier.height(20.dp)) }
          if (state.topRated.isNotEmpty()) item { MediaRow("Top Rated", state.topRated, { onNavigateToDetail(it.id, "tv") }); Spacer(Modifier.height(20.dp)) }
          if (state.onTheAir.isNotEmpty()) item { MediaRow("On The Air", state.onTheAir, { onNavigateToDetail(it.id, "tv") }); Spacer(Modifier.height(24.dp)) }
      }
  }
  