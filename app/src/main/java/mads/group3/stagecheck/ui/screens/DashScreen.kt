package mads.group3.stagecheck.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import mads.group3.stagecheck.common.components.ReusableEventCard
import mads.group3.stagecheck.navigation.Screens
import mads.group3.stagecheck.viewmodels.EventListViewModel

@Composable
fun DashScreen(navController: NavController) {
    val viewModel: EventListViewModel = viewModel()
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            error != null -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("Error: $error")
                    Button(onClick = { viewModel.refresh() }) {
                        Text("Retry")
                    }
                }
            }

            else -> LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(events, key = { it.docId }) { event ->
                    ReusableEventCard(
                        imageUrl = event.eventImage3x2,
                        headliner = event.name ?: event.headliner ?: "Unknown",
                        venue = event.venueName ?: "TBD",
                        onClick = { navController.navigate(Screens.DetailEvent.passEventId(event.docId)) },
                        localDate = event.localDate,
                        localTime = event.localTime
                    )
                }
            }
        }
    }
}