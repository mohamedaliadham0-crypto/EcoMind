package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.AppLanguage
import com.example.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EcoMindRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("ecomind_prefs", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(loadProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _appLanguage = MutableStateFlow(AppLanguage.fromCode(prefs.getString("pref_language", "en") ?: "en"))
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("pref_dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _soundEnabled = MutableStateFlow(prefs.getBoolean("pref_sound", true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _reducedMotion = MutableStateFlow(prefs.getBoolean("pref_reduced_motion", false))
    val reducedMotion: StateFlow<Boolean> = _reducedMotion.asStateFlow()

    private fun loadProfile(): UserProfile {
        return UserProfile(
            username = prefs.getString("user_name", "Green Student") ?: "Green Student",
            avatarEmoji = prefs.getString("user_avatar", "🌱") ?: "🌱",
            ecoPoints = prefs.getInt("user_points", 80),
            kwhSaved = prefs.getFloat("kwh_saved", 16.5f).toDouble(),
            litersSaved = prefs.getFloat("liters_saved", 140.0f).toDouble(),
            wasteSortedCount = prefs.getInt("waste_sorted", 8),
            completedLessonIds = prefs.getStringSet("completed_lessons", setOf("energy_solar")) ?: setOf("energy_solar"),
            completedChallengeIds = prefs.getStringSet("completed_challenges", emptySet()) ?: emptySet(),
            unlockedBadgeIds = prefs.getStringSet("unlocked_badges", setOf("badge_first_step")) ?: setOf("badge_first_step"),
            quizzesTaken = prefs.getInt("quizzes_taken", 1),
            totalQuizScore = prefs.getInt("quiz_total_score", 90)
        )
    }

    private fun saveProfile(profile: UserProfile) {
        prefs.edit().apply {
            putString("user_name", profile.username)
            putString("user_avatar", profile.avatarEmoji)
            putInt("user_points", profile.ecoPoints)
            putFloat("kwh_saved", profile.kwhSaved.toFloat())
            putFloat("liters_saved", profile.litersSaved.toFloat())
            putInt("waste_sorted", profile.wasteSortedCount)
            putStringSet("completed_lessons", profile.completedLessonIds)
            putStringSet("completed_challenges", profile.completedChallengeIds)
            putStringSet("unlocked_badges", profile.unlockedBadgeIds)
            putInt("quizzes_taken", profile.quizzesTaken)
            putInt("quiz_total_score", profile.totalQuizScore)
            apply()
        }
    }

    fun setLanguage(language: AppLanguage) {
        _appLanguage.value = language
        prefs.edit().putString("pref_language", language.code).apply()
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        prefs.edit().putBoolean("pref_dark_mode", enabled).apply()
    }

    fun setSoundEnabled(enabled: Boolean) {
        _soundEnabled.value = enabled
        prefs.edit().putBoolean("pref_sound", enabled).apply()
    }

    fun setReducedMotion(enabled: Boolean) {
        _reducedMotion.value = enabled
        prefs.edit().putBoolean("pref_reduced_motion", enabled).apply()
    }

    fun updateProfileNameAndAvatar(name: String, avatar: String) {
        _userProfile.update { current ->
            val updated = current.copy(username = name, avatarEmoji = avatar)
            saveProfile(updated)
            updated
        }
    }

    fun completeChallenge(challengeId: String, pointsAward: Int) {
        _userProfile.update { current ->
            if (current.completedChallengeIds.contains(challengeId)) return@update current
            val newChallenges = current.completedChallengeIds + challengeId
            val newPoints = current.ecoPoints + pointsAward
            val newBadges = checkAndUnlockBadges(current.copy(ecoPoints = newPoints, completedChallengeIds = newChallenges))
            val updated = current.copy(
                ecoPoints = newPoints,
                completedChallengeIds = newChallenges,
                unlockedBadgeIds = newBadges
            )
            saveProfile(updated)
            updated
        }
    }

    fun completeLesson(lessonId: String) {
        _userProfile.update { current ->
            if (current.completedLessonIds.contains(lessonId)) return@update current
            val newLessons = current.completedLessonIds + lessonId
            val newPoints = current.ecoPoints + 20
            val newBadges = checkAndUnlockBadges(current.copy(ecoPoints = newPoints, completedLessonIds = newLessons))
            val updated = current.copy(
                ecoPoints = newPoints,
                completedLessonIds = newLessons,
                unlockedBadgeIds = newBadges
            )
            saveProfile(updated)
            updated
        }
    }

    fun recordQuizResult(scorePercent: Int, categoryId: String) {
        _userProfile.update { current ->
            val newTaken = current.quizzesTaken + 1
            val newTotal = current.totalQuizScore + scorePercent
            val pointsEarned = (scorePercent / 5).coerceAtLeast(10)
            val newPoints = current.ecoPoints + pointsEarned
            val newBadges = checkAndUnlockBadges(current.copy(ecoPoints = newPoints, quizzesTaken = newTaken, totalQuizScore = newTotal))
            val updated = current.copy(
                quizzesTaken = newTaken,
                totalQuizScore = newTotal,
                ecoPoints = newPoints,
                unlockedBadgeIds = newBadges
            )
            saveProfile(updated)
            updated
        }
    }

    fun recordWasteSortingItem(isCorrect: Boolean) {
        if (!isCorrect) return
        _userProfile.update { current ->
            val newCount = current.wasteSortedCount + 1
            val newPoints = current.ecoPoints + 5
            val newBadges = checkAndUnlockBadges(current.copy(wasteSortedCount = newCount, ecoPoints = newPoints))
            val updated = current.copy(
                wasteSortedCount = newCount,
                ecoPoints = newPoints,
                unlockedBadgeIds = newBadges
            )
            saveProfile(updated)
            updated
        }
    }

    fun addImpactCalculations(kwhDelta: Double, litersDelta: Double) {
        _userProfile.update { current ->
            val newKwh = current.kwhSaved + kwhDelta
            val newLiters = current.litersSaved + litersDelta
            val newPoints = current.ecoPoints + 15
            val newBadges = checkAndUnlockBadges(current.copy(kwhSaved = newKwh, litersSaved = newLiters, ecoPoints = newPoints))
            val updated = current.copy(
                kwhSaved = newKwh,
                litersSaved = newLiters,
                ecoPoints = newPoints,
                unlockedBadgeIds = newBadges
            )
            saveProfile(updated)
            updated
        }
    }

    fun resetAllProgress() {
        val defaultProfile = UserProfile(
            username = "Eco Explorer",
            avatarEmoji = "🌱",
            ecoPoints = 0,
            kwhSaved = 0.0,
            litersSaved = 0.0,
            wasteSortedCount = 0,
            completedLessonIds = emptySet(),
            completedChallengeIds = emptySet(),
            unlockedBadgeIds = setOf("badge_first_step"),
            quizzesTaken = 0,
            totalQuizScore = 0
        )
        _userProfile.value = defaultProfile
        saveProfile(defaultProfile)
    }

    private fun checkAndUnlockBadges(profile: UserProfile): Set<String> {
        val badges = profile.unlockedBadgeIds.toMutableSet()
        badges.add("badge_first_step")
        if (profile.ecoPoints >= 150) badges.add("badge_points_150")
        if (profile.ecoPoints >= 500) badges.add("badge_points_500")
        if (profile.kwhSaved >= 20.0) badges.add("badge_energy_saver")
        if (profile.litersSaved >= 100.0) badges.add("badge_water_guardian")
        if (profile.wasteSortedCount >= 10) badges.add("badge_recycle_hero")
        if (profile.completedLessonIds.size >= 4) badges.add("badge_knowledge_master")
        if (profile.quizzesTaken >= 3) badges.add("badge_quiz_champ")
        return badges
    }

    // ----------------------------------------------------
    // STATIC DATA: Lessons, Challenges, Quizzes, Badges, Waste
    // ----------------------------------------------------

    val challenges: List<Challenge> = listOf(
        Challenge(
            id = "chal_daily_unplug",
            titleEn = "Unplug Phantom Electronics",
            titleAr = "افصل الأجهزة الكهربائية الخاملة",
            descEn = "Disconnect chargers, gaming consoles, and appliances when not actively in use to eliminate stand-by power draw.",
            descAr = "افصل شواحن الهواتف والأجهزة الكهربائية غير المستخدمة من القابس لتجنب استهلاك الطاقة الاحتياطية المهدرة.",
            category = "Energy",
            difficulty = "Easy",
            points = 25,
            criteriaEn = "Unplugged at least 3 devices before going to sleep.",
            criteriaAr = "فصلت 3 أجهزة غير مستخدمة على الأقل قبل النوم.",
            isDaily = true
        ),
        Challenge(
            id = "chal_daily_shorter_shower",
            titleEn = "4-Minute Power Shower",
            titleAr = "استحمام سريع لمدة 4 دقائق",
            descEn = "Limit shower time to under 4 minutes. Standard shower heads dispense up to 10 liters per minute!",
            descAr = "قلل مدة الاستحمام إلى 4 دقائق فقط. رؤوس الدش تستهلك قرابة 10 لترات من الماء العذب كل دقيقة!",
            category = "Water",
            difficulty = "Medium",
            points = 30,
            criteriaEn = "Kept shower duration under 4 minutes with water turned off while lathering.",
            criteriaAr = "أنهيت الاستحمام في أقل من 4 دقائق مع إغلاق الصنبور أثناء استخدام الصابون.",
            isDaily = true
        ),
        Challenge(
            id = "chal_daily_reusable_bag",
            titleEn = "Zero Single-Use Bags",
            titleAr = "يوم بدون أكياس بلاستيكية أحادية الاستخدام",
            descEn = "Refuse single-use plastic grocery bags. Bring a reusable cloth or canvas tote for all errands.",
            descAr = "ارفض استخدام الأكياس البلاستيكية أحادية الاستخدام واستعن بحقيبة قماشية مستدامة قابلة لإعادة الاستخدام.",
            category = "Waste",
            difficulty = "Easy",
            points = 25,
            criteriaEn = "Used reusable tote or refused disposable plastic bag today.",
            criteriaAr = "استخدمت حقيبة قماشية ورفضت كيساً بلاستيكياً اليوم.",
            isDaily = true
        ),
        Challenge(
            id = "chal_weekly_meatless",
            titleEn = "One Plant-Based Day",
            titleAr = "يوم نباتي صديق للمناخ",
            descEn = "Eat entirely plant-rich meals for one full day. Plant agriculture generates a fraction of animal farming emissions.",
            descAr = "اعتمد على وجبات نباتية خالية من اللحوم ليوم كامل لتقليل البصمة الكربونية الناتجة عن الزراعة الحيوانية.",
            category = "Lifestyle",
            difficulty = "Medium",
            points = 50,
            criteriaEn = "Consumed plant-based meals throughout an entire day.",
            criteriaAr = "تناولت وجبات نباتية صحية طوال اليوم.",
            isDaily = false
        ),
        Challenge(
            id = "chal_weekly_nature_walk",
            titleEn = "Green Commute Challenge",
            titleAr = "تحدي التنقل الأخضر المستدام",
            descEn = "Walk, bike, or take public transit instead of a private motorized car ride for at least two local journeys.",
            descAr = "استخدم المشي أو الدراجة أو وسائل النقل الجماعي بدلاً من السيارة الخاصة في رحلتين على الأقل.",
            category = "Lifestyle",
            difficulty = "Hard",
            points = 60,
            criteriaEn = "Replaced a car ride with walking or shared transit.",
            criteriaAr = "استبدلت مشواراً بالسيارة بالمشي أو المواصلات الجماعية.",
            isDaily = false
        ),
        Challenge(
            id = "chal_weekly_waste_audit",
            titleEn = "Household Waste Separation",
            titleAr = "فرز المخلفات المنزلية لـ 3 أيام",
            descEn = "Set up dedicated collection containers for plastics, cardboard, and organics for 3 consecutive days.",
            descAr = "خصص أوعية منفصلة لفرز البلاستيك والورق والنفايات العضوية في منزلك لثلاثة أيام متتالية.",
            category = "Waste",
            difficulty = "Hard",
            points = 70,
            criteriaEn = "Sorted domestic trash into organic vs recyclable categories.",
            criteriaAr = "تم فرز النفايات المنزلية إلى فئات قابلة للتدوير وعضوية بنجاح.",
            isDaily = false
        )
    )

    val badges: List<EcoBadge> = listOf(
        EcoBadge(
            id = "badge_first_step",
            titleEn = "First Step",
            titleAr = "الخطوة الأولى",
            descEn = "Joined the ECOMIND community and began your green journey.",
            descAr = "انضممت إلى مجتمع إيكومايند وبدأت رحلتك الخضراء.",
            emoji = "🌱",
            requirementEn = "Welcome badge upon joining.",
            requirementAr = "وسام ترحيبي بمجرد بدء التطبيق.",
            isUnlocked = true
        ),
        EcoBadge(
            id = "badge_points_150",
            titleEn = "Green Sprout",
            titleAr = "البرعم الأخضر",
            descEn = "Earned 150+ eco points through actions and learning.",
            descAr = "جمعت أكثر من 150 نقطة بيئية عبر الأنشطة والمعرفة.",
            emoji = "🌿",
            requirementEn = "Reach 150 Eco Points.",
            requirementAr = "الوصول إلى 150 نقطة بيئية."
        ),
        EcoBadge(
            id = "badge_points_500",
            titleEn = "Earth Champion",
            titleAr = "بطل الأرض",
            descEn = "Demonstrated outstanding environmental awareness and commitment.",
            descAr = "أظهرت وعياً بيئياً استثنائياً والتزاماً ملموساً بالاستدامة.",
            emoji = "🌍",
            requirementEn = "Reach 500 Eco Points.",
            requirementAr = "الوصول إلى 500 نقطة بيئية."
        ),
        EcoBadge(
            id = "badge_energy_saver",
            titleEn = "Watt Warrior",
            titleAr = "فارس الطاقة",
            descEn = "Calculated or reduced over 20 kWh of electricity.",
            descAr = "وفرت وحسبت ترشيد أكثر من 20 كيلووات/ساعة من الكهرباء.",
            emoji = "⚡",
            requirementEn = "Save 20+ kWh electricity.",
            requirementAr = "ترشيد 20 ك.و.س كهرباء."
        ),
        EcoBadge(
            id = "badge_water_guardian",
            titleEn = "Water Guardian",
            titleAr = "حارس قطرة الماء",
            descEn = "Committed to freshwater conservation of over 100 liters.",
            descAr = "التزمت بترشيد أكثر من 100 لتر من المياه العذبة الثمينة.",
            emoji = "💧",
            requirementEn = "Conserve 100+ liters of water.",
            requirementAr = "ترشيد 100 لتر مياه."
        ),
        EcoBadge(
            id = "badge_recycle_hero",
            titleEn = "Zero Waste Master",
            titleAr = "خبير إعادة التدوير",
            descEn = "Successfully sorted 10+ waste items in the sorting activity.",
            descAr = "نجحت في فرز أكثر من 10 عناصر في لعبة وتدريب الفرز.",
            emoji = "♻️",
            requirementEn = "Sort 10 items correctly in waste game.",
            requirementAr = "فرز 10 عناصر صحيحة بلعبة الفرز."
        ),
        EcoBadge(
            id = "badge_knowledge_master",
            titleEn = "Eco Scholar",
            titleAr = "الباحث البيئي",
            descEn = "Completed 4 comprehensive environmental learning modules.",
            descAr = "أتممت دراسة 4 وحدات تعليمية بيئية متكاملة.",
            emoji = "📚",
            requirementEn = "Finish 4 learning lessons.",
            requirementAr = "إتمام 4 دروس تعليمية."
        ),
        EcoBadge(
            id = "badge_quiz_champ",
            titleEn = "Green Genius",
            titleAr = "العبقري الأخضر",
            descEn = "Tested environmental knowledge with high distinction across multiple quizzes.",
            descAr = "اجتزت اختبارات المعرفة البيئية بتفوق وتميز.",
            emoji = "🏆",
            requirementEn = "Complete 3 environmental quizzes.",
            requirementAr = "إكمال 3 اختبارات بيئية."
        )
    )

    val wasteItems: List<WasteItem> = listOf(
        WasteItem(
            id = "w1",
            nameEn = "Plastic Water Bottle",
            nameAr = "زجاجة مياه بلاستيكية",
            type = WasteType.PLASTIC,
            iconEmoji = "🧴",
            tipEn = "Empty liquid and crush before placing in recycling bins. Takes ~450 years to decompose naturally.",
            tipAr = "أفرغ السائل واضغطها لتفريغ الهواء قبل وضعها في صندوق البلاستيك. تستغرق 450 عاماً للتحلل في الطبيعة!"
        ),
        WasteItem(
            id = "w2",
            nameEn = "Banana Peel",
            nameAr = "قشرة موز",
            type = WasteType.ORGANIC,
            iconEmoji = "🍌",
            tipEn = "Rich in potassium and nitrogen; perfect for organic compost to nourish agricultural soil.",
            tipAr = "غنية بالبوتاسيوم والنيتروجين، مثالية للسماد العضوي لتغذية التربة الزراعية."
        ),
        WasteItem(
            id = "w3",
            nameEn = "Old AA Battery",
            nameAr = "بطارية قلم قديمة",
            type = WasteType.E_WASTE,
            iconEmoji = "🔋",
            tipEn = "Contains heavy metals like zinc and nickel. Never put in general waste; take to specialized e-waste drops.",
            tipAr = "تحتوي على معادن ثقيلة كالزنك والنيكل. لا تلقها أبداً في القمامة العادية، بل في نقاط تجميع الإلكترونيات."
        ),
        WasteItem(
            id = "w4",
            nameEn = "Newspaper / Cardboard",
            nameAr = "جريدة أو كرتون",
            type = WasteType.PAPER,
            iconEmoji = "📰",
            tipEn = "Paper can be recycled 5 to 7 times before wood fibers break down. Keep dry and clean.",
            tipAr = "يمكن إعادة تدوير الورق من 5 إلى 7 مرات قبل تكسر الألياف. احرص على أن يظل جافاً ونظيفاً."
        ),
        WasteItem(
            id = "w5",
            nameEn = "Apple Core",
            nameAr = "بقايا تفاحة",
            type = WasteType.ORGANIC,
            iconEmoji = "🍏",
            tipEn = "Organic matter decomposing in landfills creates methane, but in composting it creates rich humus.",
            tipAr = "تحلل المواد العضوية بالمكبات ينتج غاز الميثان الضار، بينما تحويلها لسماد ينتج مادة عضوية مخصبة."
        ),
        WasteItem(
            id = "w6",
            nameEn = "Broken Smartphone Screen",
            nameAr = "شاشة هاتف مكسورة",
            type = WasteType.E_WASTE,
            iconEmoji = "📱",
            tipEn = "Electronic circuit boards contain precious metals (gold, copper, lithium) requiring circular reclamation.",
            tipAr = "تحتوي لوحات الدوائر الإلكترونية على معادن ثمينة ونادرة (ذهب، نحاس، ليثيوم) تتطلب استخلاصاً متخصصاً."
        ),
        WasteItem(
            id = "w7",
            nameEn = "Clean Milk Carton",
            nameAr = "عبوة حليب كرتونية",
            type = WasteType.PAPER,
            iconEmoji = "🥛",
            tipEn = "Multi-material carton packaging is sorted with specialized paper and fiber hydropulpers.",
            tipAr = "يتم فصل الألياف الكرتونية الممتازة في مصانع التدوير المتخصصة بعد غسلها وتجفيفها."
        ),
        WasteItem(
            id = "w8",
            nameEn = "Plastic Detergent Jug",
            nameAr = "عبوة منظف بلاستيكية (HDPE)",
            type = WasteType.PLASTIC,
            iconEmoji = "🧼",
            tipEn = "High-Density Polyethylene (HDPE #2) is highly recyclable into drainage pipes, park benches, and crates.",
            tipAr = "البولي إيثيلين عالي الكثافة (HDPE رقم 2) من أسهل اللدائن تدويراً لإنتاج أنابيب ومقاعد ومستلزمات جديدة."
        )
    )

    val verifiedFacts: List<FactItem> = listOf(
        FactItem(
            id = "fact_1",
            titleEn = "Solar Radiation Potential",
            titleAr = "الطاقة الشمسية الواصلة للأرض",
            factEn = "More solar energy strikes the Earth in a single hour and a half than the entire world uses in an entire year.",
            factAr = "كمية الطاقة الشمسية التي تصطدم بالأرض في ساعة ونصف فقط تتجاوز إجمالي استهلاك البشرية للطاقة في عام كامل!",
            source = "International Energy Agency (IEA)",
            category = "Energy"
        ),
        FactItem(
            id = "fact_2",
            titleEn = "Freshwater Scarcity",
            titleAr = "ندرة المياه العذبة",
            factEn = "Less than 1% of all water on Earth is liquid fresh water accessible for direct human ecosystem and domestic use.",
            factAr = "أقل من 1% فقط من إجمالي مياه كوكب الأرض هي مياه عذبة سائلة متاحة وميسرة للاستخدام البشري المباشر.",
            source = "United Nations Environment Programme (UNEP)",
            category = "Water"
        ),
        FactItem(
            id = "fact_3",
            titleEn = "Ocean Plastic Lifespan",
            titleAr = "بقاء البلاستيك في المحيطات",
            factEn = "Every single piece of petroleum-based plastic ever manufactured still exists today in some form, breaking down into microplastics.",
            factAr = "كل قطعة بلاستيك بترولية صُنعت منذ اختراع اللدائن لا تزال موجودة حتى اليوم، حيث تتفتت إلى جسيمات ميكروبلاستيك دقيقة.",
            source = "UNESCO & UNEP Global Plastic Outlook",
            category = "Recycling"
        ),
        FactItem(
            id = "fact_4",
            titleEn = "Benban Solar Park in Egypt",
            titleAr = "مجمع بنبان للطاقة الشمسية في مصر",
            factEn = "Egypt's Benban Solar Complex in Aswan is one of the largest photovoltaic plants in the world, generating 1,465 MW of clean green power.",
            factAr = "يعد مجمع بنبان للطاقة الشمسية بمحافظة أسوان في مصر من أضخم محطات الطاقة الشمسية في العالم بقدرة إنتاجية 1465 ميجاوات نظيفة.",
            source = "Egyptian Ministry of Electricity & Renewable Energy",
            category = "Energy"
        ),
        FactItem(
            id = "fact_5",
            titleEn = "Forest Carbon Sinks",
            titleAr = "بالوعات الكربون في الغابات",
            factEn = "A mature tree absorbs approximately 22 kilograms of carbon dioxide every year while generating clean oxygen for 2 adults.",
            factAr = "تمتص الشجرة البالغة ما يقارب 22 كيلوجراماً من ثاني أكسيد الكربون سنوياً وتنتج أكسجيناً نقياً يكفي شخصين بالغين.",
            source = "NASA Earth Observatory",
            category = "Biodiversity"
        ),
        FactItem(
            id = "fact_6",
            titleEn = "Climate vs Weather Distinction",
            titleAr = "الفرق بين الطقس والمناخ",
            factEn = "Weather describes atmospheric conditions over hours and days; climate is the statistical average of weather patterns over at least 30 years.",
            factAr = "الطقس يصف تقلبات الغلاف الجوي خلال ساعات وأيام معدودة، أما المناخ فهو المتوسط الإحصائي للأنماط الجوية على مدار 30 عاماً على الأقل.",
            source = "World Meteorological Organization (WMO)",
            category = "Climate"
        )
    )

    val lessons: List<Lesson> = listOf(
        Lesson(
            id = "energy_solar",
            categoryId = "energy",
            titleEn = "Renewable Energy: Solar & Wind Power",
            titleAr = "الطاقة المتجددة: الطاقة الشمسية وطاقة الرياح",
            summaryEn = "Harnessing the infinite kinetic power of the sun and atmospheric currents to generate zero-emission electricity.",
            summaryAr = "تسخير القدرة اللانهائية لإشعاع الشمس والتيارات الهوائية لتوليد طاقة كهربائية نظيفة وخالية من الانبعاثات.",
            scientificFactEn = "Photovoltaic cells use semiconductor materials (primarily silicon) to absorb photons, dislodging electrons and generating a direct electrical current (DC) through the photoelectric effect.",
            scientificFactAr = "تعتمد الخلايا الكهروضوئية على أشباه الموصلات (كالسيليكون) لامتصاص فوتونات الضوء مما يحرك الإلكترونات ويولد تياراً كهربائياً مستمراً بالظاهرة الكهروضوئية.",
            realWorldExampleEn = "The Benban Solar Park in Aswan, Egypt covers 37 square kilometers and generates clean electricity avoiding over 2 million tons of greenhouse gas emissions annually.",
            realWorldExampleAr = "مجمع بنبان للطاقة الشمسية بأسوان في مصر يمتد على مساحة 37 كم² ويوفر طاقة متجددة تجنب كوكبنا أكثر من مليوني طن من انبعاثات الكربون سنوياً.",
            practicalActionsEn = listOf(
                "Adopt solar water heating systems for domestic hot water.",
                "Advocate for rooftop solar installations in educational institutions.",
                "Choose appliances powered by verifiable clean energy grid mixes.",
                "Conserve electricity so the proportion of clean energy on the grid increases."
            ),
            practicalActionsAr = listOf(
                "استخدام السخانات الشمسية لتسخين المياه المنزلية بدلاً من الكهرباء والغاز.",
                "المطالبة بتركيب خلايا شمسية فوق أسطح المدارس والمباني التعليمية.",
                "استخدام أجهزة ومعدات تستهلك طاقة منخفضة لتعظيم نسبة الطاقة المتجددة في الشبكة.",
                "إطفاء الإنارة غير الضرورية لتخفيف الحمل على محطات التوليد."
            )
        ),
        Lesson(
            id = "water_conservation",
            categoryId = "water",
            titleEn = "Water Conservation: The Nile & Freshwater Security",
            titleAr = "ترشيد المياه: شريان النيل والأمن المائي العذب",
            summaryEn = "Preserving life's most precious molecule through modern irrigation, leak prevention, and watershed protection.",
            summaryAr = "حماية أغلى جزيئات الحياة عبر تحديث أساليب الري، ومنع التسريبات، وصيانة المجاري المائية والأنهار.",
            scientificFactEn = "Fresh water represents only 2.5% of Earth's water, and over 68% of that is locked in polar ice caps. Agriculture consumes ~70% of global freshwater withdrawals.",
            scientificFactAr = "تمثل المياه العذبة 2.5% فقط من مياه الأرض، وأكثر من 68% منها محتجز في القمم الجليدية، بينما يستهلك قطاع الزراعة قرابة 70% من المياه العذبة المستخرجة.",
            realWorldExampleEn = "Transitioning from traditional flood irrigation to precision drip irrigation in the Nile Delta conserves up to 40% of irrigation water while increasing crop yields.",
            realWorldExampleAr = "التحول من الري بالغمر التقليدي إلى الري بالتنقيط الحديث في حوض النيل يوفر ما يصل إلى 40% من مياه الري مع زيادة إنتاجية المحاصيل.",
            practicalActionsEn = listOf(
                "Install low-flow aerators on kitchen and bathroom faucets.",
                "Turn off the tap while brushing teeth and washing hands, saving 12 liters each time.",
                "Fix leaking toilet flappers and pipe joints promptly.",
                "Never dump chemical contaminants or plastics into water canals or rivers."
            ),
            practicalActionsAr = listOf(
                "تركيب موفرات المياه وقطع الترشيد (المهويات) على صنابير المياه المنزلية.",
                "غلق الصنبور تماماً أثناء تنظيف الأسنان أو غسل الأيدي لتوفير ما يزيد عن 12 لتراً في المرة.",
                "إصلاح التسريبات في أسرع وقت فالصنبور المتسرب يهدر آلاف اللترات سنوياً.",
                "الامتناع التام عن إلقاء المخلفات البلاستيكية أو الكيميائية في الترع ومجرى النيل."
            )
        ),
        Lesson(
            id = "waste_recycling",
            categoryId = "recycling",
            titleEn = "Recycling & The Circular Economy",
            titleAr = "إعادة التدوير والاقتصاد الدائري المستدام",
            summaryEn = "Transitioning from a linear 'take-make-waste' model into a regenerative circular loop where resources retain high value.",
            summaryAr = "التحول من نموذج 'الاستخراج والتصنيع والإلقاء' الخطي إلى دورة مغلقة تحافظ على قيمة الموارد وتمنع التلوث.",
            scientificFactEn = "Recycling an aluminum beverage can saves 95% of the energy required to smelt new aluminum from raw bauxite ore.",
            scientificFactAr = "إعادة تدوير علبة ألومنيوم واحدة يوفر 95% من الطاقة الكهربائية الهائلة اللازمة لاستخراج ألومنيوم جديد من خام البوكسيت الطبيعي.",
            realWorldExampleEn = "Community waste sorters (such as the Zabaleen in Cairo's Mokattam district) achieve recycling efficiencies exceeding 80%, outperforming many automated municipal centers worldwide.",
            realWorldExampleAr = "تحقق مجتمعات إعادة التدوير في حي المقطم بالقاهرة نسب فرز وتدوير استثنائية تتجاوز 80% متفوقة على كثير من الأنظمة العالمية الميكانيكية.",
            practicalActionsEn = listOf(
                "Implement source-separation at home: dry recyclables vs wet food scraps.",
                "Rinse food containers before placing them in the recycling bin.",
                "Replace single-use beverage containers with durable stainless steel bottles.",
                "Compost fruit and vegetable peels into garden soil conditioner."
            ),
            practicalActionsAr = listOf(
                "فصل النفايات من المنبع في المنزل: مواد جافة قابلة للتدوير ومخلفات طعام عضوية.",
                "شطف العبوات البلاستيكية والمعدنية من بقايا الأطعمة قبل التدوير لضمان جودتها.",
                "استبدال عبوات المياه البلاستيكية بزجاجات استانلس ستيل متينة تدوم لسنوات.",
                "صنع سماد عضوي من قشور الخضار والفواكه لإحياء تربة الأشجار والنباتات."
            )
        ),
        Lesson(
            id = "climate_change",
            categoryId = "climate",
            titleEn = "Climate Science: The Greenhouse Effect & Global Warming",
            titleAr = "علوم المناخ: ظاهرة الاحتباس الحراري والاحترار العالمي",
            summaryEn = "Understanding how atmospheric greenhouse gases trap thermal radiation and how global anthropogenic emissions alter planetary equilibrium.",
            summaryAr = "فهم آليات حبس الغازات الدفيئة للإشعاع الحراري وكيف تؤدي الانبعاثات البشرية إلى اضطراب التوازن المناخي لكوكب الأرض.",
            scientificFactEn = "Carbon dioxide (CO2), methane (CH4), and nitrous oxide trap outgoing infrared radiation. Since the industrial revolution, atmospheric CO2 has surged from 280 ppm to over 420 ppm.",
            scientificFactAr = "تحبس غازات ثاني أكسيد الكربون والميثان الأشعة تحت الحمراء المنبعثة من الأرض، ومنذ الثورة الصناعية قفز تركيز الكربون من 280 جزء بالمليون إلى أكثر من 420 جزءاً بالمليون.",
            realWorldExampleEn = "Rising sea temperatures accelerate polar ice shelf melting, threatening coastal deltas worldwide including the Mediterranean coastline and the Nile Delta.",
            realWorldExampleAr = "ارتفاع درجات حرارة المحيطات يسرع ذوبان الكتل الجليدية، مما يهدد السواحل والدلتا المنخفضة حول العالم ومن بينها سواحل البحر المتوسط ودلتا النيل.",
            practicalActionsEn = listOf(
                "Distinguish day-to-day weather fluctuations from multi-decade climatic trends.",
                "Calculate personal carbon footprints and minimize fossil fuel transport.",
                "Support renewable clean energy transitions in policy and personal choices.",
                "Plant native regional trees that capture carbon and cool urban heat islands."
            ),
            practicalActionsAr = listOf(
                "إدراك الفارق العلمي بين تقلبات الطقس اليومية والتحولات المناخية عبر العقود.",
                "حساب البصمة الكربونية الشخصية وتقليل الاعتماد على الوقود الأحفوري.",
                "دعم مشروعات الطاقة الخضراء والتشجير في المجتمعات المحلية والمدارس.",
                "زراعة الأشجار المحلية المقاومة للجفاف لامتصاص الكربون وتلطيف حرارة المدن."
            )
        ),
        Lesson(
            id = "biodiversity",
            categoryId = "biodiversity",
            titleEn = "Biodiversity & Ecosystem Resilience",
            titleAr = "التنوع البيولوجي وصمود النظم البيئية",
            summaryEn = "Every species, from microscopic soil mycorrhizae to apex predators, weaves the self-sustaining web of biosphere life.",
            summaryAr = "كل كائن حي من البكتيريا النافعة وفطريات التربة إلى المفترسات العليا ينسج شبكة الحياة التي تحفظ بقاء كوكبنا.",
            scientificFactEn = "Over 1 million animal and plant species are currently threatened with extinction due to habitat fragmentation, ocean acidification, and invasive species introduction.",
            scientificFactAr = "أكثر من مليون نوع من الكائنات الحية مهدد بالانقراض نتيجة تدمير البيئات الطبيعية، وتحمض المحيطات، والتلوث والصيد الجائر.",
            realWorldExampleEn = "The Red Sea coral reefs in Egypt exhibit extraordinary biological resilience and thermal tolerance to warming waters, making them a globally vital genetic sanctuary.",
            realWorldExampleAr = "تتميز الشعاب المرجانية في البحر الأحمر بمصر بقدرة فريدة على التكيف ومقاومة ارتفاع درجات الحرارة مما يجعلها ملاذاً بيولوجياً عالمياً لحماية المحيطات.",
            practicalActionsEn = listOf(
                "Avoid purchasing products derived from endangered flora or fauna.",
                "Protect local pollinator populations (bees, butterflies) by planting flowering native flora.",
                "Refuse single-use plastics that wash into marine habitats and harm sea turtles.",
                "Visit nature reserves responsibly, adhering to 'Leave No Trace' principles."
            ),
            practicalActionsAr = listOf(
                "الامتناع التام عن شراء أي منتجات مصنوعة من كائنات مهددة بالانقراض كالعاج أو الأصداف النادرة.",
                "حماية الملقحات الطبيعية كالنحل والفراشات عبر زراعة نباتات مزهرة في الحدائق والشرفات.",
                "منع وصول البلاستيك للشواطئ والبحار لحماية السلاحف البحرية والأسماك.",
                "اتباع مبادئ 'لا تترك أثراً' عند زيارة المحميات الطبيعية والمتنزهات."
            )
        ),
        Lesson(
            id = "sustainable_living",
            categoryId = "living",
            titleEn = "Sustainable Living: The Conscious Daily Citizen",
            titleAr = "الحياة المستدامة: عادات المواطن البيئي الواعي",
            summaryEn = "Transforming personal habits in food, energy, transport, and procurement into a regenerative lifestyle.",
            summaryAr = "تحويل الممارسات اليومية في الغذاء والطاقة والتنقل والاستهلاك إلى أسلوب حياة متناغم مع الطبيعة.",
            scientificFactEn = "Food waste squanders 24% of all agricultural water and is responsible for roughly 8-10% of global greenhouse emissions.",
            scientificFactAr = "هدر الطعام مسؤول عن ضياع 24% من إجمالي المياه المستخدمة بالزراعة ويسبب ما بين 8 إلى 10% من انبعاثات الغازات الدفيئة عالمياً.",
            realWorldExampleEn = "Switching from incandescent incandescent bulbs to LED lighting reduces domestic illumination electricity consumption by over 80% immediately.",
            realWorldExampleAr = "استبدال المصابيح المتوهجة القديمة بمصابيح LED موفرة يخفض استهلاك كهرباء الإضاءة بأكثر من 80% في الحال.",
            practicalActionsEn = listOf(
                "Plan weekly grocery purchases carefully to prevent perishable food spoilage.",
                "Use natural cross-ventilation before switching on air conditioning units.",
                "Carry reusable utensils, water canteens, and shopping totes everywhere.",
                "Share environmental facts and inspire fellow classmates, family, and peers."
            ),
            practicalActionsAr = listOf(
                "تخطيط شراء الوجبات بحكمة لتجنب تلف وهدر الأطعمة الغذائية.",
                "الاعتماد على التهوية الطبيعية قبل تشغيل أجهزة التكييف ذات الاستهلاك المرتفع.",
                "اصطحاب زجاجة ماء وحقيبة قماشية بصفة مستمرة لتقليل المخلفات أحادية الاستخدام.",
                "نشر الوعي البيئي وتطبيق الممارسات الخضراء بين الزملاء في المدرسة والأسرة."
            )
        )
    )

    // Over 30 Comprehensive Quiz Questions across the 6 categories!
    val quizQuestions: List<QuizQuestion> = listOf(
        // RENEWABLE ENERGY (1 - 5)
        QuizQuestion(
            id = "q_energy_1",
            categoryId = "energy",
            questionEn = "What is the primary physical effect through which solar photovoltaic panels convert sunlight into direct current?",
            questionAr = "ما هو التأثير الفيزيائي الأساسي الذي تحول به الألواح الكهروضوئية ضوء الشمس إلى تيار كهربائي مستمر؟",
            optionsEn = listOf("Photoelectric effect", "Thermonuclear fission", "Combustion reaction", "Electromagnetic induction"),
            optionsAr = listOf("الظاهرة الكهروضوئية", "الانشطار النووي الحراري", "تفاعل الاحتراق", "الحث الكهرومغناطيسي"),
            correctIndex = 0,
            explanationEn = "In 1905, Albert Einstein explained the photoelectric effect, showing that photons can knock electrons free from semiconductor atoms, creating electricity.",
            explanationAr = "فسر ألبرت أينشتاين الظاهرة الكهروضوئية عام 1905، والتي تطلق فيها فوتونات الضوء إلكترونات حرة من ذرات أشباه الموصلات لتوليد تيار كهربائي."
        ),
        QuizQuestion(
            id = "q_energy_2",
            categoryId = "energy",
            questionEn = "Where is the renowned Benban Solar Park located in Egypt?",
            questionAr = "أين يقع مجمع بنبان الشهير للطاقة الشمسية في جمهورية مصر العربية؟",
            optionsEn = listOf("Aswan Governorate", "Alexandria", "Suez", "New Valley"),
            optionsAr = listOf("محافظة أسوان", "محافظة الإسكندرية", "محافظة السويس", "الوادي الجديد"),
            correctIndex = 0,
            explanationEn = "Benban Solar Park is located in Aswan Governorate in Upper Egypt, capitalizing on one of the world's highest solar irradiance levels.",
            explanationAr = "يقع مجمع بنبان بمحافظة أسوان في صعيد مصر للاستفادة من أعلى معدلات الإشعاع الشمسي في العالم."
        ),
        QuizQuestion(
            id = "q_energy_3",
            categoryId = "energy",
            questionEn = "How do modern wind turbines generate electricity from wind?",
            questionAr = "كيف تولد توربينات الرياح الحديثة الكهرباء من حركة الهواء؟",
            optionsEn = listOf("Wind turns blades that spin a shaft connected to an electric generator", "Wind burns hydrogen fuel inside the turbine", "Wind reflects light onto photovoltaic cells", "Wind heats chemical water boilers"),
            optionsAr = listOf("الرياح تدير الشفرات التي تدير عموداً متصلاً بمولد كهربائي", "الرياح تحرق وقود الهيدروجين داخل التوربين", "الرياح تعكس الضوء على خلايا شمسية", "الرياح تسخن مراجل مائية كيميائية"),
            correctIndex = 0,
            explanationEn = "Kinetic aerodynamic lift spins turbine rotor blades, which rotate an internal shaft connected to a generator that converts mechanical energy into electricity.",
            explanationAr = "الطاقة الحركية للهواء تحرك ريش التوربين بديناميكا الرفع، فتدير عمود الدوران المتصل بمولد كهرومغناطيسي يحول الطاقة الحركية لكهرباء."
        ),
        QuizQuestion(
            id = "q_energy_4",
            categoryId = "energy",
            questionEn = "Which of the following is considered a non-intermittent (baseload) renewable energy source?",
            questionAr = "أي من المصادر التالية يعتبر مصدر طاقة متجددة مستمر يمكنه توفير حمل قاعدي دائم؟",
            optionsEn = listOf("Geothermal Energy", "Solar PV without batteries", "Small wind turbines", "Atmospheric lightning"),
            optionsAr = listOf("الطاقة الحرارية الجوفية", "الطاقة الشمسية بدون بطاريات", "توربينات الرياح الصغيرة", "البرق الجوي"),
            correctIndex = 0,
            explanationEn = "Geothermal energy draws upon the continuous, non-stop heat of the Earth's core, providing continuous power independent of daily weather.",
            explanationAr = "تستمد الطاقة الحرارية الجوفية حرارتها المستمرة من باطن الأرض دون توقف بغض النظر عن الليل والنهار أو تقلبات الطقس."
        ),
        QuizQuestion(
            id = "q_energy_5",
            categoryId = "energy",
            questionEn = "What is the key environmental advantage of replacing fossil power plants with renewables?",
            questionAr = "ما هي الميزة البيئية الجوهرية لاستبدال محطات الوقود الأحفوري بمصادر الطاقة المتجددة؟",
            optionsEn = listOf("Virtually zero greenhouse gas emissions during power generation", "Consumes massive amounts of diesel", "Produces radioactive fuel waste", "Requires building large smokestacks"),
            optionsAr = listOf("انعدام انبعاثات الغازات الدفيئة أثناء توليد الكهرباء", "تستهلك كميات ضخمة من الديزل", "تنتج مخلفات وقود مشعة", "تتطلب بناء مداخن عملاقة"),
            correctIndex = 0,
            explanationEn = "Renewable sources generate electricity without burning hydrocarbons, thereby preventing carbon dioxide and sulfur dioxide emissions.",
            explanationAr = "تولد المصادر المتجددة الطاقة دون حرق المواد الهيدروكربونية مما يمنع انبعاث غازات الاحتباس الحراري والملوثات السامة."
        ),

        // WATER CONSERVATION (6 - 10)
        QuizQuestion(
            id = "q_water_1",
            categoryId = "water",
            questionEn = "What percentage of the planet's total water is readily accessible liquid fresh water?",
            questionAr = "ما هي النسبة المئوية التقريبية للمياه العذبة السائلة المتاحة للاستخدام البشري المباشر من إجمالي مياه الأرض؟",
            optionsEn = listOf("Less than 1%", "About 25%", "Over 50%", "Roughly 10%"),
            optionsAr = listOf("أقل من 1%", "حوالي 25%", "أكثر من 50%", "قرابة 10%"),
            correctIndex = 0,
            explanationEn = "Although water covers 71% of Earth, 97.5% is saltwater and most fresh water is locked in glaciers or deep aquifers. Less than 1% is easily accessible.",
            explanationAr = "رغم تغطية المياه لـ 71% من الكوكب، فإن 97.5% منها مالحة، ومعظم العذبة مجمدة بالقطبين، ليبقى أقل من 1% متاحاً للاستخدام المباشر."
        ),
        QuizQuestion(
            id = "q_water_2",
            categoryId = "water",
            questionEn = "Which human activity accounts for the largest proportion of global freshwater withdrawals?",
            questionAr = "أي الأنشطة البشرية التالية يستأثر بالنسبة الكبرى من استهلاك وسحب المياه العذبة عالمياً؟",
            optionsEn = listOf("Agricultural irrigation", "Household showering", "Car washing", "Swimming pools"),
            optionsAr = listOf("الري والنشاط الزراعي", "الاستحمام المنزلي", "غسيل السيارات", "حمامات السباحة"),
            correctIndex = 0,
            explanationEn = "Worldwide, agriculture consumes roughly 70% of all freshwater withdrawals to irrigate crops and livestock feed.",
            explanationAr = "يستهلك قطاع الزراعة قرابة 70% من إجمالي المياه العذبة المسحوبة عالمياً لري المحاصيل وتغذية الثروة الحيوانية."
        ),
        QuizQuestion(
            id = "q_water_3",
            categoryId = "water",
            questionEn = "How much water does a single dripping faucet typically waste over the course of a year if left unrepaired?",
            questionAr = "كمية المياه التي يمكن أن يهدرها صنبور يتسرب منه قطرات مستمرة على مدار عام إذا أُهمل إصلاحه؟",
            optionsEn = listOf("Over 3,000 to 10,000 liters", "Less than 5 liters", "Around 20 liters", "Zero liters"),
            optionsAr = listOf("ما بين 3,000 إلى 10,000 لتر", "أقل من 5 لترات", "حوالي 20 لتراً", "صفر لتر"),
            correctIndex = 0,
            explanationEn = "A faucet leaking at just one drip per second wastes thousands of liters of treated freshwater annually.",
            explanationAr = "تسريب قطرة واحدة في الثانية يهدر آلاف اللترات من المياه العذبة النقية المعالجة سنوياً دون أي استفادة."
        ),
        QuizQuestion(
            id = "q_water_4",
            categoryId = "water",
            questionEn = "Why is precision drip irrigation much more sustainable than flood irrigation?",
            questionAr = "لماذا يعد الري الحديث بالتنقيط أكثر استدامة وتوفيراً من الري التقليدي بالغمر؟",
            optionsEn = listOf("Delivers water directly to the plant root zone, minimizing evaporation losses", "Floods the entire field with unnecessary mud", "Evaporates all moisture immediately", "Requires salinizing the soil"),
            optionsAr = listOf("يوصل قطرات الماء مباشرة لجذور النبات مما يقلل الفاقد بالتبخر", "يغمر الحقل بطين غير ضروري", "يبخر كل الرطوبة فوراً", "يتطلب تمليح التربة"),
            correctIndex = 0,
            explanationEn = "Drip irrigation targets roots with controlled moisture, conserving 30-50% more water compared to open surface flood irrigation.",
            explanationAr = "الري بالتنقيط يستهدف منطقة الجذور مباشرة بجرعات مدروسة مما يقلل فواقد التبخر بنسبة تصل إلى 50% مقارنة بالغمر."
        ),
        QuizQuestion(
            id = "q_water_5",
            categoryId = "water",
            questionEn = "What is the primary danger of dumping plastic and industrial waste into freshwater waterways?",
            questionAr = "ما هو الخطر الأكبر لإلقاء المخلفات البلاستيكية والصناعية في المجاري المائية العذبة؟",
            optionsEn = listOf("Bioaccumulation of toxins and poisoning of drinking supplies and aquatic ecosystems", "Makes river water taste sweet", "Increases renewable electricity", "Cools down the atmosphere"),
            optionsAr = listOf("تراكم السموم والملوثات في مياه الشرب وتسمم الكائنات الحية والأسماك", "يجعل مياه النهر حلوة المذاق", "يزيد من توليد الطاقة المتجددة", "يبرد حرارة الجو"),
            correctIndex = 0,
            explanationEn = "Chemical and microplastic pollutants disrupt biological food chains, poison fish stocks, and contaminate critical municipal drinking water reserves.",
            explanationAr = "تتسرب الملوثات الكيميائية وجسيمات الميكروبلاستيك للسلسلة الغذائية وتسمم الثروة السمكية وتلوث مآخذ محطات مياه الشرب."
        ),

        // RECYCLING & WASTE (11 - 15)
        QuizQuestion(
            id = "q_waste_1",
            categoryId = "recycling",
            questionEn = "How long does a typical petroleum-derived plastic water bottle take to decompose in nature?",
            questionAr = "كم عاماً تستغرق زجاجة المياه البلاستيكية المصنوعة من البتروكيماويات لتتحلل طبيعياً في البيئة؟",
            optionsEn = listOf("Approximately 450 years", "2 to 3 days", "About 6 months", "It dissolves instantly in rain"),
            optionsAr = listOf("قرابة 450 عاماً", "يومان إلى 3 أيام", "حوالي 6 أشهر", "تذوب فوراً في ماء المطر"),
            correctIndex = 0,
            explanationEn = "Synthetic polymers do not biodegrade easily; they fragment via UV photodegradation into microplastics over roughly four centuries.",
            explanationAr = "البوليمرات البلاستيكية المصنعة لا تتحلل بيولوجياً بسهولة، بل تتكسر تحت أشعة الشمس لجسيمات دقيقة على مدار أكثر من أربعة قرون."
        ),
        QuizQuestion(
            id = "q_waste_2",
            categoryId = "recycling",
            questionEn = "Why should batteries and electronic devices never be disposed of in regular household trash?",
            questionAr = "لماذا يجب عدم إلقاء البطاريات والأجهزة الإلكترونية القديمة في سلة القمامة المنزلية العادية؟",
            optionsEn = listOf("They contain toxic heavy metals (lead, cadmium, mercury) that leach into soil and groundwater", "They are too heavy for trash trucks", "They turn into gold automatically", "They dissolve garbage bags"),
            optionsAr = listOf("تحتوي على معادن ثقيلة سامة (رصاص، كادميوم، زئبق) تتسرب للتربة والمياه الجوفية", "لأنها ثقيلة الوزن على سيارات جمع القمامة", "لأنها تتحول تلقائياً إلى ذهب", "لأنها تذيب أكياس القمامة"),
            correctIndex = 0,
            explanationEn = "E-waste in landfills releases highly toxic heavy metals into the surrounding environment and subterranean aquifers, posing severe health hazards.",
            explanationAr = "النفايات الإلكترونية بالمكبات تسرب معادن ثقيلة شديدة السمية للتربة والمياه الجوفية مهددة صحة الإنسان والبيئة."
        ),
        QuizQuestion(
            id = "q_waste_3",
            categoryId = "recycling",
            questionEn = "What is the core philosophy of a 'Circular Economy'?",
            questionAr = "ما هو المبدأ الجوهري لنموذج 'الاقتصاد الدائري' المستدام؟",
            optionsEn = listOf("Eliminating waste by designing products to be reused, repaired, and recycled continuously", "Throwing away everything after single use", "Mining raw minerals without limits", "Burning all trash in open air"),
            optionsAr = listOf("القضاء على الهدر بتصميم منتجات يعاد استخدامها وإصلاحها وتدويرها باستمرار", "التخلص من كل المنتجات بعد استخدامها مرة واحدة", "استخراج المعادن الخام دون قيود", "حرق النفايات في الهواء الطلق"),
            correctIndex = 0,
            explanationEn = "A circular economy decouples economic activity from the consumption of finite resources by keeping materials in productive use indefinitely.",
            explanationAr = "يقوم الاقتصاد الدائري على فصل النمو عن استنزاف الموارد عبر إبقاء المواد والمنتجات في دورة الاستخدام والتدوير إلى ما لا نهاية."
        ),
        QuizQuestion(
            id = "q_waste_4",
            categoryId = "recycling",
            questionEn = "Which material can be recycled indefinitely without losing its original strength and purity?",
            questionAr = "أي المواد التالية يمكن إعادة تدويرها مرات لا نهائية دون أن تفقد جودتها أو نقاءها؟",
            optionsEn = listOf("Glass and Aluminum", "Printer paper", "Thin plastic bags", "Cardboard boxes"),
            optionsAr = listOf("الزجاج والألومنيوم", "ورق الطباعة", "الأكياس البلاستيكية الخفيفة", "صناديق الكرتون"),
            correctIndex = 0,
            explanationEn = "Both glass and aluminum can be melted down and re-manufactured endlessly with zero loss of structural integrity.",
            explanationAr = "يمكن صهر الزجاج والألومنيوم وإعادة تشكيلهما مرات غير محدودة دون أي تراجع في بنيتهما أو جودتهما."
        ),
        QuizQuestion(
            id = "q_waste_5",
            categoryId = "recycling",
            questionEn = "What beneficial product is generated by composting kitchen organic scraps like fruit peels?",
            questionAr = "ما هو المنتج البيئي المفيد الناتج عن التسميد العضوي لمخلفات المطبخ كقشور الخضروات والفواكه؟",
            optionsEn = listOf("Nutrient-rich organic compost/humus for agriculture", "Toxic nuclear slag", "Petroleum diesel", "Single-use styrofoam"),
            optionsAr = listOf("سماد عضوي طبيعي غني بالمغذيات لتخصيب التربة الزراعية", "مخلفات نووية سامة", "وقود ديزل بترولي", "رغوة بوليسترين أحادية الاستخدام"),
            correctIndex = 0,
            explanationEn = "Composting transforms organic waste through aerobic microbial decomposition into rich humus that enriches soils and eliminates landfill methane.",
            explanationAr = "يحول التسميد الهوائي بقايا الأطعمة إلى دبال عضوي غني بالعناصر المفيدة للتربة ويوفر بديلاً طبيعياً للأسمدة الكيماوية."
        ),

        // CLIMATE CHANGE (16 - 20)
        QuizQuestion(
            id = "q_climate_1",
            categoryId = "climate",
            questionEn = "What is the crucial scientific difference between 'Weather' and 'Climate'?",
            questionAr = "ما هو الفارق العلمي الجوهري بين مصطلحي 'الطقس' و 'المناخ'؟",
            optionsEn = listOf("Weather is short-term atmospheric states; climate is the long-term pattern averaged over 30+ years", "Weather applies only to rain; climate applies to oceans", "They are identical words with no difference", "Climate lasts one day while weather lasts centuries"),
            optionsAr = listOf("الطقس حالة جوية قصيرة المدى؛ المناخ هو النمط الإحصائي طويل الأمد على مدار 30 عاماً فأكثر", "الطقس للأمطار فقط والمناخ للمحيطات", "هما كلمتان متطابقتان تماماً ولا يوجد فرق", "المناخ يدوم يوماً واحداً بينما الطقس يدوم قروناً"),
            correctIndex = 0,
            explanationEn = "Weather changes minute-to-minute and day-to-day. Climate is the statistical synthesis of weather over at least 30 consecutive years.",
            explanationAr = "الطقس يتغير بين ساعة وأخرى ويوماً بيوم، بينما المناخ هو السجل الإحصائي للأحوال الجوية على مدار 30 سنة على الأقل."
        ),
        QuizQuestion(
            id = "q_climate_2",
            categoryId = "climate",
            questionEn = "Which greenhouse gas is the largest contributor to human-induced global warming by total volume?",
            questionAr = "أي غازات الدفيئة التالية يعد المساهم الأكبر حجماً في الاحترار العالمي الناتج عن الأنشطة البشرية؟",
            optionsEn = listOf("Carbon Dioxide (CO2)", "Pure Argon", "Helium", "Ozone in the stratosphere"),
            optionsAr = listOf("ثاني أكسيد الكربون (CO2)", "غاز الأرجون النقي", "الهيليوم", "الأوزون في الستراتوسفير"),
            correctIndex = 0,
            explanationEn = "Carbon dioxide from fossil fuel combustion and deforestation makes up approximately 76% of total global greenhouse emissions.",
            explanationAr = "يمثل ثاني أكسيد الكربون الناتج عن حرق الوقود الأحفوري وإزالة الغابات نحو 76% من إجمالي انبعاثات الغازات الدفيئة."
        ),
        QuizQuestion(
            id = "q_climate_3",
            categoryId = "climate",
            questionEn = "How does increased carbon dioxide absorption affect global ocean ecosystems?",
            questionAr = "كيف يؤثر امتصاص المحيطات لنسب هائلة من ثاني أكسيد الكربون على النظم البحرية؟",
            optionsEn = listOf("Causes ocean acidification, harming corals and shell-forming organisms", "Makes seawater freeze at higher temperatures", "Cleans all pollution instantly", "Turns marine water into fresh drinking water"),
            optionsAr = listOf("يسبب تحمض المحيطات مما يدمر الشعاب المرجانية والكائنات ذات الأصداف الكلسية", "يجعل مياه البحر تتجمد أسرع", "ينظف المحيطات من التلوث تلقائياً", "يحول مياه البحر إلى مياه عذبة للشرب"),
            correctIndex = 0,
            explanationEn = "CO2 reacts with seawater to form carbonic acid, reducing pH levels and dissolving carbonate ions needed by corals and shellfish.",
            explanationAr = "يتفاعل ثاني أكسيد الكربون مع ماء البحر مكوناً حمض الكربونيك مما يخفض الرقم الهيدروجيني ويذيب كربونات الكالسيوم اللازمة لنمو المرجان."
        ),
        QuizQuestion(
            id = "q_climate_4",
            categoryId = "climate",
            questionEn = "What is the target limit for global warming set by the historic Paris Climate Agreement?",
            questionAr = "ما هو الحد الأقصى المستهدف لارتفاع درجة حرارة كوكب الأرض وفق اتفاقية باريس للمناخ؟",
            optionsEn = listOf("Well below 2.0°C above pre-industrial levels, aiming for 1.5°C", "Increase temperature by at least 10°C", "No target was established", "Keep temperature exactly below -50°C"),
            optionsAr = listOf("أقل بكثير من درجتين مئويتين مع السعي لحصر الزيادة في حدود 1.5 درجة مئوية", "زيادة الحرارة بمقدار 10 درجات مئوية على الأقل", "لم يتم وضع أي هدف", "إبقاء الحرارة تحت 50 درجة مئوية تحت الصفر"),
            correctIndex = 0,
            explanationEn = "The Paris Agreement aims to limit warming to 1.5°C above pre-industrial levels to prevent irreversible catastrophic tipping points.",
            explanationAr = "تهدف اتفاقية باريس إلى حصر الاحترار العالمي في 1.5 درجة مئوية فوق مستويات ما قبل الثورة الصناعية لتجنب نقاط اللاعودة المناخية."
        ),
        QuizQuestion(
            id = "q_climate_5",
            categoryId = "climate",
            questionEn = "Which of the following describes an urban 'Heat Island' effect?",
            questionAr = "ما هو تأثير 'الجزيرة الحرارية الحضرية' في المدن؟",
            optionsEn = listOf("Dense urban centers become significantly hotter than surrounding rural areas due to asphalt and concrete absorbing heat", "Cities surrounded by warm ocean water", "Artificial islands built for tourists", "Areas with zero sunlight"),
            optionsAr = listOf("ارتفاع درجات الحرارة في المدن مقارنة بالريف المحيط بسبب امتصاص الخرسانة والأسفلت للحرارة", "المدن المحاطة بمياه المحيطات الدافئة", "جزر اصطناعية مبنية للسياحة", "مناطق لا تصلها أشعة الشمس"),
            correctIndex = 0,
            explanationEn = "Dark pavement and concrete trap solar radiation, while reduced vegetative cover limits natural evaporative cooling, elevating city temperatures.",
            explanationAr = "تمتص المباني الخرسانية والأسفلت الداكن حرارة الشمس مع قلة المساحات الخضراء، مما يرفع حرارة المدن بدرجات ملحوظة مقارنة بالمناطق الريفية."
        ),

        // BIODIVERSITY & ECOSYSTEMS (21 - 25)
        QuizQuestion(
            id = "q_bio_1",
            categoryId = "biodiversity",
            questionEn = "Why are bees and wild pollinators so essential to global food security?",
            questionAr = "لماذا يعتبر النحل والملقحات البرية عنصراً حيوياً لا غنى عنه للأمن الغذائي العالمي؟",
            optionsEn = listOf("They pollinate roughly one out of every three bites of food we eat", "They manufacture synthetic fertilizers in hives", "They eat agricultural pests exclusively", "They dig irrigation canals"),
            optionsAr = listOf("يقومون بتلقيح نحو ثلث كل قضمة طعام يتناولها البشر حول العالم", "يصنعون الأسمدة الكيماوية داخل الخلايا", "يتغذون على الآفات الزراعية فقط", "يحفرون قنوات الري الزراعية"),
            correctIndex = 0,
            explanationEn = "Over 75% of global food crops producing fruits, vegetables, and seeds depend upon animal pollination, primarily by bees.",
            explanationAr = "أكثر من 75% من المحاصيل الزراعية الرئيسية المنتجة للفواكه والخضار والبذور تعتمد مباشرة على التلقيح بواسطة النحل والملقحات."
        ),
        QuizQuestion(
            id = "q_bio_2",
            categoryId = "biodiversity",
            questionEn = "Why are the coral reefs of Egypt's Red Sea considered globally unique by marine biologists?",
            questionAr = "لماذا تحظى الشعاب المرجانية في البحر الأحمر بمصر بأهمية استثنائية لدى علماء الأحياء البحرية عالمياً؟",
            optionsEn = listOf("They exhibit high thermal tolerance, surviving temperatures that bleach reefs elsewhere", "They are made of plastic polymers", "They live in freshwater lakes", "They grow with zero sunlight"),
            optionsAr = listOf("تمتلك قدرة استثنائية على تحمل درجات الحرارة العالية مقارنة بالشعاب المرجانية الأخرى بالعالم", "لأنها مصنوعة من البلاستيك", "لأنها تعيش في بحيرات عذبة", "لأنها تنمو بدون ضوء شمس"),
            correctIndex = 0,
            explanationEn = "Red Sea corals have adapted through historical evolutionary bottlenecks to endure warmer waters, representing an indispensable global genetic ark.",
            explanationAr = "تطورت شعاب البحر الأحمر عبر آلاف السنين لتتحمل درجات حرارة مرتفعة مما يجعلها ملاذاً وراثياً حيوياً لحماية المرجان حول العالم."
        ),
        QuizQuestion(
            id = "q_bio_3",
            categoryId = "biodiversity",
            questionEn = "What is the primary driver of terrestrial biodiversity loss worldwide?",
            questionAr = "ما هو المحرك والسبب الرئيسي الأول لفقدان التنوع البيولوجي للكائنات البرية حول العالم؟",
            optionsEn = listOf("Habitat destruction and land conversion for agriculture and development", "Natural animal hibernation", "Cold winter snow", "Meteorite collisions"),
            optionsAr = listOf("تدمير البيئات الطبيعية وإزالة الغابات لصالح التوسع الزراعي والعمراني", "البيات الشتوي للحيوانات", "تساقط الثلوج في الشتاء", "اصطدام النيازك بالأرض"),
            correctIndex = 0,
            explanationEn = "Clearing forests, draining wetlands, and subdividing pristine ecosystems for human agriculture and urban sprawl strips wildlife of survival habitats.",
            explanationAr = "إزالة الغابات وتجفيف المستنقعات والتوسع العمراني الجائر يحرم الكائنات الحية من مواطنها الطبيعية ومصادر غذائها وتكاثرها."
        ),
        QuizQuestion(
            id = "q_bio_4",
            categoryId = "biodiversity",
            questionEn = "What critical ecological role do mangrove forests play along tropical and subtropical coasts?",
            questionAr = "ما هو الدور البيئي الحاسم الذي تلعبه غابات المانجروف (شجر القرم) على السواحل البحرية؟",
            optionsEn = listOf("Buffer coastlines from storms, prevent erosion, and provide fish nurseries while storing massive carbon", "Attract harmful sharks to beaches", "Kill offshore marine life", "Dry up coastal waters"),
            optionsAr = listOf("حماية السواحل من التآكل والعواصف وحضانة الأسماك وتخزين الكربون بكفاءة فائقة", "جذب أسماك القرش للشواطئ", "القضاء على الكائنات البحرية", "تجفيف مياه الساحل"),
            correctIndex = 0,
            explanationEn = "Mangroves store 'blue carbon' in their submerged root systems up to 4x faster than terrestrial forests, while protecting coastlines and nursery fisheries.",
            explanationAr = "تخزن غابات المانجروف الكربون الأزرق في جذورها أسرع بأربع مرات من الغابات البرية، وتحمي الشواطئ من الأمواج وحضانة صغار الأسماك."
        ),
        QuizQuestion(
            id = "q_bio_5",
            categoryId = "biodiversity",
            questionEn = "How does preserving biodiversity directly benefit human medical science?",
            questionAr = "كيف يساهم الحفاظ على التنوع البيولوجي الطبيعي في دعم وتطوير العلوم الطبية والصيدلانية للبشر؟",
            optionsEn = listOf("Over 50% of modern pharmaceutical drugs originate from molecules discovered in wild plants and organisms", "Doctors use leaves instead of stethoscopes", "Animals manufacture surgical tools", "It has zero medical value"),
            optionsAr = listOf("أكثر من 50% من الأدوية الحديثة مشتقة من مركبات كيميائية اكتُشفت في النباتات والكائنات الطبيعية", "الأطباء يستبدلون السماعات بأوراق الأشجار", "الحيوانات تصنع أدوات الجراحة", "لا توجد أي علاقة بينهما"),
            correctIndex = 0,
            explanationEn = "Aspirin, antibiotics, cancer therapies, and analgesics were isolated from natural plant and microbial biochemistry, which vanishing biodiversity threatens forever.",
            explanationAr = "الأسبرين والمضادات الحيوية وعلاجات الأورام استُخلصت من مركبات نباتية وفطرية برية، وفقدان الأنواع يدمر صيدلية الطبيعة المستقبلية."
        ),

        // SUSTAINABLE LIVING (26 - 30)
        QuizQuestion(
            id = "q_living_1",
            categoryId = "living",
            questionEn = "What is 'phantom energy' or 'vampire power' in domestic electricity use?",
            questionAr = "ما هي 'الطاقة الشبحية' أو 'استهلاك مصاصي الدماء' للكهرباء في المنازل؟",
            optionsEn = listOf("Electricity consumed by electronic devices while plugged in on standby mode", "Electricity made from haunted houses", "Lightning striking a power station", "Energy during night hours only"),
            optionsAr = listOf("الكهرباء التي تستهلكها الأجهزة الإلكترونية أثناء توصيلها بالقابس في وضع الاستعداد", "كهرباء مولدة من منازل مهجورة", "صواعق تضرب محطة الكهرباء", "الكهرباء في ساعات الليل فقط"),
            correctIndex = 0,
            explanationEn = "TVs, consoles, chargers, and appliances in standby mode draw up to 10% of residential power without providing active service.",
            explanationAr = "تستهلك الشواحن والشاشات وأجهزة الألعاب طاقة كامنة تصل إلى 10% من فاتورة المنزل بمجرد بقائها موصولة بالقابس في وضع الاستعداد."
        ),
        QuizQuestion(
            id = "q_living_2",
            categoryId = "living",
            questionEn = "By what percentage does LED lighting reduce electricity consumption compared to old incandescent bulbs?",
            questionAr = "بأي نسبة مئوية تخفض مصابيح LED استهلاك الكهرباء مقارنة بالمصابيح المتوهجة القديمة؟",
            optionsEn = listOf("Roughly 75% to 85%", "Only 2%", "It increases consumption by 50%", "Zero percent difference"),
            optionsAr = listOf("قرابة 75% إلى 85%", "2% فقط", "تزيد الاستهلاك بنسبة 50%", "لا يوجد أي فرق"),
            correctIndex = 0,
            explanationEn = "LEDs convert over 80% of energy into visible light, whereas incandescent bulbs waste up to 90% of electrical energy as wasted heat.",
            explanationAr = "تحول مصابيح LED أكثر من 80% من الطاقة إلى ضوء، بينما كانت المصابيح القديمة تهدر 90% من طاقتها في صورة حرارة ضائعة."
        ),
        QuizQuestion(
            id = "q_living_3",
            categoryId = "living",
            questionEn = "Why does food waste represent a major environmental and climate crisis?",
            questionAr = "لماذا يمثل هدر الطعام أزمة بيئية ومناخية كبرى على مستوى العالم؟",
            optionsEn = listOf("Wastes the water and energy used to produce it, and emits methane when rotting in landfills", "Makes grocery shelves too empty", "Animals cannot eat leftover food", "Food has zero environmental footprint"),
            optionsAr = listOf("يهدر المياه والطاقة الهائلة المستخدمة في إنتاجه، وينتج غاز الميثان الضار عند تحلله بالمكبات", "يجعل أرفف المتاجر فارغة تماماً", "لأن الحيوانات لا تأكل بقايا الطعام", "لأن الطعام لا بصمة بيئية له"),
            correctIndex = 0,
            explanationEn = "Roughly one-third of all food produced is wasted, squandering land, fresh water, and labor while generating 8-10% of human greenhouse emissions.",
            explanationAr = "يُهدر نحو ثلث الطعام المنتج عالمياً، مما يضيع المياه والأراضي والطاقة، ويولد ما بين 8 إلى 10% من انبعاثات الغازات الدفيئة."
        ),
        QuizQuestion(
            id = "q_living_4",
            categoryId = "living",
            questionEn = "What is the recommended thermostat setting for air conditioning in summer to balance comfort with energy efficiency?",
            questionAr = "ما هي درجة الحرارة المثالية الموصى بضبط مكيف الهواء عليها صيفاً لتحقيق التوازن بين الراحة وترشيد الطاقة؟",
            optionsEn = listOf("24°C to 25°C", "16°C", "35°C", "10°C"),
            optionsAr = listOf("بين 24 و 25 درجة مئوية", "16 درجة مئوية", "35 درجة مئوية", "10 درجات مئوية"),
            correctIndex = 0,
            explanationEn = "Setting ACs at 24°C-25°C maintains comfort while every degree cooler below 24°C increases electricity consumption by 6% to 8%.",
            explanationAr = "ضبط التكييف على 24-25 مئوية يوفر الراحة، في حين أن كل درجة تنخفض تحت 24 تزيد من استهلاك الكهرباء بنسبة تتراوح بين 6 إلى 8%."
        ),
        QuizQuestion(
            id = "q_living_5",
            categoryId = "living",
            questionEn = "What is the most sustainable approach to personal consumption according to the waste hierarchy?",
            questionAr = "ما هو التصرف الأكثر استدامة وفق الهرم البيئي لإدارة المخلفات والموارد؟",
            optionsEn = listOf("Reduce consumption first, then Reuse, and lastly Recycle (The 3 R's)", "Buy as much as possible and throw away immediately", "Recycle everything without reducing", "Incinerate goods right after purchase"),
            optionsAr = listOf("الحد من الاستهلاك أولاً (Reduce)، ثم إعادة الاستخدام (Reuse)، وأخيراً التدوير (Recycle)", "شراء أكبر قدر ممكن والتخلص منه فوراً", "إعادة التدوير فقط دون ترشيد", "حرق المنتجات بعد شرائها مباشرة"),
            correctIndex = 0,
            explanationEn = "Preventing waste generation at the source (Refuse/Reduce) is always far superior to expending energy on downstream recycling processes.",
            explanationAr = "منع توليد النفايات من المنبع وترشيد الشراء هو الأفضل والأعلى أثراً دائماً، يليه إعادة الاستخدام، ثم إعادة التدوير."
        )
    )
}
