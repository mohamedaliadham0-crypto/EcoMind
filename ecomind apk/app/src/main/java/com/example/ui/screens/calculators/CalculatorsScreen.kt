package com.example.ui.screens.calculators

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.Strings
import com.example.ui.components.SoundHapticManager
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorsScreen(
    language: AppLanguage,
    onBack: () -> Unit,
    onApplyImpact: (Double, Double) -> Unit,
    soundEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val isAr = language == AppLanguage.ARABIC
    val scrollState = rememberScrollState()

    // Tab state: 0 = Electricity, 1 = Water
    var selectedTab by remember { mutableIntStateOf(0) }

    // Electricity state
    var monthlyKwh by remember { mutableFloatStateOf(250f) }
    var electricityReductionPercent by remember { mutableFloatStateOf(20f) }

    // Water state
    var dailyLiters by remember { mutableFloatStateOf(180f) }
    var waterReductionPercent by remember { mutableFloatStateOf(25f) }

    var appliedSuccessMessage by remember { mutableStateOf<String?>(null) }

    BackHandler { onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = Strings.openCalculators.get(language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_back_calculators")
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tabs: Electricity vs Water
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        appliedSuccessMessage = null
                        SoundHapticManager.playClick(view, soundEnabled)
                    },
                    text = {
                        Text(
                            text = if (isAr) "⚡ حاسبة الكهرباء" else "⚡ Electricity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        appliedSuccessMessage = null
                        SoundHapticManager.playClick(view, soundEnabled)
                    },
                    text = {
                        Text(
                            text = if (isAr) "💧 حاسبة المياه" else "💧 Water",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                )
            }

            if (selectedTab == 0) {
                // ELECTRICITY CALCULATOR
                val kwhSaved = (monthlyKwh * (electricityReductionPercent / 100f))
                val remainingKwh = monthlyKwh - kwhSaved
                val co2PreventedKg = kwhSaved * 0.42 // ~0.42 kg CO2 per kWh grid factor

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isAr) "استهلاك الكهرباء الشهري التقديري:" else "Current Estimated Monthly Electricity:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "%.0f kWh / month".format(monthlyKwh),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = SolarYellow
                            )
                        }

                        Slider(
                            value = monthlyKwh,
                            onValueChange = { monthlyKwh = it },
                            valueRange = 50f..1000f,
                            steps = 18,
                            colors = SliderDefaults.colors(thumbColor = SolarYellow, activeTrackColor = SolarYellow)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isAr) "نسبة الترشيد المستهدفة:" else "Target Reduction Percentage:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "%.0f%%".format(electricityReductionPercent),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = EmeraldGreen
                        )

                        Slider(
                            value = electricityReductionPercent,
                            onValueChange = { electricityReductionPercent = it },
                            valueRange = 5f..60f,
                            steps = 10,
                            colors = SliderDefaults.colors(thumbColor = EmeraldGreen, activeTrackColor = EmeraldGreen)
                        )
                    }
                }

                // Results Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ForestGreen),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isAr) "نتائج التوفير التقديرية:" else "Estimated Electricity Savings:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = FreshGreen
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isAr) "كهرباء موفرة شهرياً" else "Monthly Power Saved",
                                    fontSize = 11.sp,
                                    color = WarmOffWhite.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "%.1f kWh".format(kwhSaved),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = PureWhite
                                )
                            }
                            Column {
                                Text(
                                    text = if (isAr) "الاستهلاك المتبقي" else "Remaining Consumption",
                                    fontSize = 11.sp,
                                    color = WarmOffWhite.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "%.1f kWh".format(remainingKwh),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = WarmOffWhite
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = FreshGreen.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🍃", fontSize = 18.sp)
                            Column {
                                Text(
                                    text = if (isAr) "انبعاثات كربون خُفضت (تقديرياً):" else "CO2 Prevented (Est.):",
                                    fontSize = 11.sp,
                                    color = WarmOffWhite.copy(alpha = 0.85f)
                                )
                                Text(
                                    text = "%.1f kg CO2 / month".format(co2PreventedKg),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = FreshGreen
                                )
                            }
                        }
                    }
                }

                // Add to impact button
                Button(
                    onClick = {
                        onApplyImpact(kwhSaved.toDouble(), 0.0)
                        appliedSuccessMessage = if (isAr) {
                            "تمت إضافة %.1f كيلووات إلى أثرك البيئي التراكمي! (+15 نقطة)".format(kwhSaved)
                        } else {
                            "Added %.1f kWh to your cumulative impact! (+15 pts)".format(kwhSaved)
                        }
                        SoundHapticManager.playSuccess(view, soundEnabled)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_apply_kwh_impact"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "تطبيق هذا الوفر على أثري التراكمي" else "Apply Savings to My Profile",
                        fontWeight = FontWeight.Bold
                    )
                }

            } else {
                // WATER CALCULATOR
                val monthlyLiters = dailyLiters * 30f
                val waterSavedLiters = monthlyLiters * (waterReductionPercent / 100f)
                val remainingLiters = monthlyLiters - waterSavedLiters

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isAr) "الاستهلاك اليومي للفرد (لتر/يوم):" else "Estimated Daily Water per Person:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "%.0f L / day".format(dailyLiters),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = SkyBlue
                        )

                        Slider(
                            value = dailyLiters,
                            onValueChange = { dailyLiters = it },
                            valueRange = 80f..400f,
                            steps = 16,
                            colors = SliderDefaults.colors(thumbColor = SkyBlue, activeTrackColor = SkyBlue)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isAr) "نسبة الترشيد المستهدفة:" else "Target Conservation Percentage:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "%.0f%%".format(waterReductionPercent),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = EmeraldGreen
                        )

                        Slider(
                            value = waterReductionPercent,
                            onValueChange = { waterReductionPercent = it },
                            valueRange = 5f..60f,
                            steps = 10,
                            colors = SliderDefaults.colors(thumbColor = EmeraldGreen, activeTrackColor = EmeraldGreen)
                        )
                    }
                }

                // Results Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ForestGreen),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isAr) "نتائج ترشيد المياه الشهرية:" else "Estimated Monthly Water Conservation:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = FreshGreen
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isAr) "مياه موفرة شهرياً" else "Monthly Water Saved",
                                    fontSize = 11.sp,
                                    color = WarmOffWhite.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "%.0f Liters".format(waterSavedLiters),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = PureWhite
                                )
                            }
                            Column {
                                Text(
                                    text = if (isAr) "الاستهلاك المتبقي" else "Remaining Usage",
                                    fontSize = 11.sp,
                                    color = WarmOffWhite.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "%.0f L / month".format(remainingLiters),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = WarmOffWhite
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = FreshGreen.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isAr) {
                                "هذا الوفر الشهري (%.0f لتر) يعادل ملء %d عبوة مياه للشرب!".format(waterSavedLiters, (waterSavedLiters / 1.5).toInt())
                            } else {
                                "This monthly saving (%.0f L) is equivalent to %d reusable drinking bottles!".format(waterSavedLiters, (waterSavedLiters / 1.5).toInt())
                            },
                            fontSize = 12.sp,
                            color = SoftMint
                        )
                    }
                }

                // Add to impact button
                Button(
                    onClick = {
                        onApplyImpact(0.0, waterSavedLiters.toDouble())
                        appliedSuccessMessage = if (isAr) {
                            "تمت إضافة %.0f لتر مياه إلى أثرك التراكمي! (+15 نقطة)".format(waterSavedLiters)
                        } else {
                            "Added %.0f L water to your cumulative impact! (+15 pts)".format(waterSavedLiters)
                        }
                        SoundHapticManager.playSuccess(view, soundEnabled)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_apply_water_impact"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "تطبيق هذا الوفر على أثري التراكمي" else "Apply Savings to My Profile",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (appliedSuccessMessage != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SoftMint,
                    border = BorderStroke(1.dp, EmeraldGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = EmeraldGreen)
                        Text(
                            text = appliedSuccessMessage!!,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ForestGreen
                        )
                    }
                }
            }

            // Scientific disclaimer
            Text(
                text = if (isAr) {
                    "* تنويه علمي: النتائج المعروضة تقديرية وتعتمد على متوسطات إحصائية ومعاملات انبعاثات دولية موثقة (IPCC / IEA). النتائج لا تمثل قياساً مادياً ملزماً ولكنها توضح الأثر التراكمي للممارسات المستدامة."
                } else {
                    "* Scientific Disclaimer: All results are mathematical models based on standard emission factors (IPCC/IEA). They reflect educational scenarios rather than metered physical measurements."
                },
                fontSize = 10.sp,
                lineHeight = 15.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
