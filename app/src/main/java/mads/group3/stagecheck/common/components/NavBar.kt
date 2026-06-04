package mads.group3.stagecheck.common.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import mads.group3.stagecheck.navigation.Screens

/* TODO
*   Add icons for each tab (maybe remove label?)
*   Fix selected to not just be stuck on dash
*/
@Composable
fun NavBar(navController: NavController) {
    val items = remember {
        listOf(
            Screens.Main.Dash,
            Screens.Main.Search,
            Screens.Main.Map,
            Screens.Main.Favourites,
            Screens.Main.Profile
        )
    }

    val currentDestination = navController.currentBackStackEntryAsState().value
    val currentRoute = currentDestination?.destination?.route

    NavigationBar {
        items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = getIconForScreen(screen, isSelected),
                        contentDescription = screen.subRoute
                    )
                },
                label = null
            )
        }
    }
}

@Composable
fun getIconForScreen(screen: Screens.Main, isSelected: Boolean): ImageVector {
    return when (screen) {
        Screens.Main.Dash -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
        Screens.Main.Search -> if (isSelected) Icons.Filled.Search else Icons.Outlined.Search
        Screens.Main.Map -> if (isSelected) Icons.Filled.Map else Icons.Outlined.Map
        Screens.Main.Favourites -> if (isSelected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
        Screens.Main.Profile -> if (isSelected) Icons.Filled.Person else Icons.Outlined.Person
    }
}