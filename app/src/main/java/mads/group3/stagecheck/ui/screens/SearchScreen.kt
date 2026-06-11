package mads.group3.stagecheck.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import mads.group3.stagecheck.common.components.EventSearchBar
import mads.group3.stagecheck.common.components.ExtendedSearchSettings
import mads.group3.stagecheck.models.ExtendedSearchOptions

@Composable
fun SearchScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var settings by remember { mutableStateOf(ExtendedSearchOptions()) }

    Column(modifier = Modifier.statusBarsPadding()) {
        EventSearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onSearchClick = {
                Log.i("Search", "Searching for $searchQuery with settings: $settings")
            },
            expandedSettingsContent = {
                ExtendedSearchSettings(
                    settings = settings,
                    onSettingsChange = { settings = it }
                )
            }
        )
    }
}