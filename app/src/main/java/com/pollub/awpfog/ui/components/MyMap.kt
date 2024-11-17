package com.pollub.awpfog.ui.components


import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.ColorValue
import com.mapbox.maps.extension.compose.style.DoubleValue
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.extension.compose.style.layers.generated.LineCapValue
import com.mapbox.maps.extension.compose.style.layers.generated.LineJoinValue
import com.mapbox.maps.extension.compose.style.layers.generated.LineLayer
import com.mapbox.maps.extension.compose.style.layers.generated.VisibilityValue
import com.mapbox.maps.extension.compose.style.sources.GeoJSONData
import com.mapbox.maps.extension.compose.style.sources.generated.rememberGeoJsonSourceState
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.directions.session.RoutesUpdatedResult
import com.pollub.awpfog.R
import com.pollub.awpfog.viewmodel.AppViewModel

@Composable
fun MyMap(
    viewModel: AppViewModel,
    context: Context,
    autoUpdateCamera: MutableState<Boolean>,
    navigateToPoint: MutableState<Boolean>
) {


    val lineString = remember {
        mutableStateOf(LineString.fromLngLats(listOf()))
    }
    var cameraOptions = remember {
        mutableStateOf<CameraOptions>(
            CameraOptions.Builder()
                .center(
                    Point.fromLngLat(
                        viewModel.currentLocation.value?.longitude ?: 0.0,
                        viewModel.currentLocation.value?.latitude ?: 0.0
                    )
                )
                .zoom(15.0)
                .build()
        )
    }

    val navigationOptions = NavigationOptions.Builder(context).build()

    val mapboxNavigation = remember {
        MapboxNavigationProvider.create(
            navigationOptions
        )
    }
    val marker = rememberIconImage(
        key = R.drawable.baseline_person_pin_24,
        painter = painterResource(R.drawable.baseline_person_pin_24)
    )

    // Routes observer
    val routesObserver = remember {
        object : RoutesObserver {
            override fun onRoutesChanged(result: RoutesUpdatedResult) {
                if (result.navigationRoutes.isNotEmpty()) {
                    val route = result.navigationRoutes.first()
                    Log.d("LOKALIZACJA", "Updated route: ${route.directionsRoute.geometry()}")
                } else {
                    Log.d("LOKALIZACJA", "No routes found")
                }
            }
        }
    }

    // Register routes observer
    DisposableEffect(Unit) {
        mapboxNavigation.registerRoutesObserver(routesObserver)
        onDispose {
            mapboxNavigation.unregisterRoutesObserver(routesObserver)
        }
    }

    // Request route when location changes
    LaunchedEffect(viewModel.reportLocation.value, viewModel.currentLocation.value) {
        viewModel.currentLocation.value?.let { location ->
            val startPoint = Point.fromLngLat(location.longitude, location.latitude)
            val endPoint = viewModel.reportLocation.value
            requestRoute(lineString, startPoint, endPoint, mapboxNavigation)
        }
    }


    val sourceState = rememberGeoJsonSourceState()
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            pitch(45.0)
            zoom(15.0)
        }
    }

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        style = {
            MapStyle(style = "mapbox://styles/mapbox/traffic-night-v2")
        },
        mapViewportState = mapViewportState
    ) {
        lineString.value?.let { line ->

            LaunchedEffect(line) {
                sourceState.data = GeoJSONData(line)
            }

            LineLayer(
                sourceState = sourceState
            ) {

                lineColor = ColorValue(Color(0x442662EF.toInt()))
                lineWidth = DoubleValue(10.0)
                lineBorderColor = ColorValue(Color(0xff2662EF.toInt()))
                lineBorderWidth = DoubleValue(1.0)
                lineCap = LineCapValue.ROUND
                lineJoin = LineJoinValue.ROUND
                visibility = VisibilityValue.VISIBLE
            }
        }

        // Dodanie punktów
        viewModel.reportLocation.value?.let { point ->
            PointAnnotation(point) {
                iconImage = marker
                iconAnchor = IconAnchor.BOTTOM
                iconSize = 1.5
            }
        }

        MapEffect(Unit) { mapView ->
            mapView.location.updateSettings {
                mapView.camera.easeTo(cameraOptions.value)
                setLocationPuck(
                    LocationPuck2D(
                        bearingImage = ImageHolder.from(R.drawable.baseline_navigation_24),
                        shadowImage = ImageHolder.from(R.drawable.baseline_navigation_shadow_24),
                        scaleExpression = Expression.Companion.interpolate {
                            linear()
                            zoom()
                            stop(
                                0.0,
                                1.5
                            )
                            stop(
                                20.0,
                                1.5
                            )

                        }.toJson()

                    )
                )
                enabled = true

                puckBearing = PuckBearing.HEADING

                puckBearingEnabled = true

                mapView.location.puckBearing = PuckBearing.HEADING
            }
            mapView.gestures.apply {
                addOnMoveListener(object : OnMoveListener {
                    override fun onMoveBegin(detector: MoveGestureDetector) {
                    }

                    override fun onMove(detector: MoveGestureDetector): Boolean {
                        navigateToPoint.value = false
                        return false
                    }

                    override fun onMoveEnd(detector: MoveGestureDetector) {
                    }
                })
            }

        }

        MapEffect(autoUpdateCamera.value, navigateToPoint.value) { mapView ->
            val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
                if (autoUpdateCamera.value) {
                    mapViewportState.setCameraOptions(CameraOptions.Builder().center(it).build())
                    mapView.gestures.focalPoint = mapView.mapboxMap.pixelForCoordinate(it)
                }
            }

            val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
                if (autoUpdateCamera.value) {
                    mapViewportState.setCameraOptions(CameraOptions.Builder().bearing(it).build())
                }
            }

            mapView.location.apply {
                enabled = true
                addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
                addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
            }

            if (navigateToPoint.value) {
                viewModel.reportLocation.value?.let {
                    cameraOptions.value = CameraOptions.Builder()
                        .center(it)
                        .zoom(15.0)
                        .build()
                    mapView.camera.easeTo(cameraOptions.value)
                }
            }
        }
    }
}

fun requestRoute(
    lineString: MutableState<LineString?>,
    startPoint: Point,
    endPoint: Point,
    mapboxNavigation: MapboxNavigation
) {
    val routeOptions = RouteOptions.builder()
        .coordinatesList(listOf(startPoint, endPoint))
        .applyDefaultNavigationOptions()
        .build()

    mapboxNavigation.requestRoutes(routeOptions, object : NavigationRouterCallback {
        override fun onRoutesReady(routes: List<NavigationRoute>, routerOrigin: String) {
            if (routes.isNotEmpty()) {
                val route = routes.first()
                val geometry = route.directionsRoute.geometry()
                if (geometry.isNullOrEmpty()) {
                    return
                }
                val newLineString = LineString.fromPolyline(
                    route.directionsRoute.geometry()!!,
                    com.mapbox.core.constants.Constants.PRECISION_6
                )
                lineString.value = newLineString
            }
        }

        override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
            Log.e("LOKALIZACJA", "ERROR  Route request failed: $reasons")
        }

        override fun onCanceled(routeOptions: RouteOptions, routerOrigin: String) {
            Log.d("LOKALIZACJA", "Route request canceled")
        }
    })
}
