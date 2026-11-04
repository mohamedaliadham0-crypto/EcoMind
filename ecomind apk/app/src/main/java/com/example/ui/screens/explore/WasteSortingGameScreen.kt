package com.example.ui.screens.explore

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.models.WasteItem
import com.example.data.models.WasteType
import com.example.ui.components.SoundHapticManager
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WasteSortingGameScreen(
    items: List<WasteItem>,
    language: AppLanguage,
    onBack: () -> Unit,
    onItemSorted: (Boolean) -> Unit,
    soundEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val isAr = language == AppLanguage.ARABIC
    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var lastFeedback by remember { mutableStateOf<Boolean?>(null) }
    var showTip by remember { mutableStateOf(false) }

    val currentItem = items.getOrNull(currentIndex % items.size)

    BackHandler { onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isAr) "لعبة فرز النفايات الذكية" else "Smart Waste Sorting Game",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_back_waste_game")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Score and Streak Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldGreen.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🏆", fontSize = 16.sp)
                        Text(
                            text = if (isAr) "النقاط: $score" else "Score: $score",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = EmeraldGreen
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SolarYellow.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🔥", fontSize = 16.sp)
                        Text(
                            text = if (isAr) "التتابع: $streak" else "Streak: $streak",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = DarkCharcoal
                        )
                    }
                }
            }

            Text(
                text = if (isAr) "اضغط على الصندوق المناسب لوضع هذا العنصر:" else "Tap the correct recycling bin for this item:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            // Current Waste Item Card
            if (currentItem != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(4.dp),
                    border = BorderStroke(1.5.dp, EmeraldGreen.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(currentItem.iconEmoji, fontSize = 54.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isAr) currentItem.nameAr else currentItem.nameEn,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Feedback Banner (Correct / Incorrect & Tip)
            AnimatedVisibility(visible = showTip && currentItem != null) {
                if (currentItem != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = if (lastFeedback == true) SoftMint else EcoRed.copy(alpha = 0.12f),
                        border = BorderStroke(
                            1.dp,
                            if (lastFeedback == true) EmeraldGreen else EcoRed
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (lastFeedback == true) Icons.Filled.Check else Icons.Filled.Close,
                                    contentDescription = null,
                                    tint = if (lastFeedback == true) EmeraldGreen else EcoRed
                                )
                                Text(
                                    text = if (lastFeedback == true) {
                                        if (isAr) "إجابة صحيحة! أحسنت (+5 نقاط)" else "Correct! Well Done (+5 pts)"
                                    } else {
                                        if (isAr) "غير صحيح! الصندوق الأنسب هو: ${currentItem.type.titleAr}" else "Incorrect! Correct bin: ${currentItem.type.titleEn}"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (lastFeedback == true) ForestGreen else EcoRed
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isAr) currentItem.tipAr else currentItem.tipEn,
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    showTip = false
                                    currentIndex++
                                },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .height(36.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (lastFeedback == true) EmeraldGreen else DarkCharcoal
                                )
                            ) {
                                Text(
                                    text = if (isAr) "العنصر التالي" else "Next Item",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 4 Sorting Bins Grid (2x2)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WasteBinButton(
                        modifier = Modifier.weight(1f),
                        type = WasteType.PLASTIC,
                        icon = "🧴",
                        isAr = isAr,
                        enabled = !showTip,
                        onClick = {
                            val isCorrect = currentItem?.type == WasteType.PLASTIC
                            lastFeedback = isCorrect
                            showTip = true
                            if (isCorrect) {
                                score += 5
                                streak++
                                SoundHapticManager.playSuccess(view, soundEnabled)
                            } else {
                                streak = 0
                                SoundHapticManager.playError(view, soundEnabled)
                            }
                            onItemSorted(isCorrect)
                        }
                    )

                    WasteBinButton(
                        modifier = Modifier.weight(1f),
                        type = WasteType.ORGANIC,
                        icon = "🍏",
                        isAr = isAr,
                        enabled = !showTip,
                        onClick = {
                            val isCorrect = currentItem?.type == WasteType.ORGANIC
                            lastFeedback = isCorrect
                            showTip = true
                            if (isCorrect) {
                                score += 5
                                streak++
                                SoundHapticManager.playSuccess(view, soundEnabled)
                            } else {
                                streak = 0
                                SoundHapticManager.playError(view, soundEnabled)
                            }
                            onItemSorted(isCorrect)
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WasteBinButton(
                        modifier = Modifier.weight(1f),
                        type = WasteType.PAPER,
                        icon = "📦",
                        isAr = isAr,
                        enabled = !showTip,
                        onClick = {
                            val isCorrect = currentItem?.type == WasteType.PAPER
                            lastFeedback = isCorrect
                            showTip = true
                            if (isCorrect) {
                                score += 5
                                streak++
                                SoundHapticManager.playSuccess(view, soundEnabled)
                            } else {
                                streak = 0
                                SoundHapticManager.playError(view, soundEnabled)
                            }
                            onItemSorted(isCorrect)
                        }
                    )

                    WasteBinButton(
                        modifier = Modifier.weight(1f),
                        type = WasteType.E_WASTE,
                        icon = "🔋",
                        isAr = isAr,
                        enabled = !showTip,
                        onClick = {
                            val isCorrect = currentItem?.type == WasteType.E_WASTE
                            lastFeedback = isCorrect
                            showTip = true
                            if (isCorrect) {
                                score += 5
                                streak++
                                SoundHapticManager.playSuccess(view, soundEnabled)
                            } else {
                                streak = 0
                                SoundHapticManager.playError(view, soundEnabled)
                            }
                            onItemSorted(isCorrect)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun WasteBinButton(
    modifier: Modifier = Modifier,
    type: WasteType,
    icon: String,
    isAr: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val binColor = Color(type.colorHex)

    Card(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(enabled = enabled) { onClick() }
            .testTag("bin_${type.name.lowercase()}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = binColor.copy(alpha = 0.15f)),
        border = BorderStroke(1.5.dp, binColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isAr) type.titleAr else type.titleEn,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
        }
    }
}
