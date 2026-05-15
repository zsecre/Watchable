package com.watchable.app
  import android.os.Bundle
  import androidx.activity.ComponentActivity
  import androidx.activity.compose.setContent
  import androidx.activity.enableEdgeToEdge
  import androidx.compose.runtime.*
  import androidx.datastore.preferences.core.booleanPreferencesKey
  import androidx.datastore.preferences.core.stringPreferencesKey
  import androidx.datastore.preferences.preferencesDataStore
  import com.watchable.app.navigation.WatchableNavHost
  import com.watchable.app.ui.theme.WatchableTheme
  import kotlinx.coroutines.flow.map

  val android.content.Context.dataStore by preferencesDataStore(name = "watchable_prefs")
  val PREF_USERNAME = stringPreferencesKey("username")
  val PREF_DARK_THEME = booleanPreferencesKey("dark_theme")
  val PREF_ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")

  class MainActivity : ComponentActivity() {
      override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          enableEdgeToEdge()
          setContent {
              val context = androidx.compose.ui.platform.LocalContext.current
              val darkTheme by remember {
                  context.dataStore.data.map { it[PREF_DARK_THEME] ?: true }
              }.collectAsState(initial = true)
              WatchableTheme(darkTheme = darkTheme) { WatchableNavHost() }
          }
      }
  }
  