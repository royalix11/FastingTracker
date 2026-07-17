package com.example.fastingtracker.ui.screens.plan

import com.example.fastingtracker.ui.components.WheelTimePicker

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fastingtracker.ui.viewmodel.FastingViewModel

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fastingtracker.ui.theme.PrimaryNeonYellow
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomFastingPlanScreen(
    onBackClick: () -> Unit,
    editStartTimeMillis: Long? = null,
    lockedDurationMillis: Long? = null,
    onPlanSelected: (startTimeMillis: Long, durationMillis: Long, isScheduled: Boolean) -> Unit
) {
    val context = LocalContext.current
    var selectedDurationHours by remember { mutableFloatStateOf(lockedDurationMillis?.let { it / (3600 * 1000f) } ?: 18f) }
    var startDateTime by remember { 
        mutableStateOf(
            editStartTimeMillis?.let { 
                java.time.Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime() 
            } ?: LocalDateTime.now()
        ) 
    }

    val calculatedEndDateTime by remember(startDateTime, selectedDurationHours) {
        derivedStateOf {
            startDateTime.plusHours(selectedDurationHours.toLong())
        }
    }

    val isLight = MaterialTheme.colorScheme.background == Color(0xFFFFFFFF)
    val sheetBg = if (isLight) Color(0xFFF0F0F0) else Color(0xFF1C1C1E)
    val cardBg = if (isLight) Color(0xFFE8E8E8) else Color(0xFF242426)
    val elapsedColor = if (isLight) Color(0xFFD4AF37) else PrimaryNeonYellow

    val fullFormatter = remember { DateTimeFormatter.ofPattern("MMM d, h:mm a") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (lockedDurationMillis != null) "Edit Start Time" else "Custom Fasting Plan",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isLight) Color.Black else Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Duration Display & Slider (only when custom plan duration is not locked)
        if (lockedDurationMillis == null) {
            Text(
                text = "Select Duration",
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${selectedDurationHours.toInt()} HOURS",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = elapsedColor,
                        fontFamily = com.example.fastingtracker.ui.theme.GeistFont
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Slider(
                        value = selectedDurationHours,
                        onValueChange = { selectedDurationHours = kotlin.math.round(it) },
                        valueRange = 1f..72f,
                        colors = SliderDefaults.colors(
                            thumbColor = elapsedColor,
                            activeTrackColor = elapsedColor,
                            inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(elapsedColor, shape = RoundedCornerShape(50))
                            )
                        },
                        track = { sliderState ->
                            SliderDefaults.Track(
                                colors = SliderDefaults.colors(
                                    thumbColor = elapsedColor,
                                    activeTrackColor = elapsedColor,
                                    inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                                ),
                                sliderState = sliderState,
                                thumbTrackGapSize = 0.dp,
                                drawStopIndicator = null
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

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
                startDateTime = startDateTime,
                onDateTimeChanged = { startDateTime = it },
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
                val durationMillis = selectedDurationHours.toLong() * 3600 * 1000L
                val startTimeMillis = startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                if (lockedDurationMillis != null) {
                    onPlanSelected(startTimeMillis, durationMillis, false)
                } else {
                    val isScheduled = startTimeMillis > System.currentTimeMillis()
                    onPlanSelected(startTimeMillis, durationMillis, isScheduled)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = elapsedColor, contentColor = if (isLight) Color.White else Color.Black)
        ) {
            Text(
                text = if (lockedDurationMillis != null) "UPDATE START TIME" else "START FAST",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
