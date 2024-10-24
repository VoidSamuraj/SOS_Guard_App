package com.pollub.awpfog.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.pollub.awpfog.data.SharedPreferencesManager
import com.pollub.awpfog.data.models.GuardInfo
import com.pollub.awpfog.repository.GuardRepository

/**
 * ViewModel that manages user authentication and guard-related operations in the application.
 * It interacts with the UserRepository to perform actions like login, registration, guard information editing,
 * password reminders, and token validation. SharedPreferencesManager is used to save or clear user data.
 */
class AppViewModel : ViewModel() {
    private val userRepository = GuardRepository()

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
}