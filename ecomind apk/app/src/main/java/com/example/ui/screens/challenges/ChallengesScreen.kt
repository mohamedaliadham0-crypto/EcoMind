package com.example.ui.screens.challenges

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.models.Challenge
import com.example.data.models.UserProfile
import com.example.ui.components.SoundHapticManager
import com.example.ui.theme.*

@Composable
fun ChallengesScreen(
    challenges: List<Challenge>,
    userProfile: UserProfile,
    language: AppLanguage,
    onCompleteChallenge: (String, Int) -> Unit,
    soundEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val isAr = language == AppLanguage.ARABIC
    val scrollState = rememberScrollState()

    var filterDailyOnly by remember { mutableStateOf<Boolean?>(null) } // null = All, true = Daily, false = Weekly
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredList = remember(challenges, filterDailyOnly, selectedCategory) {
        challenges.filter { ch ->
            val matchDaily = filterDailyOnly == null || ch.isDaily == filterDailyOnly
            val matchCat = selectedCategory == "All" || ch.category.equals(selectedCategory, ignoreCase = true)
            matchDaily && matchCat
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Column {
            Text(
                text = if (isAr) "التحديات البيئية" else "Eco Challenges",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isAr) "حول الوعي البيئي إلى سلوك يومي ملموس واكسب النقاط والأوسمة." else "Turn environmental awareness into practical daily actions to earn points and badges.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        // Eco-Level Progress Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ForestGreen)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isAr) "مستواك الحالي:" else "Your Eco Level:",
                            fontSize = 11.sp,
                            color = FreshGreen
                        )
                        Text(
                            text = if (isAr) "${userProfile.ecoLevelAr} ${userProfile.avatarEmoji}" else "${userProfile.ecoLevel} ${userProfile.avatarEmoji}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = PureWhite
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EmeraldGreen.copy(alpha = 0.35f)
                    ) {
                        Text(
                            text = "${userProfile.ecoPoints} pt",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = FreshGreen,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val progress = (userProfile.ecoPoints / userProfile.nextLevelTarget.toFloat()).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = FreshGreen,
                    trackColor = WarmOffWhite.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isAr) {
                        "الهدف للمستوى التالي: ${userProfile.nextLevelTarget} نقطة (متبقي ${(userProfile.nextLevelTarget - userProfile.ecoPoints).coerceAtLeast(0)})"
                    } else {
                        "Next Level Target: ${userProfile.nextLevelTarget} pts (${(userProfile.nextLevelTarget - userProfile.ecoPoints).coerceAtLeast(0)} remaining)"
                    },
                    fontSize = 11.sp,
                    color = WarmOffWhite.copy(alpha = 0.8f)
                )
            }
        }

        // Filter Chips (All, Daily, Weekly)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterDailyOnly == null,
                onClick = { filterDailyOnly = null },
                label = { Text(if (isAr) "الكل" else "All", fontSize = 12.sp) }
            )
            FilterChip(
                selected = filterDailyOnly == true,
                onClick = { filterDailyOnly = true },
                label = { Text(if (isAr) "تحديات يومية" else "Daily", fontSize = 12.sp) }
            )
            FilterChip(
                selected = filterDailyOnly == false,
                onClick = { filterDailyOnly = false },
                label = { Text(if (isAr) "تحديات أسبوعية" else "Weekly", fontSize = 12.sp) }
            )
        }

        // Challenges List
        filteredList.forEach { challenge ->
            val isDone = userProfile.completedChallengeIds.contains(challenge.id)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDone) SoftMint.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(2.dp),
                border = if (isDone) BorderStroke(1.dp, EmeraldGreen) else null
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (challenge.difficulty) {
                                    "Easy" -> FreshGreen.copy(alpha = 0.2f)
                                    "Medium" -> SolarYellow.copy(alpha = 0.25f)
                                    else -> EcoRed.copy(alpha = 0.2f)
                                }
                            ) {
                                Text(
                                    text = challenge.difficulty,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = challenge.category,
                                    fontSize = 10.sp,
                                    color = EmeraldGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "+${challenge.points} pt",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = EmeraldGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isAr) challenge.titleAr else challenge.titleEn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAr) challenge.descAr else challenge.descEn,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Criteria
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("📋", fontSize = 12.sp)
                            Text(
                                text = if (isAr) "المعيار: ${challenge.criteriaAr}" else "Criteria: ${challenge.criteriaEn}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isDone) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = EmeraldGreen)
                            Text(
                                text = if (isAr) "تم إنجازه بنجاح! (+${challenge.points} نقطة)" else "Completed! (+${challenge.points} pts)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = EmeraldGreen
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                onCompleteChallenge(challenge.id, challenge.points)
                                SoundHapticManager.playSuccess(view, soundEnabled)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("btn_complete_chal_${challenge.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "تأكيد إنجاز التحدي (+${challenge.points} نقطة)" else "Mark as Completed (+${challenge.points} pts)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
