import os

file_path = r"app/src/main/java/com/example/fastingtracker/ui/screens/home/HomeScreen.kt"

with open(file_path, "r", encoding="utf-8") as f:
    lines = f.readlines()

# find boundary_fix_1 and remove it
# boundary_fix_1 was:
#                 )
#             }
#         }
#     }
# }
# followed by an empty line.

cleaned_lines = []
skip = False
for i in range(len(lines)):
    if "                )\n" == lines[i] and "            }\n" == lines[i+1:i+2][0] if i+1 < len(lines) else False:
        # Check if it's the exact boundary we added
        if i+2 < len(lines) and "        }\n" == lines[i+2] and i+3 < len(lines) and "    }\n" == lines[i+3] and i+4 < len(lines) and "}\n" == lines[i+4]:
            if "CircularTimerRing" in lines[i+6] if i+6 < len(lines) else False:
                # found it, skip next 6 lines
                skip = 6
    
    if skip > 0:
        skip -= 1
        continue
        
    cleaned_lines.append(lines[i])

# Check for trailing @OptIn
while cleaned_lines and cleaned_lines[-1].strip() == "":
    cleaned_lines.pop()

if cleaned_lines and "@OptIn" in cleaned_lines[-1]:
    cleaned_lines.pop()

# Add formatMillis and TimerTextDisplay
missing_code = """
fun formatMillis(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format(java.util.Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
}

@Composable
fun TimerTextDisplay(
    isCountdown: Boolean,
    onToggleCountdown: () -> Unit,
    elapsedMillisProvider: () -> Long,
    targetMillis: Long,
    isLight: Boolean,
    timeTextColor: androidx.compose.ui.graphics.Color
) {
    val elapsed = elapsedMillisProvider()
    val displayMillis = if (isCountdown) kotlin.math.max(0, targetMillis - elapsed) else elapsed
    
    androidx.compose.material3.Text(
        text = formatMillis(displayMillis),
        color = timeTextColor,
        fontSize = 48.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
        modifier = androidx.compose.ui.Modifier.clickable { onToggleCountdown() },
        style = androidx.compose.ui.text.TextStyle(
            shadow = if (isLight) androidx.compose.ui.graphics.Shadow.None else androidx.compose.ui.graphics.Shadow(
                color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f),
                offset = androidx.compose.ui.geometry.Offset(0f, 4f),
                blurRadius = 8f
            )
        )
    )
}
"""
cleaned_lines.append(missing_code)

with open(file_path, "w", encoding="utf-8") as f:
    f.writelines(cleaned_lines)
