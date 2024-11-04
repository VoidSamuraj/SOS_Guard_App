package com.pollub.awpfog.utils

import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import com.mapbox.common.location.Location

class MyLocationObserver : LocationObserver {
    // MutableSharedFlow to emit location updates
    private val _locationFlow = MutableSharedFlow<Location>()
    // Expose a read-only Flow to observe location updates
    val locationFlow = _locationFlow.asSharedFlow()

    override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
        // Emit the enhanced location to _locationFlow when a new location matcher result is received
        _locationFlow.tryEmit(locationMatcherResult.enhancedLocation)
    }

    override fun onNewRawLocation(rawLocation:Location) {
        // Emit the raw location to _locationFlow when a new raw location is received
        _locationFlow.tryEmit(rawLocation)
    }
}

class MyMapboxNavigationObserver : MapboxNavigationObserver {
    private val locationObserver = MyLocationObserver()
    val location: Flow<Location> = locationObserver.locationFlow

    override fun onAttached(mapboxNavigation: MapboxNavigation) {
        mapboxNavigation.registerLocationObserver(locationObserver)
    }

    override fun onDetached(mapboxNavigation: MapboxNavigation) {
        mapboxNavigation.unregisterLocationObserver(locationObserver)
    }
}