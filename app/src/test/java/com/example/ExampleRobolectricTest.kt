package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.data.repository.InMemoryLearningRepository
import com.example.core.model.StudentLevel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches VidyaSetu`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("VidyaSetu", appName)
    }

    @Test
    fun `test learning repository returns courses for student levels`() = runTest {
        val repo = InMemoryLearningRepository()

        val class10Courses = repo.getCoursesFlow(StudentLevel.CLASS_10).first()
        assertTrue("Class 10 should have courses", class10Courses.isNotEmpty())

        val bachelorsCourses = repo.getCoursesFlow(StudentLevel.BACHELORS).first()
        assertTrue("Bachelor's track should have courses", bachelorsCourses.isNotEmpty())

        val course = repo.getCourseById(class10Courses.first().id)
        assertNotNull("Should find course by id", course)
    }

    @Test
    fun `test MainActivity launches without crash`() {
        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java)
        controller.create().start().resume()
        val activity = controller.get()
        assertNotNull("Activity should launch successfully", activity)
    }
}
