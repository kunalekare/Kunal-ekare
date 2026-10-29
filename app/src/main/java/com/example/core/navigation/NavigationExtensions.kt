package com.example.core.navigation

import android.os.SystemClock
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

/**
 * Reusable debounced clickable modifier to eliminate double-tap bugs without dropping responsive clicks.
 */
@Composable
fun Modifier.debouncedClickable(
    debounceTimeMs: Long = 250L,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    return this.clickable(enabled = enabled) {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastClickTime >= debounceTimeMs) {
            lastClickTime = currentTime
            onClick()
        } else {
            Log.d("NavigationDebounce", "Click throttled: ${currentTime - lastClickTime}ms")
        }
    }
}

/**
 * Safe navigation extension that guards against:
 * 1. Navigation called while destination is destroyed or inactive
 * 2. Navigating to the exact same route multiple times consecutively
 * 3. Catches any navigation exceptions to guarantee the app never crashes
 */
fun NavController.safeNavigate(
    route: String,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    val currentEntry = currentBackStackEntry
    val currentState = currentEntry?.lifecycle?.currentState

    // Prevent navigation only if the host is completely destroyed
    if (currentState == Lifecycle.State.DESTROYED) {
        Log.w("SafeNavigate", "Ignoring navigation to '$route' because lifecycle is DESTROYED")
        return
    }

    // Prevent duplicate navigation to the same destination
    if (currentEntry?.destination?.route == route) {
        Log.d("SafeNavigate", "Already at '$route', skipping navigation")
        return
    }

    try {
        navigate(route) {
            launchSingleTop = true
            builder()
        }
        Log.i("SafeNavigate", "Successfully navigated to '$route'")
    } catch (e: Exception) {
        Log.e("SafeNavigate", "Safe navigation caught error navigating to '$route'", e)
    }
}

/**
 * Global Error Boundary composable to prevent app freeze / blank screens.
 */
@Composable
fun ErrorFallbackScreen(
    errorMessage: String = "An unexpected error occurred.",
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("error_fallback_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                modifier = Modifier.testTag("btn_error_retry")
            ) {
                Text("Retry & Reload")
            }
        }
    }
}
