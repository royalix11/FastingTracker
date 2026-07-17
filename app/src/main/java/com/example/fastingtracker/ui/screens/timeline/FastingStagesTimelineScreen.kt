package com.example.fastingtracker.ui.screens.timeline

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.delay
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.fastingtracker.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fastingtracker.domain.model.FastingStage
import com.example.fastingtracker.ui.viewmodel.FastingViewModel

@Composable
fun FastingStagesTimelineScreen(
    onBackClick: () -> Unit,
    initialStageIndex: Int? = null,
    viewModel: FastingViewModel = viewModel()
) {
    val currentStage by viewModel.currentFastingStage.collectAsState()
    val elapsedTimeMillis by viewModel.elapsedTimeMillis.collectAsState()
    val isFasting by viewModel.isFasting.collectAsState()
    val listState = rememberLazyListState()
    val isLight = MaterialTheme.colorScheme.background == Color(0xFFFFFFFF)

    // Colors matching Stitch DESIGN.md
    val bgColor = if (isLight) Color(0xFFF5F5F5) else Color.Black
    val surfaceColor = if (isLight) Color.White else Color(0xFF121212)
    val primaryNeon = if (isLight) Color(0xFFD4AF37) else Color(0xFFFFDE4D)
    val onSurface = if (isLight) Color.Black else Color(0xFFE9E2D2)
    val onSurfaceVariant = if (isLight) Color.DarkGray else Color(0xFF8E8E93)
    val borderColor = if (isLight) Color(0xFFE0E0E0) else Color(0xFF1C1C1E)
    
    val stages = FastingStage.entries
    val activePainters = com.example.fastingtracker.ui.components.FastingStageIcons.getActivePainters()
    val inactivePainters = com.example.fastingtracker.ui.components.FastingStageIcons.getInactivePainters()

    LaunchedEffect(key1 = initialStageIndex, key2 = currentStage, key3 = isFasting) {
        val targetIndex = initialStageIndex ?: if (isFasting) stages.indexOf(currentStage) else -1
        if (targetIndex != -1) {
            delay(400)
            listState.slowSmoothScrollToItem(targetIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .statusBarsPadding()
    ) {
        // Page Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(surfaceColor)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = primaryNeon
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "FASTING STAGES",
                color = primaryNeon,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = com.example.fastingtracker.ui.theme.GeistFont,
                letterSpacing = (-0.5).sp
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp, start = 16.dp, end = 16.dp)
        ) {

            itemsIndexed(stages) { index, stage ->
                val isCompleted = !isFasting || stage.ordinal < currentStage.ordinal
                val isActive = isFasting && stage.ordinal == currentStage.ordinal
                val isUpcoming = isFasting && stage.ordinal > currentStage.ordinal

                val nextStage = stages.getOrNull(index + 1)
                val hoursText = if (nextStage != null) {
                    "${stage.startHour} - ${nextStage.startHour}H"
                } else {
                    "${stage.startHour}+H"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    // Left Timeline Track
                    Column(
                        modifier = Modifier.width(100.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Node Circle
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(surfaceColor)
                                .border(
                                    width = 2.dp,
                                    color = if (isActive || isCompleted) primaryNeon else borderColor,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            val currentPainter = if (isActive || isCompleted) activePainters[index] else inactivePainters[index]
                            
                            Image(
                                painter = currentPainter,
                                contentDescription = if (isActive) "Active Stage" else "Stage Icon",
                                modifier = Modifier.size(90.dp)
                            )
                        }

                        // Vertical connecting line
                        if (index != stages.lastIndex) {
                            val stageProgress = if (isActive) {
                                val nextStage = stages.getOrNull(index + 1)
                                if (nextStage != null) {
                                    val stageStartMillis = stage.startHour * 60 * 60 * 1000L
                                    val stageEndMillis = nextStage.startHour * 60 * 60 * 1000L
                                    val elapsedInStage = elapsedTimeMillis - stageStartMillis
                                    val totalStageDuration = stageEndMillis - stageStartMillis
                                    if (totalStageDuration > 0) {
                                        (elapsedInStage.toFloat() / totalStageDuration.toFloat()).coerceIn(0.0f, 1.0f)
                                    } else {
                                        0.0f
                                    }
                                } else {
                                    1.0f
                                }
                            } else if (isCompleted) {
                                1.0f
                            } else {
                                0.0f
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(4.dp)
                                    .background(borderColor),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(fraction = stageProgress)
                                        .background(primaryNeon)
                                )
                            }
                        }
                    }

                    // Right Content Card/Arrow Column
                    val cardAlpha = if (isUpcoming) 0.6f else 1f
                    val cardBorder = if (isActive) primaryNeon else borderColor

                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(surfaceColor.copy(alpha = cardAlpha))
                                .border(1.dp, cardBorder.copy(alpha = cardAlpha), RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = hoursText,
                                    color = if (isActive || isCompleted) primaryNeon.copy(alpha = cardAlpha) else onSurfaceVariant.copy(alpha = cardAlpha),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = com.example.fastingtracker.ui.theme.GeistMonoFont,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = stage.longTitle,
                                    color = onSurface.copy(alpha = cardAlpha),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stage.description,
                                color = if (isActive) onSurface else onSurfaceVariant.copy(alpha = cardAlpha),
                                fontSize = 14.sp,
                                lineHeight = 22.sp
                            )
                            
                            if (isActive) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(borderColor)
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(primaryNeon)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "CURRENT STAGE",
                                            color = primaryNeon,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = com.example.fastingtracker.ui.theme.GeistMonoFont,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }
                            }
                        }

                        if (index != stages.lastIndex) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                val lineColor = if (isCompleted || isActive) primaryNeon else borderColor
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Scroll Down",
                                    tint = lineColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

suspend fun LazyListState.slowSmoothScrollToItem(targetIndex: Int) {
    if (targetIndex < 0) return
    val currentIndex = firstVisibleItemIndex
    if (currentIndex == targetIndex && firstVisibleItemScrollOffset == 0) return

    val visibleItems = layoutInfo.visibleItemsInfo
    val avgItemSize = if (visibleItems.isNotEmpty()) {
        visibleItems.map { it.size }.average().toFloat()
    } else {
        500f
    }

    val itemsToScroll = targetIndex - currentIndex
    val estimatedTotalDistance = itemsToScroll * avgItemSize - firstVisibleItemScrollOffset

    // Smooth duration proportional to distance
    val duration = (kotlin.math.abs(itemsToScroll) * 350L).coerceIn(800L, 1800L)
    val startTime = withFrameNanos { it }
    val easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)

    var previousProgress = 0f

    scroll {
        while (true) {
            val now = withFrameNanos { it }
            val elapsedMillis = (now - startTime) / 1_000_000L
            val rawProgress = (elapsedMillis.toFloat() / duration).coerceIn(0f, 1f)
            val easedProgress = easing.transform(rawProgress)
            val deltaProgress = easedProgress - previousProgress
            previousProgress = easedProgress

            val targetItem = layoutInfo.visibleItemsInfo.find { it.index == targetIndex }
            if (targetItem != null) {
                val remainingOffset = targetItem.offset.toFloat()
                val remainingProgress = 1f - easedProgress
                if (remainingProgress > 0.001f) {
                    val step = remainingOffset * (deltaProgress / remainingProgress)
                    scrollBy(step)
                } else {
                    scrollBy(remainingOffset)
                    break
                }
            } else {
                val step = estimatedTotalDistance * deltaProgress
                scrollBy(step)
            }

            if (rawProgress >= 1f) break
        }
    }
}
