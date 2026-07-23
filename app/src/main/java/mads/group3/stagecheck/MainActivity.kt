package mads.group3.stagecheck


import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import mads.group3.stagecheck.common.LocationManager
import mads.group3.stagecheck.common.components.BannerAd
import mads.group3.stagecheck.common.components.NavBar
import mads.group3.stagecheck.navigation.NavGraph
import mads.group3.stagecheck.ui.theme.StageCheckTheme
import mads.group3.stagecheck.viewmodels.AuthViewModel

/* TODO
*   Constrain edges to not bleed off the top of the phone
*   Loading/splash screen to avoid glimpse of login?
*/

private val BottomBarHeight = 100.dp

class MainActivity : ComponentActivity() {
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            LocationManager.fetchLocation(this)
        }
    }

    private val notificationPermsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted: $isGranted")
        } else {
            Log.w("MainActivity", "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        LocationManager.initialize(this)
        enableEdgeToEdge()

        if (!hasLocationPermissions()) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            LocationManager.fetchLocation(this)
        }

        if (!hasNotificationPermission()) {
            notificationPermsLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            StageCheckTheme {
                val authViewModel: AuthViewModel = viewModel()
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry?.destination?.route

                val isDetailScreen =
                    currentRoute?.startsWith("event/") == true || currentRoute?.startsWith("artist/") == true
                val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
                val isLoggedIn = uiState.currentUser != null

                Scaffold(
                    bottomBar = {
                        when {
                            isLoggedIn && !isDetailScreen -> NavBar(
                                navController,
                                modifier = Modifier.height(BottomBarHeight)
                            )

                            isLoggedIn && isDetailScreen -> BannerAd(
                                modifier = Modifier.height(
                                    BottomBarHeight
                                )
                            )
                        }
                    }
                ) { _ ->
                    NavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        modifier = Modifier
                    )
                }
            }
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
}