package com.example.core.data.repository

import com.example.core.model.Chapter
import com.example.core.model.Course
import com.example.core.model.DoubtItem
import com.example.core.model.DoubtReply
import com.example.core.model.Lesson
import com.example.core.model.LessonType
import com.example.core.model.Question
import com.example.core.model.QuestionOption
import com.example.core.model.QuestionType
import com.example.core.model.QuizTest
import com.example.core.model.StudentLevel
import com.example.core.model.Subject
import com.example.core.model.SubjectStream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

data class BannerItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val actionText: String,
    val targetLevel: StudentLevel? = null
)

interface LearningRepository {
    fun getCoursesFlow(level: StudentLevel): Flow<List<Course>>
    fun getAllCoursesFlow(): Flow<List<Course>>
    suspend fun getCourseById(id: String): Course?
    fun getTestsFlow(level: StudentLevel): Flow<List<QuizTest>>
    suspend fun getTestById(id: String): QuizTest?
    fun getDoubtsFlow(level: StudentLevel): Flow<List<DoubtItem>>
    fun getBannersFlow(): Flow<List<BannerItem>>
    suspend fun enrollInCourse(courseId: String): Boolean
}

class InMemoryLearningRepository : LearningRepository {

    private val sampleCourses = listOf(
        // Class 10
        Course(
            id = "c10_maths_boards",
            title = "Class 10 CBSE Maths - 100/100 Masterclass",
            instructorName = "Prof. R. K. Sharma",
            instructorBio = "22+ years teaching CBSE Board toppers",
            targetLevel = StudentLevel.CLASS_10,
            stream = SubjectStream.GENERAL,
            rating = 4.9f,
            reviewCount = 1840,
            priceInr = 0,
            originalPriceInr = 1999,
            isEnrolled = true,
            badgeLabel = "Free Access",
            description = "Complete coverage of Real Numbers, Polynomials, Quadratic Equations, Triangles, Trigonometry, and NCERT Exemplar problems.",
            subjects = listOf(
                Subject(
                    id = "sub_maths_10",
                    name = "Mathematics",
                    chaptersCount = 14,
                    chapters = listOf(
                        Chapter(
                            id = "ch1_real_numbers",
                            title = "Chapter 1: Real Numbers",
                            orderNumber = 1,
                            totalLessonsCount = 4,
                            completedLessonsCount = 3,
                            lessons = listOf(
                                Lesson("l1", "Fundamental Theorem of Arithmetic", LessonType.VIDEO, 22, isFreePreview = true, isCompleted = true),
                                Lesson("l2", "Revisiting Irrational Numbers & Proofs", LessonType.VIDEO, 28, isFreePreview = true, isCompleted = true),
                                Lesson("l3", "Chapter 1 Revision Formula Sheet & Summary", LessonType.PDF_NOTES, 10, isFreePreview = true, isCompleted = true, pdfPageCount = 8),
                                Lesson("l4", "Board Exam PYQ Mock Quiz (2018-2025)", LessonType.QUIZ, 15, isFreePreview = true, isCompleted = false)
                            )
                        ),
                        Chapter(
                            id = "ch2_polynomials",
                            title = "Chapter 2: Polynomials",
                            orderNumber = 2,
                            totalLessonsCount = 3,
                            completedLessonsCount = 1,
                            lessons = listOf(
                                Lesson("l5", "Geometrical Meaning of Zeroes", LessonType.VIDEO, 25, isFreePreview = true, isCompleted = true),
                                Lesson("l6", "Relationship Between Zeroes and Coefficients", LessonType.VIDEO, 30, isFreePreview = false, isCompleted = false),
                                Lesson("l7", "Polynomials Chapter Notes & Mindmap", LessonType.PDF_NOTES, 8, isFreePreview = false, isCompleted = false, pdfPageCount = 12)
                            )
                        )
                    )
                )
            )
        ),
        Course(
            id = "c10_science_boards",
            title = "Class 10 Science - Concept to Revision",
            instructorName = "Dr. Ananya Mukherjee",
            instructorBio = "Former CBSE Curriculum Advisor",
            targetLevel = StudentLevel.CLASS_10,
            stream = SubjectStream.GENERAL,
            rating = 4.8f,
            reviewCount = 1210,
            priceInr = 499,
            originalPriceInr = 2499,
            isEnrolled = false,
            badgeLabel = "Popular",
            description = "Physics, Chemistry, and Biology combined with diagrams, laboratory experiments walkthrough, and 10-year question bank."
        ),
        // Class 11 & 12
        Course(
            id = "c12_physics_boards_jee",
            title = "Class 12 Physics - Electrostatics & Optics",
            instructorName = "Er. Saurabh Verma (IIT Roorkee)",
            instructorBio = "Top physics mentor with 100+ selections in IITs",
            targetLevel = StudentLevel.CLASS_12,
            stream = SubjectStream.SCIENCE_PCM,
            rating = 4.95f,
            reviewCount = 3450,
            priceInr = 999,
            originalPriceInr = 4999,
            isEnrolled = true,
            badgeLabel = "Bestseller",
            description = "Master Electric Charges, Gauss Law, Potential, Capacitance, Magnetism, and Wave Optics with step-by-step numerical derivations."
        ),
        Course(
            id = "c12_chemistry_neet",
            title = "NEET & Board Organic Chemistry Sprint",
            instructorName = "Dr. Meenakshi Joshi",
            instructorBio = "AIIMS gold medalist & NEET specialist",
            targetLevel = StudentLevel.CLASS_12,
            stream = SubjectStream.SCIENCE_PCB,
            rating = 4.9f,
            reviewCount = 2890,
            priceInr = 899,
            originalPriceInr = 3999,
            isEnrolled = false,
            badgeLabel = "Hot",
            description = "All named reactions, mechanisms, reaction charts, NCERT line-by-line decoding, and 500+ practice MCQs."
        ),
        // Competitive Exams
        Course(
            id = "comp_jee_advanced_crash",
            title = "JEE Advanced Rank Booster & Problem Solving",
            instructorName = "Prof. Arvind Bansal",
            instructorBio = "Trained AIR 1, 4, 18 in JEE Advanced",
            targetLevel = StudentLevel.COMPETITIVE,
            stream = SubjectStream.SCIENCE_PCM,
            rating = 5.0f,
            reviewCount = 4120,
            priceInr = 1499,
            originalPriceInr = 7999,
            isEnrolled = false,
            badgeLabel = "High Yield",
            description = "Multi-concept problems, previous 15 years analysis, time-saving short cuts, and full-length simulated mock tests."
        ),
        Course(
            id = "comp_cuet_general_test",
            title = "CUET 2026: General Test & Domain Subjects",
            instructorName = "Aman Gupta & Team",
            instructorBio = "Delhi University alumni & CUET mentors",
            targetLevel = StudentLevel.COMPETITIVE,
            stream = SubjectStream.COMMERCE,
            rating = 4.7f,
            reviewCount = 940,
            priceInr = 699,
            originalPriceInr = 2999,
            isEnrolled = false,
            badgeLabel = "New",
            description = "Quantitative reasoning, logical aptitude, general knowledge, English comprehension, and mock papers."
        ),
        // Bachelor's Skill Development
        Course(
            id = "deg_dsa_interview_prep",
            title = "Data Structures & Algorithms in Java & C++",
            instructorName = "Rohan Deshmukh",
            instructorBio = "Ex-Google Software Engineer & LeetCode Guardian",
            targetLevel = StudentLevel.BACHELORS,
            stream = SubjectStream.ENGINEERING_CS,
            rating = 4.96f,
            reviewCount = 5200,
            priceInr = 0,
            originalPriceInr = 4999,
            isEnrolled = true,
            badgeLabel = "Flagship",
            description = "Arrays, Strings, LinkedList, Trees, Graphs, Dynamic Programming, System Design basics, and Top 200 FAANG interview questions.",
            subjects = listOf(
                Subject(
                    id = "sub_dsa",
                    name = "Core DSA Modules",
                    chaptersCount = 12,
                    chapters = listOf(
                        Chapter(
                            id = "ch_dsa_trees",
                            title = "Trees, BST & Traversals",
                            orderNumber = 1,
                            totalLessonsCount = 5,
                            completedLessonsCount = 4,
                            lessons = listOf(
                                Lesson("dsa_l1", "Binary Tree Inorder, Preorder, Postorder (Iterative & Recursive)", LessonType.VIDEO, 35, isFreePreview = true, isCompleted = true),
                                Lesson("dsa_l2", "Level Order & Zigzag Traversal", LessonType.VIDEO, 28, isFreePreview = true, isCompleted = true),
                                Lesson("dsa_l3", "Lowest Common Ancestor & Diameter of Tree", LessonType.VIDEO, 30, isFreePreview = true, isCompleted = true),
                                Lesson("dsa_l4", "Binary Tree LeetCode Sheet (PDF with GitHub solutions)", LessonType.PDF_NOTES, 15, isFreePreview = true, isCompleted = true, pdfPageCount = 24),
                                Lesson("dsa_l5", "Trees Speed Coding Quiz (MCQ & Time Complexity)", LessonType.QUIZ, 20, isFreePreview = false, isCompleted = false)
                            )
                        )
                    )
                )
            )
        ),
        Course(
            id = "deg_fullstack_web_dev",
            title = "Full Stack Modern Web & Mobile Development",
            instructorName = "Pooja Hegde",
            instructorBio = "Staff Architect & Open Source Contributor",
            targetLevel = StudentLevel.BACHELORS,
            stream = SubjectStream.ENGINEERING_CS,
            rating = 4.88f,
            reviewCount = 3100,
            priceInr = 1299,
            originalPriceInr = 5999,
            isEnrolled = false,
            badgeLabel = "Project Based",
            description = "React, Node.js, Kotlin Compose, REST & GraphQL APIs, Postgres, CI/CD deployment, and building 4 portfolio projects."
        ),
        Course(
            id = "deg_aptitude_communication",
            title = "Campus Placement Aptitude & Spoken English",
            instructorName = "Vikram Aditya",
            instructorBio = "Corporate Trainer for TCS, Infosys, and Deloitte hiring drives",
            targetLevel = StudentLevel.BACHELORS,
            stream = SubjectStream.GENERAL,
            rating = 4.82f,
            reviewCount = 1950,
            priceInr = 499,
            originalPriceInr = 1999,
            isEnrolled = false,
            badgeLabel = "Placement Essential",
            description = "Speed math, Permutations & Combinations, Logical Puzzles, GD/Personal Interview skills, and Resume crafting."
        )
    )

    private val sampleTests = listOf(
        QuizTest(
            id = "t10_maths_daily",
            title = "Class 10 Daily Practice Test: Quadratic Equations",
            subjectName = "Mathematics",
            targetLevel = StudentLevel.CLASS_10,
            totalQuestions = 10,
            durationMinutes = 20,
            isLive = true,
            questions = listOf(
                Question(
                    id = "q1",
                    text = "What is the discriminant of the quadratic equation 2x² - 4x + 3 = 0?",
                    type = QuestionType.MCQ_SINGLE,
                    options = listOf(
                        QuestionOption("opt1", "-8"),
                        QuestionOption("opt2", "8"),
                        QuestionOption("opt3", "-16"),
                        QuestionOption("opt4", "16")
                    ),
                    correctOptionIds = listOf("opt1"),
                    explanation = "D = b² - 4ac = (-4)² - 4(2)(3) = 16 - 24 = -8. Since D < 0, there are no real roots.",
                    marksPositive = 4,
                    marksNegative = 1,
                    topicName = "Quadratic Equations"
                ),
                Question(
                    id = "q2",
                    text = "If one zero of the quadratic polynomial x² + 3x + k is 2, then what is the value of k?",
                    type = QuestionType.MCQ_SINGLE,
                    options = listOf(
                        QuestionOption("opt1", "10"),
                        QuestionOption("opt2", "-10"),
                        QuestionOption("opt3", "-7"),
                        QuestionOption("opt4", "-2")
                    ),
                    correctOptionIds = listOf("opt2"),
                    explanation = "Substitute x = 2: (2)² + 3(2) + k = 0 => 4 + 6 + k = 0 => k = -10.",
                    marksPositive = 4,
                    marksNegative = 1,
                    topicName = "Polynomials"
                )
            )
        ),
        QuizTest(
            id = "t12_physics_mock",
            title = "Class 12 & JEE Full Mock: Electrostatics & Current",
            subjectName = "Physics",
            targetLevel = StudentLevel.CLASS_12,
            totalQuestions = 25,
            durationMinutes = 60,
            isLive = true
        ),
        QuizTest(
            id = "t_deg_dsa_assessment",
            title = "DSA Weekly Contest: Trees, Graphs & Dynamic Programming",
            subjectName = "Computer Science",
            targetLevel = StudentLevel.BACHELORS,
            totalQuestions = 15,
            durationMinutes = 45,
            isLive = true
        )
    )

    private val sampleDoubts = listOf(
        DoubtItem(
            id = "d1",
            studentName = "Priya Sharma",
            title = "Confusion in Gauss Law application for non-conducting cylinder",
            description = "Why does the electric field inside a non-conducting uniformly charged solid cylinder vary linearly with radius r? Can someone share the derivation step?",
            subject = "Physics",
            level = StudentLevel.CLASS_12,
            upvotesCount = 18,
            repliesCount = 2,
            isResolved = true,
            replies = listOf(
                DoubtReply(
                    id = "r1",
                    authorName = "Er. Saurabh Verma",
                    authorRole = "Faculty",
                    content = "Consider a Gaussian cylinder of radius r < R and length L. Enclosed charge is Q_enc = ρ * (π r² L). By Gauss law, E * (2π r L) = Q_enc / ε₀ = (ρ π r² L) / ε₀. Hence E = (ρ * r) / (2 ε₀), which is directly proportional to r.",
                    timestampFormatted = "2 hours ago",
                    isVerifiedSolution = true,
                    upvotes = 24
                )
            )
        ),
        DoubtItem(
            id = "d2",
            studentName = "Aditya Kulkarni",
            title = "Space Complexity of Recursive Tree Traversal",
            description = "Is auxiliary space in DFS on a binary tree always O(N) in the worst case, or can it be O(log N)?",
            subject = "Algorithms",
            level = StudentLevel.BACHELORS,
            upvotesCount = 14,
            repliesCount = 1,
            isResolved = false,
            replies = listOf(
                DoubtReply(
                    id = "r2",
                    authorName = "Rohan Deshmukh",
                    authorRole = "Mentor",
                    content = "It is proportional to the maximum recursion depth, which equals the tree height H. In a skewed tree H = N, so O(N). In a balanced tree H = log N, so O(log N).",
                    timestampFormatted = "1 day ago",
                    isVerifiedSolution = true,
                    upvotes = 11
                )
            )
        )
    )

    private val sampleBanners = listOf(
        BannerItem(
            id = "b1",
            title = "🎯 Board & Entrance 2026 Test Series",
            subtitle = "Practice 10,000+ chapter-wise questions with instant video solutions and peer ranks.",
            actionText = "Start Daily Quiz",
            targetLevel = null
        ),
        BannerItem(
            id = "b2",
            title = "🚀 Bachelor's Tech & Core Placement Bootcamp",
            subtitle = "Free DSA roadmap + weekly company mock tests curated by FAANG engineers.",
            actionText = "Explore Roadmap",
            targetLevel = StudentLevel.BACHELORS
        )
    )

    private val coursesState = MutableStateFlow(sampleCourses)

    override fun getCoursesFlow(level: StudentLevel): Flow<List<Course>> {
        return coursesState.map { list ->
            list.filter { it.targetLevel == level }
        }
    }

    override fun getAllCoursesFlow(): Flow<List<Course>> = coursesState.asStateFlow()

    override suspend fun getCourseById(id: String): Course? {
        return coursesState.value.firstOrNull { it.id == id }
    }

    override fun getTestsFlow(level: StudentLevel): Flow<List<QuizTest>> {
        return MutableStateFlow(sampleTests.filter { it.targetLevel == level }).asStateFlow()
    }

    override suspend fun getTestById(id: String): QuizTest? {
        return sampleTests.firstOrNull { it.id == id }
    }

    override fun getDoubtsFlow(level: StudentLevel): Flow<List<DoubtItem>> {
        return MutableStateFlow(sampleDoubts.filter { it.level == level }).asStateFlow()
    }

    override fun getBannersFlow(): Flow<List<BannerItem>> {
        return MutableStateFlow(sampleBanners).asStateFlow()
    }

    override suspend fun enrollInCourse(courseId: String): Boolean {
        val current = coursesState.value.toMutableList()
        val index = current.indexOfFirst { it.id == courseId }
        if (index != -1) {
            current[index] = current[index].copy(isEnrolled = true)
            coursesState.value = current
            return true
        }
        return false
    }
}
