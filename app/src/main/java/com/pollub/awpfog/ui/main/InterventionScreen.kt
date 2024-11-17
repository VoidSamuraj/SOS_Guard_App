package com.pollub.awpfog.ui.main

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.mapbox.maps.extension.compose.MapboxMap
import com.pollub.awpfog.R
import com.pollub.awpfog.data.SharedPreferencesManager
import com.pollub.awpfog.ui.components.InterventionButtons
import com.pollub.awpfog.ui.components.MyMap
import com.pollub.awpfog.ui.components.RotatingLoader
import com.pollub.awpfog.ui.theme.AwpfogTheme
import com.pollub.awpfog.viewmodel.AppViewModel

/**
 * Composable function that displays a map and intervention-related controls, including a button for navigating
 * to a specific position. The user can confirm arrival, stop the intervention, call for support, and end the intervention.
 *
 * @param modifier A [Modifier] used to modify the layout or appearance of this composable.
 * @param viewModel A [AppViewModel] used to get required data to this composable.
 * @param confirmArrival A callback function triggered when the user confirms arrival at the intervention location.
 * @param stopIntervention A callback function triggered when the user stops the intervention.
 * @param callForSupport A callback function triggered when the user requests support during the intervention.
 * @param endIntervention A callback function triggered when the user ends the intervention.
 *
 * Functionality:
 * - Displays a map using [MapboxMap] with a marker.
 * - Displays basic information about the intervention, including its number and location.
 * - Provides action buttons for various intervention-related tasks using the [InterventionButtons] component.
 */
@Composable
fun InterventionScreen(
    modifier: Modifier,
    context: Context,
    viewModel: AppViewModel,
    confirmArrival: () -> Unit,
    stopIntervention: () -> Unit,
    callForSupport: () -> Unit,
    endIntervention: () -> Unit
) {

    var interventionStarted = remember { mutableStateOf(false) }
    var supportAlongTheWay = remember { mutableStateOf(false) }
    val autoUpdateCamera = remember { mutableStateOf(true) }
    val centerToDestination = remember { mutableStateOf(false) }
    val currentLocationString = remember { mutableStateOf("") }
/*
    LaunchedEffect(viewModel.reportLocation.value) {
        getAddressFromCoordinates(viewModel.reportLocation.value.latitude(),viewModel.reportLocation.value.longitude()) { location ->
            currentLocationString.value = location
        }
    }
*/

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
            MyMap(viewModel, context, autoUpdateCamera,centerToDestination)
            Column(modifier = Modifier.align(Alignment.TopEnd)) {
                Button(
                    onClick = {
                        autoUpdateCamera.value = false
                        centerToDestination.value = !centerToDestination.value
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (centerToDestination.value) Color(
                            232,
                            136,
                            115
                        ) else MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(5.dp),
                    modifier = Modifier
                        .padding(4.dp, 4.dp, 4.dp, 2.dp)
                        .size(60.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_location_searching_24),
                        contentDescription = "destination",
                        modifier = Modifier
                            .size(40.dp)
                            .rotate(45f),
                    )
                }
                Button(
                    onClick = {
                        autoUpdateCamera.value = !autoUpdateCamera.value
                        centerToDestination.value = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (autoUpdateCamera.value) Color(
                            232,
                            136,
                            115
                        ) else MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(5.dp),
                    modifier = Modifier
                        .padding(4.dp, 2.dp, 4.dp, 4.dp)
                        .size(60.dp)
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
            if (viewModel.getIsSystemConnecting().value) {
                RotatingLoader(
                    Modifier
                        .align(Alignment.Center)
                        .zIndex(10f),
                    MaterialTheme.colorScheme.primary,
                    circleRadius = 42.dp,
                    strokeWidth = 10.dp
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
                text = "Interwencja nr ${SharedPreferencesManager.getReportId()}",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp
            )
            Text(
                text = currentLocationString.value,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
        InterventionButtons(
            interventionStarted = interventionStarted,
            supportAlongTheWay = supportAlongTheWay,
            isSystemDisconnected = viewModel.getIsSystemConnecting(),
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
                LocalContext.current,
                AppViewModel(),
                {},
                {},
                {},
                {})
        }
    }
}
