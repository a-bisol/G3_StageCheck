package mads.group3.stagecheck.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(navController: NavController) {
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
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        )
    }
}