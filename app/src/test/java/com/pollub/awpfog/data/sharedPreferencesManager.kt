package com.pollub.awpfog.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.pollub.awpfog.data.models.Guard
import com.pollub.awpfog.data.models.Guard.GuardStatus
import kotlin.collections.set


/**
 * Mock implementation of SharedPreferencesManager for testing or non-persistent scenarios.
 * No context or shared preferences are used, it simply stores data in a mutable map.
 */
object SharedPreferencesManager : SharedPreferencesManagerInterface {
    private val dataStore: MutableMap<String, String?> = mutableMapOf()
    private val guardStatus: MutableState<GuardStatus?> = mutableStateOf(GuardStatus.UNAVAILABLE)

        private const val KEY_LAST_REPORT_ID = "last_report_id"
        private const val LOGIN_ID = "login"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_SURNAME = "surname"
        private const val KEY_PHONE = "phone"
        private const val KEY_EMAIL = "email"
        private const val KEY_TOKEN = "token"
        private const val SECURED_JWT = "secure_jwt"

    /**
     * Saves status of guard to the mock data store.
     *
     * @param status The [Guard.GuardStatus] representing current status.
     */
    override fun saveStatus(status: GuardStatus) {
        guardStatus.value = status
    }

    /**
     * Saves Guard information to SharedPreferences.
     *
     * @param guard The Guard object containing user details to save.
     */
    override fun saveGuard(guard: Guard) {
        dataStore[KEY_ID] = guard.id.toString()
        dataStore[LOGIN_ID] = guard.login.toString()
        dataStore[KEY_NAME] = guard.name
        dataStore[KEY_SURNAME] = guard.surname
        dataStore[KEY_PHONE] = guard.phone
        dataStore[KEY_EMAIL] = guard.email
        dataStore[KEY_TOKEN] = guard.token
    }

    /**
     * Retrieves the stored user information as a Guard object.
     *
     * @return A Guard object populated with user details from SharedPreferences.
     */
    override fun getGuard() = Guard(
        id = dataStore[KEY_ID]?.toInt() ?: -1,
        login = dataStore[LOGIN_ID] ?: "",
        password = "",
        name = dataStore[KEY_NAME] ?: "",
        surname = dataStore[KEY_SURNAME] ?: "",
        phone = dataStore[KEY_PHONE] ?: "",
        email = dataStore[KEY_EMAIL] ?: "",
        statusCode = guardStatus.value?.status ?: GuardStatus.UNAVAILABLE.status,
        location = "",
        account_deleted = false,
        token = dataStore[KEY_TOKEN]
    )

    /**
     * Retrieves the full name of the user by combining the first name and surname.
     *
     * @return A String containing the user's full name.
     */
    override fun getGuardName(): String {
        return "${dataStore[KEY_NAME].orEmpty()} ${dataStore[KEY_SURNAME].orEmpty()}".trim()
    }

    /**
     * Retrieves status of guard from the mock data store.
     *
     * @return [Guard.GuardStatus] representing current status.
     */
    override fun getStatus() = guardStatus.value?.status ?: GuardStatus.UNAVAILABLE.status

    /**
     * Saves Id of Report to SharedPreferences.
     *
     * @param reportId The Id of Report.
     */
    override fun saveReportId(reportId: Int) {
        dataStore[KEY_LAST_REPORT_ID] = reportId.toString()
    }

    /**
     * Gets Id of Report from SharedPreferences.
     *
     * @return Id of Report.
     */
    override fun getReportId(): Int {
        return dataStore[KEY_LAST_REPORT_ID]?.toInt() ?: -1
    }

    /**
     * Saves user token to the mock data store.
     *
     * @param token The Customer access token.
     */
    override fun saveToken(token: String) {
        dataStore[KEY_TOKEN] = token
    }

    /**
     * Saves long-term token in the mock data store.
     *
     * @param token The token to be saved.
     */
    override fun saveSecureToken(token: String) {
        dataStore[SECURED_JWT] = token
    }

    /**
     * Retrieves the stored long-term token from the mock data store.
     *
     * @return The stored token or null if not found.
     */
    override fun getSecureToken(): String? {
        return dataStore[SECURED_JWT]
    }

    override fun removeSecureToken() {
        dataStore.clear()
    }


    /**
     * Retrieves the stored token from the mock data store.
     *
     * @return The token string, or null if not present.
     */
    override fun getToken(): String? {
        return dataStore[KEY_TOKEN]
    }

    /**
     * Clears all data from the mock data store.
     */
    override fun clear() {
        dataStore.clear()
    }
}
