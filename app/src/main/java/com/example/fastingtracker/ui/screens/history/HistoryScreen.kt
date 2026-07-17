package com.example.fastingtracker.ui.screens.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fastingtracker.data.local.FastingLog
import com.example.fastingtracker.ui.theme.*
import com.example.fastingtracker.ui.viewmodel.FastingViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val HistoryHeaderGold = Color(0xFFE4C535)

@Composable
fun HistoryScreen(viewmodel: FastingViewModel) {
    val historyLogs by viewmodel.historyLogs.collectAsState()
    var selectedRange by remember { mutableStateOf("Month") }
    var currentYearMonth by remember { mutableStateOf(YearMonth.of(2026, 7)) }
    var selectedDayNumber by remember { mutableStateOf<Int?>(12) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Dynamic outcomes based on selected temporal range (Week, Month, Year, All-Time)
    val analyticsOutcome = remember(selectedRange, historyLogs.size) {
        getAnalyticsOutcomeForRange(selectedRange, historyLogs.size)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Tier 1: Top Header & Actions (#e4c535 header text)
        item {
            HistoryTopHeader()
        }

        // Tier 2: Temporal Range Selector (Week | Month | Year | All-Time)
        item {
            RangeSelectorPillBar(
                selectedRange = selectedRange,
                onRangeSelected = { selectedRange = it }
            )
        }

        // Tier 3: Dynamic Hero KPI Summary Stat Grid (2x2)
        item {
            KpiSummaryGrid(outcome = analyticsOutcome)
        }

        // Tier 4: Interactive Monthly Consistency Calendar Card
        item {
            InteractiveCalendarCard(
                currentYearMonth = currentYearMonth,
                selectedDay = selectedDayNumber,
                onPrevMonth = {
                    currentYearMonth = currentYearMonth.minusMonths(1)
                    selectedDayNumber = null
                },
                onNextMonth = {
                    currentYearMonth = currentYearMonth.plusMonths(1)
                    selectedDayNumber = null
                },
                onDayClick = { day ->
                    val newDay = if (selectedDayNumber == day) null else day
                    selectedDayNumber = newDay
                    if (newDay != null) {
                        val monthPrefix = currentYearMonth.month.name.take(3).lowercase()
                        val matchIndex = analyticsOutcome.feedItems.indexOfFirst { item ->
                            val lowerTitle = item.title.lowercase()
                            lowerTitle.contains("$monthPrefix $newDay,") ||
                                lowerTitle.contains(" $newDay,") ||
                                lowerTitle.startsWith("$newDay ")
                        }
                        if (matchIndex != -1) {
                            coroutineScope.launch {
                                listState.smartSmoothScrollToItem(5 + matchIndex)
                            }
                        }
                    }
                }
            )
        }

        // Tier 5: Dynamic Chronological Log Feed Section
        item {
            Text(
                text = analyticsOutcome.sectionTitle,
                fontFamily = GeistMonoFont,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = DarkOnSurfaceVariant,
                letterSpacing = 2.sp
            )
        }

        // Render dynamic log cards where green edge line goes 100% all the way down the card
        itemsIndexed(analyticsOutcome.feedItems) { _, item ->
            val monthPrefix = currentYearMonth.month.name.take(3).lowercase()
            val isSelected = selectedDayNumber != null && (
                item.title.lowercase().contains("$monthPrefix $selectedDayNumber,") ||
                item.title.lowercase().contains(" $selectedDayNumber,")
            )
            StitchFullEdgeLogCard(item = item, isSelected = isSelected)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun HistoryTopHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "History / Analytics",
            fontFamily = GeistFont,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = HistoryHeaderGold
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PrimaryNeonYellow)
                    .noRippleClickable { },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Log Manual Fast",
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DarkSurface)
                    .border(BorderStroke(1.dp, DarkOutline), CircleShape)
                    .noRippleClickable { },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Export History",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun RangeSelectorPillBar(
    selectedRange: String,
    onRangeSelected: (String) -> Unit
) {
    val options = listOf("Week", "Month", "Year", "All-Time")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(DarkSurface)
            .border(BorderStroke(1.dp, DarkOutline), RoundedCornerShape(50))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        options.forEach { option ->
            val isSelected = option == selectedRange
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) PrimaryNeonYellow else Color.Transparent)
                    .noRippleClickable { onRangeSelected(option) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    fontFamily = GeistMonoFont,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp,
                    color = if (isSelected) Color.Black else DarkOnSurfaceVariant
                )
            }
        }
    }
}

private data class AnalyticsOutcome(
    val totalFasts: String,
    val successRate: String,
    val currentStreak: String,
    val streakSubtitle: String,
    val avgDuration: String,
    val durationDelta: String,
    val autophagyTime: String,
    val autophagySubtitle: String,
    val sectionTitle: String,
    val feedItems: List<LogCardItem>
)

private data class LogCardItem(
    val title: String,
    val timeRange: String,
    val durationText: String,
    val goalText: String,
    val deltaText: String,
    val autophagyTag: String,
    val secondaryTag: String? = null
)

private fun getAnalyticsOutcomeForRange(range: String, totalLogsSize: Int): AnalyticsOutcome {
    return when (range) {
        "Week" -> AnalyticsOutcome(
            totalFasts = "5",
            successRate = "100% Success Rate",
            currentStreak = "14 Days",
            streakSubtitle = "Best: 21 Days",
            avgDuration = "17.2h",
            durationDelta = "↗ +1.2h vs last week",
            autophagyTime = "11.8h",
            autophagySubtitle = "🧬 Metabolic Zone",
            sectionTitle = "THIS WEEK (PAST 7 DAYS)",
            feedItems = listOf(
                LogCardItem(
                    title = "Jul 12, 2026 • 16:8 Intermittent Fast",
                    timeRange = "8:00 PM -> 12:45 PM",
                    durationText = "16h 45m",
                    goalText = "Goal: 16h ",
                    deltaText = "(+45m)",
                    autophagyTag = "2.7h Autophagy",
                    secondaryTag = "⚡ Energized"
                ),
                LogCardItem(
                    title = "Jul 11, 2026 • 16:8 Intermittent Fast",
                    timeRange = "8:00 PM -> 12:10 PM",
                    durationText = "16h 10m",
                    goalText = "Goal: 16h ",
                    deltaText = "(+10m)",
                    autophagyTag = "2.1h Autophagy",
                    secondaryTag = "😌 Calm"
                ),
                LogCardItem(
                    title = "Jul 10, 2026 • 18:6 Fasting Plan",
                    timeRange = "6:00 PM -> 12:20 PM",
                    durationText = "18h 20m",
                    goalText = "Goal: 18h ",
                    deltaText = "(+20m)",
                    autophagyTag = "4.3h Autophagy",
                    secondaryTag = "⚡ Deep Focus"
                ),
                LogCardItem(
                    title = "Jul 9, 2026 • 16:8 Intermittent Fast",
                    timeRange = "8:00 PM -> 12:00 PM",
                    durationText = "16h 00m",
                    goalText = "Goal: 16h ",
                    deltaText = "(Completed)",
                    autophagyTag = "2.0h Autophagy",
                    secondaryTag = null
                )
            )
        )
        "Year" -> AnalyticsOutcome(
            totalFasts = "142",
            successRate = "91% Success Rate",
            currentStreak = "14 Days",
            streakSubtitle = "Annual Best: 32 Days",
            avgDuration = "16.5h",
            durationDelta = "↗ +0.4h vs 2025",
            autophagyTime = "294.0h",
            autophagySubtitle = "🧬 Annual Total",
            sectionTitle = "2026 MONTHLY HIGHLIGHTS",
            feedItems = listOf(
                LogCardItem(
                    title = "July 2026 • Monthly Aggregate",
                    timeRange = "18 Fasts Completed • 94% Consistency",
                    durationText = "16.8h Avg",
                    goalText = "Target: 16h ",
                    deltaText = "(+0.8h Avg)",
                    autophagyTag = "38.5h Total Autophagy",
                    secondaryTag = "🏆 Peak Month"
                ),
                LogCardItem(
                    title = "June 2026 • Monthly Aggregate",
                    timeRange = "26 Fasts Completed • 92% Consistency",
                    durationText = "16.4h Avg",
                    goalText = "Target: 16h ",
                    deltaText = "(+0.4h Avg)",
                    autophagyTag = "56.2h Total Autophagy",
                    secondaryTag = "⚡ Consistent"
                ),
                LogCardItem(
                    title = "May 2026 • Monthly Aggregate",
                    timeRange = "28 Fasts Completed • 90% Consistency",
                    durationText = "16.2h Avg",
                    goalText = "Target: 16h ",
                    deltaText = "(+0.2h Avg)",
                    autophagyTag = "58.0h Total Autophagy",
                    secondaryTag = null
                )
            )
        )
        "All-Time" -> AnalyticsOutcome(
            totalFasts = "310",
            successRate = "89% Success Rate",
            currentStreak = "14 Days",
            streakSubtitle = "All-Time Best: 45 Days",
            avgDuration = "16.3h",
            durationDelta = "All-Time Lifetime Avg",
            autophagyTime = "640.5h",
            autophagySubtitle = "🧬 Lifetime Autophagy",
            sectionTitle = "ALL-TIME MILESTONES & ANNUAL SUMMARY",
            feedItems = listOf(
                LogCardItem(
                    title = "2026 Year-to-Date Summary",
                    timeRange = "142 Fasts Logged • 2,340 Total Hours",
                    durationText = "16.5h Avg",
                    goalText = "Overall Goal: 16h ",
                    deltaText = "(+0.5h)",
                    autophagyTag = "294.0h Autophagy",
                    secondaryTag = "🔥 Current Year"
                ),
                LogCardItem(
                    title = "2025 Full Year Summary",
                    timeRange = "168 Fasts Logged • 2,704 Total Hours",
                    durationText = "16.1h Avg",
                    goalText = "Overall Goal: 16h ",
                    deltaText = "(+0.1h)",
                    autophagyTag = "346.5h Autophagy",
                    secondaryTag = "🏅 Silver Milestone"
                )
            )
        )
        else -> AnalyticsOutcome( // "Month" default
            totalFasts = if (totalLogsSize > 0) totalLogsSize.toString() else "18",
            successRate = "94% Success Rate",
            currentStreak = "14 Days",
            streakSubtitle = "Best: 21 Days",
            avgDuration = "16.8h",
            durationDelta = "↗ +0.8h vs June",
            autophagyTime = "38.5h",
            autophagySubtitle = "🧬 July Total",
            sectionTitle = "THIS WEEK (JULY 2026)",
            feedItems = listOf(
                LogCardItem(
                    title = "Jul 12, 2026 • 16:8 Intermittent Fast",
                    timeRange = "8:00 PM -> 12:45 PM",
                    durationText = "16h 45m",
                    goalText = "Goal: 16h ",
                    deltaText = "(+45m)",
                    autophagyTag = "2.7h Autophagy",
                    secondaryTag = "⚡ Energized"
                ),
                LogCardItem(
                    title = "Jul 11, 2026 • 16:8 Intermittent Fast",
                    timeRange = "8:00 PM -> 12:10 PM",
                    durationText = "16h 10m",
                    goalText = "Goal: 16h ",
                    deltaText = "(+10m)",
                    autophagyTag = "2.1h Autophagy",
                    secondaryTag = null
                ),
                LogCardItem(
                    title = "Jul 10, 2026 • 16:8 Intermittent Fast",
                    timeRange = "8:00 PM -> 12:05 PM",
                    durationText = "16h 05m",
                    goalText = "Goal: 16h ",
                    deltaText = "(+05m)",
                    autophagyTag = "2.0h Autophagy",
                    secondaryTag = "😌 Calm"
                )
            )
        )
    }
}

@Composable
private fun KpiSummaryGrid(outcome: AnalyticsOutcome) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 1: Total Fasts
            KpiStatCard(
                modifier = Modifier.weight(1f),
                label = "TOTAL FASTS",
                valueText = outcome.totalFasts,
                valueColor = Color.White,
                subtitleText = outcome.successRate,
                subtitleColor = SuccessGreen
            )
            // Card 2: Current Streak
            KpiStatCard(
                modifier = Modifier.weight(1f),
                label = "CURRENT STREAK",
                valueText = outcome.currentStreak,
                valueColor = PrimaryNeonYellow,
                subtitleText = outcome.streakSubtitle,
                subtitleColor = DarkOnSurfaceVariant,
                watermarkEmoji = "🔥"
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 3: Avg Duration
            KpiStatCard(
                modifier = Modifier.weight(1f),
                label = "AVG DURATION",
                valueText = outcome.avgDuration,
                valueColor = Color.White,
                subtitleText = outcome.durationDelta,
                subtitleColor = SuccessGreen
            )
            // Card 4: Autophagy Time
            KpiStatCard(
                modifier = Modifier.weight(1f),
                label = "AUTOPHAGY TIME",
                valueText = outcome.autophagyTime,
                valueColor = CyberCyan,
                subtitleText = outcome.autophagySubtitle,
                subtitleColor = CyberCyan,
                showBottomBorderAccent = true
            )
        }
    }
}

@Composable
private fun KpiStatCard(
    modifier: Modifier = Modifier,
    label: String,
    valueText: String,
    valueColor: Color,
    subtitleText: String,
    subtitleColor: Color,
    watermarkEmoji: String? = null,
    showBottomBorderAccent: Boolean = false
) {
    Box(
        modifier = modifier
            .height(112.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(
                BorderStroke(
                    width = if (showBottomBorderAccent) 1.5.dp else 1.dp,
                    color = if (showBottomBorderAccent) CyberCyan.copy(alpha = 0.4f) else DarkOutline
                ),
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        if (watermarkEmoji != null) {
            Text(
                text = watermarkEmoji,
                fontSize = 44.sp,
                modifier = Modifier.align(Alignment.BottomEnd),
                color = Color.White.copy(alpha = 0.08f)
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontFamily = GeistMonoFont,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = DarkOnSurfaceVariant,
                letterSpacing = 1.2.sp
            )
            Column {
                Text(
                    text = valueText,
                    fontFamily = GeistMonoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = valueColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitleText,
                    fontFamily = GeistMonoFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = subtitleColor
                )
            }
        }
    }
}

@Composable
private fun InteractiveCalendarCard(
    currentYearMonth: YearMonth,
    selectedDay: Int?,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (Int) -> Unit
) {
    val monthFormatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy") }
    val formattedTitle = remember(currentYearMonth) { currentYearMonth.format(monthFormatter) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkOutline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Interactive Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                        .noRippleClickable { onPrevMonth() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Month",
                        tint = Color.White
                    )
                }
                AnimatedContent(
                    targetState = currentYearMonth,
                    transitionSpec = {
                        if (targetState.isAfter(initialState)) {
                            (slideInHorizontally(tween(350, easing = FastOutSlowInEasing)) { width -> width } +
                                fadeIn(tween(350))) togetherWith
                                (slideOutHorizontally(tween(350, easing = FastOutSlowInEasing)) { width -> -width } +
                                    fadeOut(tween(350)))
                        } else {
                            (slideInHorizontally(tween(350, easing = FastOutSlowInEasing)) { width -> -width } +
                                fadeIn(tween(350))) togetherWith
                                (slideOutHorizontally(tween(350, easing = FastOutSlowInEasing)) { width -> width } +
                                    fadeOut(tween(350)))
                        }
                    },
                    label = "calendar_month_title_transition"
                ) { targetMonth ->
                    Text(
                        text = targetMonth.format(monthFormatter),
                        fontFamily = GeistFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                        .noRippleClickable { onNextMonth() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Month",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Days of Week Header
            Row(modifier = Modifier.fillMaxWidth()) {
                val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        fontFamily = GeistMonoFont,
                        fontSize = 12.sp,
                        color = DarkOnSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dynamic Calendar Days computation with directional sliding transition
            AnimatedContent(
                targetState = currentYearMonth,
                transitionSpec = {
                    if (targetState.isAfter(initialState)) {
                        (slideInHorizontally(tween(350, easing = FastOutSlowInEasing)) { width -> width } +
                            fadeIn(tween(350))) togetherWith
                            (slideOutHorizontally(tween(350, easing = FastOutSlowInEasing)) { width -> -width } +
                                fadeOut(tween(350)))
                    } else {
                        (slideInHorizontally(tween(350, easing = FastOutSlowInEasing)) { width -> -width } +
                            fadeIn(tween(350))) togetherWith
                            (slideOutHorizontally(tween(350, easing = FastOutSlowInEasing)) { width -> width } +
                                fadeOut(tween(350)))
                    }
                },
                label = "calendar_grid_slide_transition"
            ) { targetMonth ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    val daysInMonth = targetMonth.lengthOfMonth()
                    val firstDayOfWeek = targetMonth.atDay(1).dayOfWeek.value % 7
                    val totalSlots = ((firstDayOfWeek + daysInMonth + 6) / 7) * 7
                    val rowsCount = totalSlots / 7

                    for (row in 0 until rowsCount) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (col in 0 until 7) {
                                val slotIndex = row * 7 + col
                                val dayNumber = slotIndex - firstDayOfWeek + 1
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (dayNumber in 1..daysInMonth) {
                                        val status = getStatusForCalendarDay(targetMonth, dayNumber, dayNumber == selectedDay)
                                        CalendarDayIndicator(
                                            dayText = dayNumber.toString(),
                                            status = status,
                                            onClick = { onDayClick(dayNumber) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legend Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .border(2.dp, PrimaryNeonYellow, CircleShape)
                    )
                    Text(
                        text = "Achieved",
                        fontFamily = GeistMonoFont,
                        fontSize = 11.sp,
                        color = DarkOnSurfaceVariant
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .border(2.dp, CyberCyan, CircleShape)
                    )
                    Text(
                        text = "Deep Fast",
                        fontFamily = GeistMonoFont,
                        fontSize = 11.sp,
                        color = DarkOnSurfaceVariant
                    )
                }
            }
        }
    }
}

private enum class DayStatus {
    ACHIEVED, DEEP_FAST, PARTIAL, SELECTED, NORMAL
}

private fun getStatusForCalendarDay(yearMonth: YearMonth, day: Int, isSelected: Boolean): DayStatus {
    if (isSelected) return DayStatus.SELECTED

    val cellDate = yearMonth.atDay(day)
    val today = LocalDate.now()

    // 1. Intelligence Logic: Temporal Horizon Guard
    // Any day in the future relative to the actual system clock today cannot have fasting data
    if (cellDate.isAfter(today)) {
        return DayStatus.NORMAL
    }

    // 2. Present-Day Capping for July 2026 baseline: never display fasting data for future days past July 12, 2026
    if (yearMonth == YearMonth.of(2026, 7) && day > 12) {
        return DayStatus.NORMAL
    }

    // 3. Months outside the active logged dataset return normal unlogged days
    if (yearMonth != YearMonth.of(2026, 7)) {
        return DayStatus.NORMAL
    }

    return when (day % 5) {
        1, 2, 4 -> DayStatus.ACHIEVED
        3 -> DayStatus.DEEP_FAST
        0 -> DayStatus.PARTIAL
        else -> DayStatus.NORMAL
    }
}

@Composable
private fun CalendarDayIndicator(
    dayText: String,
    status: DayStatus,
    onClick: () -> Unit
) {
    val borderStroke = when (status) {
        DayStatus.ACHIEVED -> BorderStroke(2.dp, PrimaryNeonYellow)
        DayStatus.DEEP_FAST -> BorderStroke(2.dp, CyberCyan)
        DayStatus.PARTIAL -> BorderStroke(1.dp, PrimaryNeonYellow.copy(alpha = 0.4f))
        DayStatus.SELECTED -> BorderStroke(2.dp, PrimaryNeonYellow)
        else -> null
    }

    val backgroundBrush = if (status == DayStatus.SELECTED) {
        PrimaryNeonYellow.copy(alpha = 0.2f)
    } else {
        Color.Transparent
    }

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(backgroundBrush)
            .then(
                if (borderStroke != null) Modifier.border(borderStroke, CircleShape)
                else Modifier
            )
            .noRippleClickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayText,
            fontFamily = GeistMonoFont,
            fontSize = 13.sp,
            fontWeight = if (status == DayStatus.SELECTED) FontWeight.Bold else FontWeight.Normal,
            color = if (status == DayStatus.SELECTED) PrimaryNeonYellow else Color.White
        )
    }
}

@Composable
private fun StitchFullEdgeLogCard(item: LogCardItem, isSelected: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) PrimaryNeonYellow else DarkOutline
        )
    ) {
        // IntrinsicSize.Min ensures the green side stripe stretches 100% down the card height
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Green left status strip spanning all the way down the card
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(SuccessGreen)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                // Top header line
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = SuccessGreen,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = item.title,
                            fontFamily = GeistFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = item.timeRange,
                            fontFamily = GeistMonoFont,
                            fontSize = 12.sp,
                            color = DarkOnSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hero duration and goal row
                Column(modifier = Modifier.padding(start = 34.dp)) {
                    Text(
                        text = item.durationText,
                        fontFamily = GeistMonoFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = PrimaryNeonYellow
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = item.goalText,
                            fontFamily = GeistFont,
                            fontSize = 12.sp,
                            color = DarkOnSurfaceVariant
                        )
                        Text(
                            text = item.deltaText,
                            fontFamily = GeistMonoFont,
                            fontSize = 12.sp,
                            color = SuccessGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Pills Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PillTag(
                            text = item.autophagyTag,
                            textColor = CyberCyan,
                            borderColor = CyberCyan.copy(alpha = 0.35f),
                            backgroundColor = CyberCyan.copy(alpha = 0.1f)
                        )
                        if (item.secondaryTag != null) {
                            PillTag(
                                text = item.secondaryTag,
                                textColor = PrimaryNeonYellow,
                                borderColor = PrimaryNeonYellow.copy(alpha = 0.35f),
                                backgroundColor = PrimaryNeonYellow.copy(alpha = 0.1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PillTag(
    text: String,
    textColor: Color,
    borderColor: Color,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = GeistMonoFont,
            fontSize = 11.sp,
            color = textColor
        )
    }
}

private suspend fun LazyListState.smartSmoothScrollToItem(targetIndex: Int) {
    val visibleItem = layoutInfo.visibleItemsInfo.find { it.index == targetIndex }
    if (visibleItem != null) {
        val viewportHeight = layoutInfo.viewportSize.height
        val itemTop = visibleItem.offset
        val itemBottom = visibleItem.offset + visibleItem.size
        if (itemTop >= 0 && itemBottom <= viewportHeight) {
            // Item is already completely visible inside viewport. Do not shift screen.
            return
        } else if (itemBottom > viewportHeight) {
            val scrollAmount = (itemBottom - viewportHeight + 48).toFloat()
            animateScrollBy(scrollAmount, tween(600, easing = FastOutSlowInEasing))
        } else if (itemTop < 0) {
            val scrollAmount = (itemTop - 32).toFloat()
            animateScrollBy(scrollAmount, tween(600, easing = FastOutSlowInEasing))
        }
    } else {
        animateScrollToItem(targetIndex)
    }
}
