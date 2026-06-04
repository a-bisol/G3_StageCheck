package mads.group3.stagecheck.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import mads.group3.stagecheck.navigation.Screens

@Composable
fun FavouritesScreen(navController: NavController) {
    Column{
        Text("Placeholder Favs")
        Button(onClick = {
            navController.navigate(Screens.DetailEvent.passEventId("13"))
        }) {
            Text("Go to Event 13")
        }
    }
}