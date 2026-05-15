package com.watchable.app.ui.screens
  import androidx.compose.foundation.background
  import androidx.compose.foundation.layout.*
  import androidx.compose.foundation.lazy.*
  import androidx.compose.foundation.shape.RoundedCornerShape
  import androidx.compose.foundation.text.KeyboardActions
  import androidx.compose.foundation.text.KeyboardOptions
  import androidx.compose.material.icons.Icons
  import androidx.compose.material.icons.filled.*
  import androidx.compose.material.icons.outlined.BookmarkAdd
  import androidx.compose.material3.*
  import androidx.compose.runtime.*
  import androidx.compose.ui.Alignment
  import androidx.compose.ui.Modifier
  import androidx.compose.ui.draw.clip
  import androidx.compose.ui.graphics.*
  import androidx.compose.ui.layout.ContentScale
  import androidx.compose.ui.text.font.FontWeight
  import androidx.compose.ui.text.input.ImeAction
  import androidx.compose.ui.text.style.TextOverflow
  import androidx.compose.ui.unit.*
  import coil.compose.AsyncImage
  import com.watchable.app.data.model.*
  import com.watchable.app.data.repository.MediaRepository
  import com.watchable.app.ui.theme.BrandCyan
  import com.watchable.app.ui.theme.StarYellow
  import com.watchable.app.viewmodel.MediaViewModel
  import kotlinx.coroutines.launch
  import java.util.UUID
  @Composable
  fun DetailScreen(mediaId: String, mediaType: String, mediaViewModel: MediaViewModel, onBack: () -> Unit) {
      val watchlist by mediaViewModel.watchlist.collectAsState()
      val scope = rememberCoroutineScope()
      val repo = remember { MediaRepository() }
      var item by remember { mutableStateOf<MediaItem?>(null) }
      var isLoading by remember { mutableStateOf(true) }
      val isInWatchlist = watchlist.any { it.mediaId == mediaId }
      LaunchedEffect(mediaId, mediaType) {
          isLoading = true
          item = when (mediaType) {
              "movie" -> runCatching { repo.getPopularMovies().firstOrNull { it.id == mediaId } ?: repo.getTrendingMovies().firstOrNull { it.id == mediaId } ?: repo.getTopRatedMovies().firstOrNull { it.id == mediaId } }.getOrNull()
              "tv" -> runCatching { repo.getPopularTvShows().firstOrNull { it.id == mediaId } ?: repo.getOnTheAirTvShows().firstOrNull { it.id == mediaId } ?: repo.getTopRatedTvShows().firstOrNull { it.id == mediaId } }.getOrNull()
              "anime" -> runCatching { repo.getTopAnime().firstOrNull { it.id == mediaId } ?: repo.getCurrentSeasonAnime().firstOrNull { it.id == mediaId } }.getOrNull()
              else -> null
          }
          item?.let { mediaViewModel.addToHistory(it) }
          isLoading = false
      }
      var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
      var commentText by remember { mutableStateOf("") }
      val username by mediaViewModel.username.collectAsState()
      if (isLoading) { Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BrandCyan) }; return }
      val cur = item ?: run { Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) { Text("Not found", color = MaterialTheme.colorScheme.onSurfaceVariant); TextButton(onBack) { Text("Go Back", color = BrandCyan) } } }; return }
      LazyColumn(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
          item { Box(Modifier.fillMaxWidth().height(380.dp)) {
              AsyncImage(cur.backdropPath ?: cur.posterPath, null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
              Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Black.copy(0.3f), Color.Black.copy(0.95f)))))
              IconButton(onBack, Modifier.align(Alignment.TopStart).padding(12.dp)) { Icon(Icons.Default.ArrowBack, "Back", tint = Color.White) }
              IconButton({ scope.launch { mediaViewModel.toggleWatchlist(cur) } }, Modifier.align(Alignment.TopEnd).padding(12.dp)) { Icon(if (isInWatchlist) Icons.Default.Bookmark else Icons.Outlined.BookmarkAdd, "WL", tint = if (isInWatchlist) BrandCyan else Color.White) }
              Column(Modifier.align(Alignment.BottomStart).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text(cur.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Color.White, maxLines = 2, overflow = TextOverflow.Ellipsis)
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) { Icon(Icons.Default.Star, null, tint = StarYellow, modifier = Modifier.size(16.dp)); Text(String.format("%.1f", cur.voteAverage), color = Color.White, fontWeight = FontWeight.Bold) }
                      cur.releaseDate?.take(4)?.let { Text(it, color = Color.White.copy(0.7f), fontSize = 13.sp) }
                      Box(Modifier.clip(RoundedCornerShape(4.dp)).background(BrandCyan).padding(horizontal = 8.dp, vertical = 2.dp)) { Text(when (cur.type) { MediaType.MOVIE -> "Movie"; MediaType.TV_SHOW -> "TV Show"; MediaType.ANIME -> "Anime" }, color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                  }
              }
          } }
          item { if (cur.overview.isNotBlank()) Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { Text("Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground); Text(cur.overview, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 22.sp) } }
          item {
              HorizontalDivider(Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline.copy(0.3f))
              Spacer(Modifier.height(12.dp))
              Text("Comments (${comments.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(horizontal = 16.dp))
              Spacer(Modifier.height(10.dp))
              Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  OutlinedTextField(commentText, { if (it.length <= 350) commentText = it }, placeholder = { Text("Add a comment...") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(10.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandCyan, cursorColor = BrandCyan), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send), keyboardActions = KeyboardActions(onSend = { if (commentText.trim().isNotBlank()) { comments = listOf(Comment(UUID.randomUUID().toString(), mediaId, "local", username ?: "You", commentText.trim())) + comments; commentText = "" } }))
                  IconButton({ if (commentText.trim().isNotBlank()) { comments = listOf(Comment(UUID.randomUUID().toString(), mediaId, "local", username ?: "You", commentText.trim())) + comments; commentText = "" } }, enabled = commentText.trim().isNotBlank()) { Icon(Icons.Default.Send, "Send", tint = if (commentText.trim().isNotBlank()) BrandCyan else MaterialTheme.colorScheme.onSurfaceVariant) }
              }
              Spacer(Modifier.height(12.dp))
          }
          items(comments) { c -> Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(10.dp)) { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) { Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) { Box(Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(BrandCyan), contentAlignment = Alignment.Center) { Text(c.userName.firstOrNull()?.uppercaseChar()?.toString() ?: "A", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp) }; Text(c.userName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = if (c.userId == "local") BrandCyan else MaterialTheme.colorScheme.onBackground) }; Text(c.text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground) } } }
          item { Spacer(Modifier.height(24.dp)) }
      }
  }
  