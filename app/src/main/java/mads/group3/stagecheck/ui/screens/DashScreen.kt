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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@Composable
fun DashScreen(navController: NavController) {
    val viewModel: EventListViewModel = viewModel()
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingExtra by viewModel.isLoadingExtra.collectAsState()
    val error by viewModel.error.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
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

            else -> LazyColumn(
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
                        onClick = { navController.navigate(Screens.DetailEvent.passEventId(event.docId)) },
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