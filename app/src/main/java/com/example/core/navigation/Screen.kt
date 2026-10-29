package com.example.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.School
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : Screen(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object Courses : Screen(
        route = "courses",
        title = "Courses",
        selectedIcon = Icons.Filled.School,
        unselectedIcon = Icons.Outlined.School
    )

    object Tests : Screen(
        route = "tests",
        title = "Tests",
        selectedIcon = Icons.Filled.Quiz,
        unselectedIcon = Icons.Outlined.Quiz
    )

    object Doubts : Screen(
        route = "doubts",
        title = "Doubts",
        selectedIcon = Icons.Filled.Forum,
        unselectedIcon = Icons.Outlined.Forum
    )

    object Profile : Screen(
        route = "profile",
        title = "Profile",
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle
    )

    companion object {
        val bottomNavItems = listOf(Home, Courses, Tests, Doubts, Profile)

        const val ROUTE_ONBOARDING = "onboarding"
        const val ROUTE_AUTH = "auth"
        const val ROUTE_PROFILE_SETUP = "profile_setup"

        const val ROUTE_COURSE_DETAIL = "course_detail/{courseId}"
        const val ROUTE_LESSON_VIEWER = "lesson_viewer/{lessonId}"
        const val ROUTE_TEST_ENGINE = "test_engine/{testId}"
        const val ROUTE_ASK_DOUBT = "ask_doubt"
        const val ROUTE_ADMIN_PANEL = "admin_panel"

        fun courseDetail(courseId: String): String = "course_detail/$courseId"
        fun lessonViewer(lessonId: String): String = "lesson_viewer/$lessonId"
        fun testEngine(testId: String): String = "test_engine/$testId"
    }
}
