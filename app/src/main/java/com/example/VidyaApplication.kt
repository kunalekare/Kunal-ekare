package com.example

import android.app.Application
import android.os.StrictMode
import android.util.Log
import com.example.core.di.AppContainer
import com.example.core.di.DefaultAppContainer
import com.google.firebase.FirebaseApp

class VidyaApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "VidyaApplication.onCreate starting initialization...")

        // 1. Global Safety Net: UncaughtExceptionHandler
        setupGlobalCrashHandler()

        // 2. StrictMode in Debug to catch main-thread disk/network IO and leaks
        if (BuildConfig.DEBUG) {
            setupStrictMode()
        }

        // 3. Safe Firebase verification
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.i(TAG, "Firebase initialized successfully.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Safe Firebase initialization warning", e)
        }

        // 4. Dependency Container initialization
        appContainer = DefaultAppContainer(this)
        Log.i(TAG, "VidyaApplication.onCreate completed successfully.")
    }

    private fun setupGlobalCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e(TAG, "CRITICAL: Uncaught exception on thread ${thread.name}: ${throwable.message}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun setupStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build()
        )
    }

    companion object {
        const val TAG = "VidyaApp"
    }
}
