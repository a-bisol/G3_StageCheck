package mads.group3.stagecheck.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import mads.group3.stagecheck.viewmodels.MapViewModel

@Composable
fun MapScreen(navController: NavController) {
    val viewModel: MapViewModel = viewModel()
    val venues by viewModel.venues.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val toronto = LatLng(43.6532, -79.3832)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(toronto, 12f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1f)
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
                                    Log.d("Marker tapped", venueId ?: "No venue ID")
                                    false
                                }
                            )
                        }
                    }
                }

            }
        }
    }
}