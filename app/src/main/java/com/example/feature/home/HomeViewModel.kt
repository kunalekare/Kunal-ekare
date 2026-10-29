package com.example.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.local.PreferencesManager
import com.example.core.data.repository.BannerItem
import com.example.core.data.repository.LearningRepository
import com.example.core.model.Course
import com.example.core.model.QuizTest
import com.example.core.model.StudentLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class HomeUiState(
    val currentLevel: StudentLevel = StudentLevel.CLASS_12,
    val banners: List<BannerItem> = emptyList(),
    val courses: List<Course> = emptyList(),
    val activeTests: List<QuizTest> = emptyList(),
    val streakDays: Int = 5,
    val xpPoints: Int = 1250,
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val learningRepository: LearningRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        preferencesManager.selectedLevelFlow
            .onEach { level ->
                loadDataForLevel(level)
            }
            .launchIn(viewModelScope)
    }

    private fun loadDataForLevel(level: StudentLevel) {
        combine(
            learningRepository.getCoursesFlow(level),
            learningRepository.getTestsFlow(level),
            learningRepository.getBannersFlow()
        ) { courses, tests, banners ->
            _uiState.value = _uiState.value.copy(
                currentLevel = level,
                courses = courses,
                activeTests = tests,
                banners = banners,
                isLoading = false
            )
        }.launchIn(viewModelScope)
    }
}
