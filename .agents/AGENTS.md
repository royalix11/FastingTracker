# FastingTracker — AI Agent Project Rules & Guidelines

This document defines the architectural, visual, behavioral, and deployment rules for the **FastingTracker** Android application. All AI agents working on this project MUST strictly follow these rules across every interaction.

---

## 1. Automated Android Emulator Deployment Rule

Whenever modifying source code, UI layouts, or resources, **automatically build and push completed changes to the running Android emulator** without waiting for the user to ask:

1. **Incremental Compile & Install**:
   ```powershell
   $env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"; .\gradlew.bat installDebug
   ```
2. **Smooth Foreground Activity Relaunch**:
   ```powershell
   & "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" shell am start -n com.example.fastingtracker/.MainActivity
   ```
*(Always ensure `$env:JAVA_HOME` points to `C:\Program Files\Android\Android Studio\jbr` for Gradle operations).*

---

## 2. Design Language: Obsidian Flux & Technological Stoicism

### Dark-First Canvas (`Color.kt`)
- **Primary Canvas**: Dark-first Obsidian aesthetic (`DarkBackground = #0F0E0A`, `DarkSurface = #1C1B15`).
- **Primary Accent**: Neon Yellow (`PrimaryNeonYellow = #FFDE4D`). Used for primary CTA buttons, active borders, progress rings, glowing indicators, and selected states.
- **Subtle Accents**: `UnselectedGray = #6A685D`, `DarkOutline = #2E2D25`.
- **No Light Theme Switch**: The application is locked to its signature dark obsidian theme. Never reintroduce light theme toggles or white backgrounds.

### Typography System (`Type.kt`)
- **Standard UI & Narrative Text**: Use **Geist** (`GeistFont`) for titles, headers, subtitles, labels, and descriptions.
- **Numerical & Technical Displays**: Use **Geist Mono** (`GeistMonoFont`) for countdown timers, timestamps, wheel pickers (`WheelTimePicker`), fasting duration metrics, and statistics.
- **No Default Fonts**: Never fallback to default system fonts or generic sans-serif. Always reference `GeistFont` or `GeistMonoFont`.

---

## 3. Zero Gray Highlight & No-Ripple Policy (`compose-no-gray-highlight`)

Standard Compose gray touch highlight overlays and ripple indications obscure the clean obsidian aesthetic and are **strictly forbidden** across the entire UI:

1. **Global Theme Override**: `FastingTrackerTheme` provides `LocalIndication provides NoIndication`.
2. **Interactive Elements**: Never use plain `.clickable { ... }`. Always use the custom extension:
   ```kotlin
   import com.example.fastingtracker.ui.theme.noRippleClickable

   Modifier.noRippleClickable { onClick() }
   ```
3. **Active/Selected Feedback**: Indicate selection or active state via `PrimaryNeonYellow` (`#FFDE4D`) borders (`BorderStroke(2.dp, PrimaryNeonYellow)`), text color transitions, or glow effects—never gray background fills.

---

## 4. Canonical UI Component Specifications

### Circular Progress Rings & Stage Nodes
- **Fasting Stages Circular Indicators**: All 10 fasting stage nodes must maintain uniform dimensions (`size(68.dp)`). Active stages display a glowing `PrimaryNeonYellow` ring; locked/future stages display muted dark surface styling.
- **Main Progress Ring**: Smooth animated circular ring with neon yellow arc and circular pointer tip.

### Wheel Pickers (`WheelTimePicker`)
- Precision drum roller styling using `GeistMonoFont`.
- Highlight active center selection rail with high-contrast neon styling and clean top/bottom fade masks.

### Cards & Surfaces
- Containers use `DarkSurface` (`#1C1B15`) with a subtle `1.dp` border of `DarkOutline` (`#2E2D25`) and `RoundedCornerShape(16.dp)`.

---

## 5. Compose Architecture & Code Integrity
- **State Management**: Keep UI components reactive using Compose state (`remember`, `derivedStateOf`, `StateFlow`).
- **Documentation**: Preserve all existing comments and docstrings when editing files unless explicitly requested to modify them.
- **Modular Design**: Reuse components from `com.example.fastingtracker.ui.components` and design tokens from `com.example.fastingtracker.ui.theme`.

---

## 6. SPARC Swarm Protocol (`@sparc-swarm`)
When implementing complex multi-layer features (involving both Data/Room and UI/Compose layers) or when explicitly requested by the user, follow the **SPARC Swarm** methodology:
1. Act as the **Swarm Controller** and decompose the task across architectural layers (`Room Specialist`, `Compose UI Specialist`, etc.).
2. Launch specialized parallel subagents (`invoke_subagent` with `TypeName: self`) so data persistence and UI skills are developed concurrently without file collision.
3. Ensure every subagent prompt explicitly enforces the `compose-no-gray-highlight` zero-ripple policy (`Modifier.noRippleClickable`), `#FFDE4D` neon accents, and `Geist / Geist Mono` typography.
4. Once all subagents complete, run the automated deployment pipeline (`installDebug` + multi-device `adb` push) to verify compilation and immediately display the result on the emulator.

