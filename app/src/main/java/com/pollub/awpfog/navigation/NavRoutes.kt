package com.pollub.awpfog.navigation

/**
 * Enum representing the different screens in the application.
 */
enum class Screen {
    LOGIN_SCREEN,
    REGISTER_SCREEN,
    STATUS_SCREEN,
    INTERVENTION_SCREEN,
    REMIND_PASSWORD_SCREEN,
    EDIT_GUARD_DATA_SCREEN
}

/**
 * Sealed class representing the navigation routes in the application.
 *
 * @param route The string route associated with the navigation destination.
 */
sealed class NavRoutes(val route: String) {
    object StatusScreen : NavRoutes(Screen.STATUS_SCREEN.name)
    object InterventionScreen : NavRoutes(Screen.INTERVENTION_SCREEN.name)
    object LoginScreen : NavRoutes(Screen.LOGIN_SCREEN.name)
    object RegisterScreen : NavRoutes(Screen.REGISTER_SCREEN.name)
    object RemindPasswordScreen : NavRoutes(Screen.REMIND_PASSWORD_SCREEN.name)
    object EditGuardDataScreen : NavRoutes(Screen.EDIT_GUARD_DATA_SCREEN.name)
}