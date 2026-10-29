package com.example.core.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.core.model.LanguagePreference
import com.example.core.model.StudentLevel
import com.example.core.model.SubjectStream
import com.example.core.model.TargetExam
import com.example.core.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vidyasetu_prefs")

class PreferencesManager(private val context: Context) {

    private object PreferencesKeys {
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_PROFILE_COMPLETED = booleanPreferencesKey("profile_completed")
        val KEY_STUDENT_LEVEL = stringPreferencesKey("student_level")
        val KEY_SUBJECT_STREAM = stringPreferencesKey("subject_stream")
        val KEY_TARGET_EXAM = stringPreferencesKey("target_exam")
        val KEY_LANGUAGE = stringPreferencesKey("app_language")
        val KEY_USER_ROLE = stringPreferencesKey("user_role")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    }

    val isOnboardingCompletedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.KEY_ONBOARDING_COMPLETED] ?: false
    }

    val isProfileCompletedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.KEY_PROFILE_COMPLETED] ?: false
    }

    val selectedLevelFlow: Flow<StudentLevel> = context.dataStore.data.map { preferences ->
        val levelId = preferences[PreferencesKeys.KEY_STUDENT_LEVEL] ?: StudentLevel.CLASS_12.id
        StudentLevel.fromId(levelId)
    }

    val selectedStreamFlow: Flow<SubjectStream> = context.dataStore.data.map { preferences ->
        val streamName = preferences[PreferencesKeys.KEY_SUBJECT_STREAM] ?: SubjectStream.SCIENCE_PCM.name
        try {
            SubjectStream.valueOf(streamName)
        } catch (e: Exception) {
            SubjectStream.SCIENCE_PCM
        }
    }

    val selectedTargetExamFlow: Flow<TargetExam> = context.dataStore.data.map { preferences ->
        val examCode = preferences[PreferencesKeys.KEY_TARGET_EXAM] ?: TargetExam.JEE_MAIN.name
        try {
            TargetExam.valueOf(examCode)
        } catch (e: Exception) {
            TargetExam.JEE_MAIN
        }
    }

    val languageFlow: Flow<LanguagePreference> = context.dataStore.data.map { preferences ->
        val langCode = preferences[PreferencesKeys.KEY_LANGUAGE] ?: LanguagePreference.ENGLISH.name
        try {
            LanguagePreference.valueOf(langCode)
        } catch (e: Exception) {
            LanguagePreference.ENGLISH
        }
    }

    val userRoleFlow: Flow<UserRole> = context.dataStore.data.map { preferences ->
        val roleStr = preferences[PreferencesKeys.KEY_USER_ROLE] ?: UserRole.STUDENT.name
        try {
            UserRole.valueOf(roleStr)
        } catch (e: Exception) {
            UserRole.STUDENT
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setProfileCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_PROFILE_COMPLETED] = completed
        }
    }

    suspend fun setStudentLevel(level: StudentLevel) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_STUDENT_LEVEL] = level.id
        }
    }

    suspend fun setSubjectStream(stream: SubjectStream) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_SUBJECT_STREAM] = stream.name
        }
    }

    suspend fun setTargetExam(exam: TargetExam) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_TARGET_EXAM] = exam.name
        }
    }

    suspend fun setLanguage(language: LanguagePreference) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_LANGUAGE] = language.name
        }
    }

    suspend fun setUserRole(role: UserRole) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_USER_ROLE] = role.name
        }
    }
}
