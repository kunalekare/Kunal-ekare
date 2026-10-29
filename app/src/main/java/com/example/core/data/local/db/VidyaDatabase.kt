package com.example.core.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        CourseEntity::class,
        LessonProgressEntity::class,
        OfflineDownloadEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VidyaDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun progressDao(): ProgressDao
    abstract fun downloadDao(): DownloadDao

    companion object {
        @Volatile
        private var INSTANCE: VidyaDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Preserves unsynced progress data across version upgrades
            }
        }

        fun getInstance(context: Context): VidyaDatabase {
            return INSTANCE ?: synchronized(this) {
                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    VidyaDatabase::class.java,
                    "vidyasetu_local.db"
                ).addMigrations(MIGRATION_1_2)

                // Only allowed in debug for rapid schema iteration; strictly disabled in release
                if (com.example.BuildConfig.DEBUG) {
                    builder.fallbackToDestructiveMigration(true)
                }

                val instance = builder.build()
                INSTANCE = instance
                instance
            }
        }
    }
}
