package com.pollub.awpfog.viewmodel

import com.pollub.awpfog.data.SharedPreferencesManager
import com.pollub.awpfog.data.models.Guard
import com.pollub.awpfog.data.models.GuardInfo
import com.pollub.awpfog.network.NetworkClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test


class AppViewModelTest {

    companion object {
        private val mockWebServer = MockWebServer()
        private lateinit var viewModel: AppViewModel
        @BeforeClass
        @JvmStatic
        fun setUpAll() {
            NetworkClient.setBaseUrl(mockWebServer.url("/").toString())
            viewModel = AppViewModel()
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            mockWebServer.shutdown()
        }
    }

    @Before
    fun setUp() {
        SharedPreferencesManager.clear()
    }


    @Test
    fun `test login not used `() = runBlocking {
        val response = MockResponse().setResponseCode(200)
            .setBody("false")
        mockWebServer.enqueue(response)

        var testResult: Boolean? = null

        viewModel.isLoginNotUsed(
            login = "Jan",
            onSuccess = {
                testResult = true
            },
            onFailure = {
                testResult = false
            }
        )
        while (testResult == null) {
            delay(10)
        }
        assert(testResult)

    }

    @Test
    fun `test login used `() = runBlocking {
        val response = MockResponse().setResponseCode(200)
            .setBody("true")
        mockWebServer.enqueue(response)

        var testResult: Boolean? = null

        viewModel.isLoginNotUsed(
            login = "Jan",
            onSuccess = {
                testResult = true
            },
            onFailure = {
                println(it)
                testResult = false
            }
        )
        while (testResult == null) {
            delay(10)
        }
        assert(!testResult)
    }

    @Test
    fun `test login`() = runBlocking {
        val response = MockResponse().setResponseCode(200)
            .setBody(
                """
                {
                  "first": "clientLogin",
                  "second": {
                    "id": 1,
                    "login": "JohnDoe",
                    "name": "John",
                    "surname": "Doe",
                    "phone": "123456789",
                    "email": "john.doe@example.com",
                    "statusCode": 1,
                    "location": "",
                    "account_deleted": false,
                    "token": "customerToken"
                  },
                  "third": {
                    "token": "jwtTokenExample"
                  }
                }
                """.trimIndent()
            )
        mockWebServer.enqueue(response)

        var testResult: Boolean? = null

        viewModel.login(
            login = "Jan",
            password = "qwerty",
            onSuccess = {
                testResult = true
            },
            onFailure = {
                println(it)
                println(it)
                testResult = false
            }
        )
        while (testResult == null) {
            delay(10)
        }
        assert(testResult)
        assert(SharedPreferencesManager.getToken() == "customerToken")
        assert(SharedPreferencesManager.getGuardName() == "John Doe")
    }

    @Test
    fun `test register `() = runBlocking {
        val response = MockResponse().setResponseCode(200)
            .setBody(
                """
                {
                  "first": "clientLogin",
                  "second": {
                    "id": 1,
                    "login": "AndrzejN",
                    "name": "Andrzej",
                    "surname": "Nowak",
                    "phone": "123456789",
                    "email": "john.doe@example.com",
                    "statusCode": 1,
                    "location": "",
                    "account_deleted": false,
                    "token": "customerToken"
                  },
                  "third": {
                    "token": "jwtTokenExample"
                  }
                }
                """.trimIndent()
            )
        mockWebServer.enqueue(response)

        var testResult: Boolean? = null

        viewModel.register(
            login = "Jan",
            password = "qwerty",
            guard = GuardInfo(
                id = -1,
                name = "Andrzej",
                surname = "Nowak",
                email = "john.doe@example.com",
                phone = "123456789",
                statusCode = Guard.GuardStatus.AVAILABLE.status,
                location = "",
                account_deleted = false,
            ),
            onSuccess = {
                testResult = true
            },
            onFailure = {
                testResult = false
            }
        )
        while (testResult == null) {
            delay(10)
        }
        assert(testResult)
        assert(SharedPreferencesManager.getToken() == "customerToken")
        assert(SharedPreferencesManager.getGuardName() == "Andrzej Nowak")
    }

    @Test
    fun `test logout `() = runBlocking {

        var response = MockResponse().setResponseCode(200)
            .setBody(
                """
                {
                  "first": "clientLogin",
                  "second": {
                    "id": 1,
                    "login": "AndrzejN",
                    "name": "Andrzej",
                    "surname": "Nowak",
                    "phone": "123456789",
                    "email": "john.doe@example.com",
                    "statusCode": 1,
                    "location": "",
                    "account_deleted": false,
                    "token": "customerToken"
                  },
                  "third": {
                    "token": "jwtTokenExample"
                  }
                }
                """.trimIndent()
            )
        mockWebServer.enqueue(response)

        var testResult: Boolean? = null

        viewModel.register(
            login = "Jan",
            password = "qwerty",
            guard = GuardInfo(
                id = -1,
                name = "Andrzej",
                surname = "Nowak",
                email = "john.doe@example.com",
                phone = "123456789",
                statusCode = Guard.GuardStatus.AVAILABLE.status,
                location = "",
                account_deleted = false,
            ),
            onSuccess = {
                testResult = true
            },
            onFailure = {
                testResult = false
            }
        )
        while (testResult == null) {
            delay(10)
        }
        assert(testResult == true)
        assert(SharedPreferencesManager.getToken() == "customerToken")
        assert(SharedPreferencesManager.getGuardName() == "Andrzej Nowak")

        response = MockResponse().setResponseCode(200)
            .setBody("true")
        mockWebServer.enqueue(response)

        testResult = null

        viewModel.logout(
            onSuccess = {
                testResult = true
            },
            onFailure = {
                testResult = false
            }
        )
        while (testResult == null) {
            delay(10)
        }
        assert(testResult == true)
        assert(SharedPreferencesManager.getToken() != "customerToken")
        assert(SharedPreferencesManager.getGuardName() != "John Doe")
    }

    @Test
    fun `test edit`() = runBlocking {

    }
}