package com.pollub.awpfog.repository

import android.util.Log
import com.pollub.awpfog.data.ApiService
import com.pollub.awpfog.data.models.Credentials
import com.pollub.awpfog.data.models.Guard
import com.pollub.awpfog.data.models.GuardInfo
import com.pollub.awpfog.network.NetworkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Repository class for handling user-related API calls through the ApiService.
 */
class GuardRepository {
    // Instance of ApiService for making network requests
    private val apiService: ApiService = NetworkClient.instance

    /**
     * Checks if a given login is already used in the system.
     *
     * @param login The login to check.
     * @param onSuccess Callback function that receives a Boolean indicating if the login is used.
     * @param error Callback function that handles errors by receiving an error message string.
     */
    fun isLoginUsed(
        login: String,
        onSuccess: (isUsed: Boolean) -> Unit,
        error: (errorMessage: String?) -> Unit
    ) {
        apiService.isLoginUsed(login).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    response.body()?.let { isLoginUsed ->
                        onSuccess(isLoginUsed)
                    }
                } else {
                    error(response.errorBody()?.string())
                    Log.e("UserRepository.isLoginUsed", "" + response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<Boolean?>, t: Throwable) {
                Log.e("UserRepository.isLoginUsed", "" + t.message)
            }
        })
    }

    /**
     * Logs a user into the system by verifying credentials.
     *
     * @param login The user's login.
     * @param password The user's password.
     * @param callback Callback function that receives a Guard object on success, or an error message on failure.
     */
    fun login(login: String, password: String, callback: (Guard?, String?) -> Unit) {
        val credentials = Credentials(login, password)
        apiService.loginGuard(credentials).enqueue(object : Callback<Pair<String, GuardInfo>> {
            override fun onResponse(
                call: Call<Pair<String, GuardInfo>>,
                response: Response<Pair<String, GuardInfo>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { guard ->
                        if (guard.second.token != null) {
                            callback(
                                Guard.fromGuardInfo(
                                    guardInfo = guard.second,
                                    login = guard.first,
                                    password = ""
                                ), null
                            )
                        } else {
                            callback(null, "Error: No token")
                        }
                    }
                } else {
                    callback(null, response.errorBody()?.string())
                    Log.e("UserRepository.login", "" + response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<Pair<String, GuardInfo>>, t: Throwable) {
                Log.e("UserRepository.login", t.message.toString())
            }
        })
    }

    /**
     * Registers a new guard in the system.
     *
     * @param login The login to register.
     * @param password The password for the account.
     * @param guardInfo GuardInfo containing additional user details.
     * @param callback Callback function that receives a Guard object on success, or an error message on failure.
     */
    fun register(
        login: String,
        password: String,
        guardInfo: GuardInfo,
        callback: (Guard?, String?) -> Unit
    ) {

        apiService.registerGuard(
            login = login,
            password = password,
            name = guardInfo.name,
            surname = guardInfo.surname,
            email = guardInfo.email,
            phone = guardInfo.phone
        ).enqueue(object : Callback<Pair<String, GuardInfo>> {
            override fun onResponse(
                call: Call<Pair<String, GuardInfo>>,
                response: Response<Pair<String, GuardInfo>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { guard ->
                        if (guard.second.token != null) {
                            callback(
                                Guard.fromGuardInfo(
                                    guardInfo = guard.second,
                                    login = guard.first,
                                    password = ""
                                ), null
                            )
                        } else {
                            callback(null, "Error: No token")
                        }
                    }
                } else {
                    callback(null, response.errorBody()?.string())
                    Log.e("UserRepository.register", "" + response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<Pair<String, GuardInfo>>, t: Throwable) {
                Log.e("UserRepository.register", t.message.toString())
            }
        })
    }

    /**
     * Logs the user out of the system.
     *
     * @param callback Callback function that receives true on successful logout, or false with an error message.
     */
    fun logout(callback: (Boolean, String?) -> Unit) {
        apiService.logoutGuard().enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, response.errorBody()?.string())
                    Log.e("UserRepository.logout", "" + response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("UserRepository.logout", t.message.toString())
            }
        })
    }

    /**
     * Edits the details of an existing guard.
     *
     * @param id The guard's ID.
     * @param login The new login (optional).
     * @param password The current password for authentication.
     * @param newPassword The new password (optional).
     * @param name The guard's new name (optional).
     * @param surname The guard's new surname (optional).
     * @param email The guard's new email (optional).
     * @param phone The guard's new phone number (optional).
     * @param callback Callback function that receives an updated Guard object on success, or an error message on failure.
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
        callback: (Guard?, String?) -> Unit
    ) {
        apiService.editGuard(
            id = id,
            login = login,
            password = password,
            newPassword = newPassword,
            name = name,
            surname = surname,
            email = email,
            phone = phone
        ).enqueue(object : Callback<Pair<String, GuardInfo>> {
            override fun onResponse(
                call: Call<Pair<String, GuardInfo>>,
                response: Response<Pair<String, GuardInfo>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { guard ->
                        if (guard.second.token != null) {
                            callback(
                                Guard.fromGuardInfo(
                                    guardInfo = guard.second,
                                    login = guard.first,
                                    password = ""
                                ), null
                            )
                        } else {
                            callback(null, "Error: No token")
                        }
                    }
                } else {
                    callback(null, response.errorBody()?.string())
                    Log.e("UserRepository.editGuard", "" + response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<Pair<String, GuardInfo>>, t: Throwable) {
                Log.e("UserRepository.editGuard", "" + t.message)
            }
        })
    }

    /**
     * Validates the provided guard token.
     *
     * @param token The token to validate.
     * @param callback Callback function that receives a Guard object on success, or an error message on failure.
     */
    fun checkGuardToken(token: String, callback: (Guard?, String?) -> Unit) {
        apiService.checkGuardToken(token).enqueue(object : Callback<Pair<String, GuardInfo>> {
            override fun onResponse(
                call: Call<Pair<String, GuardInfo>>,
                response: Response<Pair<String, GuardInfo>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { guard ->
                        if (guard.second.token != null) {
                            callback(
                                Guard.fromGuardInfo(
                                    guardInfo = guard.second,
                                    login = guard.first,
                                    password = ""
                                ), null
                            )
                        } else {
                            callback(null, "Error: No token")
                        }
                    }
                } else {
                    Log.e("UserRepository.checkToken", "" + response.errorBody()?.string())
                    callback(null, response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<Pair<String, GuardInfo>>, t: Throwable) {
                Log.e("UserRepository.checkClientToken", t.message.toString())
            }
        })
    }

    /**
     * Sends a password reminder email to the user.
     *
     * @param email The email address associated with the account.
     * @param onSuccess Callback function that gets invoked if the email is sent successfully.
     * @param onFailure Callback function that handles errors, receiving an error message string.
     */
    fun remindPassword(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (message: String?) -> Unit
    ) {
        apiService.remindPassword(email).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(response.errorBody()?.string())
                    Log.e("UserRepository.remindPassword", "" + response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("UserRepository.checkClientToken", t.message.toString())
            }
        })
    }
    /**
     * Retrieves the location assigned to an active intervention for a guard as a JSON string.
     *
     * If retrieves the associated location as a JSON string, e.g., `{"lat":51.1079,"lng":17.0385}`, execute onSuccess
     *
     * @param guardId The identifier of the guard whose active intervention location is being requested.
     */
    fun getActiveInterventionLocationAssignedToGuard(guardId: Int, onSuccess:(location:String)->Unit,onFailure:()->Unit){
        apiService.getActiveInterventionLocationAssignedToGuard(guardId).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                println("RESPONSE")
                println(response.body())

                if (response.isSuccessful) {
                    if(response.body()!=null){
                        onSuccess(response.body()!!)
                    }else{
                        onFailure()
                    }
                }else{
                    onFailure()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("UserRepository.checkClientToken", t.message.toString())
                onFailure()
            }
        })
    }

}