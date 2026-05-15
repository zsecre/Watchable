package com.watchable.app.ui.components
  import androidx.compose.foundation.background
  import androidx.compose.foundation.clickable
  import androidx.compose.foundation.layout.*
  import androidx.compose.foundation.shape.CircleShape
  import androidx.compose.foundation.shape.RoundedCornerShape
  import androidx.compose.material.icons.Icons
  import androidx.compose.material.icons.filled.PlayArrow
  import androidx.compose.material.icons.filled.Star
  import androidx.compose.material3.*
  import androidx.compose.runtime.*
  import androidx.compose.ui.Alignment
  import androidx.compose.ui.Modifier
  import androidx.compose.ui.draw.clip
  import androidx.compose.ui.graphics.*
  import androidx.compose.ui.layout.ContentScale
  import androidx.compose.ui.text.font.FontWeight
  import androidx.compose.ui.text.style.TextOverflow
  import androidx.compose.ui.unit.*
  import coil.compose.AsyncImage
  import com.google.accompanist.pager.*
  import com.watchable.app.data.model.MediaItem
  import com.watchable.app.ui.theme.BrandCyan
  import com.watchable.app.ui.theme.StarYellow
  import kotlinx.coroutines.delay
  @OptIn(ExperimentalPagerApi::class)
  @Composable
  fun FeaturedCarousel(items: List<MediaItem>, onItemClick: (MediaItem) -> Unit, modifier: Modifier = Modifier) {
      if (items.isEmpty()) return
      val pagerState = rememberPagerState()
      LaunchedEffect(pagerState) { while (true) { delay(4000); pagerState.animateScrollToPage((pagerState.currentPage + 1) % items.size) } }
      Box(modifier) {
          HorizontalPager(count = items.size, state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
              val item = items[page]
              Box(Modifier.fillMaxSize().clickable { onItemClick(item) }) {
                  AsyncImage(model = item.backdropPath ?: item.posterPath, contentDescription = item.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                  Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.4f), Color.Black.copy(0.9f)))))
                  Column(Modifier.align(Alignment.BottomStart).padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                      Text(item.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Color.White, maxLines = 2, overflow = TextOverflow.Ellipsis)
                      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) { Icon(Icons.Default.Star, null, tint = StarYellow, modifier = Modifier.size(14.dp)); Text(String.format("%.1f", item.voteAverage), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium) }
                          Button(onClick = { onItemClick(item) }, colors = ButtonDefaults.buttonColors(containerColor = BrandCyan, contentColor = Color.Black), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp), modifier = Modifier.height(32.dp), shape = RoundedCornerShape(8.dp)) { Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Details", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                      }
                  }
              }
          }
          Row(Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
              repeat(items.size) { i -> val sel = pagerState.currentPage == i; Box(Modifier.clip(if (sel) RoundedCornerShape(4.dp) else CircleShape).background(if (sel) BrandCyan else Color.White.copy(0.4f)).size(if (sel) 20.dp else 6.dp, 6.dp)) }
          }
      }
  }
  