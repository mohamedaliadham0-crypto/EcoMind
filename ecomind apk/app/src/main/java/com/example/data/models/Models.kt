package com.example.data.models

enum class WasteType(val titleEn: String, val titleAr: String, val colorHex: Long) {
    PLASTIC("Plastics & Recyclables", "بلاستيك ومواد قابلة للتدوير", 0xFFF4C95D),
    ORGANIC("Organic / Compost", "مواد عضوية وسماد", 0xFF13A66B),
    PAPER("Paper & Cardboard", "ورق وكرتون", 0xFF76C8E8),
    E_WASTE("Hazardous / E-Waste", "نفايات إلكترونية وخطرة", 0xFFE74C3C)
}

data class WasteItem(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val type: WasteType,
    val iconEmoji: String,
    val tipEn: String,
    val tipAr: String
)

data class LessonSection(
    val titleEn: String,
    val titleAr: String,
    val contentEn: String,
    val contentAr: String
)

data class Lesson(
    val id: String,
    val categoryId: String,
    val titleEn: String,
    val titleAr: String,
    val summaryEn: String,
    val summaryAr: String,
    val scientificFactEn: String,
    val scientificFactAr: String,
    val realWorldExampleEn: String,
    val realWorldExampleAr: String,
    val practicalActionsEn: List<String>,
    val practicalActionsAr: List<String>,
    val sections: List<LessonSection> = emptyList()
)

data class QuizQuestion(
    val id: String,
    val categoryId: String,
    val questionEn: String,
    val questionAr: String,
    val optionsEn: List<String>,
    val optionsAr: List<String>,
    val correctIndex: Int,
    val explanationEn: String,
    val explanationAr: String
)

data class Challenge(
    val id: String,
    val titleEn: String,
    val titleAr: String,
    val descEn: String,
    val descAr: String,
    val category: String,
    val difficulty: String, // Easy, Medium, Hard
    val points: Int,
    val criteriaEn: String,
    val criteriaAr: String,
    val isDaily: Boolean,
    val isCompleted: Boolean = false
)

data class EcoBadge(
    val id: String,
    val titleEn: String,
    val titleAr: String,
    val descEn: String,
    val descAr: String,
    val emoji: String,
    val requirementEn: String,
    val requirementAr: String,
    val isUnlocked: Boolean = false
)

data class FactItem(
    val id: String,
    val titleEn: String,
    val titleAr: String,
    val factEn: String,
    val factAr: String,
    val source: String,
    val category: String
)

data class UserProfile(
    val username: String = "Eco Student",
    val avatarEmoji: String = "🌱",
    val ecoPoints: Int = 50,
    val kwhSaved: Double = 14.5,
    val litersSaved: Double = 120.0,
    val wasteSortedCount: Int = 12,
    val completedLessonIds: Set<String> = setOf("energy_solar"),
    val completedChallengeIds: Set<String> = emptySet(),
    val unlockedBadgeIds: Set<String> = setOf("badge_first_step"),
    val quizzesTaken: Int = 1,
    val totalQuizScore: Int = 85
) {
    val ecoLevel: String
        get() = when {
            ecoPoints >= 600 -> "Earth Champion"
            ecoPoints >= 350 -> "Forest Guardian"
            ecoPoints >= 150 -> "Green Sprout"
            else -> "Eco Seedling"
        }

    val ecoLevelAr: String
        get() = when {
            ecoPoints >= 600 -> "بطل الأرض"
            ecoPoints >= 350 -> "حامي الغابة"
            ecoPoints >= 150 -> "برعم أخضر"
            else -> "شتلة بيئية"
        }

    val nextLevelTarget: Int
        get() = when {
            ecoPoints >= 600 -> 1000
            ecoPoints >= 350 -> 600
            ecoPoints >= 150 -> 350
            else -> 150
        }
}
