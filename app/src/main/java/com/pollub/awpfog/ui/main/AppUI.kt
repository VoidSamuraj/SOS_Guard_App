package com.pollub.awpfog.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.pollub.awpfog.ui.theme.AwpfogTheme
import androidx.navigation.compose.composable
import com.pollub.awpfoc.ui.login.LoginScreen
import com.pollub.awpfoc.ui.login.RegistrationScreen
import com.pollub.awpfog.navigation.NavRoutes
import com.pollub.awpfog.ui.components.TopBar

/**
 * Composable function that defines the main user interface of the application.
 * It sets up a navigation controller and handles navigation between various screens:
 * - LoginScreen
 * - RegisterScreen
 * - StatusScreen
 * - InterventionScreen
 *
 * The UI uses the [AwpfogTheme] for theming and a [NavHost] to handle navigation between the screens.
 *
 * The navigation routes are defined using the [NavRoutes] enum.
 *
 * Functionality:
 * - Displays different screens based on the navigation route:
 *   - [LoginScreen]: For user login.
 *   - [RegistrationScreen]: For user registration.
 *   - [StatusScreen]: Displays current status and allows navigation to the intervention screen.
 *   - [InterventionScreen]: Displays intervention-related information and controls.
 */
@Composable
fun AppUI() {
    val clientId="2137"
    val navController = rememberNavController()
    AwpfogTheme(dynamicColor = false) {
        NavHost(navController = navController, startDestination = NavRoutes.LoginScreen.route) {
            composable(NavRoutes.StatusScreen.route) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopBar(
                            clientId = clientId,
                            onLogout = {
                                navController.navigate(NavRoutes.LoginScreen.route)
                            })
                    }
                ) { innerPadding ->
                    StatusScreen(
                        modifier = Modifier.padding(innerPadding),
                        onConfirmIntervention = {
                            navController.navigate(NavRoutes.InterventionScreen.route)
                        },
                        onRejectIntervention = {

                        }
                    )
                }
            }
            composable(NavRoutes.InterventionScreen.route) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopBar(
                            clientId = clientId,
                            onLogout = {
                                navController.navigate(NavRoutes.LoginScreen.route)
                            })
                    }
                ) { innerPadding ->
                    InterventionScreen(
                        modifier = Modifier.padding(innerPadding),
                        navigateToPos = {

                        },
                        confirmArrival = {

                        },
                        stopIntervention = {
                            navController.navigate(NavRoutes.StatusScreen.route)
                        },
                        callForSupport = {

                        },
                        endIntervention = {
                            navController.navigate(NavRoutes.StatusScreen.route)
                        }
                    )
                }
            }
            composable(NavRoutes.LoginScreen.route) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(
                        modifier = Modifier.padding(innerPadding),
                        onLoginPress = {
                            navController.navigate(NavRoutes.StatusScreen.route)
                        },
                        onRemindPasswordPress = {

                        },
                        navToRegister = {
                            navController.navigate(NavRoutes.RegisterScreen.route)
                        }
                    )
                }
            }
            composable(NavRoutes.RegisterScreen.route) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegistrationScreen(
                        modifier = Modifier.padding(innerPadding),
                        navToLogin = {
                            navController.navigate(NavRoutes.LoginScreen.route)
                        },
                        onSignUp = {

                        }
                    )
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun AppUIPreview() {
    AwpfogTheme(dynamicColor = false) {
        AppUI()
    }
}