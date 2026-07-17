package com.example.fastingtracker.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.fastingtracker.R

object FastingStageIcons {
    @Composable
    fun getActivePainters(): List<Painter> = listOf(
        painterResource(id = R.drawable.ic_stage_0_active),
        painterResource(id = R.drawable.ic_stage_1_active),
        painterResource(id = R.drawable.ic_stage_2_active),
        painterResource(id = R.drawable.ic_stage_3_active),
        painterResource(id = R.drawable.ic_stage_4_active),
        painterResource(id = R.drawable.ic_stage_5_active),
        painterResource(id = R.drawable.ic_stage_6_active),
        painterResource(id = R.drawable.ic_stage_7_active),
        painterResource(id = R.drawable.ic_stage_8_active),
        painterResource(id = R.drawable.ic_stage_9_active)
    )

    @Composable
    fun getInactivePainters(): List<Painter> = listOf(
        painterResource(id = R.drawable.ic_stage_0_inactive),
        painterResource(id = R.drawable.ic_stage_1_inactive),
        painterResource(id = R.drawable.ic_stage_2_inactive),
        painterResource(id = R.drawable.ic_stage_3_inactive),
        painterResource(id = R.drawable.ic_stage_4_inactive),
        painterResource(id = R.drawable.ic_stage_5_inactive),
        painterResource(id = R.drawable.ic_stage_6_inactive),
        painterResource(id = R.drawable.ic_stage_7_inactive),
        painterResource(id = R.drawable.ic_stage_8_inactive),
        painterResource(id = R.drawable.ic_stage_9_inactive)
    )
}

fun DrawScope.drawFastingStageIcon(
    stageIndex: Int,
    center: Offset,
    radius: Float,
    color: Color
) {
    val R = radius * 0.55f // Icon boundary radius
    val strokeW = radius * 0.15f
    val stroke = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)

    when (stageIndex) {
        0 -> { // BloodSugarIncreases: Up arrow
            drawLine(color, Offset(center.x, center.y + R), Offset(center.x, center.y - R), strokeWidth = strokeW, cap = StrokeCap.Round)
            drawLine(color, Offset(center.x - R*0.5f, center.y - R*0.4f), Offset(center.x, center.y - R), strokeWidth = strokeW, cap = StrokeCap.Round)
            drawLine(color, Offset(center.x + R*0.5f, center.y - R*0.4f), Offset(center.x, center.y - R), strokeWidth = strokeW, cap = StrokeCap.Round)
        }
        1 -> { // BloodSugarDecreases: Down arrow
            drawLine(color, Offset(center.x, center.y - R), Offset(center.x, center.y + R), strokeWidth = strokeW, cap = StrokeCap.Round)
            drawLine(color, Offset(center.x - R*0.5f, center.y + R*0.4f), Offset(center.x, center.y + R), strokeWidth = strokeW, cap = StrokeCap.Round)
            drawLine(color, Offset(center.x + R*0.5f, center.y + R*0.4f), Offset(center.x, center.y + R), strokeWidth = strokeW, cap = StrokeCap.Round)
        }
        2 -> { // BloodSugarNormalizes: Drop + horizontal line
            val dropPath = Path().apply {
                moveTo(center.x, center.y - R)
                cubicTo(center.x + R*0.8f, center.y - R*0.2f, center.x + R, center.y + R, center.x, center.y + R)
                cubicTo(center.x - R, center.y + R, center.x - R*0.8f, center.y - R*0.2f, center.x, center.y - R)
                close()
            }
            drawPath(dropPath, color, style = stroke)
            drawLine(color, Offset(center.x - R, center.y + R*0.2f), Offset(center.x + R, center.y + R*0.2f), strokeWidth = strokeW, cap = StrokeCap.Round)
        }
        3 -> { // Gluconeogenesis: Hexagon
            val hexPath = Path().apply {
                for (i in 0..6) {
                    val angle = (i * 60 - 30) * (kotlin.math.PI / 180f).toFloat()
                    val x = center.x + R * kotlin.math.cos(angle)
                    val y = center.y + R * kotlin.math.sin(angle)
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
            }
            drawPath(hexPath, color, style = stroke)
        }
        4, 5 -> { // FatBurning & Ketosis: Flame
            val flamePath = Path().apply {
                moveTo(center.x, center.y - R)
                cubicTo(center.x + R, center.y, center.x + R, center.y + R, center.x, center.y + R)
                cubicTo(center.x - R, center.y + R, center.x - R, center.y, center.x, center.y - R)
                close()
            }
            drawPath(flamePath, color, style = stroke)
            if (stageIndex == 5) {
                // Inner flame for ketosis
                val innerFlame = Path().apply {
                    moveTo(center.x, center.y)
                    cubicTo(center.x + R*0.5f, center.y + R*0.3f, center.x + R*0.5f, center.y + R*0.8f, center.x, center.y + R*0.8f)
                    cubicTo(center.x - R*0.5f, center.y + R*0.8f, center.x - R*0.5f, center.y + R*0.3f, center.x, center.y)
                }
                drawPath(innerFlame, color, style = stroke)
            }
        }
        6 -> { // Autophagy: Recycle / Triangle
            val triPath = Path().apply {
                moveTo(center.x, center.y - R)
                lineTo(center.x + R*0.866f, center.y + R*0.5f)
                lineTo(center.x - R*0.866f, center.y + R*0.5f)
                close()
            }
            drawPath(triPath, color, style = stroke)
            drawLine(color, Offset(center.x, center.y - R), Offset(center.x + R*0.3f, center.y - R*0.7f), strokeWidth = strokeW, cap = StrokeCap.Round)
        }
        7 -> { // IncreasedGrowthHormones: Arrow inside circle
            drawCircle(color, radius = R, center = center, style = stroke)
            val innerR = R * 0.5f
            drawLine(color, Offset(center.x, center.y + innerR), Offset(center.x, center.y - innerR), strokeWidth = strokeW, cap = StrokeCap.Round)
            drawLine(color, Offset(center.x - innerR*0.5f, center.y - innerR*0.4f), Offset(center.x, center.y - innerR), strokeWidth = strokeW, cap = StrokeCap.Round)
            drawLine(color, Offset(center.x + innerR*0.5f, center.y - innerR*0.4f), Offset(center.x, center.y - innerR), strokeWidth = strokeW, cap = StrokeCap.Round)
        }
        8 -> { // InsulinSensitivity: Key
            val keyR = R * 0.4f
            drawCircle(color, radius = keyR, center = Offset(center.x - R + keyR, center.y - R + keyR), style = stroke)
            drawLine(color, Offset(center.x - R + keyR + keyR*0.707f, center.y - R + keyR + keyR*0.707f), Offset(center.x + R, center.y + R), strokeWidth = strokeW, cap = StrokeCap.Round)
            drawLine(color, Offset(center.x + R*0.4f, center.y + R*0.4f), Offset(center.x + R*0.7f, center.y + R*0.1f), strokeWidth = strokeW, cap = StrokeCap.Round)
            drawLine(color, Offset(center.x + R*0.7f, center.y + R*0.7f), Offset(center.x + R*1.0f, center.y + R*0.4f), strokeWidth = strokeW, cap = StrokeCap.Round)
        }
        9 -> { // ImmuneCellRegeneration: Shield with +
            val shieldPath = Path().apply {
                moveTo(center.x - R, center.y - R*0.8f)
                lineTo(center.x + R, center.y - R*0.8f)
                lineTo(center.x + R, center.y)
                cubicTo(center.x + R, center.y + R*0.8f, center.x, center.y + R*1.2f, center.x, center.y + R*1.2f)
                cubicTo(center.x, center.y + R*1.2f, center.x - R, center.y + R*0.8f, center.x - R, center.y)
                close()
            }
            drawPath(shieldPath, color, style = stroke)
            drawLine(color, Offset(center.x, center.y - R*0.3f), Offset(center.x, center.y + R*0.5f), strokeWidth = strokeW, cap = StrokeCap.Round)
            drawLine(color, Offset(center.x - R*0.4f, center.y + R*0.1f), Offset(center.x + R*0.4f, center.y + R*0.1f), strokeWidth = strokeW, cap = StrokeCap.Round)
        }
    }
}
