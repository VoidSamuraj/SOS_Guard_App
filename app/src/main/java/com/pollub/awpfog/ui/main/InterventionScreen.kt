package com.pollub.awpfog.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.pollub.awpfog.R
import com.pollub.awpfog.map.MapStyle
import com.pollub.awpfog.ui.components.InterventionButtons
import com.pollub.awpfog.ui.theme.AwpfogTheme
import com.pollub.awpfog.viewmodel.AppViewModel

/**
 * Composable function that displays a map and intervention-related controls, including a button for navigating
 * to a specific position. The user can confirm arrival, stop the intervention, call for support, and end the intervention.
 *
 * @param modifier A [Modifier] used to modify the layout or appearance of this composable.
 * @param viewModel A [AppViewModel] used to get required data to this composable.
 * @param navigateToPos A callback function triggered when the user taps the button to navigate to a specific position.
 * @param confirmArrival A callback function triggered when the user confirms arrival at the intervention location.
 * @param stopIntervention A callback function triggered when the user stops the intervention.
 * @param callForSupport A callback function triggered when the user requests support during the intervention.
 * @param endIntervention A callback function triggered when the user ends the intervention.
 *
 * Functionality:
 * - Displays a map using [GoogleMap] with a marker at a fixed location (Krańcowa 1, Lublin).
 * - Includes a button in the top-right corner for navigating to a position, which calls [navigateToPos].
 * - Displays basic information about the intervention, including its number and location.
 * - Provides action buttons for various intervention-related tasks using the [InterventionButtons] component.
 */
@Composable
fun InterventionScreen(
    modifier: Modifier,
    viewModel: AppViewModel,
    navigateToPos: () -> Unit,
    confirmArrival: () -> Unit,
    stopIntervention: () -> Unit,
    callForSupport: () -> Unit,
    endIntervention: () -> Unit
) {

    var interventionStarted = remember { mutableStateOf(false) }
    var supportAlongTheWay = remember { mutableStateOf(false) }


    var uiSettings = remember { mutableStateOf(MapUiSettings()) }

    var properties = remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                mapStyleOptions = MapStyleOptions(MapStyle.json)
            )
        )
    }

    val mapMarkerState = rememberMarkerState(position =viewModel.reportLocation.value)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.Builder()
            .target(viewModel.reportLocation.value)
            .zoom(14f)
            .build()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = properties.value,
                uiSettings = uiSettings.value

            ) {
                Marker(
                    state = mapMarkerState,
                    title = "Zgłoszenie"
                )
            }
            Button(
                onClick = { navigateToPos() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(5.dp),
                modifier = Modifier
                    .padding(5.dp)
                    .size(60.dp)
                    .align(Alignment.TopEnd),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_navigation_24),
                    contentDescription = "Call",
                    modifier = Modifier
                        .size(40.dp)
                        .rotate(45f),
                )
            }

        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 10.dp)
        ) {
            Text(
                text = "Interwencja nr 2137",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp
            )
            Text(
                text = "Krańcowa 1, Lublin, 20-001",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
        InterventionButtons(
            interventionStarted = interventionStarted,
            supportAlongTheWay = supportAlongTheWay,
            confirmArrival = confirmArrival,
            stopIntervention = stopIntervention,
            callForSupport = callForSupport,
            endIntervention = endIntervention
        )
    }
}


@Preview(showBackground = true)
@Composable
fun InterventionScreenPreview() {
    AwpfogTheme(dynamicColor = false) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            InterventionScreen(
                modifier = Modifier.padding(innerPadding),
                AppViewModel(),
                {},
                {},
                {},
                {},
                {})
        }
    }
}
