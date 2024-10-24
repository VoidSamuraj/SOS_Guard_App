package com.pollub.awpfog.network

import com.pollub.awpfog.data.ApiService
import com.pollub.awpfog.data.SharedPreferencesManager
import okhttp3.OkHttpClient
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
object RetrofitClient {
    private const val BASE_URL = "https://10.0.2.2:8443/"

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

