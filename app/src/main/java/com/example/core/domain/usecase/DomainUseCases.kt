package com.example.core.domain.usecase

import com.example.core.common.Resource
import com.example.core.data.repository.LearningRepository
import com.example.core.data.repository.UserRepository
import com.example.core.model.DoubtItem
import com.example.core.model.QuizTest
import com.example.core.model.StudentLevel
import com.example.core.model.UserProfile
import kotlinx.coroutines.flow.Flow

class GetTestsUseCase(
    private val learningRepository: LearningRepository
) {
    operator fun invoke(level: StudentLevel): Flow<List<QuizTest>> {
        return learningRepository.getTestsFlow(level)
    }
}

class GetTestDetailUseCase(
    private val learningRepository: LearningRepository
) {
    suspend operator fun invoke(testId: String): QuizTest? {
        return learningRepository.getTestById(testId)
    }
}

class GetDoubtsUseCase(
    private val learningRepository: LearningRepository
) {
    operator fun invoke(level: StudentLevel): Flow<List<DoubtItem>> {
        return learningRepository.getDoubtsFlow(level)
    }
}

class GetUserProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Resource<UserProfile?> {
        return userRepository.getUserProfile(userId)
    }

    fun observe(userId: String): Flow<UserProfile?> {
        return userRepository.observeUserProfile(userId)
    }
}

class SaveUserProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(profile: UserProfile): Resource<Unit> {
        return userRepository.saveUserProfile(profile)
    }
}
