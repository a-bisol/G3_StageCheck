package mads.group3.stagecheck.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import mads.group3.stagecheck.ui.screens.ArtistDetailScreen
import mads.group3.stagecheck.ui.screens.AuthScreen
import mads.group3.stagecheck.ui.screens.DashScreen
import mads.group3.stagecheck.ui.screens.EventDetailScreen
import mads.group3.stagecheck.ui.screens.FavouritesScreen
import mads.group3.stagecheck.ui.screens.MapScreen
import mads.group3.stagecheck.ui.screens.ProfileScreen
import mads.group3.stagecheck.ui.screens.SearchScreen
import mads.group3.stagecheck.viewmodels.AuthViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val isAuthenticated = uiState.currentUser != null

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            navController.navigate(Screens.Main.Dash.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        } else {
            navController.navigate(Screens.Login.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) Screens.Main.Dash.route else Screens.Login.route
    ) {
        composable(Screens.Login.route) {
            AuthScreen(viewModel = authViewModel)
        }

        composable(Screens.Main.Dash.route) { DashScreen(navController) }
        composable(Screens.Main.Search.route) { SearchScreen(navController) }
        composable(Screens.Main.Map.route) { MapScreen(navController) }
        composable(Screens.Main.Favourites.route) { FavouritesScreen(navController) }
        composable(Screens.Main.Profile.route) { ProfileScreen(navController) }

        composable(
            route = Screens.DetailEvent.route,
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt("eventId") ?: -1
            EventDetailScreen(eventId = eventId, onBack = { navController.popBackStack() })
        }

        composable(
            route = Screens.DetailArtist.route,
            arguments = listOf(navArgument("artistId") { type = NavType.IntType })
        ) { backStackEntry ->
            val artistId = backStackEntry.arguments?.getInt("artistId") ?: -1
            ArtistDetailScreen(artistId = artistId, onBack = { navController.popBackStack() })
        }
    }
}