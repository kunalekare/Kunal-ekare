package com.example

import com.example.core.model.Chapter
import com.example.core.model.Course
import com.example.core.model.Lesson
import com.example.core.model.LessonType
import com.example.core.model.StudentLevel
import com.example.core.model.Subject
import com.example.core.model.SubjectStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase3ContentAndPlayerUnitTest {

    @Test
    fun `test chapter progress fraction calculation`() {
        val chapter = Chapter(
            id = "ch_01",
            title = "Kinematics & Vectors",
            orderNumber = 1,
            completedLessonsCount = 3,
            totalLessonsCount = 6
        )

        assertEquals(0.5f, chapter.progressFraction, 0.001f)

        val emptyChapter = Chapter(
            id = "ch_02",
            title = "Empty",
            orderNumber = 2,
            completedLessonsCount = 0,
            totalLessonsCount = 0
        )
        assertEquals(0f, emptyChapter.progressFraction, 0.001f)
    }

    @Test
    fun `test lesson type routing discrimination`() {
        fun resolveScreenRoute(lesson: Lesson): String {
            return when (lesson.type) {
                LessonType.VIDEO -> "lesson_viewer"
                LessonType.PDF_NOTES, LessonType.FORMULA_SHEET, LessonType.PYQ_PAPER -> "pdf_viewer"
                LessonType.QUIZ -> "test_engine"
            }
        }

        val videoLesson = Lesson(
            id = "l_v1",
            title = "Projectile Motion Concept",
            type = LessonType.VIDEO,
            contentUrl = "https://example.com/video.mp4"
        )
        val pdfLesson = Lesson(
            id = "l_p1",
            title = "Kinematics Formula Sheet",
            type = LessonType.FORMULA_SHEET,
            contentUrl = "https://example.com/notes.pdf"
        )
        val quizLesson = Lesson(
            id = "l_q1",
            title = "Kinematics Practice Test",
            type = LessonType.QUIZ
        )

        assertEquals("lesson_viewer", resolveScreenRoute(videoLesson))
        assertEquals("pdf_viewer", resolveScreenRoute(pdfLesson))
        assertEquals("test_engine", resolveScreenRoute(quizLesson))
    }

    @Test
    fun `test watermark text formatting for anti piracy overlay`() {
        fun generateWatermark(userId: String, phoneLast4: String): String {
            val cleanUid = if (userId.length > 8) userId.take(8) else userId
            return "VidyaSetu Student: $cleanUid (***$phoneLast4)"
        }

        val watermark = generateWatermark("user_aarav_9988", "4321")
        assertEquals("VidyaSetu Student: user_aar (***4321)", watermark)
    }

    @Test
    fun `test course pricing logic`() {
        val freeCourse = Course(
            id = "c_free",
            title = "Foundation Bridge Course",
            instructorName = "Prof. Sharma",
            targetLevel = StudentLevel.CLASS_10,
            priceInr = 0,
            originalPriceInr = 999,
            description = "Free crash course"
        )
        val paidCourse = Course(
            id = "c_paid",
            title = "JEE Advanced Physics Mastery",
            instructorName = "Dr. Verma",
            targetLevel = StudentLevel.CLASS_12,
            priceInr = 1499,
            originalPriceInr = 4999,
            description = "Complete advance modules"
        )

        assertTrue(freeCourse.isFree)
        assertFalse(paidCourse.isFree)
        assertEquals(1499, paidCourse.priceInr)
    }

    @Test
    fun `test playback speed values`() {
        val speeds = listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
        assertTrue(speeds.contains(1.0f))
        assertTrue(speeds.first() == 0.75f)
        assertTrue(speeds.last() == 2.0f)
    }
}
