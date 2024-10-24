package com.pollub.awpfog.navigation

/**
 * Enum representing the different register screens in the application.
 */
enum class RegisterScreen {
    REGISTER_SCREEN1,
    REGISTER_SCREEN2
}

/**
 * Sealed class representing the navigation routes in the Register Screen.
 *
 * @param route The string route associated with the navigation destination.
 */
sealed class RegisterNavRoutes(val route: String) {
    object RegisterScreen1 : RegisterNavRoutes(RegisterScreen.REGISTER_SCREEN1.name)
    object RegisterScreen2 : RegisterNavRoutes(RegisterScreen.REGISTER_SCREEN2.name)
}