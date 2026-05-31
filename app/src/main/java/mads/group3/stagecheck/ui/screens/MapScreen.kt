package mads.group3.stagecheck.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import mads.group3.stagecheck.navigation.Screens

@Composable
fun MapScreen(navController: NavController) {
    Column{
        Text("Placeholder Dash")
        Button(onClick = {
            navController.navigate(Screens.DetailEvent.passEventId(12))
        }) {
            Text("Go to Event 12")
        }
    }
}