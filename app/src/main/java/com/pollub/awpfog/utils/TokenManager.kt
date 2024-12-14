package com.pollub.awpfog.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTDecodeException
import com.pollub.awpfog.data.models.TokenResponse
import com.pollub.awpfog.data.SharedPreferencesManager
import com.pollub.awpfog.network.NetworkClient

/**
 * Object to maintain calls for token refreshing, saving in SharedPreferences
 */
object TokenManager {

    const val TOKEN_EXPIRATION_THRESHOLD = 300L // 5min
    const val TOKEN_REFRESH_EXPIRATION_THRESHOLD = 86400 * 7 // 7 days

    /**
     * Save tokens in SharedPreferences
     *
     * @param accessToken - for REST and websockets
     * @param refreshToken - long term token to refresh accessToken
     */
    fun saveToken(accessToken: String, refreshToken: String?) {
        refreshToken?.let {
            SharedPreferencesManager.saveSecureToken(it)
        }
        SharedPreferencesManager.saveToken(accessToken)
    }

    /**
     * Get accessToken from SharedPreferences
     *
     * @return accessToken
     */
    fun getAccessToken(): String? {
        return SharedPreferencesManager.getToken()
    }

    /**
     * Get refreshToken from SharedPreferences
     *
     * @return refreshToken
     */
    fun getRefreshToken(): String? {
        return SharedPreferencesManager.getSecureToken()
    }

    /**
     *  Decode token and retrieves expiration time.
     *
     *  @param token
     *  @return timestamp of expiration time
     */
    fun getTokenExpirationTime(token: String): Long? {
        return try {
            val decodedJWT = JWT.decode(token)
            val expTime = decodedJWT.expiresAt
            expTime?.time?.div(1000)
        } catch (_: JWTDecodeException) {
            null
        }
    }

    /**
     * Check if access token is valid for less than [TOKEN_EXPIRATION_THRESHOLD]
     * @return is need to refresh token
     */
    fun isAccessTokenNearExpiration(): Boolean {
        val token = getAccessToken()
        return if (token != null) {
            val expirationTime = getTokenExpirationTime(token)
            val currentTime = System.currentTimeMillis() / 1000
            expirationTime?.let { it - currentTime <= TOKEN_EXPIRATION_THRESHOLD } == true
        } else
            true
    }

    /**
     * Check if refresh token is valid for less than [TOKEN_REFRESH_EXPIRATION_THRESHOLD]
     * @return is need to refresh token
     */
    fun isRefreshTokenNearExpiration(): Boolean {
        val token = getRefreshToken()
        return if (token != null) {
            val expirationTime = getTokenExpirationTime(token)
            val currentTime = System.currentTimeMillis() / 1000
            expirationTime?.let { it - currentTime <= TOKEN_REFRESH_EXPIRATION_THRESHOLD } == true
        } else
            true
    }

    /**
     * Check if refresh token has expired
     * @return is refresh token expired
     */
    fun isRefreshTokenExpired(): Boolean {
        val token = getRefreshToken()
        return if (token != null) {
            val expirationTime = getTokenExpirationTime(token)
            val currentTime = System.currentTimeMillis() / 1000
            expirationTime?.let { it - currentTime <= 0 } == true
        }else
            true
    }

    /**
     * Refresh RefreshToken and AuthToken, or AuthToken only
     *
     * @return [TokenResponse] or null if no token on device or refresh no needed
     */
    suspend fun refreshTokenIfNeeded(): TokenResponse? {
        val refreshToken = getRefreshToken() ?: return null
        return when {
            isRefreshTokenNearExpiration() -> refreshTokens(refreshToken, true)
            isAccessTokenNearExpiration() -> refreshTokens(refreshToken, false)
            else -> null
        }
    }

    /**
     * Refresh both or only access token.
     *
     * @param refreshToken token used to check permission to regenerate tokens
     * @param refreshBoth get both or only access token
     *
     * @return selected tokens
     */
    private suspend fun refreshTokens(refreshToken: String, refreshBoth: Boolean): TokenResponse? {

        try {
            if (refreshBoth) {
                val tokenResponse = NetworkClient.userRepository.refreshRefreshToken(refreshToken)
                if (tokenResponse != null) {
                    val (newAccessToken, newRefreshToken) = tokenResponse
                    if (newAccessToken?.isNotEmpty() == true) {
                        saveToken(
                            newAccessToken,
                            newRefreshToken
                        )
                        return TokenResponse(newAccessToken, newRefreshToken)
                    }
                }
            } else {
                val tokenResponse = NetworkClient.userRepository.refreshToken(refreshToken)
                if (tokenResponse != null) {
                    val (newAccessToken, _) = tokenResponse
                    if (newAccessToken?.isNotEmpty() == true) {
                        saveToken(
                            newAccessToken,
                            refreshToken
                        )
                        return TokenResponse(
                            newAccessToken,
                            refreshToken
                        )
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}
