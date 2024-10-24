package com.pollub.awpfog.data

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

object WebSocketManager {

    private lateinit var webSocket: WebSocket
    private var isConnected = false

    fun connect(url: String) {
        if (!isConnected) {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                    isConnected = true
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

    fun disconnect() {
        if (isConnected) {
            webSocket.close(1000, "Disconnect")
            isConnected = false
        }
    }
}
