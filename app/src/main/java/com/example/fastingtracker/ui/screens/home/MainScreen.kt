package com.example.fastingtracker.ui.screens.home

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.example.fastingtracker.navigation.CustomFastingPlan
import com.example.fastingtracker.navigation.FastingStagesTimeline
import com.example.fastingtracker.ui.screens.history.HistoryScreen
import com.example.fastingtracker.ui.screens.me.MeScreen
import com.example.fastingtracker.ui.screens.plan.PresetsScreen
import com.example.fastingtracker.ui.viewmodel.FastingViewModel
import androidx.compose.ui.graphics.Color
import com.example.fastingtracker.ui.theme.PrimaryNeonYellow

@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit,
    viewmodel: FastingViewModel = viewModel()
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("fasting_prefs", Context.MODE_PRIVATE) }
    var activeTabStr by remember { mutableStateOf(prefs.getString("active_tab", FastingTab.HOME.name) ?: FastingTab.HOME.name) }
    var activeTab by remember(activeTabStr) { mutableStateOf(FastingTab.valueOf(activeTabStr)) }

    DisposableEffect(Unit) {
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "active_tab") {
                activeTabStr = prefs.getString("active_tab", FastingTab.HOME.name) ?: FastingTab.HOME.name
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    val isFasting by viewmodel.isFasting.collectAsState()
    val isFastingScheduled by viewmodel.isFastingScheduled.collectAsState()
    val startTime by viewmodel.startTimeMillis.collectAsState()
    val elapsedTime by viewmodel.elapsedTimeMillis.collectAsState()
    val targetDuration by viewmodel.targetDurationMillis.collectAsState()
    val currentStage by viewmodel.currentFastingStage.collectAsState()

    Scaffold(
        bottomBar = {
            val isLight = MaterialTheme.colorScheme.background == Color(0xFFFFFFFF)
            val barBg = if (isLight) Color.White else Color.Black
            val selectedColor = if (isLight) Color(0xFFD4AF37) else PrimaryNeonYellow
            val unselectedColor = if (isLight) Color.DarkGray else Color(0xFF8E8E93)

            val navColors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                selectedTextColor = selectedColor,
                unselectedIconColor = unselectedColor,
                unselectedTextColor = unselectedColor,
                indicatorColor = Color.Transparent
            )

            NavigationBar(
                containerColor = barBg
            ) {
                NavigationBarItem(
                    selected = activeTab == FastingTab.HOME,
                    onClick = { 
                        activeTabStr = FastingTab.HOME.name
                        prefs.edit().putString("active_tab", activeTabStr).apply()
                    },
                    icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = navColors
                )
                NavigationBarItem(
                    selected = activeTab == FastingTab.PLANS,
                    onClick = { 
                        activeTabStr = FastingTab.PLANS.name
                        prefs.edit().putString("active_tab", activeTabStr).apply()
                    },
                    icon = { Icon(Icons.Outlined.DateRange, contentDescription = "Plans") },
                    label = { Text("Plans") },
                    colors = navColors
                )
                NavigationBarItem(
                    selected = activeTab == FastingTab.HISTORY,
                    onClick = { 
                        activeTabStr = FastingTab.HISTORY.name
                        prefs.edit().putString("active_tab", activeTabStr).apply()
                    },
                    icon = { Icon(Icons.Outlined.History, contentDescription = "History") },
                    label = { Text("History") },
                    colors = navColors
                )
                NavigationBarItem(
                    selected = activeTab == FastingTab.ME,
                    onClick = { 
                        activeTabStr = FastingTab.ME.name
                        prefs.edit().putString("active_tab", activeTabStr).apply()
                    },
                    icon = { Icon(Icons.Outlined.Person, contentDescription = "Me") },
                    label = { Text("Me") },
                    colors = navColors
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (activeTab) {
                FastingTab.HOME -> HomeScreen(
                    isFasting = isFasting,
                    isFastingScheduled = isFastingScheduled,
                    startTime = startTime,
                    elapsedMillisProvider = { elapsedTime },
                    targetMillis = targetDuration,
                    currentStage = currentStage,
                    onStartFast = { viewmodel.startFast() },
                    onEndFast = { viewmodel.endFast() },
                    onEditStartTimeClick = { },
                    onTimelineClick = { stageIndex -> onItemClick(FastingStagesTimeline(initialStageIndex = stageIndex)) },
                    onUpdateStartTime = { viewmodel.editActiveStartTime(it) }
                )
                FastingTab.PLANS -> PresetsScreen(
                    onItemClick = onItemClick,
                    viewmodel = viewmodel,
                    onNavigateToHome = {
                        activeTabStr = FastingTab.HOME.name
                        prefs.edit().putString("active_tab", activeTabStr).apply()
                    }
                )
                FastingTab.HISTORY -> HistoryScreen(viewmodel)
                FastingTab.ME -> MeScreen(onItemClick, viewmodel)
            }
        }
    }
}
