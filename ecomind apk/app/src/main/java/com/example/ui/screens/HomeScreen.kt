package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AppLanguage
import com.example.data.Strings
import com.example.data.models.Challenge
import com.example.data.models.UserProfile
import com.example.ui.components.Screen
import com.example.ui.components.SoundHapticManager
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    language: AppLanguage,
    userProfile: UserProfile,
    dailyChallenge: Challenge?,
    onNavigate: (Screen) -> Unit,
    onCompleteDailyChallenge: (String, Int) -> Unit,
    onOpenPresentationMode: () -> Unit,
    soundEnabled: Boolean,
    reducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val isAr = language == AppLanguage.ARABIC
    val scrollState = rememberScrollState()

    // Smooth subtle rotating animation for hero badge
    val infiniteTransition = rememberInfiniteTransition(label = "hero_anim")
    val rotationAngle by if (!reducedMotion) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(25000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "spin"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    // Daily tips list that user can browse
    val tips = remember(isAr) {
        if (isAr) {
            listOf(
                "افصل شواحن الهواتف والأجهزة من القابس عند عدم استخدامها لمنع سحب الطاقة الشبحية المهدرة.",
                "تقليل مدة الاستحمام دقيقة واحدة فقط يوفر قرابة 10 لترات من المياه العذبة النقية يومياً.",
                "استبدال زجاجات المياه البلاستيكية بقارورة ستانلس ستيل يحمي البيئة من 167 زجاجة بلاستيكية سنوياً.",
                "ضبط مكيف الهواء على 24 أو 25 درجة مئوية يخفض استهلاك الكهرباء بنسبة تصل إلى 20%.",
                "زراعة شجرة واحدة في محيطك تمتص ما يعادل 22 كجم من ثاني أكسيد الكربون كل عام."
            )
        } else {
            listOf(
                "Unplug chargers and appliances when not in use to eliminate phantom standby power drain.",
                "Shortening your shower by just one minute conserves up to 10 liters of pure fresh water daily.",
                "Replacing disposable plastic water bottles with a stainless steel canteen prevents 167 plastic bottles yearly.",
                "Setting your AC thermostat to 24°C or 25°C cuts household cooling electricity by up to 20%.",
                "Planting a single native tree absorbs roughly 22 kg of carbon dioxide every single year."
            )
        }
    }
    var currentTipIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Personalized Greeting & Level Pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isAr) "أهلاً بك، ${userProfile.username} ${userProfile.avatarEmoji}" else "Hello, ${userProfile.username} ${userProfile.avatarEmoji}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isAr) "المستوى: ${userProfile.ecoLevelAr}" else "Level: ${userProfile.ecoLevel}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldGreen
                )
            }

            // Quick Presentation Mode badge
            ElevatedButton(
                onClick = onOpenPresentationMode,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = ForestGreen,
                    contentColor = WarmOffWhite
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("btn_quick_presentation_mode")
            ) {
                Icon(Icons.Outlined.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isAr) "العرض التقديمي" else "Presentation",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 2. HERO SECTION: Earth Banner & Tagline
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, EmeraldGreen.copy(alpha = 0.25f), RoundedCornerShape(24.dp)),
            color = ForestGreen
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_earth_hero),
                        contentDescription = "Earth Ecosystem Illustration",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        ForestGreen.copy(alpha = 0.7f),
                                        ForestGreen
                                    )
                                )
                            )
                    )

                    // Rotating orbit leaf badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(42.dp)
                            .rotate(rotationAngle)
                            .clip(CircleShape)
                            .background(EmeraldGreen.copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🍃", fontSize = 18.sp)
                    }
                }

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = Strings.heroTagline.get(language),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = Strings.heroDescription.get(language),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = WarmOffWhite.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onNavigate(Screen.Infographic) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_hero_infographic"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isAr) "استكشف الإنفوجرافيك" else "Open Infographic",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = PureWhite
                            )
                        }

                        OutlinedButton(
                            onClick = { onNavigate(Screen.Explore) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_hero_explore"),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(FreshGreen, EmeraldGreen))
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FreshGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isAr) "الدروس البيئية" else "Explore Lessons",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // 3. CARD A — Today's Environmental Tip
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SolarYellow.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Lightbulb,
                                contentDescription = null,
                                tint = SolarYellow,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = Strings.todayTip.get(language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Next tip button
                    IconButton(
                        onClick = {
                            currentTipIndex = (currentTipIndex + 1) % tips.size
                            SoundHapticManager.playClick(view, soundEnabled)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = "Next Tip",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = tips[currentTipIndex],
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )
            }
        }

        // 4. CARD B — Daily Eco Challenge
        if (dailyChallenge != null) {
            val isDone = userProfile.completedChallengeIds.contains(dailyChallenge.id)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDone) SoftMint.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
                ),
                border = if (isDone) BorderStroke(1.dp, EmeraldGreen) else null,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🎯", fontSize = 18.sp)
                            Text(
                                text = Strings.dailyChallenge.get(language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "+${dailyChallenge.points} pt",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = EmeraldGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isAr) dailyChallenge.titleAr else dailyChallenge.titleEn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAr) dailyChallenge.descAr else dailyChallenge.descEn,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isDone) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = Strings.challengeDone.get(language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = EmeraldGreen
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                onCompleteDailyChallenge(dailyChallenge.id, dailyChallenge.points)
                                SoundHapticManager.playSuccess(view, soundEnabled)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("btn_complete_daily_challenge"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Strings.completeChallenge.get(language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // 5. CARD C — Your Environmental Impact
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("📊", fontSize = 18.sp)
                        Text(
                            text = Strings.yourImpact.get(language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    TextButton(
                        onClick = { onNavigate(Screen.Calculators) },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = Strings.openCalculators.get(language),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 4 Metric Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ImpactMetricItem(
                        modifier = Modifier.weight(1f),
                        icon = "⚡",
                        value = "%.1f".format(userProfile.kwhSaved),
                        unit = "kWh",
                        title = Strings.impactKwh.get(language),
                        color = SolarYellow
                    )
                    ImpactMetricItem(
                        modifier = Modifier.weight(1f),
                        icon = "💧",
                        value = "%.0f".format(userProfile.litersSaved),
                        unit = "L",
                        title = Strings.impactWater.get(language),
                        color = SkyBlue
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ImpactMetricItem(
                        modifier = Modifier.weight(1f),
                        icon = "♻️",
                        value = "${userProfile.wasteSortedCount}",
                        unit = if (isAr) "عنصر" else "items",
                        title = Strings.impactWaste.get(language),
                        color = EmeraldGreen
                    )
                    val co2Kg = userProfile.kwhSaved * 0.42
                    ImpactMetricItem(
                        modifier = Modifier.weight(1f),
                        icon = "🍃",
                        value = "%.1f".format(co2Kg),
                        unit = "kg",
                        title = Strings.impactCo2.get(language),
                        color = FreshGreen
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (isAr) "* جميع الحسابات مبنية على معاملات علمية موثقة (IEA/UNEP)." else "* Estimates based on verified IEA/UNEP emission factors.",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        // 6. CARD D — Learning Progress & Quizzes
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("📚", fontSize = 18.sp)
                        Text(
                            text = Strings.learningProgress.get(language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "${userProfile.completedLessonIds.size} / 6",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = EmeraldGreen
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { (userProfile.completedLessonIds.size / 6f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = EmeraldGreen,
                    trackColor = EmeraldGreen.copy(alpha = 0.15f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onNavigate(Screen.Explore) },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(
                            text = if (isAr) "متابعة الدروس" else "Continue Lessons",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Button(
                        onClick = { onNavigate(Screen.Quiz(null)) },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("btn_home_start_quiz"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Text(
                            text = Strings.startQuiz.get(language),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                }
            }
        }

        // 7. CARD E — Explore Quick Topic Jumpers
        Column {
            Text(
                text = Strings.quickTopics.get(language),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickTopicChip(
                    modifier = Modifier.weight(1f),
                    emoji = "☀️",
                    title = if (isAr) "طاقة نظيفة" else "Clean Energy",
                    onClick = { onNavigate(Screen.LessonDetail("energy_solar")) }
                )
                QuickTopicChip(
                    modifier = Modifier.weight(1f),
                    emoji = "💧",
                    title = if (isAr) "ترشيد المياه" else "Save Water",
                    onClick = { onNavigate(Screen.LessonDetail("water_conservation")) }
                )
                QuickTopicChip(
                    modifier = Modifier.weight(1f),
                    emoji = "♻️",
                    title = if (isAr) "لعبة الفرز" else "Sort Game",
                    onClick = { onNavigate(Screen.WasteSortingGame) }
                )
            }
        }

        // 8. CARD F — Daily Motivation
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = EmeraldGreen.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("🌍", fontSize = 26.sp)
                Column {
                    Text(
                        text = Strings.dailyMotivation.get(language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = EmeraldGreen
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isAr) {
                            "\"نحن لا نرث الأرض من أجدادنا، بل نستعيرها من أطفالنا. كل خطوة ترشيد اليوم تحمي الغد.\""
                        } else {
                            "\"We do not inherit the Earth from our ancestors; we borrow it from our children. Small actions today protect tomorrow.\""
                        },
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ImpactMetricItem(
    modifier: Modifier = Modifier,
    icon: String,
    value: String,
    unit: String,
    title: String,
    color: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(icon, fontSize = 16.sp)
                Text(
                    text = "$value $unit",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = color
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun QuickTopicChip(
    modifier: Modifier = Modifier,
    emoji: String,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}
