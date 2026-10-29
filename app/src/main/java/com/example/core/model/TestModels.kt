package com.example.core.model

enum class QuestionType {
    MCQ_SINGLE,
    MCQ_MULTIPLE,
    NUMERICAL,
    TRUE_FALSE
}

data class QuestionOption(
    val id: String,
    val text: String,
    val imageUrl: String? = null
)

data class Question(
    val id: String,
    val text: String,
    val type: QuestionType = QuestionType.MCQ_SINGLE,
    val options: List<QuestionOption> = emptyList(),
    val correctOptionIds: List<String> = emptyList(),
    val numericalCorrectAnswer: Double? = null,
    val explanation: String = "",
    val marksPositive: Int = 4,
    val marksNegative: Int = 1,
    val topicName: String = "General"
)

data class QuizTest(
    val id: String,
    val title: String,
    val subjectName: String,
    val targetLevel: StudentLevel,
    val totalQuestions: Int,
    val durationMinutes: Int = 60,
    val totalMarks: Int = totalQuestions * 4,
    val isLive: Boolean = false,
    val questions: List<Question> = emptyList()
)

data class TestAttempt(
    val testId: String,
    val score: Int,
    val totalMarks: Int,
    val accuracyPercentage: Float,
    val timeSpentSeconds: Long,
    val answeredCount: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val percentile: Float = 92.4f
)
