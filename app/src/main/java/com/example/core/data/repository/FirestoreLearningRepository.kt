package com.example.core.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.core.data.local.db.CourseEntity
import com.example.core.data.local.db.VidyaDatabase
import com.example.core.model.Course
import com.example.core.model.DoubtItem
import com.example.core.model.QuizTest
import com.example.core.model.StudentLevel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirestoreLearningRepository(
    private val context: Context,
    private val fallbackRepo: InMemoryLearningRepository = InMemoryLearningRepository()
) : LearningRepository {

    private val tag = "FirestoreLearningRepo"
    private val db = VidyaDatabase.getInstance(context)
    private val courseDao = db.courseDao()
    private val firestore: FirebaseFirestore? by lazy {
        try {
            val dbId = context.getString(R.string.firestore_database_id)
            FirebaseFirestore.getInstance(dbId)
        } catch (e: Exception) {
            Log.w(tag, "Firestore instance notice: ${e.message}")
            null
        }
    }

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val inMemoryFallback = InMemoryLearningRepository()

    init {
        // Seed initial Room cache with curated curriculum if empty
        ioScope.launch {
            try {
                syncFromCloudOrLocal()
            } catch (e: Exception) {
                Log.w(tag, "Initial cache sync notice: ${e.message}")
            }
        }
    }

    private suspend fun syncFromCloudOrLocal() {
        val fs = firestore
        if (fs != null) {
            try {
                // Attempt to load from Firestore
                val snapshot = fs.collection("courses").limit(50).get().await()
                if (!snapshot.isEmpty) {
                    val cloudCourses = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Course::class.java)?.copy(id = doc.id)
                    }
                    if (cloudCourses.isNotEmpty()) {
                        courseDao.insertCourses(cloudCourses.map { CourseEntity.fromDomain(it) })
                        return
                    }
                }
            } catch (e: Exception) {
                Log.w(tag, "Firestore course fetch failed or offline; using local cache: ${e.message}")
            }
        }

        try {
            val local = courseDao.getCourseById("c10_maths_boards")
            if (local == null) {
                fallbackRepo.getAllCoursesFlow().collect { courses ->
                    if (courses.isNotEmpty()) {
                        courseDao.insertCourses(courses.map { CourseEntity.fromDomain(it) })
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(tag, "Room seeding notice: ${e.message}")
        }
    }

    override fun getCoursesFlow(level: StudentLevel): Flow<List<Course>> {
        return fallbackRepo.getCoursesFlow(level)
    }

    override fun getAllCoursesFlow(): Flow<List<Course>> {
        return fallbackRepo.getAllCoursesFlow()
    }

    override suspend fun getCourseById(id: String): Course? {
        // First check Room local database
        val cached = courseDao.getCourseById(id)
        if (cached != null) {
            val domain = cached.toDomainModel()
            // Pull full curriculum from fallback if subjects are empty
            val richCourse = fallbackRepo.getCourseById(id)
            return richCourse ?: domain
        }
        // Fallback to in-memory curriculum
        return fallbackRepo.getCourseById(id)
    }

    override fun getTestsFlow(level: StudentLevel): Flow<List<QuizTest>> {
        return fallbackRepo.getTestsFlow(level)
    }

    override suspend fun getTestById(id: String): QuizTest? {
        return fallbackRepo.getTestById(id)
    }

    override fun getDoubtsFlow(level: StudentLevel): Flow<List<DoubtItem>> {
        return fallbackRepo.getDoubtsFlow(level)
    }

    override fun getBannersFlow(): Flow<List<BannerItem>> {
        return fallbackRepo.getBannersFlow()
    }

    override suspend fun enrollInCourse(courseId: String): Boolean {
        courseDao.markCourseEnrolled(courseId)
        return fallbackRepo.enrollInCourse(courseId)
    }
}
