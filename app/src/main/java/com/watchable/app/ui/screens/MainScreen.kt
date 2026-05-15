package com.watchable.app.ui.screens
  import androidx.compose.foundation.layout.*
  import androidx.compose.material.icons.Icons
  import androidx.compose.material.icons.filled.*
  import androidx.compose.material.icons.outlined.*
  import androidx.compose.material3.*
  import androidx.compose.runtime.*
  import androidx.compose.ui.Modifier
  import androidx.compose.ui.graphics.vector.ImageVector
  import androidx.compose.ui.unit.dp
  import com.watchable.app.ui.theme.BgSurface
  import com.watchable.app.ui.theme.BrandCyan
  import com.watchable.app.viewmodel.HomeViewModel
  import com.watchable.app.viewmodel.MediaViewModel
  data class NavItem(val label: String, val sel: ImageVector, val unsel: ImageVector)
  @Composable
  fun MainScreen(mediaViewModel: MediaViewModel, homeViewModel: HomeViewModel, onNavigateToDetail: (String, String) -> Unit) {
      var tab by remember { mutableIntStateOf(0) }
      val items = listOf(NavItem("Home", Icons.Filled.Home, Icons.Outlined.Home), NavItem("Movies", Icons.Filled.Movie, Icons.Outlined.Movie), NavItem("TV", Icons.Filled.Tv, Icons.Outlined.Tv), NavItem("Anime", Icons.Filled.Star, Icons.Outlined.StarBorder), NavItem("Search", Icons.Filled.Search, Icons.Outlined.Search), NavItem("Library", Icons.Filled.BookmarkAdded, Icons.Outlined.BookmarkAdd), NavItem("Profile", Icons.Filled.Person, Icons.Outlined.Person))
      Scaffold(bottomBar = {
          NavigationBar(containerColor = BgSurface, tonalElevation = 0.dp) {
              items.forEachIndexed { i, item ->
                  NavigationBarItem(selected = tab == i, onClick = { tab = i }, icon = { Icon(if (tab == i) item.sel else item.unsel, item.label) }, label = { Text(item.label, style = MaterialTheme.typography.labelSmall) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = BrandCyan, selectedTextColor = BrandCyan, unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant, unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant, indicatorColor = BrandCyan.copy(alpha = 0.1f)))
              }
          }
      }) { padding ->
          Box(Modifier.padding(padding)) {
              when (tab) {
                  0 -> HomeScreen(homeViewModel, onNavigateToDetail)
                  1 -> MoviesScreen(mediaViewModel, onNavigateToDetail)
                  2 -> TvShowsScreen(mediaViewModel, onNavigateToDetail)
                  3 -> AnimeScreen(mediaViewModel, onNavigateToDetail)
                  4 -> SearchScreen(mediaViewModel, onNavigateToDetail)
                  5 -> WatchlistScreen(mediaViewModel, onNavigateToDetail)
                  6 -> ProfileScreen(mediaViewModel)
              }
          }
      }
  }
  