package mads.group3.stagecheck.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import mads.group3.stagecheck.common.components.ReusableArtistCard
import mads.group3.stagecheck.navigation.Screens
import mads.group3.stagecheck.viewmodels.AuthViewModel
import mads.group3.stagecheck.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val viewModel: ProfileViewModel = viewModel()
    val artists by viewModel.artists.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFollowedArtists()
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    IconButton(onClick = { authViewModel.signOut() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Sign Out"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                error != null -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("Error: $error")
                    Button(onClick = { viewModel.refresh() }) {
                        Text("Retry")
                    }
                }

                artists.isEmpty() -> Text(
                    "You are not following any artists yet.",
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 60.dp, bottom = 100.dp)
                ) {
                    items(artists, key = { it.id }) { artist ->
                        ReusableArtistCard(
                            artist = artist,
                            onClick = {
                                navController.navigate(
                                    Screens.DetailArtist.passArtistId(artist.id)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}