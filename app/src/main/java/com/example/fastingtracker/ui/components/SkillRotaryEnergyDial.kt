package com.example.fastingtracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.PI

@Composable
fun SkillRotaryEnergyDial(
    currentScore: Int,
    onScoreChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(200.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val touchOffset = change.position
                    val dx = touchOffset.x - center.x
                    val dy = touchOffset.y - center.y
                    
                    var angle = (atan2(dy.toDouble(), dx.toDouble()) * 180 / PI).toFloat()
                    if (angle < 0) {
                        angle += 360f
                    }
                    
                    val normalizedAngle = (angle - 135f + 360f) % 360f
                    if (normalizedAngle <= 270f) {
                        val progress = normalizedAngle / 270f
                        val newScore = (1 + (progress * 9)).toInt().coerceIn(1, 10)
                        if (newScore != currentScore) {
                            onScoreChanged(newScore)
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
            val sweepAngle = ((currentScore - 1) / 9f) * 270f
            
            drawArc(
                color = Color(0xFF333129),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(size.width - strokeWidth, size.height - strokeWidth),
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
            )
            
            drawArc(
                color = Color(0xFFFFDE4D),
                startAngle = 135f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(size.width - strokeWidth, size.height - strokeWidth),
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
            )
        }
        
        Text(
            text = "${currentScore}.0",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 48.sp,
            color = Color(0xFFFFDE4D)
        )
    }
}
