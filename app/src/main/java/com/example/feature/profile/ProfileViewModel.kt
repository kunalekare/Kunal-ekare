package com.example.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.local.PreferencesManager
import com.example.core.data.repository.UserRepository
import com.example.core.model.LanguagePreference
import com.example.core.model.StudentLevel
import com.example.core.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val fullName: String = "Kunal Ekare",
    val email: String = "kunalekare02@gmail.com",
    val selectedLevel: StudentLevel = StudentLevel.CLASS_12,
    val currentRole: UserRole = UserRole.STUDENT,
    val currentLanguage: LanguagePreference = LanguagePreference.ENGLISH,
    val streakDays: Int = 5,
    val xpPoints: Int = 1250,
    val enrolledCoursesCount: Int = 3
)

class ProfileViewModel(
    private val preferencesManager: PreferencesManager,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        combine(
            preferencesManager.selectedLevelFlow,
            preferencesManager.userRoleFlow,
            preferencesManager.languageFlow
        ) { level, role, language ->
            _uiState.value = _uiState.value.copy(
                selectedLevel = level,
                currentRole = role,
                currentLanguage = language
            )
        }.launchIn(viewModelScope)
    }

    fun updateLevel(level: StudentLevel) {
        viewModelScope.launch {
            preferencesManager.setStudentLevel(level)
        }
    }

    fun updateLanguage(language: LanguagePreference) {
        viewModelScope.launch {
            preferencesManager.setLanguage(language)
        }
    }

    fun updateRole(role: UserRole) {
        viewModelScope.launch {
            preferencesManager.setUserRole(role)
        }
    }
}
