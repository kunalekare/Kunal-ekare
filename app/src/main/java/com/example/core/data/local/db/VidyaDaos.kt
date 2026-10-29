package com.example.core.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM cached_courses ORDER BY rating DESC")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM cached_courses WHERE targetLevel = :level ORDER BY rating DESC")
    fun getCoursesByLevel(level: String): Flow<List<CourseEntity>>

    @Query("SELECT * FROM cached_courses WHERE id = :courseId LIMIT 1")
    suspend fun getCourseById(courseId: String): CourseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    @Query("UPDATE cached_courses SET isEnrolled = 1 WHERE id = :courseId")
    suspend fun markCourseEnrolled(courseId: String)

    @Query("DELETE FROM cached_courses")
    suspend fun clearCourses()
}

@Dao
interface ProgressDao {
    @Query("SELECT * FROM lesson_progress WHERE courseId = :courseId")
    fun getProgressForCourse(courseId: String): Flow<List<LessonProgressEntity>>

    @Query("SELECT * FROM lesson_progress WHERE lessonId = :lessonId LIMIT 1")
    suspend fun getProgressForLesson(lessonId: String): LessonProgressEntity?

    @Query("SELECT * FROM lesson_progress WHERE isSyncedWithCloud = 0")
    suspend fun getUnsyncedProgress(): List<LessonProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: LessonProgressEntity)

    @Query("UPDATE lesson_progress SET isSyncedWithCloud = 1 WHERE lessonId IN (:lessonIds)")
    suspend fun markProgressSynced(lessonIds: List<String>)
}

@Dao
interface DownloadDao {
    @Query("SELECT * FROM offline_downloads ORDER BY expiresAtMillis ASC")
    fun getAllDownloads(): Flow<List<OfflineDownloadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: OfflineDownloadEntity)

    @Query("DELETE FROM offline_downloads WHERE contentId = :contentId")
    suspend fun deleteDownload(contentId: String)
}
