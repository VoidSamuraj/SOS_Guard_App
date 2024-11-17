package com.pollub.awpfog.utils

import android.util.Log
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import com.pollub.awpfog.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern


/**
 * Check if the provided username is valid.
 * A valid username must be between 3 and 40 characters long.
 *
 * @param username The username to validate.
 * @return True if the username is valid; otherwise, false.
 */
fun isUsernameValid(username: String): Boolean {
    return username.length in 3..40
}

/**
 * Check if the provided login username is valid.
 * A valid login username must be between 3 and 20 characters long.
 *
 * @param username The login username to validate.
 * @return True if the login username is valid; otherwise, false.
 */
fun isLoginValid(username: String): Boolean {
    return username.length in 3..20
}

/**
 * Check if the provided password is valid.
 * A valid password must be at least 8 characters long and contain at least one lowercase letter,
 * one uppercase letter, one digit, and one special character.
 *
 * @param password The password to validate.
 * @return True if the password is valid; otherwise, false.
 */
fun isPasswordValid(password: String): Boolean {
    val regex = """^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^a-zA-Z0-9]).{8,}$""".toRegex()
    return regex.matches(password)
}


/**
 * Check if the provided phone number is valid.
 * A valid phone number can optionally start with a '+' and must be between 10 and 13 digits long.
 *
 * @param phoneNumber The phone number to validate.
 * @return True if the phone number is valid; otherwise, false.
 */
fun isPhoneValid(phoneNumber: String): Boolean {
    val regex = "^[+]?[0-9]{10,13}$".toRegex()
    return regex.matches(phoneNumber)
}

/**
 * Check if the provided email address is valid.
 * A valid email address follows the standard format of local-part@domain.
 *
 * @param email The email address to validate.
 * @return True if the email address is valid; otherwise, false.
 */
fun isEmailValid(email: String): Boolean {
    val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    val pattern = Pattern.compile(emailRegex)
    return pattern.matcher(email).matches()
}

/**
 * Retrieves the street name based on latitude and longitude coordinates.
 *
 * @param latitude The latitude of the desired location.
 * @param longitude The longitude of the desired location.
 * @param onSuccess Callback which use response location string.
 * @return The street name if found, or null if not found or an error occurs.
 *
 * @throws java.io.IOException If the geocoding service is not available or fails to process the request.
 *
 * Note: The `getFromLocation` method is deprecated. Consider using
 * alternative geocoding methods or services for future implementations.
 */


fun getAddressFromCoordinates(
    latitude: Double,
    longitude: Double,
    onSuccess: (location: String) -> Unit
) {
    val geocoder = MapboxGeocoding.builder()
        .accessToken(BuildConfig.MAPBOX_ACCESS_TOKEN)
        .query(Point.fromLngLat(longitude, latitude))
        .build()

    geocoder.enqueueCall(object : Callback<GeocodingResponse> {
        override fun onResponse(
            call: Call<GeocodingResponse>,
            response: Response<GeocodingResponse>
        ) {
            if (response.isSuccessful) {
                val features = response.body()?.features()
                if (features != null && features.isNotEmpty()) {
                    val address = features[0].text()
                    address?.let { onSuccess(it) }
                } else {
                    Log.d("ReverseGeo", "No results found.")
                }
            } else {
                Log.e("ReverseGeo", "Error: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
            Log.e("ReverseGeo", "Failed to fetch data: ${t.message}")
        }
    })
}