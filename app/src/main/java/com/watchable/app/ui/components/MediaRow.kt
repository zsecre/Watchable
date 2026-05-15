package com.watchable.app.ui.components
  import androidx.compose.foundation.layout.*
  import androidx.compose.foundation.lazy.LazyRow
  import androidx.compose.foundation.lazy.items
  import androidx.compose.material3.*
  import androidx.compose.runtime.Composable
  import androidx.compose.ui.Modifier
  import androidx.compose.ui.text.font.FontWeight
  import androidx.compose.ui.unit.dp
  import com.watchable.app.data.model.MediaItem
  @Composable
  fun MediaRow(title: String, items: List<MediaItem>, onItemClick: (MediaItem) -> Unit, modifier: Modifier = Modifier) {
      if (items.isEmpty()) return
      Column(modifier) {
          Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(horizontal = 16.dp))
          Spacer(Modifier.height(10.dp))
          LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              items(items) { MediaCard(it, { onItemClick(it) }) }
          }
      }
  }
  