package mads.group3.stagecheck.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import mads.group3.stagecheck.common.components.ReusableEventCard
import mads.group3.stagecheck.navigation.Screens
import mads.group3.stagecheck.viewmodels.EventListViewModel
import mads.group3.stagecheck.viewmodels.LocationFilter

@Composable
fun DashScreen(navController: NavController) {
    val viewModel: EventListViewModel = viewModel()
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingExtra by viewModel.isLoadingExtra.collectAsState()
    val error by viewModel.error.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(listState, hasMore, isLoadingExtra) {
        snapshotFlow { listState.layoutInfo }
            .collectLatest { layoutInfo ->
                val totalItemsCount = layoutInfo.totalItemsCount
                val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1

                if (hasMore &&
                    !isLoadingExtra &&
                    totalItemsCount > 0 &&
                    lastVisibleIndex >= (totalItemsCount * 0.85).toInt()
                ) {
                    viewModel.loadMore()
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        FilterDropdown(
            selectedFilter = selectedFilter,
            onFilterSelected = { viewModel.setFilter(it) },
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                isLoading && events.isEmpty() -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

                error != null && events.isEmpty() -> {
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

                else ->
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 100.dp)
                    ) {
                        items(events, key = { it.docId }) { event ->
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

                        if (isLoadingExtra) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    selectedFilter: LocationFilter,
    onFilterSelected: (LocationFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        TextField(
            value = when (selectedFilter) {
                LocationFilter.ALL -> "All Shows"
                LocationFilter.MY_CITY -> "My City"
                LocationFilter.MY_STATE -> "My Province/State"
                LocationFilter.MY_COUNTRY -> "My Country"
            },
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            LocationFilter.entries.forEach { filter ->
                DropdownMenuItem(
                    text = {
                        Text(
                            when (filter) {
                                LocationFilter.ALL -> "All Shows"
                                LocationFilter.MY_CITY -> "My City"
                                LocationFilter.MY_STATE -> "My Province/State"
                                LocationFilter.MY_COUNTRY -> "My Country"
                            }
                        )
                    },
                    onClick = {
                        onFilterSelected(filter)
                        expanded = false
                    }
                )
            }
        }
    }
}