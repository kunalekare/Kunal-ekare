package com.example.core.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey val lessonId: String,
    val courseId: String,
    val isCompleted: Boolean,
    val lastPositionSeconds: Long,
    val lastWatchedAtMillis: Long = System.currentTimeMillis(),
    val isSyncedWithCloud: Boolean = false
)

@Entity(tableName = "offline_downloads")
data class OfflineDownloadEntity(
    @PrimaryKey val contentId: String,
    val courseId: String,
    val title: String,
    val contentType: String, // VIDEO, PDF_NOTES
    val localFilePath: String,
    val totalBytes: Long,
    val downloadedBytes: Long,
    val isCompleted: Boolean,
    val expiresAtMillis: Long
)
