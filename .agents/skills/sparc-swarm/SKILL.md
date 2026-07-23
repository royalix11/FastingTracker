---
name: sparc-swarm
description: Orchestrates multi-agent parallel development using the SPARC methodology (Specification, Pseudocode, Architecture, Refinement, Completion). Use when the user requests a complex feature across multiple architectural layers (e.g. Room DB + Jetpack Compose UI + ViewModel) or mentions @sparc-swarm.
---

# SPARC Swarm Methodology Protocol (`@sparc-swarm`)

When invoked or when tackling multi-layer features, do not write boilerplate code sequentially across multiple layers in a single thread. Instead, act as the **Swarm Controller** and coordinate specialized background subagents running in parallel using `invoke_subagent`.

## 1. The 5-Stage SPARC Lifecycle

### S — Specification
- Analyze user requirements and clearly define the boundaries of the data layer (`Entity`, `DAO`), business logic (`Repository`, `ViewModel`), and UI layer (`Compose Components`, `Theme`).
- Enforce project-wide global rules (e.g., `Obsidian Flux` theme `#FFDE4D`, `compose-no-gray-highlight`, `Geist / Geist Mono` typography).

### P — Pseudocode & Interface Contracts
- Establish exact method signatures, data structures (`bitmasks`, `flows`), and composable parameter contracts before writing implementation files so subagents can work independently without contract mismatches.

### A — Architecture & Swarm Division
Launch specialized parallel subagents (`invoke_subagent` with `TypeName: self`) dividing tasks strictly along architectural boundaries:
- **Room Database Specialist**: Handles `@Entity`, `@Dao`, Room schema migrations (`fallbackToDestructiveMigration` during dev), and data helpers.
- **Compose UI Specialist**: Handles custom composables, `graphicsLayer` animations, zero-ripple click handlers (`Modifier.noRippleClickable(indication = null)`), and dark obsidian styling.
- **Business/Integration Specialist** (if needed): Wires `Repository` into `ViewModel` and updates navigation/state flows.

### R — Refinement & Sync
- When subagents complete their tasks, verify that interfaces align and verify that zero-ripple policies and neon design tokens were respected across all newly generated files.

### C — Completion & Automated Deployment
Execute the mandatory automated build and emulator push rule:
```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"; .\gradlew.bat installDebug
```
Once build succeeds, smoothly relaunch the activity across all connected emulators without failing on multi-device setups:
```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices | Select-String -Pattern "\sdevice$" | ForEach-Object { $dev = $_.Line.Split("`t")[0].Trim(); & "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" -s $dev shell am start -n com.example.fastingtracker/.MainActivity }
```

## Best Practices for Swarm Prompts
When launching subagents with `invoke_subagent`:
1. Provide absolute file paths for every target file.
2. Embed the exact design tokens and no-ripple modifier syntax inside each subagent prompt so they don't hallucinate default Material grey behavior.
3. Instruct subagents to report back concisely with the exact summary of files created or modified.
