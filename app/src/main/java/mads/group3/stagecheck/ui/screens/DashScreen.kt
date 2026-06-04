package mads.group3.stagecheck.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mads.group3.stagecheck.common.components.ReusableEventCard
import mads.group3.stagecheck.navigation.Screens

@Composable
fun DashScreen(navController: NavController) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ReusableEventCard(
            imageUrl = null,
            headliner = "Emo Nite",
            venue = "Sneaky Dees",
            onClick = { navController.navigate(Screens.DetailEvent.passEventId("10")) }
        )
        ReusableEventCard(
            imageUrl = null,
            headliner = "Prince Daddy & The Hyena",
            venue = "Axis Club",
            onClick = { navController.navigate(Screens.DetailEvent.passEventId("12")) }
        )
        ReusableEventCard(
            imageUrl = null,
            headliner = "Emo Nite",
            venue = "Sneaky Dees",
            onClick = { navController.navigate(Screens.DetailEvent.passEventId("10_again")) }
        )
    }
}