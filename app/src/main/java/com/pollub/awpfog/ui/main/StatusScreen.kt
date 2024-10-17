package com.pollub.awpfog.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pollub.awpfog.ui.components.InterventionSection
import com.pollub.awpfog.ui.components.StatusSection
import com.pollub.awpfog.ui.theme.AwpfogTheme

/**
 * Composable function that displays the status of various system components (connection, GPS, patrol),
 * allows toggling patrol status, and manages an intervention section.
 *
 * @param modifier A [Modifier] used to modify the layout or appearance of this composable.
 * @param onConfirmIntervention A callback function triggered when the user confirms an intervention.
 * @param onRejectIntervention A callback function triggered when the user rejects an intervention.
 *
 * Functionality:
 * - Displays the current location.
 * - Shows status sections for connection, GPS, and patrol.
 * - Provides a switch to toggle the patrol status.
 * - Displays an intervention section if `isInterventionVisible` is `true`, allowing the user to confirm or reject an intervention.
 */
@Composable
fun StatusScreen(modifier: Modifier, onConfirmIntervention:()->Unit, onRejectIntervention:()->Unit) {
    val currentLocation = "Krańcowa 1, Lublin, 20-001"
    val connectionStatus by remember { mutableStateOf(true) }
    val gpsStatus by remember { mutableStateOf(true) }
    var patrolStatus by remember { mutableStateOf(true) }
    var isInterventionVisible by remember { mutableStateOf(true) }

    fun onStatusChange() {
        patrolStatus = !patrolStatus
    }

        Column(modifier = modifier.fillMaxSize()) {

            LocationInfo(currentLocation)

            StatusSection("Status połączenia z bazą", connectionStatus)
            StatusSection("Status udostępniania pozycji GPS", gpsStatus)
            StatusSection("Status patrolu", patrolStatus)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Zmień status patrolu", modifier = Modifier.weight(1f))
                Switch(
                    checked = patrolStatus,
                    onCheckedChange = { onStatusChange() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onSecondary,
                        checkedTrackColor = MaterialTheme.colorScheme.secondary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.tertiaryContainer,
                        uncheckedTrackColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ))
            }
            Spacer(modifier= Modifier.weight(1f))
            InterventionSection(
                isVisible = isInterventionVisible,
                location = currentLocation,
                onConfirm =onConfirmIntervention,
                onReject = {
                    isInterventionVisible=false
                    onRejectIntervention()
                })
            Spacer(modifier= Modifier.weight(0.2f))
        }
}

/**
 * Composable function that displays information about the current location.
 *
 * @param currentLocation A string representing the current location to be displayed.
 *
 * This section includes:
 * - A title labeled "Aktualna lokalizacja".
 * - The current location string displayed below the title in gray color.
 */
@Composable
fun LocationInfo(currentLocation: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Aktualna lokalizacja", fontSize = 18.sp)
        Text(text = currentLocation, color = Color.Gray, fontSize = 14.sp)
    }
}


@Preview(showBackground = true)
@Composable
fun StatusScreenPreview() {
    AwpfogTheme(dynamicColor = false) {
        StatusScreen(Modifier, {},{})
    }
}


