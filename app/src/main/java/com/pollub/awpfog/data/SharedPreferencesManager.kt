package com.pollub.awpfog.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.pollub.awpfog.data.models.Guard

/**
 * Singleton object to manage user session data using SharedPreferences.
 */
object SharedPreferencesManager: SharedPreferencesManagerInterface {
    private const val PREF_NAME = "app_session"
    private const val LOGIN_ID = "login"
    private const val KEY_ID = "id"
    private const val KEY_NAME = "name"
    private const val KEY_SURNAME = "surname"
    private const val KEY_PHONE = "phone"
    private const val KEY_EMAIL = "email"
    private const val KEY_TOKEN = "token"
    private const val KEY_STATUS = "status"
    private const val KEY_LAST_REPORT_ID = "last_report_id"
    private const val SECURED_JWT = "secure_jwt"

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var encryptedPreferences: SharedPreferences

    /**
     * Initializes the SessionManager with the provided context.
     *
     * @param context The application context to access SharedPreferences.
     */
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        encryptedPreferences = EncryptedSharedPreferences.create(
            "secure_prefs",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Saves status of guard to SharedPreferences.
     *
     * @param status The [Guard.GuardStatus] representing current status.
     */
    override fun saveStatus(status: Guard.GuardStatus){
        sharedPreferences.edit()
            .putInt(KEY_STATUS, status.status)
            .apply()
    }

    /**
     * Saves user information to SharedPreferences.
     *
     * @param token The Customer access token.
     */
    override fun saveToken(token: String){
        sharedPreferences.edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }

    /**
     * Saves long term token in EncryptedSharedPreferences
     *
     * @param token The token to be saved
     */
    override  fun saveSecureToken(token: String) {
        encryptedPreferences.edit()
            .putString(SECURED_JWT, token)
            .apply()
    }

    /**
     * Get long term token from EncryptedSharedPreferences
     *
     * @return The saved token
     */
    override fun getSecureToken(): String? {
        return encryptedPreferences.getString(SECURED_JWT, null)
    }

    /**
     * Remove long term token from EncryptedSharedPreferences
     */
    override fun removeSecureToken() {
        encryptedPreferences.edit().clear().apply()
    }


    /**
     * Gets status of guard from SharedPreferences.
     *
     * @return status code of [Guard.GuardStatus] representing current status, default GuardStatus.UNAVAILABLE.
     */
    override fun getStatus():Int{
        return sharedPreferences.getInt(KEY_STATUS, Guard.GuardStatus.UNAVAILABLE.status)
    }

    /**
     * Saves Id of Report to SharedPreferences.
     *
     * @param reportId The Id of Report.
     */
    override fun saveReportId(reportId: Int){
        sharedPreferences.edit()
            .putInt(KEY_LAST_REPORT_ID, reportId)
            .apply()
    }

    /**
     * Gets Id of Report from SharedPreferences.
     *
     * @return Id of Report.
     */
    override fun getReportId():Int{
        return sharedPreferences.getInt(KEY_LAST_REPORT_ID, -1)
    }


    /**
     * Saves Guard information to SharedPreferences.
     *
     * @param guard The Guard object containing user details to save.
     */
    override fun saveGuard(guard: Guard) {
        sharedPreferences.edit()
            .putString(KEY_ID, guard.id.toString())
            .putString(LOGIN_ID, guard.login.toString())
            .putString(KEY_NAME, guard.name)
            .putString(KEY_SURNAME, guard.surname)
            .putString(KEY_PHONE, guard.phone)
            .putString(KEY_EMAIL, guard.email)
            .putString(KEY_TOKEN, guard.token)
            .apply()
    }

    /**
     * Retrieves the stored user information as a Guard object.
     *
     * @return A Guard object populated with user details from SharedPreferences.
     */
    override fun getGuard(): Guard {
        return sharedPreferences.let {
            return@let Guard(
                id = it.getString(KEY_ID, "-1").toString().toInt(),
                login = it.getString(LOGIN_ID, "-").toString(),
                password = "",
                name = it.getString(KEY_NAME, "").toString(),
                surname = it.getString(KEY_SURNAME, "").toString(),
                phone = it.getString(KEY_PHONE, "").toString(),
                email = it.getString(KEY_EMAIL, "").toString(),
                statusCode = it.getInt(KEY_STATUS, Guard.GuardStatus.UNAVAILABLE.status),
                location = "",
                account_deleted = false,
                token = it.getString(KEY_TOKEN, "")
            )
        }
    }

    /**
     * Retrieves the full name of the user by combining the first name and surname.
     *
     * @return A String containing the user's full name.
     */
    override fun getGuardName(): String {
        return sharedPreferences.getString(KEY_NAME, "") + " " + sharedPreferences.getString(
            KEY_SURNAME,
            ""
        )
    }

    /**
     * Retrieves the stored token from SharedPreferences.
     *
     * @return The token string, or null if not present.
     */
    override fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    /**
     * Clears all data from SharedPreferences.
     */
    override fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}