package com.watchable.app.ui.screens
  import androidx.compose.foundation.background
  import androidx.compose.foundation.layout.*
  import androidx.compose.foundation.lazy.grid.*
  import androidx.compose.foundation.shape.RoundedCornerShape
  import androidx.compose.material.icons.Icons
  import androidx.compose.material.icons.filled.*
  import androidx.compose.material3.*
  import androidx.compose.runtime.*
  import androidx.compose.ui.Alignment
  import androidx.compose.ui.Modifier
  import androidx.compose.ui.unit.dp
  import com.watchable.app.data.model.MediaType
  import com.watchable.app.ui.components.MediaCard
  import com.watchable.app.ui.theme.BrandCyan
  import com.watchable.app.viewmodel.MediaViewModel
  @Composable
  fun SearchScreen(viewModel: MediaViewModel, onNavigateToDetail: (String, String) -> Unit) {
      val state by viewModel.searchState.collectAsState()
      var query by remember { mutableStateOf("") }
      Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)) {
          Spacer(Modifier.height(8.dp))
          Text("Search", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.onBackground)
          Spacer(Modifier.height(16.dp))
          OutlinedTextField(value = query, onValueChange = { query = it; viewModel.search(it) }, placeholder = { Text("Movies, TV shows, anime...") }, leadingIcon = { Icon(Icons.Default.Search, null, tint = BrandCyan) }, trailingIcon = { if (query.isNotEmpty()) IconButton({ query = ""; viewModel.search("") }) { Icon(Icons.Default.Close, null) } }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandCyan, cursorColor = BrandCyan), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)
          Spacer(Modifier.height(16.dp))
          when {
              state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BrandCyan) }
              query.isBlank() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Search for movies, shows & anime", color = MaterialTheme.colorScheme.onSurfaceVariant) }
              state.results.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No results for \"${query}\"", color = MaterialTheme.colorScheme.onSurfaceVariant) }
              else -> LazyVerticalGrid(GridCells.Fixed(3), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                  items(state.results) { item -> MediaCard(item, { onNavigateToDetail(item.id, when (item.type) { MediaType.MOVIE -> "movie"; MediaType.TV_SHOW -> "tv"; MediaType.ANIME -> "anime" }) }, 110.dp) }
              }
          }
      }
  }
  