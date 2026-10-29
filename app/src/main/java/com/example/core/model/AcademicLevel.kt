package com.example.core.model

enum class StudentLevel(
    val id: String,
    val title: String,
    val shortName: String,
    val description: String
) {
    CLASS_10(
        id = "class_10",
        title = "Class 10th (CBSE / State Boards)",
        shortName = "Class 10",
        description = "Foundation, Science, Maths, Social Science & Board Exam Prep"
    ),
    CLASS_11(
        id = "class_11",
        title = "Class 11th (Science, Commerce, Arts)",
        shortName = "Class 11",
        description = "Core Stream foundations & early competitive orientation"
    ),
    CLASS_12(
        id = "class_12",
        title = "Class 12th (Board & Entrance Prep)",
        shortName = "Class 12",
        description = "High-impact Board Prep + Entrance syllabus alignment"
    ),
    COMPETITIVE(
        id = "competitive",
        title = "Competitive Exams (JEE, NEET, CUET)",
        shortName = "Competitive",
        description = "JEE Main/Adv, NEET UG, CUET, MHT-CET, NDA & Foundation"
    ),
    BACHELORS(
        id = "bachelors",
        title = "Bachelor's Degree & Career Skills",
        shortName = "Bachelor's",
        description = "DSA, Full Stack, AI/Data Science, Aptitude & Job Interview Prep"
    );

    companion object {
        fun fromId(id: String): StudentLevel =
            values().firstOrNull { it.id == id } ?: CLASS_10
    }
}

enum class SubjectStream(val displayName: String) {
    GENERAL("General"),
    SCIENCE_PCM("Science (PCM - Physics, Chemistry, Maths)"),
    SCIENCE_PCB("Science (PCB - Physics, Chemistry, Biology)"),
    COMMERCE("Commerce (Accounts, Economics, Business Studies)"),
    ARTS("Arts & Humanities"),
    ENGINEERING_CS("Computer Science & Engineering"),
    DATA_AI("Data Science & AI Track")
}

enum class TargetExam(val code: String, val displayName: String) {
    CBSE_10("CBSE_10", "CBSE 10th Board"),
    CBSE_12("CBSE_12", "CBSE 12th Board"),
    STATE_BOARD("STATE_BOARD", "State Board (HSC/SSC)"),
    JEE_MAIN("JEE_MAIN", "JEE Main & Advanced"),
    NEET_UG("NEET_UG", "NEET UG"),
    CUET("CUET", "CUET (UG)"),
    MHT_CET("MHT_CET", "MHT-CET"),
    NDA("NDA", "NDA / NA"),
    CA_FOUNDATION("CA_FOUNDATION", "CA Foundation"),
    TECH_INTERVIEW("TECH_INTERVIEW", "Tech Placements & Coding")
}

enum class LanguagePreference(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    HINDI("hi", "Hindi", "हिन्दी"),
    MARATHI("mr", "Marathi", "मराठी")
}
