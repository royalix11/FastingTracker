package com.example.fastingtracker.ui.screens.me

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.example.fastingtracker.ui.theme.noRippleClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.example.fastingtracker.navigation.ThemeSettings
import com.example.fastingtracker.ui.theme.PrimaryNeonYellow
import com.example.fastingtracker.ui.viewmodel.FastingViewModel

@Composable
fun MeScreen(
    onItemClick: (NavKey) -> Unit,
    viewmodel: FastingViewModel
) {
    val historyLogs by viewmodel.historyLogs.collectAsState()
    val isLight = MaterialTheme.colorScheme.background == Color(0xFFFFFFFF)
    val cardBg = if (isLight) Color(0xFFF9F9F9) else Color(0xFF161618)
    val primaryColor = if (isLight) Color(0xFFD4AF37) else PrimaryNeonYellow
    val textColor = if (isLight) Color.Black else Color.White
    val subtitleColor = if (isLight) Color.DarkGray else Color.LightGray

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(32.dp))

        // Settings Section
        Text(
            text = "SETTINGS",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = subtitleColor,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg)
        ) {
            Column {
                SettingsRow(
                    icon = Icons.Default.Palette,
                    title = "Theme Settings",
                    subtitle = "Customize app appearance",
                    primaryColor = primaryColor,
                    textColor = textColor,
                    subtitleColor = subtitleColor,
                    onClick = { /* Disabled: White theme does not look good */ }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingsRow(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Manage alerts and reminders",
                    primaryColor = primaryColor,
                    textColor = textColor,
                    subtitleColor = subtitleColor,
                    onClick = { /* TODO: Implement Notifications Screen */ }
                )
            }
        }
    }
}

@Composable
fun ProfileStatBox(label: String, value: String, cardBg: Color, primaryColor: Color, textColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(cardBg, RoundedCornerShape(12.dp))
            .padding(vertical = 16.dp, horizontal = 12.dp)
            .width(80.dp)
    ) {
        Text(value, fontSize = 20.sp, color = primaryColor, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, color = textColor, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    primaryColor: Color,
    textColor: Color,
    subtitleColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
            Text(subtitle, fontSize = 12.sp, color = subtitleColor)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = subtitleColor)
    }
}
