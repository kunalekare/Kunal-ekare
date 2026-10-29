package com.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.core.designsystem.VidyaSetuTheme
import com.example.core.di.AppContainer
import com.example.core.di.LocalAppContainer
import com.example.core.navigation.Screen
import com.example.core.navigation.VidyaBottomNavBar
import com.example.core.navigation.VidyaNavHost
import com.example.core.navigation.safeNavigate
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MainActivity", "MainActivity.onCreate called")

        appContainer = (application as? VidyaApplication)?.appContainer
            ?: (applicationContext as? VidyaApplication)?.appContainer
            ?: com.example.core.di.DefaultAppContainer(applicationContext)

        enableEdgeToEdge()

        setContent {
            CompositionLocalProvider(LocalAppContainer provides appContainer) {
                VidyaSetuTheme {
                    var crashError by remember { mutableStateOf<String?>(null) }
                    if (crashError != null) {
                        com.example.core.navigation.ErrorFallbackScreen(
                            errorMessage = crashError ?: "An unexpected error occurred.",
                            onRetry = { crashError = null }
                        )
                    } else {
                        VidyaAppRoot(onError = { error -> crashError = error })
                    }
                }
            }
        }
    }
}

@Composable
fun VidyaAppRoot(onError: (String) -> Unit = {}) {
    val container = LocalAppContainer.current

    val isOnboardingDone by container.preferencesManager.isOnboardingCompletedFlow
        .collectAsState(initial = null)

    // Fast, flicker-free startup resolution (max 300ms cold start)
    var startRoute by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isOnboardingDone) {
        if (startRoute == null && isOnboardingDone != null) {
            startRoute = if (isOnboardingDone == false) Screen.ROUTE_ONBOARDING else Screen.Home.route
        }
    }

    LaunchedEffect(Unit) {
        delay(300L)
        if (startRoute == null) {
            startRoute = Screen.Home.route
        }
    }

    val resolvedStartRoute = startRoute
    if (resolvedStartRoute == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    Log.i("VidyaAppRoot", "Start destination resolved cleanly: $resolvedStartRoute")

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: resolvedStartRoute

    // Only show bottom navigation on the 5 primary tabs
    val showBottomBar = Screen.bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                VidyaBottomNavBar(
                    currentRoute = currentRoute,
                    onNavigateToRoute = { route ->
                        navController.safeNavigate(route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        VidyaNavHost(
            navController = navController,
            startDestination = resolvedStartRoute,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
