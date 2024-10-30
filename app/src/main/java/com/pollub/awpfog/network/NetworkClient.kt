package com.pollub.awpfog.network

import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonParser
import com.pollub.awpfog.BASE_URL
import com.pollub.awpfog.data.ApiService
import com.pollub.awpfog.data.SharedPreferencesManager
import com.pollub.awpfog.data.models.Guard
import com.pollub.awpfog.viewmodel.AppViewModel
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
        val currentLocation = mutableStateOf(Pair(0.0, 0.0))
        private var viewModel: AppViewModel? = null

        fun setViewModel(vm: AppViewModel) {
            viewModel = vm
        }

        fun setCloseCode(code: Int) {
            closeCode = code
        }

        fun setOnConnect(callback: () -> Unit) {
            onConnect = callback
        }

        fun connect(url: String) {
            closeCode = null
            if (!isConnected) {
                val request = Request.Builder().url(url).build()
                webSocket = client.newWebSocket(request, object : WebSocketListener() {
                    override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                        isConnected = true
                    }

                    override fun onMessage(webSocket: WebSocket, text: String) {
                        val jsonObject = JsonParser.parseString(text).asJsonObject
                        if (jsonObject.has("status")) {
                            when (jsonObject.get("status").asString) {
                                "connected" -> {
                                    onConnect?.invoke()
                                }

                                "confirm" -> {
                                    if(jsonObject.has("reportId") || jsonObject.has("location")) {
                                        if (jsonObject.has("reportId")) {
                                            SharedPreferencesManager.saveReportId(jsonObject.get("reportId").asInt)
                                        }
                                        if (jsonObject.has("location")) {
                                            val locJson = jsonObject.getAsJsonObject("location")
                                            if (locJson.has("lat") && locJson.has("lng")) {
                                                SharedPreferencesManager.saveStatus(Guard.GuardStatus.INTERVENTION)
                                                viewModel?.apply {
                                                    reportLocation.value = LatLng(
                                                        locJson.get("lat").asDouble,
                                                        locJson.get("lng").asDouble
                                                    )
                                                }
                                            }
                                        }
                                        viewModel?.isInterventionVisible?.value=true
                                    }
                                }

                                "warning" -> {
                                    try {
                                        viewModel?.apply {
                                            onWarning()
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
                        isConnected = false
                        t.printStackTrace()
                    }

                    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        isConnected = false
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
            viewModel?.apply {
                connectionStatus.value = false
            }
            if (isConnected) {
                if (closeCode != null)
                    webSocket.close(closeCode!!, "Disconnect")
                else
                    webSocket.close(1000, "Disconnect")
                isConnected = false
                closeCode = null
            }
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

