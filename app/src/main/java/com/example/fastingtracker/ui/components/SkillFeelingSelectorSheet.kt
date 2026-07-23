package com.example.fastingtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fastingtracker.data.local.SymptomBitmask

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillFeelingSelectorSheet(
    metabolicStageId: Int,
    elapsedHours: Int,
    onSaveCheckIn: (energyScore: Int, symptomBitmask: Long, notes: String?) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var energyScore by remember { mutableIntStateOf(5) }
    var symptomBitmask by remember { mutableLongStateOf(SymptomBitmask.NONE) }
    var notes by remember { mutableStateOf("") }

    val stageName = when (metabolicStageId) {
        1 -> "Fed State"
        2 -> "Blood Sugar Decrease"
        3 -> "Fat Burning"
        4 -> "Ketosis"
        else -> "Autophagy"
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0F0E0A)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Stage $metabolicStageId: $stageName",
                color = Color(0xFFFFDE4D),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Fasting for $elapsedHours hours",
                color = Color(0xFF8C8A7D),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Energy Level",
                color = Color(0xFFFFDE4D),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            SkillRotaryEnergyDial(
                currentScore = energyScore,
                onScoreChanged = { energyScore = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Symptoms",
                color = Color(0xFFFFDE4D),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            SkillSymptomBitmaskSelector(
                currentBitmask = symptomBitmask,
                onBitmaskChanged = { symptomBitmask = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Notes (optional)", color = Color(0xFF8C8A7D)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFFDE4D),
                    unfocusedBorderColor = Color(0xFF333129),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFFFDE4D)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                color = Color(0xFFFFDE4D),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onSaveCheckIn(energyScore, symptomBitmask, notes.takeIf { it.isNotBlank() })
                    },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Save Check-In",
                        color = Color(0xFF0F0E0A),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
