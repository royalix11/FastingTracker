package com.example.fastingtracker

import com.example.fastingtracker.navigation.*

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.fastingtracker.ui.theme.FastingTrackerTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    
    val prefs = getSharedPreferences("fasting_prefs", Context.MODE_PRIVATE)

    setContent {
      var themePref by remember { mutableStateOf(prefs.getString("theme_preference", "system") ?: "system") }

      DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
          if (key == "theme_preference") {
            themePref = prefs.getString("theme_preference", "system") ?: "system"
          }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
          prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
      }

      val darkTheme = when (themePref) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme() // "system"
      }

      FastingTrackerTheme(darkTheme = darkTheme) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          MainNavigation()
        }
      }
    }
  }
}
