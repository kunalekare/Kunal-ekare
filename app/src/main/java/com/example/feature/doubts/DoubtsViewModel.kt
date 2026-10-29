package com.example.feature.doubts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.local.PreferencesManager
import com.example.core.data.repository.LearningRepository
import com.example.core.model.DoubtItem
import com.example.core.model.StudentLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class DoubtsUiState(
    val doubts: List<DoubtItem> = emptyList(),
    val currentLevel: StudentLevel = StudentLevel.CLASS_12,
    val isLoading: Boolean = true
)

class DoubtsViewModel(
    private val learningRepository: LearningRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoubtsUiState())
    val uiState: StateFlow<DoubtsUiState> = _uiState.asStateFlow()

    init {
        preferencesManager.selectedLevelFlow
            .onEach { level ->
                _uiState.value = _uiState.value.copy(currentLevel = level, isLoading = true)
                learningRepository.getDoubtsFlow(level).collect { doubtList ->
                    _uiState.value = _uiState.value.copy(doubts = doubtList, isLoading = false)
                }
            }
            .launchIn(viewModelScope)
    }
}
