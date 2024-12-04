package com.pollub.awpfog.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.pollub.awpfog.BASE_WEBSOCKET_URL
import com.pollub.awpfog.MainActivity
import kotlinx.coroutines.*
import com.pollub.awpfog.R
import com.pollub.awpfog.data.SharedPreferencesManager
import com.pollub.awpfog.network.NetworkClient.WebSocketManager
import com.pollub.awpfog.utils.TokenManager
import com.pollub.awpfog.utils.TokenManager.isRefreshTokenExpired

class LocationService : Service() {

    companion object {
        const val NOTIFICATION_ID = 1
        private const val UPDATE_LOCATION_INTERVAL = 10_000L
        private const val CHECK_TOKEN_INTERVAL_COUNT =
            TokenManager.TOKEN_EXPIRATION_THRESHOLD * 500 / UPDATE_LOCATION_INTERVAL
        private const val MIN_DISTANCE_THRESHOLD_METERS = 5
    }

    private var lastLocation: Location? = null
    private val guard = SharedPreferencesManager.getGuard()
    private var locationCallback: LocationCallback? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var tokenCheckCounter = 0

    //associate coroutine with scope to easy cancel coroutine
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate() {
        Log.d("LocationService", "onCreate")
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        startForeground(NOTIFICATION_ID, createNotification())

        WebSocketManager.connect(BASE_WEBSOCKET_URL)
        sendStartupInfo()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("LocationService", "onStartCommand")
        startLocationUpdates()
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("LocationService", "onDestroy")
        super.onDestroy()
        WebSocketManager.disconnect()
        stopLocationUpdates()
        job.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d("LocationService", "onBind")
        return null
    }

    @SuppressLint("MissingPermission")
    private fun sendStartupInfo() {
        Log.d("LocationService", "sendStartupInfo")
        fusedLocationClient.requestLocationUpdates(
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0).build(),
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    sendInitMessage(locationResult, this)
                }
            },
            Looper.getMainLooper()
        )
    }

    fun sendInitMessage(
        locationResult: LocationResult,
        locationCallback: LocationCallback? = null
    ) {
        Log.d("LocationService", "sendInitMessage")
        val location = locationResult.lastLocation
        lastLocation=location
        if (location != null) {
            val lat = location.latitude
            val lng = location.longitude
            val locationData = """
                            {"initMessage":true, "guardId":${guard.id},"status":${SharedPreferencesManager.getStatus()}, "latitude": ${lat}, "longitude": ${lng}}
                            """.trimIndent()

            scope.launch {
                try {
                    WebSocketManager.sendMessage(
                        locationData,
                        lat,
                        lng
                    )
                    Log.d("LocationService", "Initial location sent")
                } catch (e: Exception) {
                    Log.e("LocationService", "Error sending initial location: ${e.message}")
                }
            }
            locationCallback?.let {
                fusedLocationClient.removeLocationUpdates(it)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        Log.d("LocationService", "startLocationUpdates")
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000L // every 10 seconds
        )
            .setMinUpdateIntervalMillis(10000L)
            .setMaxUpdateDelayMillis(10000L)
            .build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (tokenCheckCounter >= CHECK_TOKEN_INTERVAL_COUNT) {
                    tokenCheckCounter = 0
                    if (isRefreshTokenExpired()) {
                        WebSocketManager.disconnect()
                        return
                    }
                    runBlocking { TokenManager.refreshTokenIfNeeded() }
                } else
                    ++tokenCheckCounter

                for (location in locationResult.locations) {
                    if (lastLocation == null || location.distanceTo(lastLocation!!) >= MIN_DISTANCE_THRESHOLD_METERS) {
                        WebSocketManager.setCurrentLocation(location)
                        if (WebSocketManager.isConnecting.value)
                            sendInitMessage(locationResult)
                        else
                            sendLocationToServer(location)
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    private fun sendLocationToServer(location: Location) {
        val lat = location.latitude
        val lng = location.longitude
        val locationData = """
                            {"guardId":${guard.id},"status":${SharedPreferencesManager.getStatus()}, "latitude": ${lat}, "longitude": ${lng}}
                            """.trimIndent()
        scope.launch {
            try {
                WebSocketManager.sendMessage(locationData, lat, lng)
            } catch (e: Exception) {
                Log.e("LocationService", "Error sending location: ${e.message}")
            }
        }
    }

    private fun stopLocationUpdates() {
        Log.d("LocationService", "stopLocationUpdates")
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

    private fun createNotification(): Notification {
        Log.d("LocationService", "createNotification")
        val channelId = "location_service_channel"
        val channelName = "Location Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Firma Ochroniarska")
            .setContentText("System śledzi twoją lokalizację.")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.baseline_navigation_24)
            .build()
    }
}

