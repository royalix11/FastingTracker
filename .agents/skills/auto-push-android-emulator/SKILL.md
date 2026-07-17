---
name: auto-push-android-emulator
description: Automatically builds, installs, and pushes code changes or new features to the running Android emulator smoothly after every modification. Use this skill whenever implementing any feature, bug fix, or UI modification in an Android project.
---

# Auto-Push to Android Emulator Workflow

Every time any code change, feature implementation, or UI modification is completed, the agent **MUST automatically push the changes to the Android emulator** without waiting for the user to ask.

---

## 1. Automated Push Pipeline

After editing source files or resources, run the following sequential commands:

### Step 1: Incremental Build & Install (`installDebug`)
Compile and install the debug APK onto all connected emulators and devices:
```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"; .\gradlew.bat installDebug
```

### Step 2: Smoothly Relaunch MainActivity on Emulator
Immediately after `installDebug` succeeds, bring the updated app to the foreground on the running emulator:
```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" -s emulator-5554 shell am start -n com.example.fastingtracker/.MainActivity
```
*(If multiple emulators or physical devices are connected, also broadcast the start intent to all active devices).*

---

## 2. Environment & Settings Requirements

No manual user intervention is required during ordinary development as long as:
1. **Android Emulator Running**: An emulator instance (e.g., `emulator-5554`) or physical device is connected (`adb devices`).
2. **JAVA_HOME Configured**: Set automatically by the agent to Android Studio's bundled JDK (`C:\Program Files\Android\Android Studio\jbr`).
3. **ADB Accessible**: Located at `%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe`.

If the emulator is closed or offline when pushing changes, inform the user smoothly so they can start their preferred AVD in Android Studio.
