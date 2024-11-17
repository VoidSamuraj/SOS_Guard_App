package com.pollub.awpfog.network

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParser
import com.mapbox.common.location.Location
import com.mapbox.geojson.Point
import com.pollub.awpfog.BASE_URL
import com.pollub.awpfog.data.ApiService
import com.pollub.awpfog.data.SharedPreferencesManager
import com.pollub.awpfog.data.models.Guard
import com.pollub.awpfog.viewmodel.AppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Singleton object to manage the Retrofit client configuration.
 */
object NetworkClient {

    // OkHttpClient instance configured with SSL settings and request interceptors.
    private val client = OkHttpClient.Builder()
        //TODO REMOVE THIS After Usage of trusted ssl keys
        //TEMPORARY ALLOW ALL CERTS START
        .sslSocketFactory(createTrustAllSslSocketFactory(), object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })
        .hostnameVerifier { hostname, session -> true }
        // END
        .addInterceptor { chain ->
            // Retrieve the original request, token and provide token within header
            val originalRequest = chain.request()
            val token = SharedPreferencesManager.getToken()

            val requestBuilder = originalRequest.newBuilder()
                .apply {
                    if (token != null) {
                        header("Authorization", "Bearer $token")
                    }
                }

            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .build()

    /**
     * Lazily initializes the Retrofit instance with the configured OkHttpClient and converters.
     */
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    object WebSocketManager {

        private lateinit var webSocket: WebSocket
        private var isConnected = false
        private var closeCode: Int? = null
        private var onConnect: (() -> Unit)? = null
        private var onInterventionCancelled: (() -> Unit)? = null
        val currentLocation = mutableStateOf(Pair(0.0, 0.0))
        private var viewModel: AppViewModel? = null
        private var isReportActive = mutableStateOf(true)
        val isConnecting = mutableStateOf(false)
        var isServiceStopping = false

        fun setViewModel(vm: AppViewModel) {
            viewModel = vm
        }

        fun setCurrentLocation(androidLocation: android.location.Location){
           viewModel?.currentLocation?.value = Location.Builder()
               .latitude(androidLocation.latitude)      // szerokość geograficzna
               .longitude(androidLocation.longitude)    // długość geograficzna
               .altitude(androidLocation.altitude)      // wysokość
               .speed(androidLocation.speed.toDouble())            // prędkość
               .timestamp(androidLocation.time)         // czas w milisekundach
               .build()
        }
        fun setCloseCode(code: Int) {
            closeCode = code
        }

        fun setOnConnect(callback: () -> Unit) {
            onConnect = callback
        }

        fun setOnInterventionCancelled(callback: () -> Unit) {
            onInterventionCancelled = callback
        }

        private fun setIsConnected(connected: Boolean) {
            isConnected = connected
            viewModel?.connectionStatus?.value = connected
        }


        private fun reconnectWithDelay(url: String) {
            isConnecting.value = true
            CoroutineScope(Dispatchers.IO).launch() {
                delay(5_000L)
                if (!isServiceStopping) {
                    connect(url)
                }
            }
        }

        fun connect(url: String) {

            closeCode = null
            isServiceStopping = false
            if (!isConnected) {
                val request = Request.Builder().url(url).build()
                webSocket = client.newWebSocket(request, object : WebSocketListener() {
                    override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                        //todo check if needed here
                        setIsConnected(true)
                        Log.d("WebSocketManager", "Connecting to WebSocket")
                    }

                    override fun onMessage(webSocket: WebSocket, text: String) {
                        val jsonObject = JsonParser.parseString(text).asJsonObject

                        Log.d("WebSocketManager", "got message: $jsonObject")
                        if (jsonObject.has("status")) {
                            when (jsonObject.get("status").asString) {
                                "connected" -> {
                                    onConnect?.invoke()
                                    isConnecting.value = false
                                    setIsConnected(true)
                                }

                                "confirm" -> {
                                    if (jsonObject.has("reportId") || jsonObject.has("location")) {
                                        if (jsonObject.has("reportId")) {
                                            SharedPreferencesManager.saveReportId(jsonObject.get("reportId").asInt)
                                        }
                                        if (jsonObject.has("location")) {
                                            val locJson = jsonObject.getAsJsonObject("location")
                                            if (locJson.has("lat") && locJson.has("lng")) {
                                                SharedPreferencesManager.saveStatus(Guard.GuardStatus.INTERVENTION)
                                                viewModel?.apply {
                                                    reportLocation.value = Point.fromLngLat(
                                                        locJson.get("lng").asDouble,
                                                        locJson.get("lat").asDouble
                                                    )
                                                }
                                            }
                                        }
                                        isReportActive.value = true
                                        viewModel?.apply {
                                            isInterventionVisible.value = true
                                            askIfReportActive(isReportActive)
                                        }

                                    }
                                }

                                "warning" -> {
                                    try {
                                        viewModel?.apply {
                                            viewModelScope.launch {
                                                onWarning()
                                            }
                                        }
                                    } catch (_: Exception) {
                                    }
                                }

                                "cancel", "notActive" -> {
                                    isReportActive.value = false
                                    try {
                                        viewModel?.apply {
                                            onInterventionCancelledByUser()
                                        }
                                        CoroutineScope(Dispatchers.Main).launch {
                                            onInterventionCancelled?.invoke()
                                        }
                                    } catch (_: Exception) {
                                    }
                                }
                            }
                        }
                        println("Received message: $text")
                    }

                    override fun onFailure(
                        webSocket: WebSocket,
                        t: Throwable,
                        response: okhttp3.Response?
                    ) {
                        Log.d("WebSocketManager", "failure message: $response  $t")
                        setIsConnected(false)
                        t.printStackTrace()
                        if (!isServiceStopping) {
                            reconnectWithDelay(url)
                        }
                    }

                    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        setIsConnected(false)
                        Log.d("WebSocketManager", "closed WebSocket")
                        if (!isServiceStopping) {
                            reconnectWithDelay(url)
                        }
                    }
                })
            }
        }

        fun sendMessage(message: String) {
            if (isConnected) {
                webSocket.send(message)
            } else {
                // Optional: Handle disconnected state or reconnect
            }
        }

        fun sendMessage(message: String, lat: Double, lng: Double) {
            currentLocation.value = Pair(lat, lng)
            if (isConnected) {
                webSocket.send(message)
            } else {
                // Optional: Handle disconnected state or reconnect
            }
        }

        fun disconnect() {
            isServiceStopping = true
            viewModel?.apply {
                connectionStatus.value = false
            }
            if (isConnected) {
                if (closeCode != null)
                    webSocket.close(closeCode!!, "Disconnect")
                else
                    webSocket.close(1000, "Disconnect")
            }
            setIsConnected(false)
            closeCode = null
        }
    }


    //Custom sslFactory to allow all certs
    private fun createTrustAllSslSocketFactory(): SSLSocketFactory {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        return sslContext.socketFactory
    }
}

