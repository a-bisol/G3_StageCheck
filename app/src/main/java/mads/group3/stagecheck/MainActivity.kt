package mads.group3.stagecheck


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}