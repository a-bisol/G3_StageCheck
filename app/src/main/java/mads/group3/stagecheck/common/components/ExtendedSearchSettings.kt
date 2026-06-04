package mads.group3.stagecheck.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mads.group3.stagecheck.models.ExtendedSearchOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtendedSearchSettings(
    settings: ExtendedSearchOptions,
    onSettingsChange: (ExtendedSearchOptions) -> Unit,
    modifier: Modifier = Modifier
) {
    var unitExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = settings.artist,
            onValueChange = { onSettingsChange(settings.copy(artist = it)) },
            label = { Text("Artist") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = settings.city,
            onValueChange = { onSettingsChange(settings.copy(city = it)) },
            label = { Text("City") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = settings.venue,
            onValueChange = { onSettingsChange(settings.copy(venue = it)) },
            label = { Text("Venue") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Distance: ${settings.distance.toInt()}")
                Slider(
                    value = settings.distance,
                    onValueChange = { onSettingsChange(settings.copy(distance = it)) },
                    valueRange = 1f..100f,
                    steps = 98,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            ExposedDropdownMenuBox(
                expanded = unitExpanded,
                onExpandedChange = { unitExpanded = it }
            ) {
                OutlinedTextField(
                    value = settings.unit,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Unit") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                    modifier = Modifier
                        .width(120.dp)
                        .menuAnchor(),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = unitExpanded,
                    onDismissRequest = { unitExpanded = false }
                ) {
                    listOf("none", "km", "miles").forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option,
                                    color = if (option == "none") Color.Gray else Color.Unspecified
                                )
                            },
                            onClick = {
                                onSettingsChange(settings.copy(unit = option))
                                unitExpanded = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DatePickerField(
                label = "Start Date",
                selectedDateMillis = settings.startDate,
                onDateSelected = { onSettingsChange(settings.copy(startDate = it)) },
                modifier = Modifier.weight(1f)
            )
            DatePickerField(
                label = "End Date",
                selectedDateMillis = settings.endDate,
                onDateSelected = { onSettingsChange(settings.copy(endDate = it)) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}