package com.watchable.app.ui.components
  import androidx.compose.foundation.background
  import androidx.compose.foundation.clickable
  import androidx.compose.foundation.layout.*
  import androidx.compose.foundation.shape.RoundedCornerShape
  import androidx.compose.material.icons.Icons
  import androidx.compose.material.icons.filled.Star
  import androidx.compose.material3.*
  import androidx.compose.runtime.Composable
  import androidx.compose.ui.Alignment
  import androidx.compose.ui.Modifier
  import androidx.compose.ui.draw.clip
  import androidx.compose.ui.graphics.Color
  import androidx.compose.ui.layout.ContentScale
  import androidx.compose.ui.text.font.FontWeight
  import androidx.compose.ui.text.style.TextOverflow
  import androidx.compose.ui.unit.*
  import coil.compose.AsyncImage
  import com.watchable.app.data.model.*
  import com.watchable.app.ui.theme.*
  @Composable
  fun MediaCard(item: MediaItem, onClick: () -> Unit, width: Dp = 130.dp, modifier: Modifier = Modifier) {
      Column(modifier.width(width).clickable(onClick = onClick)) {
          Box(Modifier.fillMaxWidth().height(width * 1.5f).clip(RoundedCornerShape(10.dp)).background(BgCard)) {
              AsyncImage(model = item.posterPath, contentDescription = item.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
              Box(Modifier.align(Alignment.TopEnd).padding(6.dp).clip(RoundedCornerShape(6.dp)).background(Color.Black.copy(alpha = 0.75f)).padding(horizontal = 5.dp, vertical = 2.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                      Icon(Icons.Filled.Star, null, tint = StarYellow, modifier = Modifier.size(10.dp))
                      Text(String.format("%.1f", item.voteAverage), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                  }
              }
              Box(Modifier.align(Alignment.TopStart).padding(6.dp).clip(RoundedCornerShape(4.dp)).background(when (item.type) { MediaType.MOVIE -> BrandCyan.copy(0.85f); MediaType.TV_SHOW -> Color(0xFF6366F1).copy(0.85f); MediaType.ANIME -> Color(0xFFEC4899).copy(0.85f) }).padding(horizontal = 5.dp, vertical = 2.dp)) {
                  Text(when (item.type) { MediaType.MOVIE -> "Movie"; MediaType.TV_SHOW -> "TV"; MediaType.ANIME -> "Anime" }, color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              }
          }
          Spacer(Modifier.height(6.dp))
          Text(item.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onBackground)
      }
  }
  