package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.Strings
import com.example.data.models.UserProfile
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.SolarYellow

sealed interface Screen {
    data object Home : Screen
    data object Explore : Screen
    data object Infographic : Screen
    data object Challenges : Screen
    data object Profile : Screen
    data class LessonDetail(val lessonId: String) : Screen
    data class Quiz(val categoryId: String? = null) : Screen
    data object WasteSortingGame : Screen
    data object Calculators : Screen
    data object Settings : Screen
    data object VerifiedFacts : Screen
}

enum class NavTab(
    val labelEn: String,
    val labelAr: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val screen: Screen,
    val tag: String
) {
    HOME("Home", "الرئيسية", Icons.Filled.Home, Icons.Outlined.Home, Screen.Home, "nav_home"),
    EXPLORE("Explore", "استكشف", Icons.Filled.Eco, Icons.Outlined.Eco, Screen.Explore, "nav_explore"),
    INFOGRAPHIC("Infographic", "الإنفوجرافيك", Icons.Filled.Public, Icons.Outlined.Public, Screen.Infographic, "nav_infographic"),
    CHALLENGES("Challenges", "التحديات", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents, Screen.Challenges, "nav_challenges"),
    PROFILE("Profile", "حسابي", Icons.Filled.Person, Icons.Outlined.Person, Screen.Profile, "nav_profile");

    companion object {
        fun fromScreen(screen: Screen): NavTab? = when (screen) {
            is Screen.Home -> HOME
            is Screen.Explore -> EXPLORE
            is Screen.Infographic -> INFOGRAPHIC
            is Screen.Challenges -> CHALLENGES
            is Screen.Profile -> PROFILE
            else -> null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcoMindTopBar(
    language: AppLanguage,
    userProfile: UserProfile,
    onLanguageToggle: () -> Unit,
    onProfileClick: () -> Unit,
    onInfoClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ForestGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌱", fontSize = 20.sp)
                }
                Column {
                    Text(
                        text = Strings.appTitle.get(language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = if (language == AppLanguage.ARABIC) "عقول خضراء" else "Green Minds Egypt",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            // Right side actions: Eco Points Pill, Language Switcher, Settings
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Points badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = EmeraldGreen.copy(alpha = 0.15f),
                    modifier = Modifier.clickable { onProfileClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("⚡", fontSize = 13.sp)
                        Text(
                            text = "${userProfile.ecoPoints} pt",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Language toggle button
                IconButton(
                    onClick = onLanguageToggle,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("btn_language_toggle")
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (language == AppLanguage.ARABIC) "EN" else "عربي",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Info / Competition info button
                IconButton(
                    onClick = onInfoClick,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("btn_info_modal")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Competition info",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Settings icon
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("btn_settings")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EcoMindBottomNav(
    currentScreen: Screen,
    language: AppLanguage,
    onTabSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeTab = NavTab.fromScreen(currentScreen)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTab.entries.forEach { tab ->
                val isSelected = activeTab == tab
                val interactionSource = remember { MutableInteractionSource() }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = ripple()
                        ) {
                            onTabSelected(tab.screen)
                        }
                        .testTag(tab.tag),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) EmeraldGreen.copy(alpha = 0.18f) else Color.Transparent
                                )
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = if (language == AppLanguage.ARABIC) tab.labelAr else tab.labelEn,
                                tint = if (isSelected) EmeraldGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            text = if (language == AppLanguage.ARABIC) tab.labelAr else tab.labelEn,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) EmeraldGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
