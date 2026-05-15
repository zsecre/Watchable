package com.watchable.app.ui.screens
  import androidx.compose.foundation.background
  import androidx.compose.foundation.layout.*
  import androidx.compose.foundation.shape.RoundedCornerShape
  import androidx.compose.foundation.text.KeyboardActions
  import androidx.compose.foundation.text.KeyboardOptions
  import androidx.compose.material.icons.Icons
  import androidx.compose.material.icons.filled.PlayArrow
  import androidx.compose.material3.*
  import androidx.compose.runtime.*
  import androidx.compose.ui.Alignment
  import androidx.compose.ui.Modifier
  import androidx.compose.ui.draw.clip
  import androidx.compose.ui.graphics.*
  import androidx.compose.ui.text.font.FontWeight
  import androidx.compose.ui.text.input.*
  import androidx.compose.ui.text.style.TextAlign
  import androidx.compose.ui.unit.*
  import com.watchable.app.ui.theme.BgDeep
  import com.watchable.app.ui.theme.BrandCyan
  @Composable
  fun OnboardingScreen(onComplete: (String) -> Unit) {
      var name by remember { mutableStateOf("") }
      val isValid = name.trim().length >= 2
      Box(Modifier.fillMaxSize().background(BgDeep)) {
          Box(Modifier.fillMaxWidth().height(300.dp).background(Brush.verticalGradient(listOf(BrandCyan.copy(alpha = 0.15f), Color.Transparent))))
          Column(Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
              Box(Modifier.size(88.dp).clip(RoundedCornerShape(24.dp)).background(BrandCyan), contentAlignment = Alignment.Center) {
                  Icon(Icons.Default.PlayArrow, null, tint = Color.Black, modifier = Modifier.size(48.dp))
              }
              Spacer(Modifier.height(24.dp))
              Text("Welcome to", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text("Watchable", style = MaterialTheme.typography.displayLarge.copy(fontSize = 40.sp), fontWeight = FontWeight.Black, color = BrandCyan)
              Text("Your ultimate destination for movies,\nTV shows & anime.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
              Spacer(Modifier.height(32.dp))
              Text("What should we call you?", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
              Spacer(Modifier.height(8.dp))
              OutlinedTextField(value = name, onValueChange = { if (it.length <= 10) name = it }, placeholder = { Text("Your name (max 10 chars)") }, singleLine = true, keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done), keyboardActions = KeyboardActions(onDone = { if (isValid) onComplete(name.trim()) }), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandCyan, cursorColor = BrandCyan), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
              Text("${name.length}/10", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.End))
              Spacer(Modifier.height(16.dp))
              Button(onClick = { if (isValid) onComplete(name.trim()) }, enabled = isValid, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = BrandCyan, contentColor = Color.Black, disabledContainerColor = BrandCyan.copy(alpha = 0.3f), disabledContentColor = Color.Black.copy(alpha = 0.4f)), shape = RoundedCornerShape(12.dp)) {
                  Text("Get Started", fontWeight = FontWeight.Bold, fontSize = 16.sp)
              }
          }
      }
  }
  