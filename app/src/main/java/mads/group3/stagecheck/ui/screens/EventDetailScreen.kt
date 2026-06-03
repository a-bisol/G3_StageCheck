package mads.group3.stagecheck.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EventDetailScreen(
    eventId: Int,
    onBack: () -> Unit
) {
    Column() {
        Text("Event Detail Screen")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Received eventId = $eventId")
        Button(onClick = onBack) {
            Text("<- Back")
        }
    }
}