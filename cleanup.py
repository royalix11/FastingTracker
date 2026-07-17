import os
import re

base_dir = r"app/src/main/java/com/example/fastingtracker"

files_to_clean = [
    r"ui/screens/home/MainScreen.kt",
    r"ui/screens/plan/PresetsScreen.kt",
    r"ui/screens/history/HistoryScreen.kt",
    r"ui/screens/me/MeScreen.kt"
]

for path in files_to_clean:
    full_path = os.path.join(base_dir, path)
    if os.path.exists(full_path):
        with open(full_path, "r", encoding="utf-8") as f:
            content = f.read()
        
        content = re.sub(r"enum class FastingTab \{.*\}\n", "", content, flags=re.DOTALL)
        content = re.sub(r"data class PlanPreset\([^)]+\)\n", "", content, flags=re.DOTALL)
        
        with open(full_path, "w", encoding="utf-8") as f:
            f.write(content)

print("Cleanup successful.")
