package com.example.fastingtracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fastingtracker.ui.theme.GeistMonoFont
import com.example.fastingtracker.ui.theme.PrimaryNeonYellow

@Composable
fun SkillEndFastButton(
    modifier: Modifier = Modifier,
    width: Dp = 300.dp,
    onClick: () -> Unit
) {
    val isLight = MaterialTheme.colorScheme.background == Color(0xFFFFFFFF)
    val elapsedColor = if (isLight) Color(0xFFD4AF37) else PrimaryNeonYellow
    val buttonBgColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.5f)
    val haptic = LocalHapticFeedback.current

    Button(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = buttonBgColor,
            contentColor = elapsedColor
        ),
        border = BorderStroke(1.dp, elapsedColor),
        shape = RoundedCornerShape(50),
        modifier = modifier
            .width(width)
            .height(44.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = "End Fast",
            color = elapsedColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = GeistMonoFont,
            letterSpacing = 0.7.sp
        )
    }
}
