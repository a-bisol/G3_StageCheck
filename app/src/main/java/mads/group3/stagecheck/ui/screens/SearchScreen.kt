package mads.group3.stagecheck.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import mads.group3.stagecheck.common.components.EventSearchBar
import mads.group3.stagecheck.common.components.ExtendedSearchSettings
import mads.group3.stagecheck.common.components.ReusableEventCard
import mads.group3.stagecheck.models.ExtendedSearchOptions
import mads.group3.stagecheck.navigation.Screens
import mads.group3.stagecheck.viewmodels.SearchViewModel

@Composable
fun SearchScreen(navController: NavController) {
    val viewModel: SearchViewModel = viewModel()

    var searchQuery by remember { mutableStateOf("") }
    var settings by remember { mutableStateOf(ExtendedSearchOptions()) }

    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) {
        EventSearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onSearchClick = {
                Log.i("Search", "Searching for $searchQuery with settings: $settings")
                viewModel.search(searchQuery, settings)
            },
            expandedSettingsContent = {
                ExtendedSearchSettings(
                    settings = settings,
                    onSettingsChange = { settings = it }
                )
            }
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null && searchResults.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text("Error: $error")
                }
            }

            searchResults.isNotEmpty() -> {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(bottom = 100.dp)
                        .fillMaxSize()
                ) {
                    items(searchResults, key = { it.docId }) { event ->
                        ReusableEventCard(
                            imageUrl = event.eventImage3x2,
                            headliner = event.name ?: event.headliner ?: "Unknown",
                            venue = event.venueName ?: "TBD",
                            onClick = {
                                navController.navigate(
                                    Screens.DetailEvent.passEventId(
                                        event.docId
                                    )
                                )
                            },
                            localDate = event.localDate,
                            localTime = event.localTime
                        )
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text("Enter a search term to find events")
                }
            }
        }
    }
}