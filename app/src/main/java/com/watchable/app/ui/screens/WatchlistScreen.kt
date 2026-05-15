package com.watchable.app.ui.screens
  import androidx.compose.foundation.background
  import androidx.compose.foundation.layout.*
  import androidx.compose.foundation.lazy.grid.*
  import androidx.compose.material.icons.Icons
  import androidx.compose.material.icons.outlined.BookmarkAdd
  import androidx.compose.material3.*
  import androidx.compose.runtime.*
  import androidx.compose.ui.Alignment
  import androidx.compose.ui.Modifier
  import androidx.compose.ui.unit.dp
  import com.watchable.app.data.model.*
  import com.watchable.app.ui.components.MediaCard
  import com.watchable.app.viewmodel.MediaViewModel
  @Composable
  fun WatchlistScreen(viewModel: MediaViewModel, onNavigateToDetail: (String, String) -> Unit) {
      val watchlist by viewModel.watchlist.collectAsState()
      Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)) {
          Spacer(Modifier.height(8.dp))
          Text("My Library", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.onBackground)
          Text("${watchlist.size} saved titles", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Spacer(Modifier.height(16.dp))
          if (watchlist.isEmpty()) {
              Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) { Icon(Icons.Outlined.BookmarkAdd, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp)); Text("Your watchlist is empty", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.titleMedium) } }
          } else {
              LazyVerticalGrid(GridCells.Fixed(3), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                  items(watchlist) { w -> MediaCard(MediaItem(w.mediaId, w.title, w.posterPath, null, 0.0, "", w.type, null), { onNavigateToDetail(w.mediaId, when (w.type) { MediaType.MOVIE -> "movie"; MediaType.TV_SHOW -> "tv"; MediaType.ANIME -> "anime" }) }, 110.dp) }
              }
          }
      }
  }
  