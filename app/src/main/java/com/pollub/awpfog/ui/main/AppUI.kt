package com.pollub.awpfog.ui.main

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.pollub.awpfog.ui.theme.AwpfogTheme
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.pollub.awpfog.MainActivity
import com.pollub.awpfog.ui.login.LoginScreen
import com.pollub.awpfog.utils.CustomSnackBar
import com.pollub.awpfog.navigation.NavRoutes
import com.pollub.awpfog.ui.components.TopBar
import com.pollub.awpfog.R
import com.pollub.awpfog.data.SharedPreferencesManager
import com.pollub.awpfog.data.models.Guard
import com.pollub.awpfog.data.models.GuardInfo
import com.pollub.awpfog.navigation.RegisterNavRoutes
import com.pollub.awpfog.network.NetworkClient.WebSocketManager
import com.pollub.awpfog.service.LocationService
import com.pollub.awpfog.ui.components.EditGuardDataScreen
import com.pollub.awpfog.ui.login.RegistrationScreen
import com.pollub.awpfog.ui.login.RegistrationScreenPersonalInformation
import com.pollub.awpfog.ui.login.RemindPasswordScreen
import com.pollub.awpfog.utils.TokenManager
import com.pollub.awpfog.viewmodel.AppViewModel
import com.pollub.awpfog.viewmodel.RegisterScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

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
 * @param mainActivity The MainActivity to use functionality requiring app context.
 * @param viewModel The AppViewModel to interact with data layer.
 *
 * Functionality:
 * - Displays different screens based on the navigation route:
 *   - [LoginScreen]: For Guard login.
 *   - [RegistrationScreen]: For Guard registration.
 *   - [StatusScreen]: Displays current status and allows navigation to the intervention screen.
 *   - [InterventionScreen]: Displays intervention-related information and controls.
 */
@Composable
fun AppUI(
    mainActivity: MainActivity,
    viewModel: AppViewModel,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val density = LocalDensity.current.density
    val screenWidthPx = screenWidth.value * density

    val locationServiceIntent = Intent(mainActivity, LocationService::class.java)

    var isNavigatingToLogin by remember { mutableStateOf(false) }
    var isNavigatingToHome by remember { mutableStateOf(false) }

    val isSnackBarVisible = remember { mutableStateOf(false) }
    val snackBarMessage = remember { mutableStateOf("") }
    val snackBarIcon = remember { mutableIntStateOf(R.drawable.baseline_error_outline_24) }

    var defaultColor: Color = MaterialTheme.colorScheme.error
    val snackBarColor = remember { mutableStateOf(defaultColor) }

    val registerScreenViewModel: RegisterScreenViewModel = viewModel()
    val navController = rememberNavController()

    var isLocationTurnedOn = rememberLocationState(mainActivity)

    WebSocketManager.setOnInterventionCancelled {
        navController.navigate(NavRoutes.StatusScreen.route) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
            restoreState = false
        }
    }

    AwpfogTheme(dynamicColor = false) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHost(navController = navController, startDestination = NavRoutes.LoginScreen.route) {
                composable(NavRoutes.StatusScreen.route) {

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopBar(
                                clientName = SharedPreferencesManager.getGuardName(),
                                iconId = R.drawable.baseline_account_circle_24,
                                onIconClick = {
                                    navController.navigate(NavRoutes.EditGuardDataScreen.route)
                                },
                                onLogout = {
                                    viewModel.logout(
                                        onSuccess = {
                                            WebSocketManager.setCloseCode(4000)
                                            mainActivity.stopService(locationServiceIntent)
                                            navController.navigate(NavRoutes.LoginScreen.route) {
                                                popUpTo(0) { inclusive = true }
                                                launchSingleTop = true
                                                restoreState = false
                                            }
                                        },
                                        onFailure = { message ->
                                            snackBarMessage.value = message
                                            isSnackBarVisible.value = true
                                        })
                                })
                        }
                    ) { innerPadding ->
                        LaunchedEffect(true) {
                            viewModel.setIsSystemConnected(mainActivity)
                            viewModel.getActiveInterventionLocationAssignedToGuard(
                                guardId = SharedPreferencesManager.getGuard().id,
                                onSuccess = { location ->
                                    viewModel.reportLocation.value = location
                                    SharedPreferencesManager.saveStatus(Guard.GuardStatus.INTERVENTION)
                                    viewModel.patrolStatusEnum.value =
                                        Guard.GuardStatus.INTERVENTION.status
                                    navController.navigate(NavRoutes.InterventionScreen.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                        restoreState = false
                                    }
                                },
                                onFailure = {
                                    //prevent changing not_responding to unavailable
                                    if (viewModel.isPatrolActive(SharedPreferencesManager.getStatus())) {
                                        viewModel.patrolStatusEnum.value =
                                            Guard.GuardStatus.UNAVAILABLE.status
                                        SharedPreferencesManager.saveStatus(Guard.GuardStatus.UNAVAILABLE)
                                    }
                                })

                        }
                        StatusScreen(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = viewModel,
                            onConnectionButtonClick = { wasConnectedBefore, onSuccess ->
                                if (wasConnectedBefore) {
                                    WebSocketManager.setCloseCode(4000)
                                    mainActivity.stopService(locationServiceIntent)
                                } else {
                                    WebSocketManager.setOnConnect { onSuccess() }
                                    mainActivity.startService(locationServiceIntent)
                                }
                            },
                            onConfirmIntervention = {
                                viewModel.confirmIntervention(){
                                    navController.navigate(NavRoutes.InterventionScreen.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                        restoreState = false
                                    }
                                }
                            },
                            onRejectIntervention = {
                                viewModel.rejectIntervention()
                            }
                        )
                    }
                }
                composable(NavRoutes.InterventionScreen.route) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopBar(
                                clientName = SharedPreferencesManager.getGuardName(),
                                iconId = R.drawable.baseline_account_circle_24,
                                onIconClick = {
                                    navController.navigate(NavRoutes.EditGuardDataScreen.route)
                                },
                                onLogout = {
                                    viewModel.logout(
                                        onSuccess = {
                                            WebSocketManager.setCloseCode(4000)
                                            mainActivity.stopService(locationServiceIntent)
                                            navController.navigate(NavRoutes.LoginScreen.route) {
                                                popUpTo(0) { inclusive = true }
                                                launchSingleTop = true
                                                restoreState = false
                                            }
                                        },
                                        onFailure = { message ->
                                            snackBarMessage.value = message
                                            isSnackBarVisible.value = true
                                        })
                                })
                        }
                    ) { innerPadding ->
                        viewModel.connectIfNotConnected(mainActivity)
                        InterventionScreen(
                            modifier = Modifier.padding(innerPadding),
                            context = mainActivity,
                            viewModel = viewModel,
                            confirmArrival = {
                                viewModel.confirmInterventionArrival()
                                viewModel.isInterventionVisible.value = false
                            },
                            stopIntervention = {
                                viewModel.cancelStartedIntervention()
                                viewModel.isInterventionVisible.value = false
                                viewModel.sendStatusChange(Guard.GuardStatus.AVAILABLE)
                                navController.navigate(NavRoutes.StatusScreen.route) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            },
                            callForSupport = {
                                viewModel.isInterventionVisible.value = false
                                viewModel.callForSupport()
                            },
                            endIntervention = {
                                viewModel.finishIntervention()
                                viewModel.isInterventionVisible.value = false
                                viewModel.sendStatusChange(Guard.GuardStatus.AVAILABLE)
                                navController.navigate(NavRoutes.StatusScreen.route) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            }
                        )
                    }
                }
                composable(NavRoutes.LoginScreen.route) {
                    val token = SharedPreferencesManager.getToken()
                    LaunchedEffect(token) {
                        if (token != null) {
                            viewModel.checkGuardToken(token,
                                onSuccess = {
                                    navController.navigate(NavRoutes.StatusScreen.route) {
                                        popUpTo(NavRoutes.LoginScreen.route) { inclusive = true }
                                    }
                                },
                                onFailure = { message ->
                                    val securedToken = SharedPreferencesManager.getSecureToken()
                                    if (securedToken != null) {
                                        viewModel.checkGuardToken(securedToken,
                                            onSuccess = {
                                                runBlocking {
                                                    TokenManager.refreshTokenIfNeeded()
                                                    navController.navigate(NavRoutes.StatusScreen.route) {
                                                        popUpTo(NavRoutes.LoginScreen.route) {
                                                            inclusive = true
                                                        }
                                                    }
                                                }

                                            },
                                            onFailure = {
                                                snackBarMessage.value = "Sesja wygasła"
                                                isSnackBarVisible.value = true
                                            }
                                        )
                                    }
                                })
                        }
                    }
                    LoginScreen(
                        modifier = Modifier.padding(innerPadding),
                        onLoginPress = { login, password ->
                            viewModel.login(login = login, password,
                                onSuccess = {
                                navController.navigate(NavRoutes.StatusScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                                },
                                onFailure = { message ->
                                    snackBarMessage.value = message
                                    isSnackBarVisible.value = true
                                })
                        },
                        onRemindPasswordPress = {
                            navController.navigate(NavRoutes.RemindPasswordScreen.route)
                        },
                        navToRegister = {
                            navController.navigate(NavRoutes.RegisterScreen.route)
                        }
                    )
                }
                navigation(
                    startDestination = RegisterNavRoutes.RegisterScreen1.route,
                    route = NavRoutes.RegisterScreen.route
                ) {
                    composable(
                        RegisterNavRoutes.RegisterScreen1.route,
                        enterTransition = {
                            isNavigatingToLogin = false
                            fadeIn(animationSpec = tween(300))
                        },
                        popEnterTransition = {
                            slideInHorizontally(
                                initialOffsetX = { -screenWidthPx.toInt() },
                                animationSpec = tween(500)
                            )
                        },
                        exitTransition = {
                            if (!isNavigatingToLogin)
                                slideOutHorizontally(
                                    targetOffsetX = { -screenWidthPx.toInt() },
                                    animationSpec = tween(500)
                                )
                            else
                                fadeOut(animationSpec = tween(300))
                        }
                    ) {
                        RegistrationScreen(
                            modifier = Modifier.padding(innerPadding),
                            registerScreenViewModel = registerScreenViewModel,
                            navToLogin = {
                                isNavigatingToLogin = true
                                navController.popBackStack()
                            },
                            navToNextScreen = {
                                viewModel.isLoginNotUsed(
                                    registerScreenViewModel.login,
                                    onSuccess = {
                                        navController.navigate(RegisterNavRoutes.RegisterScreen2.route)
                                    }, onFailure = { message ->
                                        snackBarMessage.value = message
                                        isSnackBarVisible.value = true
                                    })
                            }
                        )
                    }
                    composable(
                        RegisterNavRoutes.RegisterScreen2.route,
                        enterTransition = {
                            isNavigatingToHome = false
                            slideInHorizontally(
                                initialOffsetX = { screenWidthPx.toInt() },
                                animationSpec = tween(500)
                            )
                        },
                        exitTransition = {
                            if (!isNavigatingToHome)
                                slideOutHorizontally(
                                    targetOffsetX = { screenWidthPx.toInt() },
                                    animationSpec = tween(500)
                                )
                            else
                                fadeOut(animationSpec = tween(300))
                        }
                    ) { backStackEntry ->
                        RegistrationScreenPersonalInformation(
                            modifier = Modifier.padding(innerPadding),
                            registerScreenViewModel = registerScreenViewModel,
                            navBack = {
                                navController.popBackStack()
                            },
                            onSignUp = { guardInfo: GuardInfo ->
                                viewModel.register(login = registerScreenViewModel.login,
                                    password = registerScreenViewModel.password,
                                    guard = guardInfo,
                                    onSuccess = {
                                        registerScreenViewModel.clearAllFields()
                                        isNavigatingToHome = true
                                        navController.navigate(NavRoutes.StatusScreen.route) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                        }
                                    },
                                    onFailure = { message ->
                                        snackBarMessage.value = message
                                        isSnackBarVisible.value = true
                                    })

                            }
                        )
                    }
                    composable(NavRoutes.RemindPasswordScreen.route) {
                        RemindPasswordScreen(
                            modifier = Modifier.padding(innerPadding),
                            navBack = {
                                navController.popBackStack()
                            },
                            onSendPress = { email ->
                                viewModel.remindPassword(email, onSuccess = {
                                    snackBarMessage.value = "Email został wysłany na podany adres"
                                    snackBarColor.value = Color(0xFF5FBF2F)
                                    snackBarIcon.intValue =
                                        R.drawable.outline_check_circle_outline_24
                                    isSnackBarVisible.value = true
                                    navController.popBackStack()
                                },
                                    onFailure = { message ->
                                        snackBarMessage.value = message
                                        isSnackBarVisible.value = true
                                    })
                            })
                    }
                    composable(NavRoutes.EditGuardDataScreen.route) {
                        Scaffold(modifier = Modifier.fillMaxSize(),
                            topBar = {
                                TopBar(
                                    clientName = SharedPreferencesManager.getGuardName(),
                                    iconId = R.drawable.baseline_arrow_back_24,
                                    onIconClick = {
                                        navController.navigate(NavRoutes.StatusScreen.route)
                                    },
                                    onLogout = {
                                        viewModel.logout(onSuccess = {
                                            navController.navigate(NavRoutes.LoginScreen.route) {
                                                popUpTo(0) { inclusive = true }
                                                launchSingleTop = true
                                                restoreState = false
                                            }
                                        },
                                            onFailure = { message ->
                                                snackBarMessage.value = message
                                                isSnackBarVisible.value = true
                                            })
                                    })
                            }
                        ) { innerPadding ->
                            val customer = SharedPreferencesManager.getGuard()
                            EditGuardDataScreen(
                                modifier = Modifier.padding(innerPadding),
                                customer,
                                onSavePress = { login, password, newPassword, name, surname, email, phone ->
                                    viewModel.editGuard(id = customer.id, login = login,
                                        password = password,
                                        newPassword = newPassword,
                                        name = name,
                                        surname = surname,
                                        email = email,
                                        phone = phone,
                                        onSuccess = {
                                            snackBarMessage.value =
                                                "Użytkownik został zaktualizowany na podany adres"
                                            snackBarColor.value = Color(0xFF5FBF2F)
                                            snackBarIcon.intValue =
                                                R.drawable.outline_check_circle_outline_24
                                            isSnackBarVisible.value = true
                                            navController.popBackStack()
                                        },
                                        onFailure = { message ->
                                            snackBarMessage.value = message
                                            isSnackBarVisible.value = true
                                        })
                                })
                        }
                    }
                }
            }
            if (viewModel.isDialogVisible.value)
                AlertDialog(
                    onDismissRequest = { },
                    title = { Text("Uwaga !!!") },
                    text = { Text("Z powodu braku reakcji na wezwanie, twój status zmienił się na nieaktywny") },
                    confirmButton = {
                        Button(modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            onClick = { viewModel.isDialogVisible.value = false }) {
                            Text("OK", color = MaterialTheme.colorScheme.onSecondary)
                        }
                    }
                )

            if (isSnackBarVisible.value || !isLocationTurnedOn.value) {
                if (!isLocationTurnedOn.value) {
                    snackBarMessage.value = "Aby kożystać z systemu musisz włączyć lokalizację!"
                }
                CustomSnackBar(
                    modifier = Modifier.padding(innerPadding),
                    message = snackBarMessage.value,
                    backgroundColor = snackBarColor.value,
                    iconResId = snackBarIcon.intValue
                ) {
                    if (isLocationTurnedOn.value) {
                        isSnackBarVisible.value = false
                        snackBarColor.value = defaultColor
                        snackBarIcon.intValue = R.drawable.baseline_error_outline_24
                    }
                }
            }
        }

    }
}

/**
 * Function to check if location is enabled in device
 *
 * @param context [Context]app context
 *
 * @return [MutableState] of boolean representing if location is turned on
 */
@Composable
fun rememberLocationState(context: Context): MutableState<Boolean> {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isLocationEnabled = remember { mutableStateOf(checkLocationEnabled(locationManager)) }

    LaunchedEffect(Unit) {
        while (true) {
            val currentStatus = checkLocationEnabled(locationManager)
            if (isLocationEnabled.value != currentStatus) {
                isLocationEnabled.value = currentStatus
            }
            delay(1000L)
        }
    }

    return isLocationEnabled
}

private fun checkLocationEnabled(locationManager: LocationManager): Boolean {
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}