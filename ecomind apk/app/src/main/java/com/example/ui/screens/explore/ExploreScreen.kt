package com.example.ui.screens.explore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.Strings
import com.example.data.models.Lesson
import com.example.data.models.UserProfile
import com.example.ui.components.Screen
import com.example.ui.theme.*

@Composable
fun ExploreScreen(
    language: AppLanguage,
    userProfile: UserProfile,
    lessons: List<Lesson>,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        Column {
            Text(
                text = Strings.navExplore.get(language),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isAr) "استكشف العلوم البيئية وحلول الاستدامة الموثقة علمياً" else "Discover verified environmental science, renewable solutions, and practical habits.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        // Feature Action Row: Waste Sorting Game & Calculators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Waste Sorting Game Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigate(Screen.WasteSortingGame) }
                    .testTag("btn_explore_waste_game"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldGreen.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("♻️", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isAr) "لعبة فرز النفايات" else "Waste Sorting Game",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = EmeraldGreen
                    )
                    Text(
                        text = if (isAr) "تدريب تفاعلي للفرز" else "Interactive Mini-Game",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Calculators Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigate(Screen.Calculators) }
                    .testTag("btn_explore_calculators"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SolarYellow.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, SolarYellow.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("🧮", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = Strings.openCalculators.get(language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DarkCharcoal
                    )
                    Text(
                        text = if (isAr) "حاسبة الكهرباء والمياه" else "Energy & Water Math",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Verified Facts and Knowledge Quiz Banners
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onNavigate(Screen.VerifiedFacts) },
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("📰", fontSize = 20.sp)
                    Column {
                        Text(
                            text = if (isAr) "حقائق موثقة" else "Verified Facts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "UNEP • NASA • WMO",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onNavigate(Screen.Quiz(null)) }
                    .testTag("btn_explore_all_quizzes"),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🏆", fontSize = 20.sp)
                    Column {
                        Text(
                            text = if (isAr) "اختبار المعرفة" else "Knowledge Quiz",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isAr) "30 سؤالاً علمياً" else "30+ Questions",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 6 Educational Learning Units
        Text(
            text = if (isAr) "الوحدات التعليمية الست:" else "The 6 Environmental Learning Modules:",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.primary
        )

        lessons.forEach { lesson ->
            val isCompleted = userProfile.completedLessonIds.contains(lesson.id)
            val iconEmoji = when (lesson.categoryId) {
                "energy" -> "☀️"
                "water" -> "💧"
                "recycling" -> "♻️"
                "climate" -> "🌡️"
                "biodiversity" -> "🦁"
                "living" -> "🌱"
                else -> "📚"
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onNavigate(Screen.LessonDetail(lesson.id)) }
                    .testTag("card_lesson_${lesson.id}"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                border = if (isCompleted) BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.5f)) else null
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCompleted) EmeraldGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(iconEmoji, fontSize = 22.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isAr) lesson.titleAr else lesson.titleEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isAr) lesson.summaryAr else lesson.summaryEn,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isCompleted) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldGreen.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = if (isAr) "تمت دراسته ✓" else "Completed ✓",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = EmeraldGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            } else {
                                Text(
                                    text = if (isAr) "ابدأ التعلم +20 نقطة" else "Start Lesson +20 pts",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldGreen
                                )
                            }
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Lesson",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
