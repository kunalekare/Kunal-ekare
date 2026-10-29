package com.example.feature.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.repository.LearningRepository
import com.example.core.model.Course
import com.example.core.model.StudentLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class CoursesUiState(
    val allCourses: List<Course> = emptyList(),
    val filteredCourses: List<Course> = emptyList(),
    val selectedLevel: StudentLevel? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

class CoursesViewModel(
    private val learningRepository: LearningRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoursesUiState())
    val uiState: StateFlow<CoursesUiState> = _uiState.asStateFlow()

    init {
        learningRepository.getAllCoursesFlow()
            .onEach { list ->
                _uiState.value = _uiState.value.copy(
                    allCourses = list,
                    isLoading = false
                )
                applyFilters()
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun onLevelFilterChanged(level: StudentLevel?) {
        _uiState.value = _uiState.value.copy(selectedLevel = level)
        applyFilters()
    }

    private fun applyFilters() {
        val current = _uiState.value
        val filtered = current.allCourses.filter { course ->
            val matchesLevel = current.selectedLevel == null || course.targetLevel == current.selectedLevel
            val matchesQuery = current.searchQuery.isEmpty() ||
                    course.title.contains(current.searchQuery, ignoreCase = true) ||
                    course.instructorName.contains(current.searchQuery, ignoreCase = true) ||
                    course.description.contains(current.searchQuery, ignoreCase = true)
            matchesLevel && matchesQuery
        }
        _uiState.value = _uiState.value.copy(filteredCourses = filtered)
    }
}

sealed interface CourseDetailUiState {
    object Loading : CourseDetailUiState
    data class Success(val course: Course) : CourseDetailUiState
    data class NotFound(val courseId: String) : CourseDetailUiState
    data class Error(val message: String) : CourseDetailUiState
}

class CourseDetailViewModel(
    private val courseId: String,
    private val learningRepository: LearningRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CourseDetailUiState>(CourseDetailUiState.Loading)
    val uiState: StateFlow<CourseDetailUiState> = _uiState.asStateFlow()

    init {
        loadCourse()
    }

    fun loadCourse() {
        viewModelScope.launch {
            _uiState.value = CourseDetailUiState.Loading
            try {
                val course = learningRepository.getCourseById(courseId)
                if (course != null) {
                    _uiState.value = CourseDetailUiState.Success(course)
                } else {
                    _uiState.value = CourseDetailUiState.NotFound(courseId)
                }
            } catch (e: Exception) {
                _uiState.value = CourseDetailUiState.Error(e.message ?: "Failed to load course details")
            }
        }
    }

    fun enroll() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is CourseDetailUiState.Success) {
                val success = learningRepository.enrollInCourse(courseId)
                if (success) {
                    _uiState.value = CourseDetailUiState.Success(
                        currentState.course.copy(isEnrolled = true)
                    )
                }
            }
        }
    }
}
