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
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.pollub.awpfog.BASE_WEBSOCKET_URL
import kotlinx.coroutines.*
import com.pollub.awpfog.R
import com.pollub.awpfog.data.SharedPreferencesManager
import com.pollub.awpfog.network.NetworkClient.WebSocketManager
import kotlin.random.Random

class LocationService : Service() {
    companion object {
        const val NOTIFICATION_ID = 1
    }

    private val guard = SharedPreferencesManager.getGuard()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //associate coroutine with scope to easy cancel coroutine
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        startForeground(NOTIFICATION_ID, createNotification())

        WebSocketManager.connect(BASE_WEBSOCKET_URL)
        sendStartupInfo()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationUpdates()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        job.cancel()
        WebSocketManager.disconnect()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    private fun sendStartupInfo() {
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
        val location = locationResult.lastLocation
        if (location != null) {
            val locationData = """
                            {"initMessage":true, "guardId":${guard.id},"status":${SharedPreferencesManager.getStatus()}, "latitude": ${location.latitude}, "longitude": ${location.longitude}}
                            """.trimIndent()

            scope.launch {
                try {
                    WebSocketManager.sendMessage(
                        locationData,
                        location.latitude,
                        location.longitude
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
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000L // every 10 seconds
        )
            .setMinUpdateIntervalMillis(10000L)
            .setMaxUpdateDelayMillis(10000L)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        if (WebSocketManager.isConnecting.value)
                            sendInitMessage(locationResult)
                        else
                            sendLocationToServer(location)
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    private fun sendLocationToServer(location: Location) {
        //test
        val lat = Random.nextDouble(49.0, 54.8)
        val lng = Random.nextDouble(14.1, 24.2)
        /*
        val locationData =  """
                            {"guardId":${guard.id},"status":${SharedPreferencesManager.getStatus()}, "latitude": ${location.latitude}, "longitude": ${location.longitude}}
                            """.trimIndent()
        */
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
        fusedLocationClient.removeLocationUpdates(object : LocationCallback() {})
    }

    private fun createNotification(): Notification {
        val channelId = "location_service_channel"
        val channelName = "Location Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Firma Ochroniarska")
            .setContentText("Twoja lokalizacja jest udostępniana ochroniarzowi.")
            .setSmallIcon(R.drawable.baseline_navigation_24)
            .build()
    }
}

