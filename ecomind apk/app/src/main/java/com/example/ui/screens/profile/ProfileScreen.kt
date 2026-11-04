package com.example.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AppLanguage
import com.example.data.Strings
import com.example.data.models.EcoBadge
import com.example.data.models.UserProfile
import com.example.ui.components.Screen
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    badges: List<EcoBadge>,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit,
    onUpdateProfile: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC
    val scrollState = rememberScrollState()

    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(userProfile.username) }
    var editAvatar by remember { mutableStateOf(userProfile.avatarEmoji) }

    val availableAvatars = listOf("🌱", "🌿", "🌍", "⚡", "💧", "♻️", "🦁", "🐬", "🦅", "🪴", "☀️", "🐝")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = Strings.navProfile.get(language),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary
            )

            IconButton(
                onClick = { onNavigate(Screen.Settings) },
                modifier = Modifier.testTag("btn_profile_to_settings")
            ) {
                Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.primary)
            }
        }

        // Profile Identity Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = ForestGreen),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreen.copy(alpha = 0.35f))
                        .clickable { showEditDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(userProfile.avatarEmoji, fontSize = 42.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = userProfile.username,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = PureWhite
                    )
                    IconButton(
                        onClick = {
                            editName = userProfile.username
                            editAvatar = userProfile.avatarEmoji
                            showEditDialog = true
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Edit name",
                            tint = FreshGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldGreen.copy(alpha = 0.3f),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = if (isAr) userProfile.ecoLevelAr else userProfile.ecoLevel,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = FreshGreen,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Points & Actions Counters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ProfileStatItem(
                        value = "${userProfile.ecoPoints}",
                        label = if (isAr) "نقاط بيئية" else "Eco Points"
                    )
                    ProfileStatItem(
                        value = "${userProfile.completedChallengeIds.size}",
                        label = if (isAr) "تحديات منجزة" else "Challenges"
                    )
                    ProfileStatItem(
                        value = "${userProfile.completedLessonIds.size}/6",
                        label = if (isAr) "دروس منتهية" else "Lessons"
                    )
                    ProfileStatItem(
                        value = "${userProfile.quizzesTaken}",
                        label = if (isAr) "اختبارات" else "Quizzes"
                    )
                }
            }
        }

        // Achievements Badges Showcase
        Column {
            Text(
                text = if (isAr) "الأوسمة والإنجازات البيئية:" else "Eco Achievement Badges:",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                badges.forEach { badge ->
                    val isUnlocked = userProfile.unlockedBadgeIds.contains(badge.id)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        border = if (isUnlocked) BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.35f)) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isUnlocked) EmeraldGreen.copy(alpha = 0.18f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isUnlocked) badge.emoji else "🔒",
                                    fontSize = 22.sp
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = if (isAr) badge.titleAr else badge.titleEn,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    if (isUnlocked) {
                                        Text("✓", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                                Text(
                                    text = if (isAr) badge.descAr else badge.descEn,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    lineHeight = 15.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isAr) "الشرط: ${badge.requirementAr}" else "Req: ${badge.requirementEn}",
                                    fontSize = 10.sp,
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Edit Name & Avatar Dialog
    if (showEditDialog) {
        Dialog(onDismissRequest = { showEditDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isAr) "تعديل الملف الشخصي" else "Edit Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text(if (isAr) "اسم البطل البيئي" else "Eco Username") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isAr) "اختر الأفاتار الرمزي:" else "Choose Avatar:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        availableAvatars.take(6).forEach { emoji ->
                            val isSel = editAvatar == emoji
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSel) EmeraldGreen.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { editAvatar = emoji },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 20.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        availableAvatars.drop(6).forEach { emoji ->
                            val isSel = editAvatar == emoji
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSel) EmeraldGreen.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { editAvatar = emoji },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 20.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showEditDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (isAr) "إلغاء" else "Cancel")
                        }

                        Button(
                            onClick = {
                                if (editName.isNotBlank()) {
                                    onUpdateProfile(editName.trim(), editAvatar)
                                }
                                showEditDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Text(if (isAr) "حفظ" else "Save", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            color = PureWhite
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = WarmOffWhite.copy(alpha = 0.8f)
        )
    }
}
