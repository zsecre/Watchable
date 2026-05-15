package com.watchable.app.ui.theme
  import androidx.compose.material3.*
  import androidx.compose.runtime.Composable
  import androidx.compose.ui.graphics.Color
  private val DarkColorScheme = darkColorScheme(
      primary = BrandCyan, onPrimary = Color.Black, primaryContainer = BgCard,
      background = BgDeep, onBackground = TextMain, surface = BgSurface, onSurface = TextMain,
      surfaceVariant = BgCard, onSurfaceVariant = TextMuted, outline = BorderSubtle, error = ErrorRed
  )
  private val LightColorScheme = lightColorScheme(
      primary = BrandCyanDim, onPrimary = Color.White, primaryContainer = LightBgCard,
      background = LightBgDeep, onBackground = LightTextMain, surface = LightBgSurface,
      onSurface = LightTextMain, surfaceVariant = LightBgCard, onSurfaceVariant = LightTextMuted, error = ErrorRed
  )
  @Composable
  fun WatchableTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
      MaterialTheme(colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme, typography = WatchableTypography, content = content)
  }
  