package com.example.fastingtracker.ui.screens.home

import com.example.fastingtracker.ui.components.*
import com.example.fastingtracker.ui.screens.home.*
import com.example.fastingtracker.ui.screens.history.*
import com.example.fastingtracker.ui.screens.plan.*
import com.example.fastingtracker.ui.screens.me.*
import com.example.fastingtracker.domain.model.*
import com.example.fastingtracker.navigation.*
import com.example.fastingtracker.ui.theme.*
import com.example.fastingtracker.data.local.*

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.example.fastingtracker.ui.theme.noRippleClickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.zIndex
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import com.example.fastingtracker.R
import com.example.fastingtracker.ui.components.SkillEndFastButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.example.fastingtracker.navigation.Main
import com.example.fastingtracker.navigation.ThemeSettings
import com.example.fastingtracker.data.local.FastingLog
import com.example.fastingtracker.ui.theme.DarkBackground
import com.example.fastingtracker.ui.theme.DarkOutline
import com.example.fastingtracker.ui.theme.DarkOutlineVariant
import com.example.fastingtracker.ui.theme.DarkSurface
import com.example.fastingtracker.ui.theme.PrimaryNeonYellow
import com.example.fastingtracker.ui.theme.UnselectedGray
import com.example.fastingtracker.ui.theme.DeepCrimsonRed
import com.example.fastingtracker.ui.viewmodel.FastingViewModel
import com.example.fastingtracker.domain.model.FastingStage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import com.example.fastingtracker.ui.components.WheelTimePicker
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.Instant
import java.time.format.DateTimeFormatter
import androidx.compose.material3.rememberModalBottomSheetState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isFasting: Boolean,
    isFastingScheduled: Boolean,
    startTime: Long,
    elapsedMillisProvider: () -> Long,
    targetMillis: Long,
    currentStage: FastingStage,
    onStartFast: () -> Unit,
    onEndFast: () -> Unit,
    onEditStartTimeClick: () -> Unit,
    onTimelineClick: (Int?) -> Unit = {},
    onUpdateStartTime: ((Long) -> Unit)? = null
) {
    var showStartTimeSheet by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Spacer(modifier = Modifier.height(44.dp))
            val isLight = MaterialTheme.colorScheme.background == Color(0xFFFFFFFF)
            val buttonBgColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.5f)
            val elapsedColor = if (isLight) Color(0xFFD4AF37) else Color(0xFFFFDE4D)
            
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(175.dp)
                    .height(40.dp)
                    .border(BorderStroke(1.dp, elapsedColor), RoundedCornerShape(50))
                    .clip(RoundedCornerShape(50))
                    .background(buttonBgColor)
                    .noRippleClickable { onTimelineClick(null) }
                    .padding(horizontal = 8.dp)
            ) {
                val stageText by remember(isFasting, targetMillis, currentStage) {
                    derivedStateOf {
                        val remaining = targetMillis - elapsedMillisProvider()
                        if (!isFasting) "FASTING STAGES" else if (remaining > 0) currentStage.title else "OVERTIME"
                    }
                }
                Crossfade(
                    targetState = stageText,
                    animationSpec = tween(400),
                    label = "stageTransition"
                ) { targetText ->
                    Text(
                        text = targetText,
                        color = elapsedColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = com.example.fastingtracker.ui.theme.GeistMonoFont,
                        letterSpacing = 0.7.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularTimerRing(
                    isFasting = isFasting,
                    isFastingScheduled = isFastingScheduled,
                    startTime = startTime,
                    elapsedMillisProvider = elapsedMillisProvider,
                    targetMillis = targetMillis,
                    currentStage = currentStage,
                    onStartFast = onStartFast,
                    onEndFast = onEndFast,
                    onTimelineClick = onTimelineClick,
                    modifier = Modifier.size(380.dp)
                )
            }
        }

        item {
            if (isFasting) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    SkillEndFastButton(
                        width = 175.dp,
                        onClick = onEndFast
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Started Card
                FastingInfoCard(
                    title = "Started",
                    value = if ((isFasting || isFastingScheduled) && startTime > 0L) {
                        SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(startTime))
                    } else {
                        "--:--"
                    },
                    showEditIcon = isFasting || isFastingScheduled,
                    modifier = Modifier.weight(1f).noRippleClickable(enabled = isFasting || isFastingScheduled) {
                        if (onUpdateStartTime != null) {
                            showStartTimeSheet = true
                        } else {
                            onEditStartTimeClick()
                        }
                    }
                )

                // Goal Card
                FastingInfoCard(
                    title = "Goal",
                    value = if ((isFasting || isFastingScheduled) && startTime > 0L) {
                        if (targetMillis >= 168 * 3600000L) {
                            "Infinite"
                        } else {
                            val targetEnd = startTime + targetMillis
                            SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(targetEnd))
                        }
                    } else {
                        "--:--"
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            FastingTipsSection()
        }
    }

    if (showStartTimeSheet) {
        val isLight = MaterialTheme.colorScheme.background == Color(0xFFFFFFFF)
        val sheetBg = if (isLight) Color(0xFFF0F0F0) else Color(0xFF1C1C1E)
        val elapsedColor = if (isLight) Color(0xFFD4AF37) else PrimaryNeonYellow
        var selectedDateTime by remember(startTime) {
            mutableStateOf(
                if (startTime > 0L) Instant.ofEpochMilli(startTime).atZone(ZoneId.systemDefault()).toLocalDateTime()
                else LocalDateTime.now()
            )
        }
        val fullFormatter = remember { DateTimeFormatter.ofPattern("MMM d, h:mm a") }
        val calculatedEndDateTime = remember(selectedDateTime, targetMillis) {
            val startMillis = selectedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endMillis = startMillis + targetMillis
            Instant.ofEpochMilli(endMillis).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
        val cardBg = if (isLight) Color(0xFFE8E8E8) else Color(0xFF242426)
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showStartTimeSheet = false },
            sheetState = sheetState,
            containerColor = sheetBg,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Edit Start Time",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isLight) Color.DarkGray else Color(0xFFB0B3B8)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    WheelTimePicker(
                        startDateTime = selectedDateTime,
                        onDateTimeChanged = { selectedDateTime = it },
                        elapsedColor = elapsedColor,
                        backgroundColor = cardBg,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                // Calculated End Time Display
                Text(
                    text = "Calculated End Time",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isLight) Color.DarkGray else Color(0xFFB0B3B8)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Outlined.DateRange, contentDescription = null, tint = elapsedColor)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = "End Time", fontSize = 12.sp, color = (if (isLight) Color.Black else Color.White).copy(alpha = 0.7f))
                            Text(
                                text = calculatedEndDateTime.format(fullFormatter),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLight) Color.Black else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        val newTimeMillis = selectedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        if (onUpdateStartTime != null) {
                            onUpdateStartTime.invoke(newTimeMillis)
                        } else {
                            onEditStartTimeClick()
                        }
                        showStartTimeSheet = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = elapsedColor, contentColor = if (isLight) Color.White else Color.Black)
                ) {
                    Text("UPDATE START TIME", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun FastingTipsSection(modifier: Modifier = Modifier) {
    val isLight = MaterialTheme.colorScheme.background == Color(0xFFFFFFFF)
    val cardBg = if (isLight) Color(0xFFF5F5F5) else Color.Black
    val textColor = if (isLight) Color.DarkGray else Color(0xFFB0B3B8)
    val titleColor = if (isLight) Color.Black else Color.White
    val primaryNeon = if (isLight) Color(0xFFD4AF37) else Color(0xFFFFDE4D)
    val haptic = LocalHapticFeedback.current
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    val tips = listOf(
        "Stay hydrated" to "Drink plenty of water throughout your fasting period to stay hydrated and help manage hunger and fatigue.",
        "Stay busy" to "Keep yourself occupied with work, hobbies, or exercise to distract from hunger and make fasting hours pass more quickly.",
        "Listen to your body" to "Pay attention to how your body responds to fasting and adjust your fasting schedule as needed. If you feel unwell, consider breaking your fast.",
        "Consult your doctor" to "This app is not a substitute for professional advice. Always consult with your doctor before fasting to determine if, and which fasting plan is right for you."
    )

    Column(modifier = modifier.fillMaxWidth().padding(bottom = 32.dp)) {
        Text(
            text = "Tips",
            color = titleColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, primaryNeon), RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .padding(16.dp)
        ) {
            Column {
                tips.forEachIndexed { index, (title, description) ->
                    Text(
                        text = androidx.compose.ui.text.buildAnnotatedString {
                            pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold, color = titleColor))
                            append("$title: ")
                            pop()
                            pushStyle(androidx.compose.ui.text.SpanStyle(color = textColor))
                            append(description)
                            pop()
                        },
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                    if (index < tips.lastIndex) {
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Read more button opening web link
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        uriHandler.openUri("https://www.hopkinsmedicine.org/health/wellness-and-prevention/intermittent-fasting-what-is-it-and-how-does-it-work")
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = primaryNeon
                    ),
                    border = BorderStroke(1.dp, primaryNeon),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(36.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "read more",
                        color = primaryNeon,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun CircularTimerRing(
    isFasting: Boolean,
    isFastingScheduled: Boolean,
    startTime: Long,
    elapsedMillisProvider: () -> Long,
    targetMillis: Long,
    currentStage: FastingStage,
    onStartFast: () -> Unit,
    onEndFast: () -> Unit,
    onTimelineClick: (Int?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val startupAnimation = remember { androidx.compose.animation.core.Animatable(if (isFasting) 1f else 0f) }
    LaunchedEffect(isFasting) {
        if (isFasting && startupAnimation.value < 1f) {
            startupAnimation.animateTo(1f, animationSpec = tween(800))
        } else if (!isFasting) {
            startupAnimation.snapTo(0f)
        }
    }

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("fasting_prefs", Context.MODE_PRIVATE) }
    var isCountdown by remember { mutableStateOf(prefs.getBoolean("is_countdown", true)) }

    // Animated glow pulse transition
    val infiniteTransition = rememberInfiniteTransition(label = "glowTransition")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    val glowWidthOffset by infiniteTransition.animateFloat(
        initialValue = 2f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowWidth"
    )

    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveScale"
    )

    val waveAlpha by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveAlpha"
    )

    val isLight = MaterialTheme.colorScheme.background == Color(0xFFFFFFFF)
    val strokeWidth = 40.dp
    val thinStrokeWidth = 1.dp
    val elapsedColor = if (isLight) Color(0xFFD4AF37) else Color(0xFFFFDE4D) // Neon Yellow
    val trackColor = if (isLight) Color(0xFFD4AF37) else Color(0xFFFFDE4D) // Thin Yellow track
    val timeTextColor = if (isLight) Color.Black else Color(0xFFFFFBFB)
    val buttonBgColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.5f)

    val haptic = LocalHapticFeedback.current

    val smoothedProgress = remember { floatArrayOf(-1f) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        // Transparent touch layer to capture circle taps when fasting, or start fast when idle
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .zIndex(1.5f)
                .pointerInput(isFasting, isFastingScheduled, targetMillis) {
                    detectTapGestures { tapOffset ->
                        if (!isFasting && !isFastingScheduled) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onStartFast()
                        } else {
                            val ringRadius = 157.5.dp.toPx()
                            val totalSweepAngle = 300f
                            val minAngleGap = (44.dp.toPx() / (2f * kotlin.math.PI.toFloat() * ringRadius)) * 360f
                            val centerOffset = Offset(size.width / 2f, size.height / 2f)

                            val targetHours = targetMillis.toFloat() / 3600000f
                            val reachableStages = FastingStage.entries.filter { it.startHour <= targetHours }
                            val effectiveRingHours = if (targetHours >= 168f) 72f else targetHours.coerceAtLeast(0.1f)

                            val idealAngles = reachableStages.map { 
                                (it.startHour.toFloat() / effectiveRingHours) * totalSweepAngle 
                            }.toMutableList()

                            for (i in 1 until idealAngles.size) {
                                if (idealAngles[i] - idealAngles[i - 1] < minAngleGap) {
                                    idealAngles[i] = idealAngles[i - 1] + minAngleGap
                                }
                            }
                            if (idealAngles.isNotEmpty() && idealAngles.last() > totalSweepAngle) {
                                idealAngles[idealAngles.lastIndex] = totalSweepAngle
                                for (i in idealAngles.lastIndex - 1 downTo 0) {
                                    if (idealAngles[i + 1] - idealAngles[i] < minAngleGap) {
                                        idealAngles[i] = idealAngles[i + 1] - minAngleGap
                                    }
                                }
                            }

                            var tappedStageIndex: Int? = null
                            var minDistance = Float.MAX_VALUE
                            val tapThreshold = 35.dp.toPx()

                            reachableStages.forEachIndexed { i, stage ->
                                val visualAngle = idealAngles[i]
                                val angleRad = (visualAngle - 90f) * (kotlin.math.PI / 180f).toFloat()
                                val cx = centerOffset.x + ringRadius * kotlin.math.cos(angleRad)
                                val cy = centerOffset.y + ringRadius * kotlin.math.sin(angleRad)
                                val dist = kotlin.math.hypot(tapOffset.x - cx, tapOffset.y - cy)
                                if (dist < tapThreshold && dist < minDistance) {
                                    minDistance = dist
                                    tappedStageIndex = FastingStage.entries.indexOf(stage)
                                }
                            }

                            if (tappedStageIndex != null) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onTimelineClick(tappedStageIndex)
                            }
                        }
                    }
                }
        )

        val activePainters = com.example.fastingtracker.ui.components.FastingStageIcons.getActivePainters()
        val inactivePainters = com.example.fastingtracker.ui.components.FastingStageIcons.getInactivePainters()

        val targetFastingAlpha = if (isFasting) 1f else 0f
        val fastingAlpha by animateFloatAsState(targetValue = targetFastingAlpha, animationSpec = tween(600), label = "fastingAlpha")

        Canvas(
            modifier = Modifier
                .size(450.dp)
                .zIndex(1f)
        ) {
            val ringRadius = 157.5.dp.toPx()

            val totalSweepAngle = 300f // Ends at 10 o'clock
            val minAngleGap = (44.dp.toPx() / (2f * kotlin.math.PI.toFloat() * ringRadius)) * 360f

            val thinRingRadius = ringRadius + (strokeWidth.toPx() / 2f) - (thinStrokeWidth.toPx() / 2f)
            
            val currentRadius = thinRingRadius + (ringRadius - thinRingRadius) * fastingAlpha
            val currentStrokeWidth = thinStrokeWidth.toPx() + (strokeWidth.toPx() - thinStrokeWidth.toPx()) * fastingAlpha
            
            val tunnelColor = if (isLight) Color(0xFFE5E5EA) else Color(0xFF1C1C1E)
            val currentTrackColor = androidx.compose.ui.graphics.lerp(trackColor, tunnelColor, fastingAlpha)

            // 1. Draw base track/tunnel (always drawn, morphs smoothly)
            drawCircle(
                color = currentTrackColor,
                radius = currentRadius,
                style = Stroke(width = currentStrokeWidth)
            )

            val targetHours = targetMillis.toFloat() / 3600000f
            val reachableStages = FastingStage.entries.filter { it.startHour <= targetHours }
            val effectiveRingHours = if (targetHours >= 168f) 72f else targetHours.coerceAtLeast(0.1f)
            
            val idealAngles = reachableStages.map { 
                (it.startHour.toFloat() / effectiveRingHours) * totalSweepAngle 
            }.toMutableList()

            for (i in 1 until idealAngles.size) {
                if (idealAngles[i] - idealAngles[i - 1] < minAngleGap) {
                    idealAngles[i] = idealAngles[i - 1] + minAngleGap
                }
            }
            
            if (idealAngles.isNotEmpty() && idealAngles.last() > totalSweepAngle) {
                idealAngles[idealAngles.lastIndex] = totalSweepAngle
                for (i in idealAngles.lastIndex - 1 downTo 0) {
                    if (idealAngles[i + 1] - idealAngles[i] < minAngleGap) {
                        idealAngles[i] = idealAngles[i + 1] - minAngleGap
                    }
                }
            }

            val targetProgress = if (isFasting && reachableStages.isNotEmpty()) {
                val elapsedHours = elapsedMillisProvider().toFloat() / 3600000f
                val visualAngle = when {
                    elapsedHours <= reachableStages.first().startHour -> idealAngles.first()
                    elapsedHours >= effectiveRingHours -> totalSweepAngle
                    else -> {
                        var idx = 0
                        while (idx < reachableStages.lastIndex && reachableStages[idx + 1].startHour <= elapsedHours) {
                            idx++
                        }
                        if (idx == reachableStages.lastIndex) {
                            val h1 = reachableStages[idx].startHour.toFloat()
                            val a1 = idealAngles[idx]
                            val frac = ((elapsedHours - h1) / (effectiveRingHours - h1)).coerceIn(0f, 1f)
                            a1 + frac * (totalSweepAngle - a1)
                        } else {
                            val h1 = reachableStages[idx].startHour.toFloat()
                            val h2 = reachableStages[idx + 1].startHour.toFloat()
                            val a1 = idealAngles[idx]
                            val a2 = idealAngles[idx + 1]
                            val frac = ((elapsedHours - h1) / (h2 - h1)).coerceIn(0f, 1f)
                            a1 + frac * (a2 - a1)
                        }
                    }
                }
                (visualAngle / totalSweepAngle).coerceIn(0f, 360f / totalSweepAngle)
            } else 0f
            
            if (smoothedProgress[0] < 0f) {
                smoothedProgress[0] = targetProgress
            } else {
                val diff = targetProgress - smoothedProgress[0]
                if (kotlin.math.abs(diff) > 0.0001f) {
                    smoothedProgress[0] += diff * 0.03f 
                } else {
                    smoothedProgress[0] = targetProgress
                }
            }

            if (fastingAlpha > 0f) {
                val drawnProgress = smoothedProgress[0] * startupAnimation.value

                // 2. Draw active progress line
                drawArc(
                    color = elapsedColor.copy(alpha = fastingAlpha),
                    startAngle = -90f,
                    sweepAngle = drawnProgress * totalSweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - currentRadius, center.y - currentRadius),
                    size = androidx.compose.ui.geometry.Size(currentRadius * 2f, currentRadius * 2f),
                    style = Stroke(width = currentStrokeWidth, cap = StrokeCap.Round)
                )

                // 3. Calculate tip coordinates using trigonometry
                val angleRadians = (-90f + drawnProgress * totalSweepAngle) * (kotlin.math.PI / 180f).toFloat()
                val tipX = center.x + currentRadius * kotlin.math.cos(angleRadians)
                val tipY = center.y + currentRadius * kotlin.math.sin(angleRadians)
                val tipOffset = androidx.compose.ui.geometry.Offset(tipX, tipY)

                // 4. Draw ripple wave animating outward from the tip
                val snakeHeadColor = if (isLight) Color(0xFFD4AF37) else Color(0xFFFFDE4D)
                val maxWaveRadius = currentStrokeWidth * 0.75f
                drawCircle(
                    color = snakeHeadColor.copy(alpha = waveAlpha * fastingAlpha),
                    radius = (currentStrokeWidth / 2f) + (maxWaveRadius * waveScale),
                    center = tipOffset,
                    style = Stroke(width = (2.dp.toPx() * fastingAlpha).coerceAtLeast(0.01f))
                )

                // 5. Draw glowing tip (snake head)
                drawCircle(
                    color = snakeHeadColor.copy(alpha = fastingAlpha),
                    radius = currentStrokeWidth / 2.5f,
                    center = tipOffset
                )
                // Draw black dot at center of snake head
                drawCircle(
                    color = Color(0xFF333333).copy(alpha = fastingAlpha),
                    radius = currentStrokeWidth / 7f,
                    center = tipOffset
                )

                // 6. Draw small circles for reachable stages proportionally
                val smallCircleColor = if (isLight) Color(0xFFCCCCCC) else Color(0xFF121212)
                val defaultIconColor = if (isLight) Color(0xFF1C1C1E) else Color(0xFF1C1C1E)
                val activeIconColor = if (isLight) Color(0xFFD4AF37) else Color(0xFFFFDE4D)
                val smallCircleRadius = (38f / 2f).dp.toPx()
                
                val currentRingAngle = drawnProgress * totalSweepAngle

                reachableStages.forEachIndexed { i, stage ->
                    val stageIndex = FastingStage.entries.indexOf(stage)
                    val visualAngle = idealAngles[i]
                    val angleRad = (visualAngle - 90f) * (kotlin.math.PI / 180f).toFloat()
                    
                    val cx = center.x + currentRadius * kotlin.math.cos(angleRad)
                    val cy = center.y + currentRadius * kotlin.math.sin(angleRad)
                    
                    val isStageActive = currentRingAngle >= visualAngle
                    val currentIconColor = if (isStageActive) activeIconColor else defaultIconColor
                    
                    drawCircle(
                        color = smallCircleColor.copy(alpha = fastingAlpha),
                        radius = smallCircleRadius * fastingAlpha,
                        center = Offset(cx, cy)
                    )
                    if (stageIndex in 0..9) {
                        val sizePx = smallCircleRadius * 2f * fastingAlpha
                        val left = cx - sizePx / 2
                        val top = cy - sizePx / 2
                        val painter = if (isStageActive) activePainters[stageIndex] else inactivePainters[stageIndex]
                        translate(left, top) {
                            with(painter) {
                                draw(size = Size(sizePx.coerceAtLeast(0.01f), sizePx.coerceAtLeast(0.01f)), alpha = fastingAlpha)
                            }
                        }
                    }
                }
            }
        }

        if (isFasting) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().zIndex(2f)
            ) {
                val stageIndex = FastingStage.entries.indexOf(currentStage)
                if (stageIndex in 0..9) {
                    Box(
                        modifier = Modifier
                            .offset(y = (-68).dp)
                            .size(76.dp)
                            .noRippleClickable { onTimelineClick(stageIndex) },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = activePainters[stageIndex],
                            contentDescription = currentStage.title,
                            modifier = Modifier.size(76.dp)
                        )
                    }
                }

                TimerTextDisplay(
                    isCountdown = isCountdown,
                    onToggleCountdown = { 
                        isCountdown = !isCountdown
                        prefs.edit().putBoolean("is_countdown", isCountdown).apply()
                    },
                    elapsedMillisProvider = elapsedMillisProvider,
                    targetMillis = targetMillis,
                    isLight = isLight,
                    timeTextColor = timeTextColor
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp).zIndex(2f)
            ) {
                if (isFastingScheduled) {
                    var currentRemaining by remember(startTime) { mutableStateOf(startTime - System.currentTimeMillis()) }
                    LaunchedEffect(startTime) {
                        while (true) {
                            currentRemaining = startTime - System.currentTimeMillis()
                            kotlinx.coroutines.delay(1000)
                        }
                    }

                    Text(
                        text = "STARTS IN",
                        color = elapsedColor.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = com.example.fastingtracker.ui.theme.GeistMonoFont,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "- " + (if (currentRemaining > 0) formatMillis(currentRemaining) else "00:00:00"),
                        color = timeTextColor,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = com.example.fastingtracker.ui.theme.GeistFont,
                        letterSpacing = (-0.96).sp,
                        style = TextStyle(
                            shadow = if (isLight) Shadow.None else Shadow(
                                color = Color.Black.copy(alpha = 0.5f),
                                offset = Offset(0f, 4f),
                                blurRadius = 8f
                            )
                        )
                    )

                } else {
                    // Idle state: Central START FAST trigger text with pulsing guide
                    Text(
                        text = "START FAST",
                        color = elapsedColor,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = com.example.fastingtracker.ui.theme.GeistFont,
                        letterSpacing = (-0.5).sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun FastingInfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    showEditIcon: Boolean = false
) {
    val isLight = MaterialTheme.colorScheme.background == Color(0xFFFFFFFF)
    val cardBg = if (isLight) Color.White else Color(0xFF121212)
    val cardBorder = if (isLight) Color.LightGray else Color(0xFF1C1C1E)
    val labelColor = if (isLight) Color.DarkGray else Color(0xFFCFC6AE)
    val valueColor = if (isLight) Color.Black else Color.White
    val accentColor = if (isLight) Color(0xFFD4AF37) else Color(0xFFFFDE4D)

    Card(
        modifier = modifier
            .height(90.dp)
            .border(BorderStroke(1.dp, cardBorder), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title.uppercase(),
                    color = labelColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = com.example.fastingtracker.ui.theme.GeistMonoFont,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    color = valueColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = com.example.fastingtracker.ui.theme.GeistFont
                )
            }
            if (showEditIcon) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = accentColor,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 14.dp, end = 14.dp)
                        .size(16.dp)
                )
            }
        }
    }
}



fun formatMillis(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format(java.util.Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
}

@Composable
fun TimerTextDisplay(
    isCountdown: Boolean,
    onToggleCountdown: () -> Unit,
    elapsedMillisProvider: () -> Long,
    targetMillis: Long,
    isLight: Boolean,
    timeTextColor: androidx.compose.ui.graphics.Color
) {
    val elapsed = elapsedMillisProvider()
    val displayMillis = if (isCountdown && targetMillis < 168 * 3600000L) kotlin.math.max(0, targetMillis - elapsed) else elapsed
    
    androidx.compose.material3.Text(
        text = formatMillis(displayMillis),
        color = timeTextColor,
        fontSize = 48.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
        fontFamily = com.example.fastingtracker.ui.theme.GeistFont,
        modifier = androidx.compose.ui.Modifier.noRippleClickable { onToggleCountdown() },
        style = androidx.compose.ui.text.TextStyle(
            shadow = if (isLight) androidx.compose.ui.graphics.Shadow.None else androidx.compose.ui.graphics.Shadow(
                color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f),
                offset = androidx.compose.ui.geometry.Offset(0f, 4f),
                blurRadius = 8f
            )
        )
    )
}
