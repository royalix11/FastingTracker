package com.example.fastingtracker.ui.screens.plan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.example.fastingtracker.ui.theme.noRippleClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.example.fastingtracker.domain.model.PlanPreset
import com.example.fastingtracker.navigation.CustomFastingPlan
import com.example.fastingtracker.ui.screens.plan.CustomFastingPlanScreen
import com.example.fastingtracker.ui.theme.PrimaryNeonYellow
import com.example.fastingtracker.ui.viewmodel.FastingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetsScreen(
    onItemClick: (NavKey) -> Unit,
    viewmodel: FastingViewModel,
    onNavigateToHome: () -> Unit = {}
) {
    val isLight = MaterialTheme.colorScheme.background == Color(0xFFFFFFFF)
    val cardBg = if (isLight) Color(0xFFF9F9F9) else Color(0xFF161618)
    val primaryColor = if (isLight) Color(0xFFD4AF37) else PrimaryNeonYellow
    val textColor = if (isLight) Color.Black else Color.White
    val subtitleColor = if (isLight) Color.DarkGray else Color.LightGray

    var showCustomPlanSheet by remember { mutableStateOf(false) }

    val targetDuration by viewmodel.targetDurationMillis.collectAsState()

    val plans = listOf(
        PlanPreset(168, 0, "Infinite Fast", "An open-ended fast with no set end time. All 10 fasting stages will be tracked.", false),
        PlanPreset(12, 0, "12:12 Circadian Rhythm", "A gentle fast for beginners, aligning with your natural sleep cycle.", false),
        PlanPreset(14, 0, "14:10 Early Autophagy", "A moderate step up, allowing more time for cellular repair.", false),
        PlanPreset(16, 0, "16:8 Lean Protocol", "The most popular intermittent fasting method for weight loss and health.", true),
        PlanPreset(18, 0, "18:6 Deep Ketosis", "For experienced fasters aiming for deeper fat burning and autophagy.", false),
        PlanPreset(20, 0, "20:4 The Warrior Diet", "A challenging fast with a condensed 4-hour eating window.", false),
        PlanPreset(24, 0, "OMAD (One Meal A Day)", "24 hours of fasting followed by a single nutrient-dense meal.", true)
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Fasting Plans",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    fontFamily = com.example.fastingtracker.ui.theme.GeistFont,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Select a plan that fits your lifestyle.",
                    fontSize = 14.sp,
                    color = subtitleColor,
                    fontFamily = com.example.fastingtracker.ui.theme.GeistFont,
                    textAlign = TextAlign.Center
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(plans) { plan ->
                val planDurationMillis = (plan.hours * 3600000L) + (plan.minutes * 60000L)
                val isSelected = (planDurationMillis == targetDuration)
                PlanCard(
                    plan = plan,
                    isSelected = isSelected,
                    cardBg = cardBg,
                    primaryColor = primaryColor,
                    textColor = textColor,
                    subtitleColor = subtitleColor,
                    onClick = {
                        viewmodel.updateTargetDuration(planDurationMillis)
                        viewmodel.startFast()
                        onNavigateToHome()
                    }
                )
            }

            // Custom Plan
            item {
                val isCustomSelected = plans.none { (it.hours * 3600000L) + (it.minutes * 60000L) == targetDuration } && targetDuration > 0L
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(if (isCustomSelected) BorderStroke(2.dp, primaryColor) else BorderStroke(1.dp, Color.Transparent), RoundedCornerShape(16.dp))
                        .noRippleClickable { showCustomPlanSheet = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(primaryColor.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Custom Plan", tint = primaryColor)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Create Custom Plan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
                                Text("Set your own fasting schedule", fontSize = 14.sp, color = subtitleColor)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = "Go", tint = subtitleColor)
                        }
                        if (isCustomSelected) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Surface(
                                    color = primaryColor,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("SELECTED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (showCustomPlanSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val sheetBg = if (isLight) Color(0xFFF0F0F0) else Color(0xFF1C1C1E)

            ModalBottomSheet(
                onDismissRequest = { showCustomPlanSheet = false },
                sheetState = sheetState,
                containerColor = sheetBg,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                CustomFastingPlanScreen(
                    onBackClick = { showCustomPlanSheet = false },
                    editStartTimeMillis = null,
                    lockedDurationMillis = null,
                    onPlanSelected = { startTimeMillis, durationMillis, isScheduled ->
                        if (isScheduled) {
                            viewmodel.scheduleCustomFast(startTimeMillis, durationMillis)
                        } else {
                            viewmodel.startCustomFast(startTimeMillis, durationMillis)
                        }
                        showCustomPlanSheet = false
                        onNavigateToHome()
                    }
                )
            }
        }
    }
}

@Composable
fun PlanCard(
    plan: PlanPreset,
    isSelected: Boolean,
    cardBg: Color,
    primaryColor: Color,
    textColor: Color,
    subtitleColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = if (isSelected) BorderStroke(2.dp, primaryColor) else BorderStroke(1.dp, Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = if (isSelected) primaryColor else subtitleColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = plan.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = plan.description,
                fontSize = 14.sp,
                color = subtitleColor,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    val fastingText = if (plan.hours >= 168) "Open" else "${plan.hours}h"
                    val eatingText = if (plan.hours >= 168) "Open" else "${24 - plan.hours}h"
                    StatChip("Fasting", fastingText, cardBg, primaryColor, textColor)
                    Spacer(modifier = Modifier.width(12.dp))
                    StatChip("Eating", eatingText, cardBg, primaryColor.copy(alpha=0.5f), textColor)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (isSelected) {
                        Surface(
                            color = primaryColor,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("SELECTED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                    if (plan.isPopular && !isSelected) {
                        Surface(
                            color = primaryColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = primaryColor, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("POPULAR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatChip(label: String, value: String, cardBg: Color, accentColor: Color, textColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(accentColor))
        Spacer(modifier = Modifier.width(8.dp))
        Text("$label:", fontSize = 12.sp, color = textColor.copy(alpha=0.7f))
        Spacer(modifier = Modifier.width(4.dp))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}
