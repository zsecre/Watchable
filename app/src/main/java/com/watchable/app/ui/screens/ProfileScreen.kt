package com.watchable.app.ui.screens
  import androidx.compose.foundation.background
  import androidx.compose.foundation.layout.*
  import androidx.compose.foundation.lazy.LazyColumn
  import androidx.compose.foundation.lazy.items
  import androidx.compose.foundation.shape.*
  import androidx.compose.material.icons.Icons
  import androidx.compose.material.icons.filled.*
  import androidx.compose.material3.*
  import androidx.compose.runtime.*
  import androidx.compose.ui.Alignment
  import androidx.compose.ui.Modifier
  import androidx.compose.ui.draw.clip
  import androidx.compose.ui.graphics.Color
  import androidx.compose.ui.layout.ContentScale
  import androidx.compose.ui.text.font.FontWeight
  import androidx.compose.ui.unit.dp
  import coil.compose.AsyncImage
  import com.watchable.app.ui.theme.BrandCyan
  import com.watchable.app.viewmodel.MediaViewModel
  @Composable
  fun ProfileScreen(viewModel: MediaViewModel) {
      val username by viewModel.username.collectAsState()
      val darkTheme by viewModel.darkTheme.collectAsState()
      val watchlist by viewModel.watchlist.collectAsState()
      val history by viewModel.history.collectAsState()
      var showClear by remember { mutableStateOf(false) }
      var showEdit by remember { mutableStateOf(false) }
      var newName by remember { mutableStateOf("") }
      if (showClear) AlertDialog(onDismissRequest = { showClear = false }, title = { Text("Clear History") }, text = { Text("Clear your entire watch history?") }, confirmButton = { TextButton({ viewModel.clearHistory(); showClear = false }) { Text("Clear", color = MaterialTheme.colorScheme.error) } }, dismissButton = { TextButton({ showClear = false }) { Text("Cancel") } })
      if (showEdit) AlertDialog(onDismissRequest = { showEdit = false }, title = { Text("Edit Name") }, text = { OutlinedTextField(newName, { if (it.length <= 10) newName = it }, placeholder = { Text("Your name") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandCyan, cursorColor = BrandCyan), singleLine = true) }, confirmButton = { TextButton({ if (newName.trim().length >= 2) { viewModel.setUsername(newName.trim()); showEdit = false } }) { Text("Save", color = BrandCyan) } }, dismissButton = { TextButton({ showEdit = false }) { Text("Cancel") } })
      LazyColumn(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
          item { Spacer(Modifier.height(8.dp)); Text("Profile", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.onBackground) }
          item { Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
              Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                  Box(Modifier.size(64.dp).clip(CircleShape).background(BrandCyan), contentAlignment = Alignment.Center) { Text((username?.firstOrNull() ?: 'W').uppercaseChar().toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Color.Black) }
                  Column(Modifier.weight(1f)) { Text(username ?: "Watchable", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground); Text("${watchlist.size} saved • ${history.size} watched", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                  IconButton({ newName = username ?: ""; showEdit = true }) { Icon(Icons.Default.Edit, "Edit", tint = BrandCyan) }
              }
          } }
          item { Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
              Column(Modifier.padding(8.dp)) {
                  Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) { Icon(if (darkTheme) Icons.Default.DarkMode else Icons.Default.LightMode, null, tint = BrandCyan, modifier = Modifier.size(22.dp)); Text(if (darkTheme) "Dark Mode" else "Light Mode", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f)); Switch(checked = darkTheme, onCheckedChange = { viewModel.setDarkTheme(it) }, colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = BrandCyan)) }
                  HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f))
                  Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) { Icon(Icons.Default.History, null, tint = BrandCyan, modifier = Modifier.size(22.dp)); Column(Modifier.weight(1f)) { Text("Watch History", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground); Text("${history.size} items", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }; TextButton({ showClear = true }) { Text("Clear", color = MaterialTheme.colorScheme.error) } }
              }
          } }
          if (history.isNotEmpty()) {
              item { Text("Recently Watched", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground) }
              items(history.take(10)) { h -> Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) { Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) { AsyncImage(h.posterPath, h.title, contentScale = ContentScale.Crop, modifier = Modifier.size(48.dp, 72.dp).clip(RoundedCornerShape(8.dp))); Text(h.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f)); Icon(Icons.Default.History, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) } } }
          }
      }
  }
  