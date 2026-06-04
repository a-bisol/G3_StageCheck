package mads.group3.stagecheck.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import mads.group3.stagecheck.navigation.Screens
import mads.group3.stagecheck.viewmodels.AuthViewModel

@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel) {
    Column {
        Text("Placeholder Profile")
        Button(onClick = {
            navController.navigate(Screens.DetailArtist.passArtistId(1))
        }) {
            Text("Go to Artist 1")
        }
        Button(onClick = { authViewModel.signOut() }, shape = MaterialTheme.shapes.medium) {
            Text("Sign Out")
        }
    }
}