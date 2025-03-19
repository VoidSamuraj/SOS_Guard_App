package com.pollub.awpfog.network

import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

class NetworkClientTest {
    private lateinit var mockServer: MockWebServer

    @Before
    fun setUp() {
        mockServer = MockWebServer()
        mockServer.start()
    }

    @After
    fun tearDown() {
        NetworkClient.WebSocketManager.disconnect()
        Thread.sleep(1000)
        try {
            mockServer.shutdown()
        }catch (_: Exception){}
    }

    @Test
    fun `test connect successfully opens a WebSocket connection`() {
        val response = MockResponse()
            .withWebSocketUpgrade(object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                    webSocket.send("""{"status": "connected"}""")
                }
            })
        mockServer.enqueue(response)

        NetworkClient.WebSocketManager.connect(mockServer.url("/").toString())

        Thread.sleep(500)

        assert(NetworkClient.WebSocketManager.isConnected)
    }

    @Test
    fun `test connect handles server failure`() {
        val response = MockResponse().setResponseCode(500)
        mockServer.enqueue(response)

        NetworkClient.WebSocketManager.connect(mockServer.url("/").toString())

        Thread.sleep(500)

        assert(!NetworkClient.WebSocketManager.isConnected)
    }

    @Test
    fun `test sendMessage sends data when connected`() {
        val response = MockResponse()
            .withWebSocketUpgrade(object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                    webSocket.send("""{"status": "connected"}""")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    assert(text == """{"action": "testMessage"}""")
                }
            })
        mockServer.enqueue(response)

        NetworkClient.WebSocketManager.connect(mockServer.url("/").toString())
        Thread.sleep(500) // Wait for connection

        NetworkClient.WebSocketManager.sendMessage("""{"action": "testMessage"}""")
    }

    @Test
    fun `test sendMessage does not send when disconnected`() {
        NetworkClient.WebSocketManager.disconnect()
        Thread.sleep(500)

        NetworkClient.WebSocketManager.sendMessage("""{"action": "testMessage"}""")

        assert(!NetworkClient.WebSocketManager.isConnected)
    }

    @Test
    fun `test disconnect closes the WebSocket connection`() {
        val response = MockResponse()
            .withWebSocketUpgrade(object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                    webSocket.send("""{"status": "connected"}""")
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    assert(code == 1000)
                }
            })
        mockServer.enqueue(response)

        NetworkClient.WebSocketManager.connect(mockServer.url("/").toString())
        Thread.sleep(500)

        NetworkClient.WebSocketManager.disconnect()
        Thread.sleep(500)

        assert(!NetworkClient.WebSocketManager.isConnected)
    }
}