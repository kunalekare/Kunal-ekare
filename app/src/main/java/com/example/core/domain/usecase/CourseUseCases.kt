package com.example.core.domain.usecase

import android.content.Context
import com.example.core.data.local.db.LessonProgressEntity
import com.example.core.data.local.db.VidyaDatabase
import com.example.core.data.repository.LearningRepository
import com.example.core.data.sync.StudyProgressSyncWorker
import com.example.core.model.Course
import com.example.core.model.StudentLevel
import kotlinx.coroutines.flow.Flow

class GetCoursesUseCase(
    private val learningRepository: LearningRepository
) {
    operator fun invoke(level: StudentLevel? = null): Flow<List<Course>> {
        return if (level == null) {
            learningRepository.getAllCoursesFlow()
        } else {
            learningRepository.getCoursesFlow(level)
        }
    }
}

class GetCourseDetailUseCase(
    private val learningRepository: LearningRepository
) {
    suspend operator fun invoke(courseId: String): Course? {
        return learningRepository.getCourseById(courseId)
    }
}

class EnrollCourseUseCase(
    private val learningRepository: LearningRepository,
    private val context: Context
) {
    suspend operator fun invoke(courseId: String): Boolean {
        val success = learningRepository.enrollInCourse(courseId)
        if (success) {
            // Also update Room local cache
            val db = VidyaDatabase.getInstance(context)
            db.courseDao().markCourseEnrolled(courseId)
        }
        return success
    }
}

class RecordLessonProgressUseCase(
    private val context: Context
) {
    suspend operator fun invoke(
        courseId: String,
        lessonId: String,
        completed: Boolean,
        positionSeconds: Long
    ) {
        val db = VidyaDatabase.getInstance(context)
        val entity = LessonProgressEntity(
            lessonId = lessonId,
            courseId = courseId,
            isCompleted = completed,
            lastPositionSeconds = positionSeconds,
            lastWatchedAtMillis = System.currentTimeMillis(),
            isSyncedWithCloud = false
        )
        db.progressDao().saveProgress(entity)
        // Enqueue background sync via WorkManager
        StudyProgressSyncWorker.enqueue(context)
    }
}
