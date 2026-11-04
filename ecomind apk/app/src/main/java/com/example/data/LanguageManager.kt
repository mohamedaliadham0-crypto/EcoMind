package com.example.data

import androidx.compose.ui.unit.LayoutDirection

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String, val direction: LayoutDirection) {
    ENGLISH("en", "English", "English", LayoutDirection.Ltr),
    ARABIC("ar", "Arabic", "العربية", LayoutDirection.Rtl);

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.find { it.code == code } ?: ENGLISH
    }
}

data class LocalizedText(
    val en: String,
    val ar: String
) {
    fun get(language: AppLanguage): String = if (language == AppLanguage.ARABIC) ar else en
}

object Strings {
    val appTitle = LocalizedText("ECOMIND", "إيكومايند")
    val appSubtitle = LocalizedText("Smart Environmental Awareness", "التوعية والإعلام البيئي الذكي")
    val heroTagline = LocalizedText("Small Actions. Big Changes.", "خطوات صغيرة. تغييرات كبيرة.")
    val heroDescription = LocalizedText(
        "Empowering minds to understand climate systems, conserve natural resources, and take daily action for a sustainable future.",
        "تمكين العقول من فهم النظم المناخية، والحفاظ على الموارد الطبيعية، واتخاذ خطوات يومية لمستقبل مستدام."
    )

    // Navigation
    val navHome = LocalizedText("Home", "الرئيسية")
    val navExplore = LocalizedText("Explore", "استكشف")
    val navInfographic = LocalizedText("Infographic", "الإنفوجرافيك")
    val navChallenges = LocalizedText("Challenges", "التحديات")
    val navProfile = LocalizedText("Profile", "الملف الشخصي")

    // Home Screen Sections
    val todayTip = LocalizedText("Today's Eco Tip", "نصيحة اليوم البيئية")
    val dailyChallenge = LocalizedText("Daily Eco Challenge", "تحدي اليوم البيئي")
    val completeChallenge = LocalizedText("Mark as Completed", "تم إنجاز التحدي")
    val challengeDone = LocalizedText("Completed Today! +25 pts", "تم الإنجاز اليوم! +25 نقطة")
    val yourImpact = LocalizedText("Your Cumulative Impact", "أثرك البيئي التراكمي")
    val impactKwh = LocalizedText("Electricity Saved", "كهرباء موفرة")
    val impactWater = LocalizedText("Water Conserved", "مياه موفرة")
    val impactWaste = LocalizedText("Items Sorted", "عناصر مفروزة")
    val impactCo2 = LocalizedText("CO2 Reduced", "انبعاثات خُفضت")
    val learningProgress = LocalizedText("Learning Progress", "تقدم التعلم")
    val lessonsCompleted = LocalizedText("Lessons Finished", "دروس مكتملة")
    val quizAverage = LocalizedText("Quiz Score", "معدل الاختبارات")
    val quickTopics = LocalizedText("Explore Topics", "استكشف الموضوعات")
    val dailyMotivation = LocalizedText("Daily Motivation", "حكمة بيئية ملهمة")
    val presentationMode = LocalizedText("Presentation Mode", "وضع العرض التقديمي")
    val presentationModeDesc = LocalizedText("Interactive deck for teachers & judges", "عرض تفاعلي مخصص للتحكيم والفصول")

    // Categories
    val catEnergy = LocalizedText("Renewable Energy", "الطاقة المتجددة")
    val catWater = LocalizedText("Water Conservation", "ترشيد المياه")
    val catRecycling = LocalizedText("Waste & Recycling", "إدارة وتدوير المخلفات")
    val catClimate = LocalizedText("Climate Change", "التغير المناخي")
    val catBiodiversity = LocalizedText("Biodiversity", "التنوع البيولوجي")
    val catLiving = LocalizedText("Sustainable Living", "الحياة المستدامة")

    // Buttons & Common
    val startQuiz = LocalizedText("Take Knowledge Quiz", "ابدأ اختبار المعرفة")
    val playSortingGame = LocalizedText("Play Waste Sorting Game", "العب لعبة فرز النفايات")
    val openCalculators = LocalizedText("Eco Calculators", "الحاسبات البيئية")
    val electricityCalculator = LocalizedText("Electricity Savings Calculator", "حاسبة توفير الكهرباء")
    val waterCalculator = LocalizedText("Water Savings Calculator", "حاسبة ترشيد المياه")
    val calculate = LocalizedText("Calculate", "احسب التوفير")
    val learnMore = LocalizedText("Explore Lesson", "استكشف الدرس")
    val back = LocalizedText("Back", "رجوع")
    val close = LocalizedText("Close", "إغلاق")
    val next = LocalizedText("Next", "التالي")
    val previous = LocalizedText("Previous", "السابق")
    val share = LocalizedText("Share", "مشاركة")
    val exitPresentation = LocalizedText("Exit Presentation", "إنهاء العرض")
    val ecoPoints = LocalizedText("Eco Points", "نقاط بيئية")
    val ecoLevel = LocalizedText("Eco Level", "المستوى البيئي")

    // Settings
    val settingsTitle = LocalizedText("Settings", "الإعدادات")
    val languageSetting = LocalizedText("Language", "اللغة")
    val themeSetting = LocalizedText("Dark Mode", "الوضع الليلي")
    val soundSetting = LocalizedText("Sound & Haptics", "المؤثرات الصوتية والاهتزاز")
    val motionSetting = LocalizedText("Reduced Motion", "تقليل الحركة")
    val resetProgress = LocalizedText("Reset Progress", "إعادة ضبط التقدم")
    val resetConfirm = LocalizedText("Are you sure you want to reset all your points and achievements?", "هل أنت متأكد من رغبتك في إعادة ضبط جميع النقاط والإنجازات؟")
    val confirm = LocalizedText("Confirm", "تأكيد")
    val cancel = LocalizedText("Cancel", "إلغاء")
    val aboutApp = LocalizedText("About ECOMIND", "عن إيكومايند")
    val competitionInfo = LocalizedText(
        "Submitted to the Egyptian Ministry of Education's Green Minds Competition (مسابقة عقول خضراء) — Category 4: Smart Environmental Awareness and Media (التوعية والإعلام البيئي الذكي).",
        "مقدم لمسابقة عقول خضراء التابعة لوزارة التربية والتعليم والتعليم الفني المصرية — الفئة الرابعة: التوعية والإعلام البيئي الذكي."
    )
    val developerCredit = LocalizedText("Developer: Adham Mohamed Ali", "المطور: أدهم محمد علي")
}
