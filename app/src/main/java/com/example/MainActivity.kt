package com.example

import android.os.Bundle
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.core.designsystem.VidyaSetuTheme
import com.example.core.di.DefaultAppContainer
import com.example.core.di.LocalAppContainer
import com.example.core.navigation.Screen
import com.example.core.navigation.VidyaBottomNavBar
import com.example.core.navigation.VidyaNavHost

class MainActivity : ComponentActivity() {

    private lateinit var appContainer: DefaultAppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = DefaultAppContainer(applicationContext)

        enableEdgeToEdge()

        setContent {
            CompositionLocalProvider(LocalAppContainer provides appContainer) {
                VidyaSetuTheme {
                    VidyaAppRoot()
                }
            }
        }
    }
}

@Composable
fun VidyaAppRoot() {
    val container = LocalAppContainer.current

    val isOnboardingDone by container.preferencesManager.isOnboardingCompletedFlow
        .collectAsState(initial = null)
    val isProfileDone by container.preferencesManager.isProfileCompletedFlow
        .collectAsState(initial = null)
    val currentUser by container.authRepository.authStateFlow
        .collectAsState(initial = container.authRepository.currentUser)

    // Wait until preferences are read on cold start
    if (isOnboardingDone == null || isProfileDone == null) {
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

    val startDestination = when {
        isOnboardingDone == false -> Screen.ROUTE_ONBOARDING
        currentUser == null -> Screen.ROUTE_AUTH
        isProfileDone == false -> Screen.ROUTE_PROFILE_SETUP
        else -> Screen.Home.route
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: startDestination

    // Only show bottom navigation on the 5 primary tabs
    val showBottomBar = Screen.bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                VidyaBottomNavBar(
                    currentRoute = currentRoute,
                    onNavigateToRoute = { route ->
                        navController.navigate(route) {
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
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
