package com.example.core.di

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.core.data.local.PreferencesManager
import com.example.core.data.repository.AuthRepository
import com.example.core.data.repository.FirebaseAuthRepository
import com.example.core.data.repository.FirestoreLearningRepository
import com.example.core.data.repository.FirestoreUserRepository
import com.example.core.data.repository.InMemoryLearningRepository
import com.example.core.data.repository.LearningRepository
import com.example.core.data.repository.UserRepository

interface AppContainer {
    val preferencesManager: PreferencesManager
    val learningRepository: LearningRepository
    val authRepository: AuthRepository
    val userRepository: UserRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val preferencesManager: PreferencesManager by lazy {
        PreferencesManager(context.applicationContext)
    }

    override val learningRepository: LearningRepository by lazy {
        FirestoreLearningRepository(context.applicationContext)
    }

    override val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository()
    }

    override val userRepository: UserRepository by lazy {
        FirestoreUserRepository(context.applicationContext, preferencesManager)
    }
}

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("AppContainer not provided. Ensure CompositionLocalProvider wraps your app root.")
}
