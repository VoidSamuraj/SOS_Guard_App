package com.pollub.awpfog.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.pollub.awpfog.BuildConfig
import com.pollub.awpfog.data.SharedPreferencesManager
import com.pollub.awpfog.data.models.Guard
import com.pollub.awpfog.network.NetworkClient
import com.pollub.awpfog.ui.components.InterventionSection
import com.pollub.awpfog.ui.components.RotatingLoader
import com.pollub.awpfog.ui.components.StatusSection
import com.pollub.awpfog.ui.theme.AwpfogTheme
import com.pollub.awpfog.utils.getStreetName
import com.pollub.awpfog.viewmodel.AppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Composable function that displays the status of various system components (connection, GPS, patrol),
 * allows toggling patrol status, and manages an intervention section.
 *
 * @param modifier A [Modifier] used to modify the layout or appearance of this composable.
 * @param viewModel A [AppViewModel] used to get required data to this composable.
 * @param onConnectionButtonClick A callback function triggered when the user click on button for connect/disconnect with system, takes previous state argument of boolean type and callback invoked on successful connection.
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
fun StatusScreen(
    modifier: Modifier,
    viewModel: AppViewModel,
    onConnectionButtonClick: (wasConnectedBefore: Boolean, onSuccessConnect: () -> Unit) -> Unit,
    onConfirmIntervention: () -> Unit,
    onRejectIntervention: () -> Unit
) {
    var currentLocation by remember { mutableStateOf("") }
    var reportLocation by remember { mutableStateOf("") }

    LaunchedEffect(NetworkClient.WebSocketManager.currentLocation.value) {
        val location = NetworkClient.WebSocketManager.currentLocation.value
        CoroutineScope(Dispatchers.IO).launch {
            getStreetName(location.first, location.second, BuildConfig.MAPS_API_KEY)?.let {
                currentLocation = it
            }
        }
    }
    LaunchedEffect(viewModel.reportLocation.value) {
        val location = viewModel.reportLocation.value
        CoroutineScope(Dispatchers.IO).launch {
            getStreetName(
                location.latitude,
                location.longitude,
                BuildConfig.MAPS_API_KEY
            )?.let { reportLocation = it }
        }
    }

    fun onStatusChange() {
        val statusNow =
            if (!viewModel.isPatrolActive()) Guard.GuardStatus.AVAILABLE else Guard.GuardStatus.UNAVAILABLE
        viewModel.sendStatusChange(SharedPreferencesManager.getGuard().id, statusNow)
    }

    Column(modifier = modifier.fillMaxSize()) {

        LocationInfo(currentLocation)

        StatusSection("Status połączenia z bazą", viewModel.connectionStatus.value)
        StatusSection("Status patrolu", viewModel.patrolStatusEnum.value)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Zmień status patrolu", modifier = Modifier.weight(1f))
            Switch(
                checked = viewModel.isPatrolActive(),
                onCheckedChange = { onStatusChange() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onSecondary,
                    checkedTrackColor = MaterialTheme.colorScheme.secondary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.tertiaryContainer,
                    uncheckedTrackColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            )
        }
        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(80.dp),
            shape = CutCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = (if (viewModel.connectionStatus.value) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondary)),
            onClick = {
                if (viewModel.connectionStatus.value) {
                    onConnectionButtonClick(viewModel.connectionStatus.value, {})
                    viewModel.connectionStatus.value = !viewModel.connectionStatus.value
                } else
                    onConnectionButtonClick(viewModel.connectionStatus.value, {
                        viewModel.connectionStatus.value = !viewModel.connectionStatus.value
                    })
            }) {
            Text(if (viewModel.connectionStatus.value) "Rozłącz" else "Połącz", color = Color.White)
        }
        if(viewModel.getIsSystemConnecting().value){
            Spacer(modifier = Modifier.weight(1f))
            RotatingLoader(Modifier.align(Alignment.CenterHorizontally),MaterialTheme.colorScheme.primary, circleRadius = 42.dp, strokeWidth = 10.dp)
            Spacer(modifier = Modifier.weight(1f))
        }else{
            Spacer(modifier = Modifier.weight(1f))
        }
        InterventionSection(
            isVisible = viewModel.isInterventionVisible,
            isConnecting = viewModel.getIsSystemConnecting(),
            location = reportLocation,
            onConfirm = onConfirmIntervention,
            onReject = {
                viewModel.isInterventionVisible.value = false
                onRejectIntervention()
            })
        Spacer(modifier = Modifier.weight(0.2f))
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
    if (currentLocation.isNotEmpty())
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Aktualna lokalizacja", fontSize = 18.sp)
            Text(text = currentLocation, color = Color.Gray, fontSize = 14.sp)
        }
}

@Preview(showBackground = true)
@Composable
fun StatusScreenPreview() {
    AwpfogTheme(dynamicColor = false) {
        StatusScreen(Modifier, AppViewModel(), { _, _ -> }, {}, {})
    }
}