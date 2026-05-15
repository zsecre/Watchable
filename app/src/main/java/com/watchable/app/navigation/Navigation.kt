package com.watchable.app.navigation
  import androidx.compose.runtime.*
  import androidx.lifecycle.viewmodel.compose.viewModel
  import androidx.navigation.*
  import androidx.navigation.compose.*
  import com.watchable.app.ui.screens.*
  import com.watchable.app.viewmodel.*
  sealed class Screen(val route: String) {
      object Onboarding : Screen("onboarding")
      object Main : Screen("main")
      object Detail : Screen("detail/{mediaId}/{mediaType}") { fun createRoute(id: String, type: String) = "detail/${id}/${type}" }
  }
  @Composable
  fun WatchableNavHost() {
      val navController = rememberNavController()
      val mediaViewModel: MediaViewModel = viewModel()
      val homeViewModel: HomeViewModel = viewModel()
      val onboardingDone by mediaViewModel.onboardingDone.collectAsState()
      NavHost(navController, startDestination = if (onboardingDone) Screen.Main.route else Screen.Onboarding.route) {
          composable(Screen.Onboarding.route) { OnboardingScreen { name -> mediaViewModel.setUsername(name); navController.navigate(Screen.Main.route) { popUpTo(Screen.Onboarding.route) { inclusive = true } } } }
          composable(Screen.Main.route) { MainScreen(mediaViewModel, homeViewModel) { id, type -> navController.navigate(Screen.Detail.createRoute(id, type)) } }
          composable(Screen.Detail.route, listOf(navArgument("mediaId") { type = NavType.StringType }, navArgument("mediaType") { type = NavType.StringType })) { back ->
              DetailScreen(back.arguments?.getString("mediaId") ?: return@composable, back.arguments?.getString("mediaType") ?: return@composable, mediaViewModel) { navController.popBackStack() }
          }
      }
  }
  