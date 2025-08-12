package com.example.wifimeasure

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay

@Composable
fun PermissionRequestScreen() {
    val context = LocalContext.current

    val permissions = remember {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    val retryRequest = remember { mutableStateOf(false) }
    val deniedCount = remember { mutableStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val allGranted = result.values.all { it }

        if (!allGranted) {
            Toast.makeText(
                context,
                "Wi-Fi scan permissions are required for app functionality.",
                Toast.LENGTH_LONG
            ).show()

            deniedCount.value += 1

            if (deniedCount.value >= 3) {
                retryRequest.value = false
                // Open app settings to manually enable permissions
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
            else {
                // Trigger retry through state
                retryRequest.value = true
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(300) // Give UI time to draw
        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) {
            permissionLauncher.launch(permissions)
        }
    }

    // Retry request triggered by state
    LaunchedEffect(retryRequest.value) {
        if (retryRequest.value) {
            retryRequest.value = false
            permissionLauncher.launch(permissions)
        }
    }
}
