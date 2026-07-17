import os
import re

base_dir = r"app/src/main/java/com/example/fastingtracker"

# Read the three parts
with open(os.path.join(base_dir, r"ui/screens/home/HomeScreen.kt"), "r", encoding="utf-8") as f:
    home_lines = f.readlines()

with open(os.path.join(base_dir, r"ui/components/CircularTimerRing.kt"), "r", encoding="utf-8") as f:
    circ_lines = f.readlines()

with open(os.path.join(base_dir, r"ui/components/FastingInfoCard.kt"), "r", encoding="utf-8") as f:
    fast_lines = f.readlines()

# The header ends at line 111 (0-indexed 110) where 'val isPopular: Boolean = false' and ')' are.
# Wait, let's find the exact end of the header dynamically:
def get_body(lines):
    for i, line in enumerate(lines):
        if line.strip() == ")" and "isPopular" in "".join(lines[i-5:i+1]):
            return lines[i+1:]
    return lines

home_header = home_lines[:112] # Keep header for HomeScreen
# But wait, we need to remove the duplicate FastingTab and PlanPreset from home_header
home_header_str = "".join(home_header)
home_header_str = re.sub(r"enum class FastingTab \{.*\}\n", "", home_header_str, flags=re.DOTALL)
home_header_str = re.sub(r"data class PlanPreset\([^)]+\)\n", "", home_header_str, flags=re.DOTALL)

home_body = get_body(home_lines)
circ_body = get_body(circ_lines)
fast_body = get_body(fast_lines)

# Also fix the boundaries:
# home_body ends with "modifier = Modifier.weight(1f).clickable(enabled = isFasting || isFastingScheduled) { onEditStartTimeClick() }"
# Let's add the closing parenthesis and braces that might be missing:
# Wait, let's look at what's missing at the boundary.
# CircularTimerRing.kt body starts with "fun CircularTimerRing("
# That means home_body is missing the closing ) for FastingInfoCard, and } for Row, and } for item!
boundary_fix_1 = "                )\n            }\n        }\n    }\n}\n\n"

# circ_body ends with "haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)"
# fast_body starts with "                    },"
# Nothing is missing there! Just a newline maybe.
boundary_fix_2 = "\n"

with open(os.path.join(base_dir, r"ui/screens/home/HomeScreen.kt"), "w", encoding="utf-8") as f:
    f.write(home_header_str)
    f.writelines(home_body)
    f.write(boundary_fix_1)
    f.write("@Composable\n") # ensure CircularTimerRing has @Composable
    f.writelines(circ_body)
    f.write(boundary_fix_2)
    f.writelines(fast_body)

print("Merged successfully.")
