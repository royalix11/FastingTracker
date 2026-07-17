package com.example.fastingtracker.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.fastingtracker.ui.screens.home.MainScreen
import com.example.fastingtracker.ui.screens.settings.ThemeSettingsScreen
import com.example.fastingtracker.ui.screens.plan.CustomFastingPlanScreen
import com.example.fastingtracker.ui.viewmodel.FastingViewModel

@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(Main)
  val viewModel: FastingViewModel = viewModel()
  val context = androidx.compose.ui.platform.LocalContext.current
  val prefs = androidx.compose.runtime.remember { context.getSharedPreferences("fasting_prefs", android.content.Context.MODE_PRIVATE) }

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Main> {
          MainScreen(onItemClick = { navKey -> backStack.add(navKey) })
        }
        entry<ThemeSettings> {
          ThemeSettingsScreen(onBackClick = { backStack.removeLastOrNull() })
        }
        entry<FastingStagesTimeline> { key ->
          com.example.fastingtracker.ui.screens.timeline.FastingStagesTimelineScreen(
            onBackClick = { backStack.removeLastOrNull() },
            initialStageIndex = key.initialStageIndex
          )
        }
        entry<CustomFastingPlan> { key ->
          CustomFastingPlanScreen(
            onBackClick = { backStack.removeLastOrNull() },
            editStartTimeMillis = key.editStartTimeMillis,
            lockedDurationMillis = key.lockedDurationMillis,
            onPlanSelected = { startTimeMillis, durationMillis, isScheduled ->
                if (key.editStartTimeMillis != null) {
                    viewModel.editActiveStartTime(startTimeMillis)
                } else if (isScheduled) {
                    viewModel.scheduleCustomFast(startTimeMillis, durationMillis)
                } else {
                    viewModel.startCustomFast(startTimeMillis, durationMillis)
                }
                prefs.edit().putString("active_tab", "HOME").apply()
                backStack.removeLastOrNull()
            }
          )
        }
      },
  )
}
