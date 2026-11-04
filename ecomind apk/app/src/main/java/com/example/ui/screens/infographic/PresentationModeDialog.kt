package com.example.ui.screens.infographic

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AppLanguage
import com.example.ui.components.shareProject
import com.example.ui.theme.*
import kotlinx.coroutines.delay

data class PresentationSlide(
    val titleEn: String,
    val titleAr: String,
    val subtitleEn: String,
    val subtitleAr: String,
    val iconEmoji: String,
    val bulletsEn: List<String>,
    val bulletsAr: List<String>,
    val judgeNoteEn: String,
    val judgeNoteAr: String
)

@Composable
fun PresentationModeDialog(
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isAr = language == AppLanguage.ARABIC
    var currentSlideIndex by remember { mutableIntStateOf(0) }
    var secondsElapsed by remember { mutableIntStateOf(0) }

    // Simple presentation timer
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            secondsElapsed++
        }
    }

    val slides = remember(isAr) {
        listOf(
            PresentationSlide(
                titleEn = "ECOMIND: Smart Environmental Media",
                titleAr = "إيكومايند: التوعية والإعلام البيئي الذكي",
                subtitleEn = "Green Minds Competition — Category 4: Ministry of Education",
                subtitleAr = "مسابقة عقول خضراء — الفئة الرابعة: وزارة التربية والتعليم",
                iconEmoji = "🌍",
                bulletsEn = listOf(
                    "Project Goal: Transform passive reading into active, digital environmental stewardship.",
                    "Integrates real-time calculations, bilingual localization, and scientific rigor.",
                    "Author / Developer: Adham Mohamed Ali",
                    "Submission Category: Smart Environmental Awareness and Media (التوعية والإعلام البيئي الذكي)"
                ),
                bulletsAr = listOf(
                    "الهدف: تحويل التوعية البيئية من قراءة سلبية إلى تفاعل رقمي وسلوك يومي مستدام.",
                    "دمج الحسابات العلمية الواقعية، الثنائية اللغوية التامة، ومحاكاة النظم البيئية.",
                    "المطور والمبتكر: أدهم محمد علي (Adham Mohamed Ali)",
                    "فئة المشاركة: التوعية والإعلام البيئي الذكي بمسابقة عقول خضراء الرسمية."
                ),
                judgeNoteEn = "ECOMIND delivers a production-grade native Android app with zero mockups, offering real interactivity for Egyptian schools.",
                judgeNoteAr = "يقدم المشروع تطبيقاً حقيقياً متكاملاً بدون قوالب زائفة، مصمماً خصيصاً للتطبيق الفعلي في المدارس المصرية."
            ),
            PresentationSlide(
                titleEn = "Planetary Equilibrium & Key Challenges",
                titleAr = "حالة الكوكب والتحديات البيئية الكبرى",
                subtitleEn = "Scientific Diagnosis of Anthropogenic Pressures",
                subtitleAr = "التشخيص العلمي للضغوط البيئية على كوكب الأرض",
                iconEmoji = "⚠️",
                bulletsEn = listOf(
                    "Global ecological overshoot: humanity consumes 1.75 Earths of annual biocapacity.",
                    "6 interconnected planetary boundaries are under critical pressure: Climate, Oceans, Forests, Freshwater, Air, and Soil.",
                    "Accurate distinction between day-to-day weather vs 30-year climate averages.",
                    "Data backed by UNEP, IPCC, and NASA Earth Observatory."
                ),
                bulletsAr = listOf(
                    "تجاوز القدرة البيئية: البشرية تستهلك سنوياً ما يعادل 1.75 كوكب أرض من الموارد المتجددة.",
                    "6 محاور كوكبية تحت ضغط شديد: المناخ، المحيطات، الغابات، المياه العذبة، الهواء، والتربة.",
                    "تأصيل الفارق العلمي الدقيق بين تقلبات الطقس الآنية والمتوسط المناخي عبر العقود.",
                    "بيانات مستندة إلى تقارير برنامج الأمم المتحدة للبيئة (UNEP) ومرصد ناسا."
                ),
                judgeNoteEn = "Clear scientific accuracy avoids sensationalism and instills factual comprehension in students.",
                judgeNoteAr = "الالتزام بالدقة العلمية الموثقة دون تهويل، مع ترسيخ الفهم المنهجي لدى الطلاب والنشء."
            ),
            PresentationSlide(
                titleEn = "Renewable Energy in Egypt & Worldwide",
                titleAr = "الطاقة المتجددة في مصر والعالم",
                subtitleEn = "Harnessing Solar Irradiance & Kinetic Wind Currents",
                subtitleAr = "استثمار الإشعاع الشمسي وحركة الرياح لتوليد طاقة نظيفة",
                iconEmoji = "☀️",
                bulletsEn = listOf(
                    "Benban Solar Park (Aswan, Egypt): 1,465 MW capacity, avoiding 2M tons of CO2 annually.",
                    "Zafarana & Gulf of Suez Wind Farms: exploiting ideal Red Sea thermal wind corridors.",
                    "Photovoltaic physics: silicon semiconductor excitation via the photoelectric effect.",
                    "Transitioning from fossil fuels to clean electrons decarbonizes industrial grids."
                ),
                bulletsAr = listOf(
                    "مجمع بنبان للطاقة الشمسية بأسوان: قدرة 1465 ميجاوات توفر مليوني طن من انبعاثات الكربون سنوياً.",
                    "مزارع رياح الزعفرانة وخليج السويس: استغلال تيارات الرياح الحرارية الاستثنائية بالبحر الأحمر.",
                    "فيزياء الخلايا الكهروضوئية: تحرير الإلكترونات بفوتونات الضوء في أشباه موصلات السيليكون.",
                    "التحول نحو الكهرباء النظيفة هو مفتاح نزع الكربون من الصناعة والمدن الحضرية."
                ),
                judgeNoteEn = "Connects universal environmental science directly with Egypt's pioneering clean megaprojects.",
                judgeNoteAr = "ربط المفاهيم البيئية العالمية مباشرة بالمشروعات القومية المصرية الرائدة في أسوان والسويس."
            ),
            PresentationSlide(
                titleEn = "Circular Economy & Smart Waste Sorting",
                titleAr = "الاقتصاد الدائري والفرز الذكي للنفايات",
                subtitleEn = "Eliminating the Linear 'Take-Make-Waste' Model",
                subtitleAr = "القضاء على نموذج الاستهلاك الخطي والانتقال للحلقات المغلقة",
                iconEmoji = "♻️",
                bulletsEn = listOf(
                    "95% energy savings achieved by recycling aluminum versus raw bauxite smelting.",
                    "Interactive sorting game trains students on 4 distinct waste streams (Plastics, Organics, Paper, E-waste).",
                    "Organic composting prevents dangerous anaerobic methane generation in municipal landfills.",
                    "Elimination of single-use plastics to protect marine life and human food safety."
                ),
                bulletsAr = listOf(
                    "توفير 95% من الطاقة الكهربائية عند إعادة تدوير الألومنيوم مقارنة باستخراجه من خام البوكسيت.",
                    "لعبة وتدريب تفاعلي يعلم الطلاب كيفية فرز 4 مسارات: البلاستيك، العضوي، الورق، والإلكترونيات.",
                    "التسميد العضوي المنزلي يمنع انبعاث غاز الميثان الضار من مكبات القمامة وينتج سماداً خصباً.",
                    "الاستغناء عن البلاستيك أحادي الاستخدام لحماية الكائنات البحرية والإنسان من الميكروبلاستيك."
                ),
                judgeNoteEn = "Hands-on gamification drives lasting habit formation rather than temporary memorization.",
                judgeNoteAr = "التلعيب (Gamification) يحول السلوكيات الخضراء إلى عادات راسخة بدلاً من مجرد الحفظ النظري."
            ),
            PresentationSlide(
                titleEn = "Water Security & River Nile Protection",
                titleAr = "الأمن المائي وحماية نهر النيل الخالد",
                subtitleEn = "Every Drop Counts in Egypt's Arid Climate",
                subtitleAr = "كل قطرة ماء تساوي حياة في بيئتنا الجافة",
                iconEmoji = "💧",
                bulletsEn = listOf(
                    "The River Nile supplies 95%+ of Egypt's freshwater needs.",
                    "Transitioning to modern precision drip irrigation saves up to 40% of agricultural water.",
                    "Domestic faucet aerators reduce residential consumption by 30-40% without compromising comfort.",
                    "Integrated Water Calculator provides instant math on household conservation impact."
                ),
                bulletsAr = listOf(
                    "نهر النيل يمد مصر بأكثر من 95% من احتياجاتها المائية العذبة.",
                    "تحديث منظومات الري والتحول للتنقيط الذكي يوفر ما يصل إلى 40% من مياه الزراعة.",
                    "تركيب مهويات وقطع الترشيد يخفض استهلاك المنازل بنسبة 30 إلى 40% دون التأثير على الكفاءة.",
                    "الحاسبة المائية المدمجة توضح للأسرة والطلاب كميات المياه الموفرة بلغة الأرقام الدقيقة."
                ),
                judgeNoteEn = "Directly addresses national environmental priorities: water scarcity and food security.",
                judgeNoteAr = "يخاطب مباشرة الأولويات البيئية والوطنية الملحة في ترشيد المياه وتعزيز الأمن المائي."
            ),
            PresentationSlide(
                titleEn = "Empowered Youth: Small Actions, Big Changes",
                titleAr = "شباب مصر الواعي: خطوات صغيرة، تغييرات كبيرة",
                subtitleEn = "Building a Sustainable Tomorrow Together",
                subtitleAr = "معاً نبني مستقبلاً أخضر مستداماً لأجيالنا القادمة",
                iconEmoji = "✨",
                bulletsEn = listOf(
                    "Gamified challenges with verifiable criteria and eco-level roadmaps.",
                    "30+ localized educational quizzes testing knowledge and reinforcing scientific retention.",
                    "100% offline-ready application architecture ensuring accessibility across all school environments.",
                    "ECOMIND is ready for direct deployment in classrooms and green student clubs."
                ),
                bulletsAr = listOf(
                    "تحديات بيئية يومية وأسبوعية ذات معايير واضحة وخريطة تقدم ونقاط شرفية.",
                    "بنك أسئلة يضم أكثر من 30 سؤالاً علمياً مع شروحات فورية لتعزيز التحصيل الدراسي.",
                    "معمارية تعمل 100% بدون إنترنت لضمان سهولة التشغيل في جميع المدارس والمناطق.",
                    "تطبيق إيكومايند جاهز تماماً للاستخدام الفعلي في الفصول والأندية البيئية المدرسية."
                ),
                judgeNoteEn = "Demonstrates complete technical, design, and pedagogical readiness for Green Minds 2026.",
                judgeNoteAr = "جاهزية برمجية وفنية وتربوية شاملة تليق بمسابقة عقول خضراء لوزارة التربية والتعليم."
            )
        )
    }

    val currentSlide = slides[currentSlideIndex]

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(ForestGreen),
            color = ForestGreen
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                // Top Presentation Bar: Timer, Slide Count, Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Timer & Category Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldGreen.copy(alpha = 0.25f)
                        ) {
                            val mins = secondsElapsed / 60
                            val secs = secondsElapsed % 60
                            Text(
                                text = "⏱️ %02d:%02d".format(mins, secs),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = FreshGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = if (isAr) "الشريحة ${currentSlideIndex + 1} / ${slides.size}" else "Slide ${currentSlideIndex + 1} of ${slides.size}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = WarmOffWhite.copy(alpha = 0.8f)
                        )
                    }

                    // Share & Close
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconButton(
                            onClick = { shareProject(context, isAr) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = "Share", tint = PureWhite)
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("btn_close_presentation")
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Close", tint = PureWhite)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Slide Progress Indicator Bar
                LinearProgressIndicator(
                    progress = { (currentSlideIndex + 1) / slides.size.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = FreshGreen,
                    trackColor = WarmOffWhite.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Slide Main Display Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Slide Header with Icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(EmeraldGreen, ForestGreen))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(currentSlide.iconEmoji, fontSize = 28.sp)
                            }

                            Column {
                                Text(
                                    text = if (isAr) currentSlide.titleAr else currentSlide.titleEn,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = PureWhite
                                )
                                Text(
                                    text = if (isAr) currentSlide.subtitleAr else currentSlide.subtitleEn,
                                    fontSize = 12.sp,
                                    color = FreshGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 14.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )

                        // Bullet Points
                        val bullets = if (isAr) currentSlide.bulletsAr else currentSlide.bulletsEn
                        bullets.forEach { bullet ->
                            Row(
                                modifier = Modifier.padding(vertical = 6.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("🌱", fontSize = 16.sp)
                                Text(
                                    text = bullet,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    color = WarmOffWhite.copy(alpha = 0.9f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Special Note for Competition Judges
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = ForestGreen.copy(alpha = 0.8f),
                            border = BorderStroke(1.dp, SolarYellow.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("🎯", fontSize = 14.sp)
                                    Text(
                                        text = if (isAr) "ملاحظة هيئة التحكيم والتقييم:" else "Judges & Evaluators Takeaway:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = SolarYellow
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isAr) currentSlide.judgeNoteAr else currentSlide.judgeNoteEn,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    color = WarmOffWhite
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Slide Navigation Controls (Previous / Next)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Slide Button
                    OutlinedButton(
                        onClick = {
                            if (currentSlideIndex > 0) currentSlideIndex--
                        },
                        enabled = currentSlideIndex > 0,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = FreshGreen,
                            disabledContentColor = WarmOffWhite.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_prev_slide")
                    ) {
                        Icon(
                            if (isAr) Icons.AutoMirrored.Filled.ArrowForward else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "السابق" else "Previous",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Next / Finish Button
                    Button(
                        onClick = {
                            if (currentSlideIndex < slides.size - 1) {
                                currentSlideIndex++
                            } else {
                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_next_slide")
                    ) {
                        Text(
                            text = if (currentSlideIndex == slides.size - 1) {
                                if (isAr) "إنهاء العرض" else "Finish Presentation"
                            } else {
                                if (isAr) "التالي" else "Next Slide"
                            },
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            if (isAr) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = PureWhite
                        )
                    }
                }
            }
        }
    }
}
