package mads.group3.stagecheck.common.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import mads.group3.stagecheck.navigation.Screens

/* TODO
*   Add icons for each tab (maybe remove label?)
*   Fix selected to not just be stuck on dash
*/
@Composable
fun NavBar(navController: NavController) {
    val items = listOf(
        Screens.Main.Dash,
        Screens.Main.Search,
        Screens.Main.Map,
        Screens.Main.Favourites,
        Screens.Main.Profile
    )

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                selected = navController.currentDestination?.route == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {},
                label = { Text(screen.subRoute) }
            )
        }
    }
}