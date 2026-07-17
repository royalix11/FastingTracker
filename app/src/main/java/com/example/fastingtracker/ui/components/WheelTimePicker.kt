package com.example.fastingtracker.ui.components

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fastingtracker.ui.viewmodel.FastingViewModel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelTimePicker(
    startDateTime: LocalDateTime,
    onDateTimeChanged: (LocalDateTime) -> Unit,
    modifier: Modifier = Modifier,
    elapsedColor: Color,
    backgroundColor: Color = Color(0xFF1C1C1E)
) {
    val today = LocalDate.now()
    val dates = remember { (-30L..30L).map { today.plusDays(it) } }
    val hours = remember { (1..12).toList() }
    val minutes = remember { (0..59).toList() }
    val periods = remember { listOf("AM", "PM") }

    val initialDateIndexRaw = dates.indexOfFirst { it == startDateTime.toLocalDate() }.coerceAtLeast(0)
    val initialHourIndexRaw = hours.indexOfFirst { 
        val h = startDateTime.hour % 12
        it == (if (h == 0) 12 else h)
    }.coerceAtLeast(0)
    val initialMinuteIndexRaw = minutes.indexOfFirst { it == startDateTime.minute }.coerceAtLeast(0)
    val initialPeriodIndexRaw = if (startDateTime.hour < 12) 0 else 1

    val dateMiddle = (Int.MAX_VALUE / 2 / dates.size) * dates.size
    val hourMiddle = (Int.MAX_VALUE / 2 / hours.size) * hours.size
    val minuteMiddle = (Int.MAX_VALUE / 2 / minutes.size) * minutes.size

    val dateState = rememberLazyListState(initialFirstVisibleItemIndex = dateMiddle + initialDateIndexRaw)
    val hourState = rememberLazyListState(initialFirstVisibleItemIndex = hourMiddle + initialHourIndexRaw)
    val minuteState = rememberLazyListState(initialFirstVisibleItemIndex = minuteMiddle + initialMinuteIndexRaw)
    val periodState = rememberLazyListState(initialFirstVisibleItemIndex = initialPeriodIndexRaw)

    val itemHeight = 48.dp
    val visibleItemsCount = 3
    val pickerHeight = itemHeight * visibleItemsCount

    // Listen to scroll changes to update state only when scrolling stops
    LaunchedEffect(dateState.isScrollInProgress, hourState.isScrollInProgress, minuteState.isScrollInProgress, periodState.isScrollInProgress) {
        val isScrolling = dateState.isScrollInProgress || hourState.isScrollInProgress || minuteState.isScrollInProgress || periodState.isScrollInProgress
        if (!isScrolling) {
            val dIdx = dateState.firstVisibleItemIndex % dates.size
            val hIdx = hourState.firstVisibleItemIndex % hours.size
            val mIdx = minuteState.firstVisibleItemIndex % minutes.size
            val pIdx = periodState.firstVisibleItemIndex % periods.size
            
            val d = dates.getOrNull(dIdx) ?: dates[0]
            val h = hours.getOrNull(hIdx) ?: hours[0]
            val m = minutes.getOrNull(mIdx) ?: minutes[0]
            val p = periods.getOrNull(pIdx) ?: periods[0]

            var hour24 = h
            if (p == "PM" && h < 12) hour24 += 12
            if (p == "AM" && h == 12) hour24 = 0

            val newDateTime = d.atTime(hour24, m)
            if (newDateTime != startDateTime) {
                onDateTimeChanged(newDateTime)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(pickerHeight)
    ) {
        // Selection highlight lines
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(itemHeight))
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .drawWithContent {
                    drawContent()
                    val yTop = 0f
                    val yBottom = size.height
                    drawLine(
                        color = elapsedColor.copy(alpha = 0.5f),
                        start = Offset(0f, yTop),
                        end = Offset(size.width, yTop),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = elapsedColor.copy(alpha = 0.5f),
                        start = Offset(0f, yBottom),
                        end = Offset(size.width, yBottom),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            )
        }

        // Columns
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val columnModifier = Modifier.weight(1f)
            
            // Date Column
            WheelPickerColumn(
                state = dateState,
                items = dates,
                itemHeight = itemHeight,
                elapsedColor = elapsedColor,
                modifier = columnModifier,
                textMapper = { date ->
                    when (date) {
                        today -> "Today"
                        today.plusDays(1) -> "Tomorrow"
                        today.minusDays(1) -> "Yesterday"
                        else -> date.format(DateTimeFormatter.ofPattern("MMM dd"))
                    }
                }
            )

            // Hour Column
            WheelPickerColumn(
                state = hourState,
                items = hours,
                itemHeight = itemHeight,
                elapsedColor = elapsedColor,
                modifier = columnModifier,
                textMapper = { String.format("%02d", it) }
            )

            // Minute Column
            WheelPickerColumn(
                state = minuteState,
                items = minutes,
                itemHeight = itemHeight,
                elapsedColor = elapsedColor,
                modifier = columnModifier,
                textMapper = { String.format("%02d", it) }
            )

            // Period Column
            WheelPickerColumn(
                state = periodState,
                items = periods,
                itemHeight = itemHeight,
                elapsedColor = elapsedColor,
                modifier = columnModifier,
                isInfinite = false,
                textMapper = { it }
            )
        }

        // Fading gradients (3D cylinder effect)
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight)
            .align(Alignment.TopCenter)
            .background(
                Brush.verticalGradient(
                    colors = listOf(backgroundColor, Color.Transparent)
                )
            )
        )
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight)
            .align(Alignment.BottomCenter)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, backgroundColor)
                )
            )
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> WheelPickerColumn(
    state: androidx.compose.foundation.lazy.LazyListState,
    items: List<T>,
    itemHeight: Dp,
    elapsedColor: Color,
    modifier: Modifier = Modifier,
    isInfinite: Boolean = true,
    textMapper: (T) -> String
) {
    val itemCount = if (isInfinite) Int.MAX_VALUE else items.size
    LazyColumn(
        state = state,
        modifier = modifier.height(itemHeight * 3), // 3 visible items
        flingBehavior = rememberSnapFlingBehavior(lazyListState = state),
        contentPadding = PaddingValues(vertical = itemHeight) // Pad top/bottom by exactly 1 item to allow snapping to ends
    ) {
        items(itemCount) { index ->
            val actualIndex = if (isInfinite) index % items.size else index
            val isSelected by remember {
                derivedStateOf { state.firstVisibleItemIndex == index }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = textMapper(items[actualIndex]),
                    style = if (isSelected) {
                        TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = elapsedColor,
                            fontFamily = com.example.fastingtracker.ui.theme.GeistFont,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            fontFamily = com.example.fastingtracker.ui.theme.GeistMonoFont,
                            textAlign = TextAlign.Center
                        )
                    }
                )
            }
        }
    }
}
