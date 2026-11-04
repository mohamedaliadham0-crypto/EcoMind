package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppLanguage
import com.example.data.repository.EcoMindRepository
import com.example.ui.components.*
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.calculators.CalculatorsScreen
import com.example.ui.screens.challenges.ChallengesScreen
import com.example.ui.screens.explore.ExploreScreen
import com.example.ui.screens.explore.LessonDetailScreen
import com.example.ui.screens.explore.VerifiedFactsScreen
import com.example.ui.screens.explore.WasteSortingGameScreen
import com.example.ui.screens.infographic.InfographicScreen
import com.example.ui.screens.infographic.PresentationModeDialog
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.profile.SettingsScreen
import com.example.ui.screens.quizzes.QuizScreen
import com.example.ui.theme.EcoMindTheme

class MainActivity : ComponentActivity() {

    private lateinit var repository: EcoMindRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = EcoMindRepository(applicationContext)

        setContent {
            val userProfile by repository.userProfile.collectAsStateWithLifecycle()
            val language by repository.appLanguage.collectAsStateWithLifecycle()
            val isDarkMode by repository.isDarkMode.collectAsStateWithLifecycle()
            val soundEnabled by repository.soundEnabled.collectAsStateWithLifecycle()
            val reducedMotion by repository.reducedMotion.collectAsStateWithLifecycle()

            var navStack by remember { mutableStateOf(listOf<Screen>(Screen.Home)) }
            val currentScreen = navStack.lastOrNull() ?: Screen.Home

            var showPresentationDialog by remember { mutableStateOf(false) }
            var showInfoDialog by remember { mutableStateOf(false) }

            fun navigateTo(screen: Screen) {
                if (navStack.lastOrNull() != screen) {
                    navStack = navStack + screen
                }
            }

            fun navigateTab(screen: Screen) {
                // Switching primary tab clears sub-screen stack
                navStack = listOf(screen)
            }

            fun navigateBack() {
                if (navStack.size > 1) {
                    navStack = navStack.dropLast(1)
                }
            }

            // Provide RTL layout when Arabic is chosen, LTR when English
            CompositionLocalProvider(LocalLayoutDirection provides language.direction) {
                EcoMindTheme(darkTheme = isDarkMode) {
                    val isMainTab = NavTab.fromScreen(currentScreen) != null

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            if (isMainTab) {
                                EcoMindTopBar(
                                    language = language,
                                    userProfile = userProfile,
                                    onLanguageToggle = {
                                        val nextLang = if (language == AppLanguage.ARABIC) AppLanguage.ENGLISH else AppLanguage.ARABIC
                                        repository.setLanguage(nextLang)
                                    },
                                    onProfileClick = { navigateTab(Screen.Profile) },
                                    onInfoClick = { showInfoDialog = true },
                                    onSettingsClick = { navigateTo(Screen.Settings) }
                                )
                            }
                        },
                        bottomBar = {
                            if (isMainTab) {
                                EcoMindBottomNav(
                                    currentScreen = currentScreen,
                                    language = language,
                                    onTabSelected = { tabScreen -> navigateTab(tabScreen) }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            when (currentScreen) {
                                is Screen.Home -> {
                                    val dailyChal = repository.challenges.firstOrNull { it.isDaily }
                                    HomeScreen(
                                        language = language,
                                        userProfile = userProfile,
                                        dailyChallenge = dailyChal,
                                        onNavigate = { screen -> navigateTo(screen) },
                                        onCompleteDailyChallenge = { chalId, pts ->
                                            repository.completeChallenge(chalId, pts)
                                        },
                                        onOpenPresentationMode = { showPresentationDialog = true },
                                        soundEnabled = soundEnabled,
                                        reducedMotion = reducedMotion
                                    )
                                }
                                is Screen.Explore -> {
                                    ExploreScreen(
                                        language = language,
                                        userProfile = userProfile,
                                        lessons = repository.lessons,
                                        onNavigate = { screen -> navigateTo(screen) }
                                    )
                                }
                                is Screen.Infographic -> {
                                    InfographicScreen(
                                        language = language,
                                        onOpenPresentationMode = { showPresentationDialog = true },
                                        soundEnabled = soundEnabled,
                                        reducedMotion = reducedMotion
                                    )
                                }
                                is Screen.Challenges -> {
                                    ChallengesScreen(
                                        challenges = repository.challenges,
                                        userProfile = userProfile,
                                        language = language,
                                        onCompleteChallenge = { chalId, pts ->
                                            repository.completeChallenge(chalId, pts)
                                        },
                                        soundEnabled = soundEnabled
                                    )
                                }
                                is Screen.Profile -> {
                                    ProfileScreen(
                                        userProfile = userProfile,
                                        badges = repository.badges,
                                        language = language,
                                        onNavigate = { screen -> navigateTo(screen) },
                                        onUpdateProfile = { name, avatar ->
                                            repository.updateProfileNameAndAvatar(name, avatar)
                                        }
                                    )
                                }
                                is Screen.LessonDetail -> {
                                    val lesson = repository.lessons.find { it.id == currentScreen.lessonId }
                                        ?: repository.lessons.first()
                                    val isCompleted = userProfile.completedLessonIds.contains(lesson.id)
                                    LessonDetailScreen(
                                        lesson = lesson,
                                        language = language,
                                        isCompleted = isCompleted,
                                        onBack = { navigateBack() },
                                        onCompleteLesson = {
                                            repository.completeLesson(lesson.id)
                                        },
                                        onStartQuiz = { catId ->
                                            navigateTo(Screen.Quiz(catId))
                                        },
                                        soundEnabled = soundEnabled
                                    )
                                }
                                is Screen.Quiz -> {
                                    QuizScreen(
                                        questions = repository.quizQuestions,
                                        language = language,
                                        categoryId = currentScreen.categoryId,
                                        onBack = { navigateBack() },
                                        onQuizCompleted = { score, catId ->
                                            repository.recordQuizResult(score, catId)
                                        },
                                        soundEnabled = soundEnabled
                                    )
                                }
                                is Screen.WasteSortingGame -> {
                                    WasteSortingGameScreen(
                                        items = repository.wasteItems,
                                        language = language,
                                        onBack = { navigateBack() },
                                        onItemSorted = { isCorrect ->
                                            repository.recordWasteSortingItem(isCorrect)
                                        },
                                        soundEnabled = soundEnabled
                                    )
                                }
                                is Screen.Calculators -> {
                                    CalculatorsScreen(
                                        language = language,
                                        onBack = { navigateBack() },
                                        onApplyImpact = { kwh, liters ->
                                            repository.addImpactCalculations(kwh, liters)
                                        },
                                        soundEnabled = soundEnabled
                                    )
                                }
                                is Screen.Settings -> {
                                    SettingsScreen(
                                        language = language,
                                        isDarkMode = isDarkMode,
                                        soundEnabled = soundEnabled,
                                        reducedMotion = reducedMotion,
                                        onBack = { navigateBack() },
                                        onLanguageChange = { repository.setLanguage(it) },
                                        onDarkModeChange = { repository.setDarkMode(it) },
                                        onSoundChange = { repository.setSoundEnabled(it) },
                                        onReducedMotionChange = { repository.setReducedMotion(it) },
                                        onResetProgress = { repository.resetAllProgress() }
                                    )
                                }
                                is Screen.VerifiedFacts -> {
                                    VerifiedFactsScreen(
                                        facts = repository.verifiedFacts,
                                        language = language,
                                        onBack = { navigateBack() }
                                    )
                                }
                            }
                        }

                        // Dialogs
                        if (showPresentationDialog) {
                            PresentationModeDialog(
                                language = language,
                                onDismiss = { showPresentationDialog = false }
                            )
                        }

                        if (showInfoDialog) {
                            CompetitionInfoDialog(
                                language = language,
                                onDismiss = { showInfoDialog = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
