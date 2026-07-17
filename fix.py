import os
import re

base_dir = r"app/src/main/java/com/example/fastingtracker"

# 1. Fix package names
files_to_fix_packages = {
    r"ui/screens/plan/CustomFastingPlanScreen.kt": "package com.example.fastingtracker.ui.screens.plan",
    r"ui/screens/settings/ThemeSettingsScreen.kt": "package com.example.fastingtracker.ui.screens.settings",
    r"ui/screens/timeline/FastingStagesTimelineScreen.kt": "package com.example.fastingtracker.ui.screens.timeline",
    r"ui/components/WheelTimePicker.kt": "package com.example.fastingtracker.ui.components",
    r"ui/screens/plan/PresetsScreen.kt": "package com.example.fastingtracker.ui.screens.plan",
    r"ui/screens/history/HistoryScreen.kt": "package com.example.fastingtracker.ui.screens.history",
    r"ui/screens/me/MeScreen.kt": "package com.example.fastingtracker.ui.screens.me",
    r"ui/screens/home/HomeScreen.kt": "package com.example.fastingtracker.ui.screens.home",
    r"ui/screens/home/MainScreen.kt": "package com.example.fastingtracker.ui.screens.home",
    r"ui/components/CircularTimerRing.kt": "package com.example.fastingtracker.ui.components",
    r"ui/components/FastingInfoCard.kt": "package com.example.fastingtracker.ui.components",
}

for path, correct_pkg in files_to_fix_packages.items():
    full_path = os.path.join(base_dir, path)
    if os.path.exists(full_path):
        with open(full_path, "r", encoding="utf-8") as f:
            content = f.read()
        
        # Replace the wrong package line
        content = re.sub(r"package com\.example\.fastingtracker\..*\n", f"{correct_pkg}\n", content, count=1)
        
        # Add necessary imports if missing
        if "androidx.lifecycle.viewmodel.compose.viewModel" not in content:
            content = content.replace(f"{correct_pkg}\n", f"{correct_pkg}\n\nimport androidx.lifecycle.viewmodel.compose.viewModel\nimport com.example.fastingtracker.ui.viewmodel.FastingViewModel\n")
        if "import com.example.fastingtracker.ui.components.WheelTimePicker" not in content and "CustomFastingPlanScreen" in path:
            content = content.replace(f"{correct_pkg}\n", f"{correct_pkg}\n\nimport com.example.fastingtracker.ui.components.WheelTimePicker\n")
            
        with open(full_path, "w", encoding="utf-8") as f:
            f.write(content)

# 2. Add @Composable to functions missing it
screens_needing_composable = [
    r"ui/screens/plan/PresetsScreen.kt",
    r"ui/screens/history/HistoryScreen.kt",
    r"ui/screens/me/MeScreen.kt",
    r"ui/screens/home/HomeScreen.kt"
]

for path in screens_needing_composable:
    full_path = os.path.join(base_dir, path)
    if os.path.exists(full_path):
        with open(full_path, "r", encoding="utf-8") as f:
            lines = f.readlines()
        
        func_name = path.split("/")[-1].replace(".kt", "")
        
        new_lines = []
        for i, line in enumerate(lines):
            if line.startswith(f"fun {func_name}("):
                if i == 0 or "@Composable" not in lines[i-1]:
                    new_lines.append("@Composable\n")
            new_lines.append(line)
            
        with open(full_path, "w", encoding="utf-8") as f:
            f.writelines(new_lines)

# Fix FastingStagesTimelineScreen error WideNavigationRailValue
full_path = os.path.join(base_dir, r"ui/screens/timeline/FastingStagesTimelineScreen.kt")
if os.path.exists(full_path):
    with open(full_path, "r", encoding="utf-8") as f:
        content = f.read()
    # The error WideNavigationRailValue is caused by some weird replacement? Let's fix that later, we didn't touch it. Oh wait, "stages.lastIndex" might have been affected.
    # Actually, the error was: Cannot access 'fun WideNavigationRailValue.not(): WideNavigationRailValue': it is internal in file.
    # What was WideNavigationRailValue? It's probably related to `!isFasting`. If isFasting is unresolved, it might be interpreted as WideNavigationRailValue for some weird reason.

print("Fixes applied.")
