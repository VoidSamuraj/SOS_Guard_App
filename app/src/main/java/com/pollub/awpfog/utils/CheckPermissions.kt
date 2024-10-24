package com.pollub.awpfog.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat

/**
 * A composable function that checks if the necessary permissions are granted.
 * If any required permissions are missing, it will request them using the provided `requestPermissionsLauncher`.
 * If all permissions are already granted, the `content` composable will be displayed.
 *
 * @param context The current context used to check the permissions.
 * @param requestPermissionsLauncher The launcher used to request permissions if they are not granted.
 * @param content The composable content to display if all permissions are granted.
 *
 * ### Permissions Checked:
 * - ACCESS_COARSE_LOCATION
 * - ACCESS_FINE_LOCATION
 * - INTERNET
 * - POST_NOTIFICATIONS
 * - FOREGROUND_SERVICE (or FOREGROUND_SERVICE_LOCATION for Android UPSIDE_DOWN_CAKE and above)
 *
 * ### Usage:
 * Call this function in your UI layer to ensure required permissions are granted before proceeding.
 * If permissions are not granted, the system permission request dialog will be shown.
 *
 * @see ActivityResultLauncher
 * @see Manifest.permission
 */
@Composable
fun CheckPermissions(context: Context, requestPermissionsLauncher:ActivityResultLauncher<Array<String>>, content:@Composable ()->Unit) {

    /**
     * Helper function that checks a specific permission and adds it to the permissions list
     * if it is not granted.
     *
     * @param permissionToCheck The permission to check (e.g., ACCESS_FINE_LOCATION).
     * @param context The context used for permission checking.
     * @param permissionsList The list to store permissions that are not granted.
     */
    fun checkPermission(
        permissionToCheck: String,
        context: Context,
        permissionsList: MutableList<String>
    ) {
        if (ContextCompat.checkSelfPermission(context, permissionToCheck)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsList.add(permissionToCheck)
        }
    }

    val permissionsToRequest = mutableListOf<String>()

    checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, context, permissionsToRequest)
    checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, context, permissionsToRequest)
    checkPermission(Manifest.permission.INTERNET, context, permissionsToRequest)
    checkPermission(Manifest.permission.POST_NOTIFICATIONS, context, permissionsToRequest)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        checkPermission(
            Manifest.permission.FOREGROUND_SERVICE_LOCATION,
            context,
            permissionsToRequest
        )
    } else {
        checkPermission(Manifest.permission.FOREGROUND_SERVICE, context, permissionsToRequest)
    }

    if (permissionsToRequest.isNotEmpty()) {
        requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
    }else{
        content()
    }
}