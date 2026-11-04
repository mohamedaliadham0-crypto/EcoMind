package com.example.ui.screens.infographic

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.ui.components.SoundHapticManager
import com.example.ui.components.shareProject
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

enum class InfographicSection(val titleEn: String, val titleAr: String, val icon: String) {
    TODAY("Our Planet Today", "كوكبنا اليوم", "🌍"),
    CHALLENGES("Challenges", "التحديات الكبرى", "⚠️"),
    RENEWABLE("Renewable Energy", "طاقة المستقبل", "⚡"),
    RECYCLING("Recycling Loop", "دورة التدوير", "♻️"),
    WATER("Water Is Life", "الماء سر الحياة", "💧"),
    ACTIONS("Small Actions", "خطوات مؤثرة", "🌱"),
    FUTURE("Sustainable Future", "مستقبلنا الأخضر", "✨")
}

@Composable
fun InfographicScreen(
    language: AppLanguage,
    onOpenPresentationMode: () -> Unit,
    soundEnabled: Boolean,
    reducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val isAr = language == AppLanguage.ARABIC
    var selectedSection by remember { mutableStateOf(InfographicSection.TODAY) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        // Top Infographic Header Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = ForestGreen
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isAr) "مستقبل كوكبنا" else "THE FUTURE OF OUR PLANET",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = PureWhite,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (isAr) "فهم كوكبنا. حماية مستقبلنا." else "Understanding Our Planet. Protecting Our Future.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = FreshGreen
                        )
                    }

                    // Presentation Mode Launch Button
                    Button(
                        onClick = {
                            SoundHapticManager.playClick(view, soundEnabled)
                            onOpenPresentationMode()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_launch_presentation_mode")
                    ) {
                        Icon(Icons.Filled.Slideshow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAr) "وضع التحكيم" else "Presentation",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Central Animated Earth Visualization Canvas
                InteractiveEarthOrbiter(
                    reducedMotion = reducedMotion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )
            }
        }

        // Section Tabs Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfographicSection.entries.forEach { section ->
                val isSelected = selectedSection == section
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedSection = section
                        SoundHapticManager.playClick(view, soundEnabled)
                    },
                    label = {
                        Text(
                            text = "${section.icon} ${if (isAr) section.titleAr else section.titleEn}",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldGreen,
                        selectedLabelColor = PureWhite,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("tab_section_${section.name.lowercase()}")
                )
            }
        }

        // Animated Content Section based on selected tab
        AnimatedContent(
            targetState = selectedSection,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(200))
            },
            label = "section_content",
            modifier = Modifier.padding(horizontal = 16.dp)
        ) { section ->
            when (section) {
                InfographicSection.TODAY -> SectionOurPlanetToday(isAr)
                InfographicSection.CHALLENGES -> SectionEnvironmentalChallenges(isAr, soundEnabled)
                InfographicSection.RENEWABLE -> SectionRenewableEnergyVisualizer(isAr, reducedMotion, soundEnabled)
                InfographicSection.RECYCLING -> SectionRecyclingCycleLoop(isAr, soundEnabled)
                InfographicSection.WATER -> SectionWaterIsLife(isAr)
                InfographicSection.ACTIONS -> SectionSmallActionsBigImpact(isAr)
                InfographicSection.FUTURE -> SectionSustainableFuture(isAr, context)
            }
        }
    }
}

// ----------------------------------------------------------------------------
// INTERACTIVE EARTH ORBITER CANVAS
// ----------------------------------------------------------------------------
@Composable
fun InteractiveEarthOrbiter(
    reducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "earth_orbit")
    val orbitAngle by if (!reducedMotion) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(12000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "angle"
        )
    } else {
        remember { mutableFloatStateOf(45f) }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val earthRadius = 45.dp.toPx()
            val orbitRadiusX = 130.dp.toPx()
            val orbitRadiusY = 55.dp.toPx()

            // Orbit path ellipse
            drawOval(
                color = FreshGreen.copy(alpha = 0.25f),
                topLeft = Offset(center.x - orbitRadiusX, center.y - orbitRadiusY),
                size = androidx.compose.ui.geometry.Size(orbitRadiusX * 2, orbitRadiusY * 2),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            // Earth outer atmosphere glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SkyBlue.copy(alpha = 0.5f), Color.Transparent),
                    center = center,
                    radius = earthRadius * 1.5f
                ),
                radius = earthRadius * 1.5f,
                center = center
            )

            // Earth body
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SkyBlue, EmeraldGreen, ForestGreen),
                    center = Offset(center.x - 10.dp.toPx(), center.y - 10.dp.toPx()),
                    radius = earthRadius
                ),
                radius = earthRadius,
                center = center
            )

            // Orbiting satellite elements (Solar particle, clean droplet, green leaf)
            val rad = Math.toRadians(orbitAngle.toDouble())
            val x1 = center.x + (orbitRadiusX * cos(rad)).toFloat()
            val y1 = center.y + (orbitRadiusY * sin(rad)).toFloat()
            drawCircle(color = SolarYellow, radius = 6.dp.toPx(), center = Offset(x1, y1))

            val rad2 = Math.toRadians((orbitAngle + 120.0).mod(360.0))
            val x2 = center.x + (orbitRadiusX * cos(rad2)).toFloat()
            val y2 = center.y + (orbitRadiusY * sin(rad2)).toFloat()
            drawCircle(color = SkyBlue, radius = 5.dp.toPx(), center = Offset(x2, y2))

            val rad3 = Math.toRadians((orbitAngle + 240.0).mod(360.0))
            val x3 = center.x + (orbitRadiusX * cos(rad3)).toFloat()
            val y3 = center.y + (orbitRadiusY * sin(rad3)).toFloat()
            drawCircle(color = FreshGreen, radius = 5.5.dp.toPx(), center = Offset(x3, y3))
        }

        // Center emoji
        Text("🌍", fontSize = 38.sp)
    }
}

// ----------------------------------------------------------------------------
// SECTION A: OUR PLANET TODAY
// ----------------------------------------------------------------------------
@Composable
fun SectionOurPlanetToday(isAr: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        InfographicCard(
            title = if (isAr) "توازن النظم البيئية والموارد الطبيعية" else "Earth's Delicate Ecological Balance",
            icon = "🌿"
        ) {
            Text(
                text = if (isAr) {
                    "كوكب الأرض نظام حيوي مغلق متكامل. توفر الغابات والمحيطات والتربة خدمات بيئية لا تقدر بثمن: تنقية الهواء، تنظيم درجات الحرارة العالمية، تدوير المياه العذبة، وتغذية كافة الكائنات الحية."
                } else {
                    "Earth functions as an interconnected living biosphere. Forests, oceans, and living soils provide invaluable ecological services: purifying air, regulating global temperature, circulating freshwater, and nourishing all species."
                },
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Data Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricPill(
                    modifier = Modifier.weight(1f),
                    label = if (isAr) "عمر الأرض" else "Earth's Age",
                    value = "4.5B",
                    unit = if (isAr) "سنة" else "yrs",
                    color = EmeraldGreen
                )
                MetricPill(
                    modifier = Modifier.weight(1f),
                    label = if (isAr) "سكان الكوكب" else "Global Pop.",
                    value = "8.1B",
                    unit = if (isAr) "نسمة" else "people",
                    color = SolarYellow
                )
                MetricPill(
                    modifier = Modifier.weight(1f),
                    label = if (isAr) "غطاء المحيطات" else "Oceans Cover",
                    value = "71%",
                    unit = if (isAr) "من المساحة" else "surface",
                    color = SkyBlue
                )
            }
        }

        InfographicCard(
            title = if (isAr) "يوم تجاوز القدرة البيئية (Earth Overshoot Day)" else "Ecological Overshoot & Human Footprint",
            icon = "⏱️"
        ) {
            Text(
                text = if (isAr) {
                    "يستهلك البشر حالياً الموارد الطبيعية بمعدل يعادل 1.75 كوكب أرض! هذا الاستنزاف المتسارع للمياه والأسماك والغابات يتجاوز قدرة كوكبنا الطبيعية على التجدد الذاتي سنوياً."
                } else {
                    "Humanity currently consumes ecological resources at a rate equivalent to 1.75 Earths! This accelerated extraction of water, timber, and fisheries outpaces our planet's annual regenerative biocapacity."
                },
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )
        }
    }
}

// ----------------------------------------------------------------------------
// SECTION B: ENVIRONMENTAL CHALLENGES (6 Interactive Expandable Cards)
// ----------------------------------------------------------------------------
@Composable
fun SectionEnvironmentalChallenges(isAr: Boolean, soundEnabled: Boolean) {
    val view = LocalView.current
    var expandedCardId by remember { mutableStateOf<String?>("chal_climate") }

    val challengesList = remember(isAr) {
        listOf(
            ChallengeDetail(
                id = "chal_climate",
                titleEn = "1. Climate Change & Global Warming",
                titleAr = "1. التغير المناخي والاحترار العالمي",
                icon = "🔥",
                problemEn = "Rising concentrations of greenhouse gases trap excessive solar heat in the lower atmosphere.",
                problemAr = "تراكم الغازات الدفيئة يحبس الإشعاع الحراري في الغلاف الجوي مسبباً ارتفاع درجات الحرارة القياسية.",
                causesEn = "Burning fossil fuels (coal, petroleum, gas), deforestation, and industrial cement production.",
                causesAr = "حرق الوقود الأحفوري، إزالة الغابات، والعمليات الصناعية الكثيفة للانبعاثات.",
                consequencesEn = "Extreme droughts, violent storms, sea-level rise threatening coastal deltas, and agricultural disruption.",
                consequencesAr = "موجات جفاف قاسية، فيضانات عارمة، غرق السواحل والدلتا المنخفضة، واضطراب إنتاج المحاصيل.",
                solutionsEn = "Transitioning to 100% clean renewables, mass transit electrification, and reforestation.",
                solutionsAr = "التحول الشامل للطاقة النظيفة، كهربة المواصلات، والتشجير الكثيف لامتصاص الكربون."
            ),
            ChallengeDetail(
                id = "chal_air",
                titleEn = "2. Air Pollution & Toxic Smog",
                titleAr = "2. تلوث الهواء والضباب الدخاني السام",
                icon = "💨",
                problemEn = "Fine particulate matter (PM2.5) and nitrogen oxides contaminate the air breathed by billions.",
                problemAr = "الجسيمات العالقة الدقيقة (PM2.5) وأكاسيد النيتروجين تلوث الهواء وتتسلل للرئتين ومجرى الدم.",
                causesEn = "Vehicle tailpipe exhausts, unscrubbed factory emissions, and agricultural open burning.",
                causesAr = "عوادم المركبات القديمة، انبعاثات المصانع غير المعالجة، والحرق المكشوف للمخلفات الزراعية.",
                consequencesEn = "Respiratory illness, asthma, premature cardiovascular mortality, and damaging acid rain.",
                consequencesAr = "أمراض الرئة والربو وأمراض القلب الوعائية، وتشكل الأمطار الحمضية الضارة بالنبات والتربة.",
                solutionsEn = "Electric mobility, stringent industrial filtration standards, and urban green belts.",
                solutionsAr = "اعتماد السيارات والحافلات الكهربائية، فلاتر صناعية صارمة، وأحزمة خضراء حول المدن."
            ),
            ChallengeDetail(
                id = "chal_water",
                titleEn = "3. Freshwater Scarcity & Depletion",
                titleAr = "3. ندرة المياه العذبة واستنزاف الموارد",
                icon = "🚰",
                problemEn = "Over 2 billion people worldwide live in nations experiencing high water stress.",
                problemAr = "أكثر من ملياري إنسان حول العالم يعانون من فقر مائي حاد وضغوط شديدة على إمدادات الشرب.",
                causesEn = "Inefficient agricultural flooding, underground aquifer overpumping, and municipal water leaks.",
                causesAr = "الري بالغمر المهدد للمياه، السحب الجائر للمياه الجوفية، وشبكات الأنابيب المتهالكة.",
                consequencesEn = "Desertification, crop failures, regional geopolitical tensions, and ecosystem drying.",
                consequencesAr = "التصحر، تراجع الرقعة الزراعية، النزاعات على الأنهار المشتركة، وجفاف الواحات والبحيرات.",
                solutionsEn = "Modern drip irrigation, wastewater recycling, leak telemetry, and desalination powered by solar.",
                solutionsAr = "التحول للري بالتنقيط، معالجة مياه الصرف الزراعي والصحي، وتحلية مياه البحر بالطاقة الشمسية."
            ),
            ChallengeDetail(
                id = "chal_plastic",
                titleEn = "4. Ocean Plastic Pollution",
                titleAr = "4. التلوث البلاستيكي في المحيطات والبحار",
                icon = "🥤",
                problemEn = "Over 11 million metric tons of plastics enter aquatic ecosystems every single year.",
                problemAr = "أكثر من 11 مليون طن من المخلفات البلاستيكية تلقى في البحار والأنهار سنوياً.",
                causesEn = "Single-use packaging culture, poorly managed landfills, and lack of municipal sorting.",
                causesAr = "ثقافة التغليف البلاستيكي أحادي الاستخدام وضعف منظومات الفرز والتدوير من المنبع.",
                consequencesEn = "Ingestion by marine wildlife, choking coral reefs, and microplastics infiltrating human food.",
                consequencesAr = "ابتلاع الأسماك والسلاحف للبلاستيك وموتها، وتسلل جزيئات الميكروبلاستيك لطعام الإنسان.",
                solutionsEn = "Banning single-use plastic bags, bottle-deposit refund schemes, and circular bio-polymers.",
                solutionsAr = "حظر الأكياس البلاستيكية الخفيفة، أنظمة استرداد الزجاجات، وتبني بدائل حيوية قابلة للتحلل."
            ),
            ChallengeDetail(
                id = "chal_forest",
                titleEn = "5. Deforestation & Land Degradation",
                titleAr = "5. إزالة الغابات وتدهور الأراضي",
                icon = "🪓",
                problemEn = "10 million hectares of pristine forests are cleared every year, stripping planetary lungs.",
                problemAr = "إبادة 10 ملايين هكتار من الغابات الاستوائية والبرية سنوياً يمزق رئة الأرض الطبيعية.",
                causesEn = "Clear-cutting for cattle grazing, monoculture palm oil plantations, and commercial timber logging.",
                causesAr = "قطع الأشجار للمراعي الحيوانية، مزارع زيت النخيل الأحادية، والتجارة الجائرة بالأخشاب.",
                consequencesEn = "Massive carbon emission release, soil erosion, and disruption of continental rainfall cycles.",
                consequencesAr = "انطلاق مليارات أطنان الكربون المخزن، انجراف التربة الخصبة، وتراجع هطول الأمطار.",
                solutionsEn = "Strict national park conservation, satellite monitoring, and sustainable agroforestry.",
                solutionsAr = "حماية المحميات الطبيعية بالأقمار الصناعية، وتجريم القطع، وتطبيق الزراعة الحراجية المستدامة."
            ),
            ChallengeDetail(
                id = "chal_bio",
                titleEn = "6. Accelerating Biodiversity Loss",
                titleAr = "6. تدهور التنوع البيولوجي والانقراض",
                icon = "🦁",
                problemEn = "Current species extinction rates are estimated at 1,000 times higher than natural background levels.",
                problemAr = "معدلات انقراض الأنواع الحالية تزيد 1,000 مرة عن المعدلات الطبيعية التاريخية للأرض.",
                causesEn = "Habitat fragmentation, illegal poaching, pesticide toxicity, and warming ocean waters.",
                causesAr = "تفتيت المواطن الطبيعية، الصيد الجائر، المبيدات الكيميائية السامة، وابيضاض المرجان.",
                consequencesEn = "Collapse of pollination chains, weakening biosphere resilience, and loss of future medicines.",
                consequencesAr = "انهيار خدمات التلقيح الزراعي، فقدان صمود النظم البيئية، وضياع مصادر الأدوية الطبيعية.",
                solutionsEn = "Ecological corridors connecting reserves, marine protected zones, and pollinator gardens.",
                solutionsAr = "إنشاء ممرات بيئية بين المحميات، حماية الشعاب المرجانية، وزراعة حدائق الملقحات والنباتات المحلية."
            )
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = if (isAr) "اضغط على أي تحدٍ لاستعراض أسبابه، نتائجه، والحلول المقترحة:" else "Tap any challenge card to explore causes, consequences, and solutions:",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        challengesList.forEach { item ->
            val isExpanded = expandedCardId == item.id

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        expandedCardId = if (isExpanded) null else item.id
                        SoundHapticManager.playClick(view, soundEnabled)
                    }
                    .testTag("card_chal_${item.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isExpanded) SoftMint.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                ),
                border = if (isExpanded) BorderStroke(1.dp, EmeraldGreen) else BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(item.icon, fontSize = 20.sp)
                            Text(
                                text = if (isAr) item.titleAr else item.titleEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = EmeraldGreen
                        )
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            DetailSectionRow(
                                title = if (isAr) "ما هي المشكلة؟" else "The Problem",
                                content = if (isAr) item.problemAr else item.problemEn,
                                color = EcoRed
                            )
                            DetailSectionRow(
                                title = if (isAr) "الأسباب الرئيسية:" else "Main Causes",
                                content = if (isAr) item.causesAr else item.causesEn,
                                color = SolarYellow
                            )
                            DetailSectionRow(
                                title = if (isAr) "الآثار والنتائج:" else "Consequences",
                                content = if (isAr) item.consequencesAr else item.consequencesEn,
                                color = EcoOrange
                            )
                            DetailSectionRow(
                                title = if (isAr) "الحلول المستدامة:" else "Sustainable Solutions",
                                content = if (isAr) item.solutionsAr else item.solutionsEn,
                                color = EmeraldGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

data class ChallengeDetail(
    val id: String,
    val titleEn: String,
    val titleAr: String,
    val icon: String,
    val problemEn: String,
    val problemAr: String,
    val causesEn: String,
    val causesAr: String,
    val consequencesEn: String,
    val consequencesAr: String,
    val solutionsEn: String,
    val solutionsAr: String
)

@Composable
fun DetailSectionRow(
    title: String,
    content: String,
    color: Color
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = color
            )
        }
        Text(
            text = content,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
            modifier = Modifier.padding(start = 14.dp, top = 2.dp)
        )
    }
}

// ----------------------------------------------------------------------------
// SECTION C: THE POWER OF RENEWABLE ENERGY (Interactive Solar, Wind, Hydro)
// ----------------------------------------------------------------------------
enum class EnergyType { SOLAR, WIND, HYDRO }

@Composable
fun SectionRenewableEnergyVisualizer(
    isAr: Boolean,
    reducedMotion: Boolean,
    soundEnabled: Boolean
) {
    val view = LocalView.current
    var selectedEnergy by remember { mutableStateOf(EnergyType.SOLAR) }

    // Canvas animation tick
    val transition = rememberInfiniteTransition(label = "energy_anim")
    val pulse by if (!reducedMotion) {
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "pulse"
        )
    } else {
        remember { mutableFloatStateOf(0.5f) }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Selector Buttons: Solar, Wind, Hydro
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    selectedEnergy = EnergyType.SOLAR
                    SoundHapticManager.playClick(view, soundEnabled)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedEnergy == EnergyType.SOLAR) SolarYellow else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selectedEnergy == EnergyType.SOLAR) DarkCharcoal else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (isAr) "☀️ شمسية" else "☀️ Solar", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = {
                    selectedEnergy = EnergyType.WIND
                    SoundHapticManager.playClick(view, soundEnabled)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedEnergy == EnergyType.WIND) SkyBlue else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selectedEnergy == EnergyType.WIND) DarkCharcoal else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (isAr) "💨 رياح" else "💨 Wind", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = {
                    selectedEnergy = EnergyType.HYDRO
                    SoundHapticManager.playClick(view, soundEnabled)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedEnergy == EnergyType.HYDRO) EmeraldGreen else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selectedEnergy == EnergyType.HYDRO) PureWhite else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (isAr) "🌊 مائية" else "🌊 Hydro", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        // Animated Energy Diagram Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Interactive Diagram Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ForestGreen),
                    contentAlignment = Alignment.Center
                ) {
                    val damOutlineColor = MaterialTheme.colorScheme.outline
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        when (selectedEnergy) {
                            EnergyType.SOLAR -> {
                                // Sun rays shooting into solar panel
                                val sunCenter = Offset(w * 0.2f, h * 0.3f)
                                drawCircle(color = SolarYellow, radius = 22.dp.toPx(), center = sunCenter)

                                // Animated photons
                                for (i in 0..4) {
                                    val progress = (pulse + i * 0.2f).mod(1.0f)
                                    val px = sunCenter.x + (w * 0.6f - sunCenter.x) * progress
                                    val py = sunCenter.y + (h * 0.7f - sunCenter.y) * progress
                                    drawCircle(color = SolarYellow.copy(alpha = 1f - progress * 0.5f), radius = 3.dp.toPx(), center = Offset(px, py))
                                }

                                // Solar Panel grid
                                val panelTopLeft = Offset(w * 0.55f, h * 0.5f)
                                drawRect(
                                    color = SkyBlue.copy(alpha = 0.85f),
                                    topLeft = panelTopLeft,
                                    size = androidx.compose.ui.geometry.Size(w * 0.35f, h * 0.38f)
                                )
                                // Panel grid lines
                                drawLine(color = PureWhite, start = Offset(w * 0.55f, h * 0.69f), end = Offset(w * 0.9f, h * 0.69f), strokeWidth = 2f)
                                drawLine(color = PureWhite, start = Offset(w * 0.72f, h * 0.5f), end = Offset(w * 0.72f, h * 0.88f), strokeWidth = 2f)
                            }
                            EnergyType.WIND -> {
                                // Turbine mast
                                val mastBase = Offset(w * 0.5f, h * 0.9f)
                                val hubCenter = Offset(w * 0.5f, h * 0.35f)
                                drawLine(color = WarmOffWhite, start = mastBase, end = hubCenter, strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)

                                // Rotating blades
                                val bladeAngle = pulse * 360f
                                for (b in 0..2) {
                                    val ang = Math.toRadians((bladeAngle + b * 120.0).toDouble())
                                    val bx = hubCenter.x + (38.dp.toPx() * cos(ang)).toFloat()
                                    val by = hubCenter.y + (38.dp.toPx() * sin(ang)).toFloat()
                                    drawLine(color = SkyBlue, start = hubCenter, end = Offset(bx, by), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
                                }
                                drawCircle(color = PureWhite, radius = 5.dp.toPx(), center = hubCenter)
                            }
                            EnergyType.HYDRO -> {
                                // Water dam with rushing current
                                val waterFlowX = (pulse * w)
                                drawCircle(color = SkyBlue.copy(alpha = 0.6f), radius = 8.dp.toPx(), center = Offset(waterFlowX, h * 0.6f))
                                drawCircle(color = FreshGreen.copy(alpha = 0.5f), radius = 6.dp.toPx(), center = Offset((waterFlowX + w * 0.3f).mod(w), h * 0.65f))

                                // Dam wall
                                drawRect(color = damOutlineColor, topLeft = Offset(w * 0.6f, h * 0.2f), size = androidx.compose.ui.geometry.Size(w * 0.15f, h * 0.8f))
                            }
                        }
                    }

                    // Educational Overlay Label
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .background(DarkCharcoal.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = when (selectedEnergy) {
                                EnergyType.SOLAR -> if (isAr) "تحويل الفوتونات لتيار كهربائي مباشر (DC)" else "Photons -> Direct Current (DC)"
                                EnergyType.WIND -> if (isAr) "الطاقة الحركية تدير التوربين الكهرومغناطيسي" else "Kinetic Airflow -> Turbine Generator"
                                EnergyType.HYDRO -> if (isAr) "ضغط المياه المندفعة يدير المولدات" else "Hydrostatic Head -> Turbine Induction"
                            },
                            fontSize = 10.sp,
                            color = WarmOffWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Detailed Scientific Explanation
                when (selectedEnergy) {
                    EnergyType.SOLAR -> {
                        Text(
                            text = if (isAr) "كيف تعمل الطاقة الشمسية؟" else "How Solar Photovoltaics Work:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isAr) {
                                "تمتص خلايا السيليكون فوتونات أشعة الشمس، مما يمنح الإلكترونات طاقة كافية للتحرر والتحرك عبر الدائرة لتوليد تيار كهربائي مستمر. يقوم العاكس (Inverter) بتحويله إلى تيار متردد (AC) يغذي المنازل والشبكة القومية."
                            } else {
                                "Silicon semiconductor cells absorb sunlight photons, energizing electrons to flow through an electric circuit as direct current (DC). An inverter immediately converts it into alternating current (AC) ready to power homes and schools."
                            },
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        )
                    }
                    EnergyType.WIND -> {
                        Text(
                            text = if (isAr) "كيف تعمل طاقة الرياح؟" else "How Wind Power Operates:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isAr) {
                                "تستغل شفرات التوربين الديناميكا الهوائية مثل أجنحة الطائرات، مما يدير محور الدوران المتصل بصندوق تروس ومولد كهرومغناطيسي يحول الطاقة الحركية مباشرة إلى طاقة كهربائية نظيفة."
                            } else {
                                "Aerodynamic lift spins the turbine's blades like an airplane wing. This mechanical rotation drives a central shaft connected to a high-efficiency generator, transforming atmospheric kinetic wind directly into clean megawatts."
                            },
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        )
                    }
                    EnergyType.HYDRO -> {
                        Text(
                            text = if (isAr) "كيف تعمل الطاقة الكهرومائية؟" else "How Hydroelectric Dams Function:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isAr) {
                                "تستغل السدود فارق الارتفاع المائي؛ حيث يندفع الماء عبر بوابات وأنفاق ضخمة ليدير توربينات ضخمة (مثل توربينات السد العالي بأسوان)، موفراً طاقة كهربائية متجددة ومستقرة."
                            } else {
                                "Dams capture the gravitational potential energy of falling water. Controlled currents rush through high-pressure penstocks to spin massive hydraulic runners (like Egypt's Aswan High Dam), delivering stable, continuous green electricity."
                            },
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// SECTION D: THE RECYCLING CYCLE (5-Stage Circular Diagram)
// ----------------------------------------------------------------------------
@Composable
fun SectionRecyclingCycleLoop(isAr: Boolean, soundEnabled: Boolean) {
    val view = LocalView.current
    var activeStageIndex by remember { mutableIntStateOf(0) }

    val stages = remember(isAr) {
        listOf(
            RecycleStage("1. Collection", "1. التجميع من المنبع", "🚛",
                "Dedicated municipal and community trucks collect separated dry recyclables from households and businesses.",
                "تقوم عربات مخصصة بجمع المخلفات الجافة المفروزة من المنازل والمدارس والمتاجر دورياً لمنع اختلاطها."),
            RecycleStage("2. Sorting", "2. الفرز والتصنيف", "🔍",
                "Advanced optical sorters and skilled workers classify plastics by polymer resin code (PET, HDPE, PP) and metals.",
                "فرز وتصنيف الخامات بحسب نوع البوليمر (PET, HDPE) والورق والمعادن لضمان أعلى درجات النقاء الصناعي."),
            RecycleStage("3. Processing", "3. المعالجة والغسيل", "⚙️",
                "Materials are washed, shredded into flakes, and decontaminated to eliminate chemical residue.",
                "غسل المواد بدقة وتفتيتها إلى رقائق وحبيبات نظيفة خالية من بقايا الزيوت والأطعمة."),
            RecycleStage("4. Manufacturing", "4. إعادة التصنيع", "🏭",
                "Clean pellets are melted down and molded into brand-new consumer products, clothing fibers, and containers.",
                "صهر الرقائق وتشكيلها لإنتاج منتجات استهلاكية جديدة وألياف ملابس وأنابيب وحاويات متينة."),
            RecycleStage("5. Reuse & Circularity", "5. الاستخدام والاقتصاد الدائري", "🔄",
                "Products re-enter the economy for extended lifecycles, completing the closed-loop circular chain.",
                "عودة المنتجات للدورة الاقتصادية للاستفادة منها لسنوات طويلة، لتكتمل الحلقة الدائرية المغلقة.")
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = if (isAr) "اضغط على كل مرحلة من مراحل الدورة الخماسية لاستكشافها:" else "Tap through each of the 5 circular stages to see how materials cycle:",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        // 5 Stage Badges Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            stages.forEachIndexed { index, stage ->
                val isSelected = activeStageIndex == index
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        activeStageIndex = index
                        SoundHapticManager.playClick(view, soundEnabled)
                    },
                    label = {
                        Text(
                            text = "${stage.icon} ${if (isAr) stage.titleAr.split(".")[0] else stage.titleEn.split(".")[0]}",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldGreen,
                        selectedLabelColor = PureWhite
                    )
                )
            }
        }

        // Active Stage Spotlight Card
        val active = stages[activeStageIndex]
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp),
            border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(active.icon, fontSize = 24.sp)
                    }

                    Column {
                        Text(
                            text = if (isAr) active.titleAr else active.titleEn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isAr) "المرحلة ${activeStageIndex + 1} من 5" else "Stage ${activeStageIndex + 1} of 5",
                            fontSize = 11.sp,
                            color = EmeraldGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (isAr) active.descAr else active.descEn,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )
            }
        }
    }
}

data class RecycleStage(
    val titleEn: String,
    val titleAr: String,
    val icon: String,
    val descEn: String,
    val descAr: String
)

// ----------------------------------------------------------------------------
// SECTION E: WATER IS LIFE
// ----------------------------------------------------------------------------
@Composable
fun SectionWaterIsLife(isAr: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        InfographicCard(
            title = if (isAr) "شريان الحياة: حماية نهر النيل والمياه العذبة" else "Every Drop Counts: Freshwater Preservation",
            icon = "💧"
        ) {
            Text(
                text = if (isAr) {
                    "نهر النيل هو شريان الحياة لمصر ومصدر أكثر من 95% من مياهها العذبة. الحفاظ على كل قطرة ماء مسؤولية وطنية وأخلاقية مشتركة تبدأ من ترشيد الاستهلاك المنزلي وحتى تحديث الزراعة."
                } else {
                    "The River Nile is Egypt's lifeblood, providing over 95% of the nation's freshwater. Preserving every single droplet is both an ecological necessity and a shared civic duty."
                },
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Household Water Conservation Actions
            val tips = if (isAr) {
                listOf(
                    "تركيب مهويات صنابير المياه يوفر ما يصل إلى 40% من التدفق دون التأثير على الضغط.",
                    "غلق الصنبور أثناء تنظيف الأسنان يوفر 12 لتراً نقياً في كل دقيقة.",
                    "إصلاح تسريب واحد في المرحاض يوفر حتى 200 لتر ماء يومياً.",
                    "الري في الصباح الباكر أو بعد الغروب يمنع تبخر أكثر من 30% من مياه الري الزراعي."
                )
            } else {
                listOf(
                    "Installing tap aerators cuts faucet flow by 40% without reducing pressure.",
                    "Turning the tap off while brushing teeth saves up to 12 liters of water each minute.",
                    "Repairing a single running toilet flapper saves up to 200 liters of water daily.",
                    "Irrigating gardens in early morning prevents 30% of moisture loss to direct solar evaporation."
                )
            }

            tips.forEach { tip ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("💧", fontSize = 13.sp)
                    Text(
                        text = tip,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// SECTION F: SMALL ACTIONS, BIG IMPACT
// ----------------------------------------------------------------------------
@Composable
fun SectionSmallActionsBigImpact(isAr: Boolean) {
    val habits = remember(isAr) {
        if (isAr) {
            listOf(
                HabitItem("حقيبة قماشية للتسوق", "تمنع 170 كيس بلاستيكي سنوياً من الوصول إلى مقالب القمامة.", "🛍️"),
                HabitItem("إطفاء الإنارة غير المستخدمة", "توفير 15% من فاتورة الكهرباء المنزلية وحرق الفحم.", "💡"),
                HabitItem("قارورة مياه مستدامة", "حماية المحيطات من 160 زجاجة بلاستيكية وحفظ 3 لتر ماء تصنيعي لكل زجاجة.", "🍶"),
                HabitItem("زراعة شرفة خضراء", "تنقية الهواء المحيط وتوفير رحيق للملقحات كالنحل والفراشات.", "🪴")
            )
        } else {
            listOf(
                HabitItem("Reusable Shopping Bag", "Prevents ~170 single-use plastic bags from entering ecosystems yearly.", "🛍️"),
                HabitItem("Switch Off Idle Lighting", "Cuts household lighting power demand and fossil generation by 15%.", "💡"),
                HabitItem("Stainless Steel Water Canteen", "Eliminates ~160 disposable bottles and saves 3L of manufacturing water per bottle.", "🍶"),
                HabitItem("Window / Balcony Garden", "Purifies urban air and creates vital nectar waypoints for wild pollinators.", "🪴")
            )
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        habits.forEach { habit ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(habit.icon, fontSize = 26.sp)
                    Column {
                        Text(
                            text = habit.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = habit.desc,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                        )
                    }
                }
            }
        }
    }
}

data class HabitItem(val title: String, val desc: String, val icon: String)

// ----------------------------------------------------------------------------
// SECTION G: OUR SUSTAINABLE FUTURE (Call to Action & Pledge)
// ----------------------------------------------------------------------------
@Composable
fun SectionSustainableFuture(isAr: Boolean, context: Context) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ForestGreen),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🌍✨", fontSize = 42.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isAr) "مستقبل كوكبنا يُصنع بقراراتنا اليوم" else "The Future of Our Planet is Shaped by Today's Choices",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = PureWhite
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isAr) {
                    "كل شجرة نزرعها، وكل قطرة ماء نوفرها، وكل كيلووات كهرباء نرشده، وكل فكرة وعي ننشرها بين زملائنا ومجتمعنا تصنع الفارق الحقيقي لمستقبل مستدام لكوكب الأرض."
                } else {
                    "Every tree planted, every liter conserved, every kilowatt saved, and every mind awakened creates the resilient foundation for tomorrow's flourishing Earth."
                },
                fontSize = 12.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.Center,
                color = WarmOffWhite.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { shareProject(context, isAr) },
                colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_share_infographic")
            ) {
                Icon(Icons.Filled.Share, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAr) "مشاركة وثيقة كوكبنا الخضراء" else "Share the Planet Earth Pledge",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ForestGreen
                )
            }
        }
    }
}

// ----------------------------------------------------------------------------
// COMMON INFOGRAPHIC CARD
// ----------------------------------------------------------------------------
@Composable
fun InfographicCard(
    title: String,
    icon: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(icon, fontSize = 20.sp)
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
fun MetricPill(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    unit: String,
    color: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = color
            )
            Text(
                text = unit,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
