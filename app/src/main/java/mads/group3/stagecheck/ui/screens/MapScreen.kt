package mads.group3.stagecheck.ui.screens

import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction.Companion.Search
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mads.group3.stagecheck.common.components.ReusableEventCard
import mads.group3.stagecheck.navigation.Screens
import mads.group3.stagecheck.viewmodels.MapViewModel
import okio.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController) {
    val viewModel: MapViewModel = viewModel()
    val venues by viewModel.venues.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val selectedVenueId by viewModel.selectedVenueId.collectAsState()
    val selectedVenueEvents by viewModel.selectedVenueEvents.collectAsState()
    val isLoadingEvents by viewModel.isLoadingEvents.collectAsState()
    val eventsError by viewModel.eventsError.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        viewModel.loadVenues()
    }

    val toronto = LatLng(43.6532, -79.3832)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(toronto, 12f)
    }

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun performSearch(query: String) {
        if (query.isBlank()) return
        isSearching = true
        scope.launch {
            try {
                val addresses = withContext(Dispatchers.IO) {
                    Geocoder(context).getFromLocationName(query, 1)
                }

                if (!addresses.isNullOrEmpty()) {
                    val location = addresses[0]
                    val latLng = LatLng(location.latitude, location.longitude)
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(latLng, 14f),
                        durationMs = 1000
                    )
                } else {
                    Log.d("performSearch", "No location found for :$query")
                }
            } catch (e: IOException) {
                e.message?.let { Log.e("performSearch", it) }
            } finally {
                isSearching = false
            }
        }
    }

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
                Button(onClick = { viewModel.refresh() }) { Text("Retry") }
            }

            else -> {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    venues.forEach { venue ->
                        venue.location?.let { geoPoint ->
                            val position = LatLng(geoPoint.latitude, geoPoint.longitude)
                            Marker(
                                state = MarkerState(position = position),
                                title = venue.name ?: "Venue",
                                tag = venue.id,
                                onClick = { marker ->
                                    val venueId = marker.tag as? String
                                    if (venueId != null) {
                                        viewModel.selectVenue(venueId)
                                    }
                                    Log.d("Marker tapped", venueId ?: "No venue ID")
                                    false
                                }
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        placeholder = {
                            Text(
                                text = "Search for a place",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        singleLine = true,
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),

                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,

                            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = 0.6f
                            ),
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = 0.6f
                            ),

                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,

                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = MaterialTheme.shapes.large,
                        keyboardOptions = KeyboardOptions(
                            imeAction = Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                performSearch(searchQuery)
                            }
                        )
                    )
                }
                if (selectedVenueId != null) {
                    ModalBottomSheet(
                        onDismissRequest = { viewModel.clearSelectedVenue() },
                        sheetState = sheetState,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    ) {
                        val venueName = venues.find { it.id == selectedVenueId }?.name ?: "Events"
                        Text(
                            text = venueName,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                        )
                        when {
                            isLoadingEvents -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            eventsError != null -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(32.dp)
                                ) {
                                    Text("Error: $eventsError")
                                    Button(
                                        onClick = {
                                            selectedVenueId?.let { viewModel.selectVenue(it) }
                                        }
                                    ) { Text("Retry") }
                                }
                            }

                            selectedVenueEvents.isEmpty() -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No events at this venue")
                                }
                            }

                            else -> {
                                LazyColumn(
                                    contentPadding = PaddingValues(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = 16.dp)
                                ) {
                                    items(selectedVenueEvents, key = {it.docId}) {event ->
                                        ReusableEventCard(
                                            imageUrl = event.eventImage3x2,
                                            headliner = event.name ?: event.headliner ?: "Unknown",
                                            venue = event.venueName ?: "TBD",
                                            onClick = {
                                                navController.navigate(Screens.DetailEvent.passEventId(event.docId))
                                                viewModel.clearSelectedVenue()
                                            },
                                            localDate = event.localDate,
                                            localTime = event.localTime,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}