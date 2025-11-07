package com.example.everywrite.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class NotificationPermissionHelper(private val context: Context) {

    fun hasNotificationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun shouldShowRequestPermissionRationale(activity: Activity): Boolean {
        return activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
    }
}

@Composable
fun RequestNotificationPermission(
    onPermissionGranted: () -> Unit = {},
    onPermissionDenied: () -> Unit = {},
    onShowRationale: () -> Unit = {}
) {
    val context = LocalContext.current
    val permissionHelper = remember { NotificationPermissionHelper(context) }
    val activity = context as? FragmentActivity

    // Use rememberLauncherForActivityResult at the composable level, not inside SideEffect
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }
    )

    // Track if we've already requested permission to avoid infinite loops
    var hasRequestedPermission by remember { mutableStateOf(false) }

    LaunchedEffect(permissionHelper, activity) {
        if (activity != null && !hasRequestedPermission) {
            if (!permissionHelper.hasNotificationPermission()) {
                if (permissionHelper.shouldShowRequestPermissionRationale(activity)) {
                    onShowRationale()
                } else {
                    // Launch permission request
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    hasRequestedPermission = true
                }
            } else {
                onPermissionGranted()
                hasRequestedPermission = true
            }
        }
    }
}

// Alternative version with manual trigger control
@Composable
fun useNotificationPermission(
    shouldRequest: Boolean = true
): NotificationPermissionState {
    val context = LocalContext.current
    val permissionHelper = remember { NotificationPermissionHelper(context) }
    val activity = context as? FragmentActivity

    var permissionState by remember { mutableStateOf(NotificationPermissionState.IDLE) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            permissionState = if (isGranted) {
                NotificationPermissionState.GRANTED
            } else {
                NotificationPermissionState.DENIED
            }
        }
    )

    LaunchedEffect(shouldRequest, permissionHelper, activity) {
        if (shouldRequest && activity != null) {
            when {
                permissionHelper.hasNotificationPermission() -> {
                    permissionState = NotificationPermissionState.GRANTED
                }
                permissionHelper.shouldShowRequestPermissionRationale(activity) -> {
                    permissionState = NotificationPermissionState.SHOW_RATIONALE
                }
                else -> {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    return permissionState
}

// Enum to represent different permission states
enum class NotificationPermissionState {
    IDLE,
    GRANTED,
    DENIED,
    SHOW_RATIONALE
}

// Simple composable to check permission status without auto-requesting
@Composable
fun rememberNotificationPermissionState(): Boolean {
    val context = LocalContext.current
    val permissionHelper = remember { NotificationPermissionHelper(context) }

    return permissionHelper.hasNotificationPermission()
}

// Manual permission request function
@Composable
fun rememberNotificationPermissionRequest(): () -> Unit {
    val context = LocalContext.current
    val permissionHelper = remember { NotificationPermissionHelper(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { /* You can handle the result elsewhere */ }
    )

    return {
        if (!permissionHelper.hasNotificationPermission()) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}