package com.pollub.awpfog.viewmodel

import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.gson.JsonParser
import com.mapbox.geojson.Point
import com.pollub.awpfog.MainActivity
import com.pollub.awpfog.data.SharedPreferencesManager
import com.pollub.awpfog.data.models.Guard
import com.pollub.awpfog.data.models.GuardInfo
import com.pollub.awpfog.network.NetworkClient.WebSocketManager
import com.pollub.awpfog.repository.GuardRepository
import com.pollub.awpfog.service.LocationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel that manages user authentication and guard-related operations in the application.
 * It interacts with the UserRepository to perform actions like login, registration, guard information editing,
 * password reminders, and token validation. SharedPreferencesManager is used to save or clear user data.
 */
class AppViewModel : ViewModel() {
    private val userRepository = GuardRepository()

    var isInterventionVisible = mutableStateOf(false)

    val reportLocation = mutableStateOf(Point.fromLngLat (0.0, 0.0))

    var patrolStatusEnum = mutableStateOf(SharedPreferencesManager.getStatus())

    fun isPatrolActive(): Boolean {
        return patrolStatusEnum.value == Guard.GuardStatus.INTERVENTION.status || patrolStatusEnum.value == Guard.GuardStatus.AVAILABLE.status
    }

    fun isPatrolActive(guardStatus: Int): Boolean {
        return guardStatus == Guard.GuardStatus.INTERVENTION.status || guardStatus == Guard.GuardStatus.AVAILABLE.status
    }

    var connectionStatus = mutableStateOf(false)

    fun setIsSystemConnected(activity: MainActivity) {
        connectionStatus.value = isForegroundServiceRunning(activity)
    }

    fun getIsSystemConnecting() = WebSocketManager.isConnecting

    fun connectIfNotConnected(activity: MainActivity) {
        if (!isForegroundServiceRunning(activity)) {
            val locationServiceIntent = Intent(activity, LocationService::class.java)
            WebSocketManager.setOnConnect { connectionStatus.value = true }
            activity.startService(locationServiceIntent)
        }
    }

    var isDialogVisible = mutableStateOf(false)

    fun onWarning() {
            isDialogVisible.value = true
            isInterventionVisible.value = false
            patrolStatusEnum.value = Guard.GuardStatus.NOT_RESPONDING.status
            SharedPreferencesManager.saveStatus(Guard.GuardStatus.NOT_RESPONDING)
    }
    fun onInterventionCancelledByUser(){
            isInterventionVisible.value = false
            patrolStatusEnum.value = Guard.GuardStatus.UNAVAILABLE.status
            SharedPreferencesManager.saveStatus(Guard.GuardStatus.UNAVAILABLE)
    }

    fun askIfReportActive(isActive: MutableState<Boolean>){
        CoroutineScope(Dispatchers.IO).launch {
            repeat(6) {
                if (!isActive.value) {
                    return@launch
                }

                WebSocketManager.sendMessage("""{"ask": isActive, "reportId": ${SharedPreferencesManager.getReportId()}}""")
                delay(5000)
            }
        }
    }

    fun clearReport() {
        isInterventionVisible.value = false
        reportLocation.value = Point.fromLngLat(0.0, 0.0)
    }

    fun sendStatusChange(guardId: Int, status: Guard.GuardStatus) {
        SharedPreferencesManager.saveStatus(status)
        patrolStatusEnum.value = status.status
        WebSocketManager.sendMessage("""{"guardId": $guardId, "status": ${status.status}}""")
    }

    fun sendStatusChange(status: Guard.GuardStatus) {
        SharedPreferencesManager.saveStatus(status)
        patrolStatusEnum.value = status.status
        WebSocketManager.sendMessage("""{"guardId": ${SharedPreferencesManager.getGuard().id}, "status": ${status.status}}""")
    }


    /**
     * Checks if the provided login is not already in use.
     *
     * @param login The login string to be checked.
     * @param onSuccess Callback function to be executed if the login is not in use.
     * @param onFailure Callback function to be executed with an error message if the login is already used or if an error occurs.
     */
    fun isLoginNotUsed(login: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        userRepository.isLoginUsed(login, onSuccess = { isUsed ->
            if (isUsed)
                onFailure("Login is already used")
            else
                onSuccess()
        },
            error = { error ->
                error?.let {
                    onFailure(error)
                }
                Log.e("AuthViewModel", error ?: "Unknown error")
            })
    }

    /**
     * Logs in a user with the provided login and password.
     *
     * @param login The login string.
     * @param password The password string.
     * @param onSuccess Callback function to be executed on successful login.
     * @param onFailure Callback function to be executed with an error message if login fails.
     */
    fun login(
        login: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        userRepository.login(login, password) { guard, error ->
            if (guard != null) {
                SharedPreferencesManager.saveGuard(guard)
                onSuccess()
            } else {
                error?.let {
                    onFailure(error)
                }
                Log.e("AuthViewModel", error ?: "Unknown error")
            }
        }
    }

    /**
     * Logs out the currently logged-in user.
     *
     * @param onSuccess Callback function to be executed on successful logout.
     * @param onFailure Callback function to be executed with an error message if logout fails.
     */
    fun logout(onSuccess: () -> Unit, onFailure: (message: String) -> Unit) {
        userRepository.logout { success, error ->
            if (success) {
                SharedPreferencesManager.clear()
                onSuccess()
            } else {
                error?.let {
                    onFailure(error)
                }
                Log.e("AuthViewModel", error ?: "Unknown error")
            }
        }
    }

    /**
     * Registers a new guard with the provided login, password, and guard information.
     *
     * @param login The login string for the new guard.
     * @param password The password string for the new guard.
     * @param guard The GuardInfo object containing guard details.
     * @param onSuccess Callback function to be executed on successful registration.
     * @param onFailure Callback function to be executed with an error message if registration fails.
     */
    fun register(
        login: String,
        password: String,
        guard: GuardInfo,
        onSuccess: () -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        userRepository.register(login, password, guard) { guard, error ->
            if (guard != null) {
                SharedPreferencesManager.saveGuard(guard)
                onSuccess()
            } else {
                error?.let {
                    onFailure(error)
                }
                Log.e("AuthViewModel", error ?: "Unknown error")
            }
        }
    }

    /**
     * Edits guard information based on the provided parameters.
     *
     * @param id The guard's ID.
     * @param login Optional new login string.
     * @param password Current password string.
     * @param newPassword Optional new password string.
     * @param name Optional new first name string.
     * @param surname Optional new surname string.
     * @param email Optional new email address string.
     * @param phone Optional new phone number string.
     * @param onSuccess Callback function to be executed on successful update.
     * @param onFailure Callback function to be executed with an error message if the update fails.
     */
    fun editGuard(
        id: Int,
        login: String?,
        password: String,
        newPassword: String?,
        name: String?,
        surname: String?,
        email: String?,
        phone: String?,
        onSuccess: () -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        userRepository.editGuard(
            id = id,
            login = login,
            password = password,
            newPassword = newPassword,
            name = name,
            surname = surname,
            email = email,
            phone = phone,
            callback = { guard, error ->
                if (guard != null) {
                    SharedPreferencesManager.saveGuard(guard)
                    onSuccess()
                } else {
                    error?.let {
                        onFailure(error)
                    }
                    Log.e("AuthViewModel", error ?: "Unknown error")
                }
            }
        )
    }

    /**
     * Validates the guard's token and retrieves guard information if the token is valid.
     *
     * @param token The guard token string to be validated.
     * @param onSuccess Callback function to be executed on successful validation.
     * @param onFailure Callback function to be executed with an error message if validation fails.
     */
    fun checkGuardToken(
        token: String,
        onSuccess: () -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        userRepository.checkGuardToken(token) { guard, error ->
            if (guard != null) {
                SharedPreferencesManager.saveGuard(guard)
                onSuccess()
            } else {
                error?.let {
                    onFailure(error)
                }
                Log.e("AuthViewModel", error ?: "Unknown error")
            }
        }
    }

    /**
     * Sends a password reminder to the specified email.
     *
     * @param email The email address to send the password reminder to.
     * @param onSuccess Callback function to be executed on successful reminder.
     * @param onFailure Callback function to be executed with an error message if the reminder fails.
     */
    fun remindPassword(email: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        userRepository.remindPassword(email, onSuccess = onSuccess, onFailure = { error ->
            error?.let {
                onFailure(error)
            }
            Log.e("AuthViewModel", error ?: "Unknown error")
        })
    }

    private fun isForegroundServiceRunning(context: Context): Boolean {
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val activeNotifications = notificationManager.activeNotifications
        return activeNotifications.any { it.id == LocationService.NOTIFICATION_ID }
    }

    fun getActiveInterventionLocationAssignedToGuard(
        guardId: Int,
        onSuccess: (point: Point) -> Unit,
        onFailure: () -> Unit
    ) {

        userRepository.getActiveInterventionLocationAssignedToGuard(
            guardId, onSuccess = { location ->
                if (location.isNotEmpty()) {
                    val jsonObject = JsonParser.parseString(location).asJsonObject
                    if (jsonObject.has("reportId")) {
                        SharedPreferencesManager.saveReportId(jsonObject.get("reportId").asInt)
                    }
                    if (jsonObject.has("lat") && jsonObject.has("lng")) {
                        onSuccess(
                            Point.fromLngLat(
                                jsonObject.get("lng").asDouble,
                                jsonObject.get("lat").asDouble
                            )
                        )
                    } else {
                        onFailure()
                    }
                } else {
                    onFailure()
                }

            },
            onFailure = { onFailure() }
        )
    }

    fun confirmIntervention() {
        WebSocketManager.sendMessage("""{"intervention": accept }""")
    }

    fun rejectIntervention() {
        WebSocketManager.sendMessage("""{"intervention": cancel }""")
    }

    fun confirmInterventionArrival() {
        WebSocketManager.sendMessage("""{"reportId":${SharedPreferencesManager.getReportId()}, "intervention": confirmArrival }""")
    }

    fun finishIntervention() {
        clearReport()
        WebSocketManager.sendMessage("""{"reportId":${SharedPreferencesManager.getReportId()}, "intervention": finish }""")
    }

    fun cancelStartedIntervention() {
        clearReport()
        WebSocketManager.sendMessage("""{"reportId":${SharedPreferencesManager.getReportId()}, "intervention": cancelStarted }""")
    }


    fun callForSupport() {
        WebSocketManager.sendMessage("""{"reportId":${SharedPreferencesManager.getReportId()}, "intervention": supportNeeded }""")
    }

}