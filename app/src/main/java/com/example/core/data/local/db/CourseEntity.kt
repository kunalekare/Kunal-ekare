package com.example.core.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core.model.Course
import com.example.core.model.StudentLevel
import com.example.core.model.SubjectStream

@Entity(tableName = "cached_courses")
data class CourseEntity(
    @PrimaryKey val id: String,
    val title: String,
    val instructorName: String,
    val instructorBio: String,
    val targetLevel: String,
    val stream: String,
    val rating: Float,
    val reviewCount: Int,
    val priceInr: Int,
    val originalPriceInr: Int,
    val isEnrolled: Boolean,
    val badgeLabel: String,
    val thumbnailUrl: String,
    val description: String,
    val cachedAtMillis: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): Course {
        val level = try {
            StudentLevel.valueOf(targetLevel)
        } catch (_: Exception) {
            StudentLevel.CLASS_10
        }
        val subjectStream = try {
            SubjectStream.valueOf(stream)
        } catch (_: Exception) {
            SubjectStream.GENERAL
        }
        return Course(
            id = id,
            title = title,
            instructorName = instructorName,
            instructorBio = instructorBio,
            targetLevel = level,
            stream = subjectStream,
            rating = rating,
            reviewCount = reviewCount,
            priceInr = priceInr,
            originalPriceInr = originalPriceInr,
            isEnrolled = isEnrolled,
            badgeLabel = badgeLabel,
            thumbnailUrl = thumbnailUrl,
            description = description
        )
    }

    companion object {
        fun fromDomain(course: Course): CourseEntity {
            return CourseEntity(
                id = course.id,
                title = course.title,
                instructorName = course.instructorName,
                instructorBio = course.instructorBio,
                targetLevel = course.targetLevel.name,
                stream = course.stream.name,
                rating = course.rating,
                reviewCount = course.reviewCount,
                priceInr = course.priceInr,
                originalPriceInr = course.originalPriceInr,
                isEnrolled = course.isEnrolled,
                badgeLabel = course.badgeLabel,
                thumbnailUrl = course.thumbnailUrl,
                description = course.description
            )
        }
    }
}
