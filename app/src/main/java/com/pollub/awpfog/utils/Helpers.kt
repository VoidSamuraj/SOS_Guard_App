package com.pollub.awpfog.utils

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
