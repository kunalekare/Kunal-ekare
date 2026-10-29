package com.example.core.data.repository

import android.content.Context
import com.example.R
import com.example.core.common.AppError
import com.example.core.common.Resource
import com.example.core.data.local.PreferencesManager
import com.example.core.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

interface UserRepository {
    fun observeUserProfile(userId: String): Flow<UserProfile?>
    suspend fun getUserProfile(userId: String): Resource<UserProfile?>
    suspend fun saveUserProfile(profile: UserProfile): Resource<Unit>
    suspend fun isProfileComplete(userId: String): Boolean
}

class FirestoreUserRepository(
    private val context: Context,
    private val preferencesManager: PreferencesManager,
    customFirestore: FirebaseFirestore? = null
) : UserRepository {

    private val firestore: FirebaseFirestore? by lazy {
        customFirestore ?: try {
            val dbId = context.getString(R.string.firestore_database_id)
            FirebaseFirestore.getInstance(dbId)
        } catch (e: Exception) {
            null
        }
    }

    override fun observeUserProfile(userId: String): Flow<UserProfile?> {
        val fs = firestore ?: return flow { emit(null) }
        return fs.collection("users").document(userId)
            .snapshots()
            .map { doc ->
                if (doc.exists()) doc.toObject(UserProfile::class.java) else null
            }
    }

    override suspend fun getUserProfile(userId: String): Resource<UserProfile?> {
        return try {
            val fs = firestore ?: return Resource.Success(null)
            val doc = fs.collection("users").document(userId).get().await()
            val profile = if (doc.exists()) doc.toObject(UserProfile::class.java) else null
            Resource.Success(profile)
        } catch (e: Exception) {
            Resource.Error(AppError.fromThrowable(e), e)
        }
    }

    override suspend fun saveUserProfile(profile: UserProfile): Resource<Unit> {
        return try {
            val fs = firestore
            if (fs != null) {
                val docRef = fs.collection("users").document(profile.userId)
                val data = hashMapOf(
                    "userId" to profile.userId,
                    "fullName" to profile.fullName,
                    "email" to profile.email,
                    "phoneNumber" to profile.phoneNumber,
                    "role" to profile.role.name,
                    "level" to profile.level.name,
                    "stream" to profile.stream.name,
                    "targetExam" to profile.targetExam.name,
                    "language" to profile.language.name,
                    "city" to profile.city,
                    "dateOfBirth" to profile.dateOfBirth,
                    "isMinor" to profile.isMinor,
                    "parentConsentGiven" to profile.parentConsentGiven,
                    "parentEmailOrPhone" to profile.parentEmailOrPhone,
                    "termsAccepted" to profile.termsAccepted,
                    "dailyGoalMinutes" to profile.dailyGoalMinutes,
                    "reminderTime" to profile.reminderTime,
                    "streakDays" to profile.streakDays,
                    "xpPoints" to profile.xpPoints,
                    "isProfileComplete" to true,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(data).await()
            }

            // Mirror to DataStore for offline and immediate reactive personalization
            preferencesManager.setStudentLevel(profile.level)
            preferencesManager.setSubjectStream(profile.stream)
            preferencesManager.setTargetExam(profile.targetExam)
            preferencesManager.setLanguage(profile.language)
            preferencesManager.setUserRole(profile.role)

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(AppError.fromThrowable(e), e)
        }
    }

    override suspend fun isProfileComplete(userId: String): Boolean {
        return try {
            val fs = firestore ?: return true
            val doc = fs.collection("users").document(userId).get().await()
            doc.exists() && (doc.getBoolean("isProfileComplete") == true)
        } catch (_: Exception) {
            false
        }
    }
}
