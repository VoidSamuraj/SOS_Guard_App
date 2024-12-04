package com.pollub.awpfog.data

import com.pollub.awpfog.data.models.JWTToken
import com.pollub.awpfog.data.models.TokenResponse
import com.pollub.awpfog.data.models.Credentials
import com.pollub.awpfog.data.models.GuardInfo
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query


/**
 * Interface defining API endpoints for guard-related operations.
 */
interface ApiService {
    /**
     * Checks if a given login is already in use.
     *
     * @param login The login to check for availability.
     * @return A [Call] that returns true if the login is used, otherwise false.
     */
    @GET("auth/guard/isLoginUsed")
    fun isLoginUsed(@Query("login") login: String): Call<Boolean>

    /**
     * Registers a new guard with the provided details.
     *
     * @param login The login for the new guard.
     * @param password The password for the new guard.
     * @param name The first name of the new guard.
     * @param surname The last name of the new guard.
     * @param email The email address of the new guard.
     * @param phone The phone number of the new guard.
     * @return A [Call] that returns a Triple containing a login, registered [GuardInfo], and [JWTToken].
     */
    @FormUrlEncoded
    @POST("auth/guard/register")
    fun registerGuard(
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("name") name: String,
        @Field("surname") surname: String,
        @Field("email") email: String,
        @Field("phone") phone: String
    ): Call<Triple<String, GuardInfo, JWTToken>>

    /**
     * Logs in a guard using their credentials.
     *
     * @param credentials The login credentials of the guard.
     * @return A [Call] that returns a Triple containing a login, registered [GuardInfo], and [JWTToken].
     */
    @POST("auth/guard/login")
    fun loginGuard(@Body credentials: Credentials): Call<Triple<String, GuardInfo, JWTToken>>

    /**
     * Checks the validity of the guard's session token.
     *
     * @param token The session token to check.
     * @return A [Call] that returns a pair containing a status message and the associated [GuardInfo].
     */
    @POST("auth/guard/checkToken")
    fun checkGuardToken(@Body token: String): Call<Pair<String, GuardInfo>>

    /**
     * Sends a password reminder to the specified email address.
     *
     * @param email The email address to send the password reminder to.
     * @return A [Call] that returns a status message regarding the reminder request.
     */
    @FormUrlEncoded
    @POST("auth/guard/remind-password")
    fun remindPassword(@Field("email") email: String): Call<String>

    /**
     * Logs out the currently authenticated guard.
     *
     * @return A [Call] that returns a status message indicating the result of the logout operation.
     */
    @POST("auth/guard/logout")
    fun logoutGuard(): Call<String>

    /**
     * Edits the details of an existing guard.
     *
     * @param id The unique identifier of the guard to edit.
     * @param login The new login for the guard, if changing.
     * @param password The current password of the guard.
     * @param newPassword The new password for the guard, if changing.
     * @param name The new first name for the guard, if changing.
     * @param surname The new last name for the guard, if changing.
     * @param email The new email address for the guard, if changing.
     * @param phone The new phone number for the guard, if changing.
     * @return A [Call] that returns a pair containing a status message and the updated [GuardInfo].
     */
    @FormUrlEncoded
    @PATCH("auth/guard/edit")
    fun editGuard(
        @Field("id") id: Int,
        @Field("login") login: String?,
        @Field("password") password: String,
        @Field("newPassword") newPassword: String?,
        @Field("name") name: String?,
        @Field("surname") surname: String?,
        @Field("email") email: String?,
        @Field("phone") phone: String?
    ): Call<Pair<String, GuardInfo>>

    /**
     *  Retrieves the new [TokenResponse.accessToken]
     *
     *  @param refreshToken valid RefreshToken
     *  @return [TokenResponse] with new [TokenResponse.accessToken]
     */
    @POST("auth/guard/refresh_token")
    suspend fun refreshToken(
        @Body refreshToken: String
    ): Response<TokenResponse>

    /**
     *  Retrieves the new [TokenResponse] containing [TokenResponse.refreshToken] and [TokenResponse.accessToken]
     *
     *  @param refreshToken valid RefreshToken
     *  @return [TokenResponse] with two new tokens
     */
    @POST("auth/guard/refresh_refresh_token")
    suspend fun refreshRefreshToken(
        @Body refreshToken: String
    ): Response<TokenResponse>


    /**
     * Retrieves the location assigned to an active intervention for a guard as a JSON string.
     *
     * This function sends an HTTP POST request to the endpoint `/action/getActiveInterventionLocationAssignedToGuard`,
     * passing the guard’s ID (`guardId`). If the guard has an intervention with the status
     * `IN_PROGRESS`, it returns the associated location as a JSON string, e.g., `{"lat":51.1079,"lng":17.0385}`.
     * Otherwise, it returns `null`.
     *
     * @param guardId The identifier of the guard whose active intervention location is being requested.
     * @return A `Call<String>` object that contains the location as a JSON string if the guard has an active intervention,
     *         or `null` if there is no active or assigned intervention.
     */
    @FormUrlEncoded
    @POST("action/getActiveInterventionLocationAssignedToGuard")
    fun getActiveInterventionLocationAssignedToGuard(@Field("guardId") guardId: Int): Call<String>
}
