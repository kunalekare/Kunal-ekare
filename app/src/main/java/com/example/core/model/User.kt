package com.example.core.model

import com.google.firebase.firestore.IgnoreExtraProperties

enum class UserRole {
    STUDENT,
    TEACHER_ADMIN,
    SUPER_ADMIN
}

@IgnoreExtraProperties
data class UserProfile(
    val uid: String = "",
    val userId: String = uid,
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val role: UserRole = UserRole.STUDENT,
    val level: StudentLevel = StudentLevel.CLASS_12,
    val stream: SubjectStream = SubjectStream.SCIENCE_PCM,
    val targetExam: TargetExam = TargetExam.JEE_MAIN,
    val language: LanguagePreference = LanguagePreference.ENGLISH,
    val city: String = "Mumbai",
    val dateOfBirth: String = "",
    val isMinor: Boolean = false,
    val parentConsentGiven: Boolean = false,
    val parentEmailOrPhone: String = "",
    val termsAccepted: Boolean = true,
    val dailyGoalMinutes: Int = 60,
    val reminderTime: String = "19:00",
    val streakDays: Int = 1,
    val xpPoints: Int = 50,
    val isProfileComplete: Boolean = false,
    val enrolledCourseIds: List<String> = emptyList(),
    val avatarUrl: String? = null
)
