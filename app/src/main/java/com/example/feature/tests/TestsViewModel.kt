package com.example.feature.tests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.local.PreferencesManager
import com.example.core.data.repository.LearningRepository
import com.example.core.model.QuizTest
import com.example.core.model.StudentLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class TestsUiState(
    val tests: List<QuizTest> = emptyList(),
    val currentLevel: StudentLevel = StudentLevel.CLASS_12,
    val isLoading: Boolean = true
)

class TestsViewModel(
    private val learningRepository: LearningRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(TestsUiState())
    val uiState: StateFlow<TestsUiState> = _uiState.asStateFlow()

    init {
        preferencesManager.selectedLevelFlow
            .onEach { level ->
                _uiState.value = _uiState.value.copy(currentLevel = level, isLoading = true)
                learningRepository.getTestsFlow(level).collect { testList ->
                    _uiState.value = _uiState.value.copy(tests = testList, isLoading = false)
                }
            }
            .launchIn(viewModelScope)
    }
}
