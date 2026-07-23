package com.example.fastingtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fastingtracker.data.local.SymptomBitmask

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillSymptomBitmaskSelector(
    currentBitmask: Long,
    onBitmaskChanged: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val symptoms = listOf(
        "Hunger Spike" to SymptomBitmask.HUNGER_SPIKE,
        "Headache" to SymptomBitmask.HEADACHE,
        "Fatigue" to SymptomBitmask.FATIGUE,
        "High Focus" to SymptomBitmask.HIGH_FOCUS,
        "Chills" to SymptomBitmask.CHILLS,
        "Euphoria" to SymptomBitmask.EUPHORIA,
        "Dizziness" to SymptomBitmask.DIZZINESS,
        "Cravings" to SymptomBitmask.CRAVINGS
    )

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        symptoms.forEach { (name, mask) ->
            val isSelected = SymptomBitmask.hasSymptom(currentBitmask, mask)
            
            val backgroundColor = if (isSelected) Color(0xFF1C1B15) else Color.Transparent
            val borderColor = if (isSelected) Color(0xFFFFDE4D) else Color(0xFF333129)
            val textColor = if (isSelected) Color(0xFFFFDE4D) else Color(0xFF8C8A7D)

            Box(
                modifier = Modifier
                    .background(backgroundColor, CutCornerShape(4.dp))
                    .border(1.dp, borderColor, CutCornerShape(4.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        val newMask = if (isSelected) {
                            SymptomBitmask.removeSymptom(currentBitmask, mask)
                        } else {
                            SymptomBitmask.addSymptom(currentBitmask, mask)
                        }
                        onBitmaskChanged(newMask)
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = name,
                    color = textColor,
                    fontSize = 14.sp
                )
            }
        }
    }
}
