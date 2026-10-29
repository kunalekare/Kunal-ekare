package com.example.core.model

enum class LessonType {
    VIDEO,
    PDF_NOTES,
    FORMULA_SHEET,
    QUIZ,
    PYQ_PAPER
}

data class Lesson(
    val id: String,
    val title: String,
    val type: LessonType,
    val durationMinutes: Int = 15,
    val contentUrl: String = "",
    val isFreePreview: Boolean = false,
    val isCompleted: Boolean = false,
    val resumePositionSeconds: Long = 0L,
    val pdfPageCount: Int = 0
)

data class Chapter(
    val id: String,
    val title: String,
    val orderNumber: Int,
    val lessons: List<Lesson> = emptyList(),
    val totalLessonsCount: Int = lessons.size,
    val completedLessonsCount: Int = 0
) {
    val progressFraction: Float
        get() = if (totalLessonsCount > 0) completedLessonsCount.toFloat() / totalLessonsCount else 0f
}

data class Subject(
    val id: String,
    val name: String,
    val iconName: String = "book",
    val chaptersCount: Int = 0,
    val chapters: List<Chapter> = emptyList()
)

data class Course(
    val id: String,
    val title: String,
    val instructorName: String,
    val instructorBio: String = "Senior Faculty & Subject Matter Expert",
    val targetLevel: StudentLevel,
    val stream: SubjectStream = SubjectStream.GENERAL,
    val rating: Float = 4.8f,
    val reviewCount: Int = 420,
    val priceInr: Int = 0,
    val originalPriceInr: Int = 0,
    val isEnrolled: Boolean = false,
    val badgeLabel: String = "Popular",
    val thumbnailUrl: String = "",
    val description: String,
    val subjects: List<Subject> = emptyList(),
    val validityDays: Int = 365,
    val certificateIncluded: Boolean = true
) {
    val isFree: Boolean get() = priceInr <= 0
}
