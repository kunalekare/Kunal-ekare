package com.example.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.core.designsystem.VidyaAmberSecondary
import com.example.core.designsystem.VidyaEmeraldTertiary
import com.example.core.designsystem.component.VidyaPrimaryButton
import com.example.core.di.LocalAppContainer
import com.example.core.model.Course
import com.example.core.model.LessonType
import com.example.core.model.QuizTest
import com.example.feature.auth.AuthScreen
import com.example.feature.courses.CoursesScreen
import com.example.feature.doubts.DoubtsScreen
import com.example.feature.home.HomeScreen
import com.example.feature.onboarding.OnboardingScreen
import com.example.feature.profile.ProfileScreen
import com.example.feature.profile.ProfileSetupScreen
import com.example.feature.tests.TestsScreen
import kotlinx.coroutines.launch

@Composable
fun VidyaNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Home.route,
    modifier: Modifier = Modifier
) {
    val container = LocalAppContainer.current

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.ROUTE_ONBOARDING) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Screen.ROUTE_AUTH) {
                        popUpTo(Screen.ROUTE_ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ROUTE_AUTH) {
            AuthScreen(
                onAuthSuccess = { userId ->
                    navController.navigate(Screen.ROUTE_PROFILE_SETUP) {
                        popUpTo(Screen.ROUTE_AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ROUTE_PROFILE_SETUP) {
            val currentUserId = container.authRepository.currentUserId ?: "guest_user"
            ProfileSetupScreen(
                userId = currentUserId,
                onProfileSetupComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.ROUTE_PROFILE_SETUP) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCourse = { courseId ->
                    navController.navigate(Screen.courseDetail(courseId))
                },
                onNavigateToTest = { testId ->
                    navController.navigate(Screen.testEngine(testId))
                },
                onNavigateToDoubts = {
                    navController.navigate(Screen.Doubts.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(Screen.Courses.route) {
            CoursesScreen(
                onNavigateToCourseDetail = { courseId ->
                    navController.navigate(Screen.courseDetail(courseId))
                }
            )
        }

        composable(Screen.Tests.route) {
            TestsScreen(
                onNavigateToTestEngine = { testId ->
                    navController.navigate(Screen.testEngine(testId))
                }
            )
        }

        composable(Screen.Doubts.route) {
            DoubtsScreen(
                onAskDoubtClick = {
                    navController.navigate(Screen.ROUTE_ASK_DOUBT)
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToAdmin = {
                    navController.navigate(Screen.ROUTE_ADMIN_PANEL)
                }
            )
        }

        composable(
            route = Screen.ROUTE_COURSE_DETAIL,
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            CourseDetailScreen(
                courseId = courseId,
                onBackClick = { navController.popBackStack() },
                onStartLesson = { lessonId ->
                    navController.navigate(Screen.lessonViewer(lessonId))
                }
            )
        }

        composable(
            route = Screen.ROUTE_TEST_ENGINE,
            arguments = listOf(navArgument("testId") { type = NavType.StringType })
        ) { backStackEntry ->
            val testId = backStackEntry.arguments?.getString("testId") ?: ""
            TestEnginePreviewScreen(
                testId = testId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.ROUTE_ASK_DOUBT) {
            AskDoubtScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: String,
    onBackClick: () -> Unit,
    onStartLesson: (String) -> Unit
) {
    val container = LocalAppContainer.current
    val coroutineScope = rememberCoroutineScope()
    val course by produceState<Course?>(initialValue = null, key1 = courseId) {
        value = container.learningRepository.getCourseById(courseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(course?.title ?: "Course Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        val currentCourse = course
        if (currentCourse == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading course details...")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
                    .testTag("course_detail_content")
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = currentCourse.targetLevel.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = if (currentCourse.isFree) "FREE COURSE" else "₹${currentCourse.priceInr}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (currentCourse.isFree) VidyaEmeraldTertiary else MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = currentCourse.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Taught by ${currentCourse.instructorName} • ${currentCourse.instructorBio}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = currentCourse.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        VidyaPrimaryButton(
                            text = if (currentCourse.isEnrolled) "Enrolled • Continue" else "Enroll Now",
                            onClick = {
                                coroutineScope.launch {
                                    container.learningRepository.enrollInCourse(courseId)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            tag = "btn_course_enroll"
                        )
                    }
                }

                item {
                    Text(
                        text = "COURSE CURRICULUM & SYLLABUS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
                    )
                }

                val subjects = currentCourse.subjects
                if (subjects.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Comprehensive module chapters, lecture videos, notes, and tests are unlocked for enrolled students.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    subjects.forEach { subject ->
                        items(subject.chapters) { chapter ->
                            ChapterCurriculumItem(
                                chapter = chapter,
                                onLessonClick = onStartLesson
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChapterCurriculumItem(
    chapter: com.example.core.model.Chapter,
    onLessonClick: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = chapter.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            chapter.lessons.forEach { lesson ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onLessonClick(lesson.id) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val icon = when (lesson.type) {
                            LessonType.VIDEO -> Icons.Default.PlayCircle
                            LessonType.PDF_NOTES, LessonType.FORMULA_SHEET, LessonType.PYQ_PAPER -> Icons.Default.Description
                            LessonType.QUIZ -> Icons.Default.Quiz
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (lesson.isCompleted) VidyaEmeraldTertiary else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = lesson.title,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${lesson.type.name.replace("_", " ")} • ${lesson.durationMinutes} mins",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (lesson.isFreePreview) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = VidyaEmeraldTertiary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "FREE PREVIEW",
                                style = MaterialTheme.typography.labelSmall,
                                color = VidyaEmeraldTertiary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestEnginePreviewScreen(
    testId: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mock Test Examination") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "EXAM ENVIRONMENT SIMULATION",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Text(
                text = "Timer, Question Palette & Auto-Submit Ready",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "This test adheres to official negative marking (+4 correct, -1 incorrect). Answers are automatically encrypted and synced with the leaderboard upon completion.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))
            VidyaPrimaryButton(
                text = "Back to Tests Hub",
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskDoubtScreen(
    onBackClick: () -> Unit
) {
    var doubtTitle by remember { mutableStateOf("") }
    var doubtDesc by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ask Faculty a Doubt") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
            Text(
                text = "Question Title",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = doubtTitle,
                onValueChange = { doubtTitle = it },
                placeholder = { Text("e.g. Inconsistency in Bernoulli's principle derivation...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Describe your doubt with steps or equations",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = doubtDesc,
                onValueChange = { doubtDesc = it },
                placeholder = { Text("Mention chapter name, theorem, or question number...") },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
            VidyaPrimaryButton(
                text = "Submit Doubt to Faculty",
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
