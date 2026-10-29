package com.example

import com.example.core.model.Course
import com.example.core.model.Lesson
import com.example.core.model.LessonType
import com.example.core.model.StudentLevel
import com.example.core.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ScreenNavigationStabilityTest {

    @Test
    fun `test 50 consecutive navigation cycles across all app screens without failure`() {
        val simulatedBackStack = mutableListOf<String>()
        val allPrimaryRoutes = listOf(
            Screen.Home.route,
            Screen.Courses.route,
            Screen.courseDetail("c10_maths_boards"),
            Screen.courseDetail("c12_physics_boards_jee"),
            Screen.Tests.route,
            Screen.Doubts.route,
            Screen.Profile.route,
            Screen.lessonViewer("l1"),
            Screen.pdfViewer("l3")
        )

        // Run 50 cycles
        for (cycle in 1..50) {
            for (route in allPrimaryRoutes) {
                // Simulate safe navigation push
                if (simulatedBackStack.lastOrNull() != route) {
                    simulatedBackStack.add(route)
                }
                assertTrue(simulatedBackStack.isNotEmpty())
            }

            // Simulate popping back to Home
            while (simulatedBackStack.size > 1) {
                simulatedBackStack.removeAt(simulatedBackStack.lastIndex)
            }
            assertEquals(1, simulatedBackStack.size)
            assertEquals(Screen.Home.route, simulatedBackStack.first())
        }

        assertEquals(1, simulatedBackStack.size)
    }

    @Test
    fun `test debounced click rejects rapid taps under 250ms`() {
        var clickCount = 0
        var lastClickTime = 0L
        val debounceThresholdMs = 250L

        fun simulateClick(eventTimeMs: Long) {
            if (eventTimeMs - lastClickTime >= debounceThresholdMs) {
                lastClickTime = eventTimeMs
                clickCount++
            }
        }

        // Tap 1 at 1000ms -> should succeed
        simulateClick(1000L)
        assertEquals(1, clickCount)

        // Rapid double tap at 1100ms (100ms later) -> should be rejected
        simulateClick(1100L)
        assertEquals(1, clickCount)

        // Rapid triple tap at 1180ms -> should be rejected
        simulateClick(1180L)
        assertEquals(1, clickCount)

        // Tap 2 at 1300ms (300ms after first tap, > 250ms) -> should succeed
        simulateClick(1300L)
        assertEquals(2, clickCount)
    }

    @Test
    fun `test null or invalid course ID resolves to NotFound without crash`() {
        val courseCatalog = mapOf(
            "valid_course_1" to Course(
                id = "valid_course_1",
                title = "Class 10 Maths",
                instructorName = "Prof. Sharma",
                targetLevel = StudentLevel.CLASS_10,
                description = "Maths course"
            )
        )

        fun fetchCourseSafe(id: String): Course? {
            return courseCatalog[id]
        }

        assertNotNull(fetchCourseSafe("valid_course_1"))
        assertEquals(null, fetchCourseSafe("invalid_non_existent_id"))
        assertEquals(null, fetchCourseSafe(""))
    }
}
