package com.example.ui.screens.quizzes

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
import com.example.data.models.QuizQuestion
import com.example.ui.components.SoundHapticManager
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    questions: List<QuizQuestion>,
    language: AppLanguage,
    categoryId: String?,
    onBack: () -> Unit,
    onQuizCompleted: (Int, String) -> Unit,
    soundEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val isAr = language == AppLanguage.ARABIC

    // Filter questions by category if specified, else pick 10 random
    val quizDeck = remember(categoryId, questions) {
        val filtered = if (categoryId != null) {
            questions.filter { it.categoryId == categoryId }
        } else {
            questions
        }
        filtered.shuffled().take(10.coerceAtMost(filtered.size))
    }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }

    val currentQ = quizDeck.getOrNull(currentQuestionIndex)

    BackHandler { onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isAr) "اختبار المعرفة البيئية" else "Environmental Knowledge Quiz",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_back_quiz")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isFinished) {
                // RESULTS SCREEN
                val scorePercent = ((correctCount.toFloat() / quizDeck.size) * 100).toInt()
                val pointsAwarded = (scorePercent / 5).coerceAtLeast(10)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(4.dp),
                    border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (scorePercent >= 80) "🏆🌟" else if (scorePercent >= 50) "🌱👍" else "📚💪",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (scorePercent >= 80) {
                                if (isAr) "أداء بيئي استثنائي!" else "Outstanding Eco Scholar!"
                            } else if (scorePercent >= 50) {
                                if (isAr) "نتيجة طيبة ومبشرة!" else "Great Green Effort!"
                            } else {
                                if (isAr) "فرصة رائعة لتعزيز المعرفة!" else "Keep Learning and Growing!"
                            },
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$correctCount / ${quizDeck.size} ${if (isAr) "إجابات صحيحة" else "correct answers"}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = EmeraldGreen
                        )
                        Text(
                            text = "$scorePercent%",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp,
                            color = ForestGreen
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SoftMint,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isAr) "+$pointsAwarded نقطة بيئية مضافة لملفك الشخصي!" else "+$pointsAwarded Eco Points Added to Profile!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = ForestGreen,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    currentQuestionIndex = 0
                                    selectedOptionIndex = null
                                    hasAnswered = false
                                    correctCount = 0
                                    isFinished = false
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isAr) "إعادة الاختبار" else "Retry Quiz")
                            }

                            Button(
                                onClick = onBack,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                            ) {
                                Text(if (isAr) "العودة للدروس" else "Done", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else if (currentQ != null) {
                // QUESTION SCREEN
                // Progress Bar & Count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "السؤال ${currentQuestionIndex + 1} من ${quizDeck.size}" else "Question ${currentQuestionIndex + 1} of ${quizDeck.size}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (isAr) "الصحيح: $correctCount" else "Score: $correctCount",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldGreen
                    )
                }

                LinearProgressIndicator(
                    progress = { (currentQuestionIndex + 1) / quizDeck.size.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = EmeraldGreen,
                    trackColor = EmeraldGreen.copy(alpha = 0.15f)
                )

                // Question Text Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = if (isAr) currentQ.questionAr else currentQ.questionEn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Options (4 Choices)
                val options = if (isAr) currentQ.optionsAr else currentQ.optionsEn
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    options.forEachIndexed { index, optionText ->
                        val isSelected = selectedOptionIndex == index
                        val isCorrect = index == currentQ.correctIndex

                        val containerColor = when {
                            !hasAnswered -> if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            isCorrect -> SoftMint
                            isSelected -> EcoRed.copy(alpha = 0.12f)
                            else -> MaterialTheme.colorScheme.surface
                        }

                        val borderColor = when {
                            !hasAnswered -> if (isSelected) EmeraldGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            isCorrect -> EmeraldGreen
                            isSelected -> EcoRed
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable(enabled = !hasAnswered) {
                                    selectedOptionIndex = index
                                    hasAnswered = true
                                    if (index == currentQ.correctIndex) {
                                        correctCount++
                                        SoundHapticManager.playSuccess(view, soundEnabled)
                                    } else {
                                        SoundHapticManager.playError(view, soundEnabled)
                                    }
                                }
                                .testTag("quiz_opt_$index"),
                            shape = RoundedCornerShape(14.dp),
                            color = containerColor,
                            border = BorderStroke(1.5.dp, borderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (hasAnswered && isCorrect) EmeraldGreen
                                            else if (hasAnswered && isSelected) EcoRed
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (hasAnswered && isCorrect) {
                                        Icon(Icons.Filled.Check, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                                    } else if (hasAnswered && isSelected) {
                                        Icon(Icons.Filled.Close, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                                    } else {
                                        val letters = listOf("A", "B", "C", "D")
                                        val arLetters = listOf("أ", "ب", "ج", "د")
                                        Text(
                                            text = if (isAr) arLetters.getOrElse(index) { "${index+1}" } else letters.getOrElse(index) { "${index+1}" },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Text(
                                    text = optionText,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected || (hasAnswered && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Explanation Banner after answering
                AnimatedVisibility(visible = hasAnswered) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftMint.copy(alpha = 0.4f)),
                        border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("💡", fontSize = 16.sp)
                                Text(
                                    text = if (isAr) "التفسير العلمي:" else "Scientific Explanation:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ForestGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isAr) currentQ.explanationAr else currentQ.explanationEn,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    if (currentQuestionIndex < quizDeck.size - 1) {
                                        currentQuestionIndex++
                                        selectedOptionIndex = null
                                        hasAnswered = false
                                    } else {
                                        isFinished = true
                                        val scorePct = ((correctCount.toFloat() / quizDeck.size) * 100).toInt()
                                        onQuizCompleted(scorePct, categoryId ?: "general")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("btn_next_question"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                            ) {
                                Text(
                                    text = if (currentQuestionIndex == quizDeck.size - 1) {
                                        if (isAr) "عرض النتيجة النهائية" else "View Results"
                                    } else {
                                        if (isAr) "السؤال التالي" else "Next Question"
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
