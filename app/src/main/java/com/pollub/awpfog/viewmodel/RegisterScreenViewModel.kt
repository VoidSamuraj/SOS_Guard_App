package com.pollub.awpfog.viewmodel

import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

/**
 * ViewModel responsible for managing the state of the registration screen.
 * Holds the user's registration input data, such as login, password, name, surname, email, phone, and PESEL.
 * It also provides a method to clear all the fields.
 */
class RegisterScreenViewModel : ViewModel() {
    var login by mutableStateOf("")
    var password by mutableStateOf("")
    var name by mutableStateOf("")
    var surname by mutableStateOf("")
    var email by mutableStateOf("")
    var phone by mutableStateOf("")
    var pesel by mutableStateOf("")

    /**
     * Clears all the fields by resetting them to empty strings.
     * This can be used when the user cancels or successfully completes the registration.
     */
    fun clearAllFields() {
        login = ""
        password = ""
        name = ""
        surname = ""
        email = ""
        phone = ""
        pesel = ""
    }
}