package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AppLanguage
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.WarmOffWhite

@Composable
fun CompetitionInfoDialog(
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isAr = language == AppLanguage.ARABIC

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
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
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ForestGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏆", fontSize = 18.sp)
                        }
                        Column {
                            Text(
                                text = if (isAr) "مسابقة عقول خضراء" else "Green Minds Competition",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (isAr) "وزارة التربية والتعليم والتعليم الفني" else "Egyptian Ministry of Education",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_info_dialog")
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

                // Category badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldGreen.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isAr) "الفئة الرابعة (Category 4):" else "Competition Category 4:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isAr) "التوعية والإعلام البيئي الذكي" else "Smart Environmental Awareness and Media",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Developer credit (Explicitly requested by user)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("👨‍💻", fontSize = 24.sp)
                        Column {
                            Text(
                                text = if (isAr) "المطور المبتكر:" else "Project Developer:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Adham Mohamed Ali (أدهم محمد علي)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Project Overview
                Text(
                    text = if (isAr) "عن مشروع إيكومايند (ECOMIND):" else "About Project ECOMIND:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isAr) {
                        "إيكومايند هو منصة توعية بيئية تفاعلية شاملة مصممة لدعم المدارس والطلاب نحو تبني الممارسات المستدامة. يدمج المشروع بين إنفوجرافيك تفاعلي متميز ('مستقبل كوكبنا') مزود بوضع عرض تقديمي خاص لهيئات التحكيم، ووحدات تعليمية، ولعبة ذكية لفرز النفايات، وحاسبات توفير الطاقة والمياه، وبنك اختبارات علمي يضم أكثر من 30 سؤالاً موثقاً."
                    } else {
                        "ECOMIND is a comprehensive smart environmental awareness application designed to empower students and families toward sustainable living. It unites an interactive infographic ('The Future of Our Planet') with an independent presentation deck for judges, rich scientific learning modules, an interactive waste sorting game, real-world energy & water calculators, and 30+ verified quiz challenges."
                    },
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Scientific references
                Text(
                    text = if (isAr) "المراجع العلمية المعتمدة:" else "Scientific References & Sources:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                val sources = if (isAr) {
                    listOf(
                        "برنامج الأمم المتحدة للبيئة (UNEP)",
                        "مرصد ناسا للأرض (NASA Earth Observatory)",
                        "المنظمة العالمية للأرصاد الجوية (WMO)",
                        "وكالة الطاقة الدولية (IEA)",
                        "وزارة الكهرباء والطاقة المتجددة المصرية (مجمع بنبان)"
                    )
                } else {
                    listOf(
                        "United Nations Environment Programme (UNEP)",
                        "NASA Earth Observatory",
                        "World Meteorological Organization (WMO)",
                        "International Energy Agency (IEA)",
                        "Egyptian Ministry of Electricity & Renewable Energy (Benban Solar)"
                    )
                }
                sources.forEach { source ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("• ", color = EmeraldGreen, fontWeight = FontWeight.Bold)
                        Text(
                            text = source,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Share Project button
                Button(
                    onClick = {
                        shareProject(context, isAr)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_share_project_info"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "مشاركة بطاقة المشروع" else "Share ECOMIND Project",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun shareProject(context: Context, isAr: Boolean) {
    val message = if (isAr) {
        """
        🌱 تطبيق إيكومايند (ECOMIND)
        المشارك في مسابقة عقول خضراء - وزارة التربية والتعليم
        الفئة 4: التوعية والإعلام البيئي الذكي
        المطور: أدهم محمد علي (Adham Mohamed Ali)
        
        "خطوات صغيرة. تغييرات كبيرة."
        اكتشف مستقبل كوكبنا، وتعلم ترشيد المياه والطاقة، وافرز النفايات بطريقة ذكية! 🌍💚
        """.trimIndent()
    } else {
        """
        🌱 ECOMIND App
        Submitted to the Green Minds Competition — Egyptian Ministry of Education
        Category 4: Smart Environmental Awareness and Media
        Developer: Adham Mohamed Ali
        
        "Small Actions. Big Changes."
        Discover the future of our planet with interactive infographics, waste sorting, and eco-calculators! 🌍💚
        """.trimIndent()
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "ECOMIND — Green Minds Project")
        putExtra(Intent.EXTRA_TEXT, message)
    }
    context.startActivity(Intent.createChooser(intent, if (isAr) "مشاركة عبر" else "Share via"))
}
