package com.example.fastingtracker.ui.screens.settings

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fastingtracker.ui.viewmodel.FastingViewModel

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.example.fastingtracker.ui.theme.noRippleClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fastingtracker.ui.theme.*

@Composable
fun ThemeSettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("fasting_prefs", Context.MODE_PRIVATE) }
    var currentTheme by remember { mutableStateOf(prefs.getString("theme_preference", "system") ?: "system") }
    val haptic = LocalHapticFeedback.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .statusBarsPadding()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                val isLight = MaterialTheme.colorScheme.background == Color(0xFFFFFFFF)
                val primaryTitleColor = if (isLight) Color.Black else Color(0xFFFFDE4D)
                
                Text(
                    text = "SELECT THEME",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryTitleColor,
                    fontFamily = com.example.fastingtracker.ui.theme.GeistFont,
                    letterSpacing = (-1).sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select your preferred interface aesthetic. Dark mode is recommended for optimal metabolic focus.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = com.example.fastingtracker.ui.theme.GeistFont,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(28.dp))
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Dark Option
                    ThemeOptionCard(
                        title = "Dark",
                        description = "Technological Stoicism",
                        icon = Icons.Default.DarkMode,
                        isSelected = currentTheme == "dark",
                        onClick = {
                            prefs.edit().putString("theme_preference", "dark").apply()
                            currentTheme = "dark"
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    )

                    // Light Option
                    ThemeOptionCard(
                        title = "Light",
                        description = "High Noon Clarity",
                        icon = Icons.Default.LightMode,
                        isSelected = currentTheme == "light",
                        onClick = {
                            prefs.edit().putString("theme_preference", "light").apply()
                            currentTheme = "light"
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    )

                    // System Default Option
                    ThemeOptionCard(
                        title = "System Default",
                        description = "Circadian Sync",
                        icon = Icons.Default.SettingsBrightness,
                        isSelected = currentTheme == "system",
                        onClick = {
                            prefs.edit().putString("theme_preference", "system").apply()
                            currentTheme = "system"
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    )
                }
            }
        }

        // Fixed Back Button
        val isLight = MaterialTheme.colorScheme.background == Color(0xFFFFFFFF)
        val backBtnBg = if (isLight) Color(0xFFD4AF37) else Color(0xFFFFDE4D)
        val backBtnIcon = if (isLight) Color.White else Color(0xFF3A3000)

        Box(
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(backBtnBg)
                .noRippleClickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onBackClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Go back",
                tint = backBtnIcon,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
}

@Composable
fun ThemeOptionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isLight = MaterialTheme.colorScheme.background == Color(0xFFFFFFFF)
    val elapsedColor = if (isLight) Color(0xFFD4AF37) else Color(0xFFFFDE4D)
    val cardBg = if (isLight) Color.White else Color(0xFF121212)
    val inactiveBorder = if (isLight) Color.LightGray else Color(0xFF1C1C1E)
    val borderColor = if (isSelected) elapsedColor else inactiveBorder
    val borderWidth = if (isSelected) 1.5.dp else 1.dp
    val iconBg = if (isLight) Color(0xFFE2E2E2) else Color(0xFF1C1C1E)
    val titleColor = if (isLight) Color.Black else Color.White
    val subtitleColor = if (isLight) Color.DarkGray else Color(0xFFCFC6AE)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .border(BorderStroke(borderWidth, borderColor), RoundedCornerShape(12.dp))
            .noRippleClickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon wrapper
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(iconBg)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isSelected) elapsedColor else subtitleColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        color = titleColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        color = subtitleColor,
                        fontSize = 12.sp,
                        fontFamily = com.example.fastingtracker.ui.theme.GeistMonoFont
                    )
                }
            }

            // Radio Button indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(24.dp)
                    .border(
                        border = BorderStroke(2.dp, borderColor),
                        shape = CircleShape
                    )
                    .background(
                        color = if (isSelected) elapsedColor else Color.Transparent,
                        shape = CircleShape
                    )
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = if (isLight) Color.White else Color(0xFF3A3000),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
