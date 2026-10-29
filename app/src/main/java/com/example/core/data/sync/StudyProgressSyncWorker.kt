package com.example.core.data.sync

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.R
import com.example.core.data.local.db.VidyaDatabase
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class StudyProgressSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val currentUser = Firebase.auth.currentUser ?: return Result.success()
        val db = VidyaDatabase.getInstance(applicationContext)
        val progressDao = db.progressDao()
        val unsynced = progressDao.getUnsyncedProgress()

        if (unsynced.isEmpty()) {
            return Result.success()
        }

        return try {
            val dbId = applicationContext.getString(R.string.firestore_database_id)
            val firestore = FirebaseFirestore.getInstance(dbId)
            val userProgressCollection = firestore.collection("users")
                .document(currentUser.uid)
                .collection("progress")

            val syncedIds = mutableListOf<String>()
            for (item in unsynced) {
                val data = mapOf(
                    "lessonId" to item.lessonId,
                    "courseId" to item.courseId,
                    "isCompleted" to item.isCompleted,
                    "lastPositionSeconds" to item.lastPositionSeconds,
                    "syncedAt" to FieldValue.serverTimestamp()
                )
                userProgressCollection.document(item.lessonId)
                    .set(data, SetOptions.merge())
                    .await()
                syncedIds.add(item.lessonId)
            }

            if (syncedIds.isNotEmpty()) {
                progressDao.markProgressSynced(syncedIds)
            }
            Log.d(TAG, "Successfully synced ${syncedIds.size} progress entries to Firestore.")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing study progress to Firestore: ${e.message}", e)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "ProgressSyncWorker"
        private const val WORK_NAME = "vidya_study_progress_sync"

        fun enqueue(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<StudyProgressSyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }
}
