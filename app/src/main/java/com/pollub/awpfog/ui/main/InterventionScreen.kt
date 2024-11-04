package com.pollub.awpfog.ui.main

import android.annotation.SuppressLint
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.pollub.awpfog.R
import com.pollub.awpfog.ui.components.InterventionButtons
import com.pollub.awpfog.ui.components.RotatingLoader
import com.pollub.awpfog.ui.theme.AwpfogTheme
import com.pollub.awpfog.utils.MyMapboxNavigationObserver
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
 * - Displays a map using [MapboxMap] with a marker.
 * - Includes a button in the top-right corner for navigating to a position, which calls [navigateToPos].
 * - Displays basic information about the intervention, including its number and location.
 * - Provides action buttons for various intervention-related tasks using the [InterventionButtons] component.
 */
@Composable
fun InterventionScreen(
    modifier: Modifier,
    context: Context,
    viewModel: AppViewModel,
    navigateToPos: () -> Unit,
    confirmArrival: () -> Unit,
    stopIntervention: () -> Unit,
    callForSupport: () -> Unit,
    endIntervention: () -> Unit
) {

    var interventionStarted = remember { mutableStateOf(false) }
    var supportAlongTheWay = remember { mutableStateOf(false) }

val mapViewportState = rememberMapViewportState{
    setCameraOptions {
        zoom(5.0)
        center(viewModel.reportLocation.value)
        pitch(0.0)
        bearing(0.0)
    }
}
    val marker = rememberIconImage(key = R.drawable.baseline_person_pin_24, painter = painterResource(R.drawable.baseline_person_pin_24))


    val lifecycleOwner = LocalLifecycleOwner.current

    // Setup MapboxNavigationApp jeśli nie jest jeszcze skonfigurowany

    val myObserver = MyMapboxNavigationObserver()

    // Zarządzaj podłączaniem i odłączaniem w cyklu życia z DisposableEffect
    DisposableEffect(lifecycleOwner) {
        // Listener cyklu życia - dołączanie nawigacji
        val observer = object : DefaultLifecycleObserver {
            @SuppressLint("MissingPermission")
            override fun onResume(owner: LifecycleOwner) {
                MapboxNavigationApp.attach(owner)
                MapboxNavigationApp.registerObserver(myObserver)
                MapboxNavigationApp.current()?.startTripSession()
            }

            override fun onPause(owner: LifecycleOwner) {
                MapboxNavigationApp.detach(owner)
                MapboxNavigationApp.unregisterObserver(myObserver)
                MapboxNavigationApp.current()?.stopTripSession()
            }

            override fun onCreate(owner: LifecycleOwner) {
                if (!MapboxNavigationApp.isSetup()) {
                    MapboxNavigationApp.setup {
                        NavigationOptions.Builder(context).build()
                    }
                }
            }
        }

        // Dodaj obserwatora do cyklu życia
        lifecycleOwner.lifecycle.addObserver(observer)

        // Zwróć funkcję czyszczącą, aby usunąć obserwatora przy odmontowaniu
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
            MapboxMap(
                Modifier.fillMaxSize(),
                mapViewportState =mapViewportState,
                style = {
                    MapStyle(style = "mapbox://styles/karolloo/cm30ff2p000vz01pm6y7o4dt7")
                }

            ) {
                // Insert a PointAnnotation composable function with the geographic coordinate to the content of MapboxMap composable function.
                PointAnnotation(point = viewModel.reportLocation.value) {
                    iconImage = marker
                }
                MapStyle(style = Style.TRAFFIC_NIGHT)
                MapEffect(Unit) { mapView ->
                    mapView.mapboxMap.loadStyle(Style.TRAFFIC_NIGHT)
                    mapView.location.updateSettings {
                        setLocationPuck(
                            LocationPuck2D(

                            bearingImage = ImageHolder.from(R.drawable.baseline_navigation_24),

                            shadowImage = ImageHolder.from(R.drawable.baseline_navigation_shadow_24),

                            scaleExpression = interpolate {
                                linear()
                                zoom()

                                stop(
                                    0.0,
                                    0.6
                                )
                                stop(
                                    20.0,
                                    1.0
                                )

                            }.toJson()

                        )
                        )
                        enabled = true

                        puckBearing = PuckBearing.HEADING

                        puckBearingEnabled = true

                        mapView.location.puckBearing = PuckBearing.HEADING
                    }
                }
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
            if(viewModel.getIsSystemConnecting().value){
                RotatingLoader(Modifier.align(Alignment.Center).zIndex(10f),MaterialTheme.colorScheme.primary, circleRadius = 42.dp, strokeWidth = 10.dp)
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
                {},
                {})
        }
    }
}
