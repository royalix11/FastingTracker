# Competitive Research & UX Design Report: Circular Timer Ring Interior Architecture

**Project**: FastingTracker  
**Design System**: Obsidian Flux & Technological Stoicism  
**Date**: July 2026  
**Document Path**: `docs/circular_ring_interior_ux_research.md`  

---

## 1. Executive Summary

The circular timer ring is the focal element and emotional center of the FastingTracker experience. As users glance at the app dozens of times throughout a fast, the interior space of the ring must balance **rich contextual feedback** with **technological stoicism and visual breathability**.

This research evaluates top-tier market competitors (*Zero*, *Life Fasting Tracker*, *Fastic*, *Simple*, *BodyFast*, and *Kompanion*) to identify high-value data points, interactive micro-affordances, and biological indicators that can be elegantly displayed inside the main circular ring without overwhelming the user.

---

## 2. Competitive Landscape Analysis

| App | Ring Interior Elements | Interactive Features | Key Takeaway / UX Highlight |
|---|---|---|---|
| **Zero** | Large Elapsed/Remaining Time, Target End Time (`8:00 PM`), Stage Name pill, Percent Complete ring. | Tap time to toggle countdown/countup; tap stage pill to view stage details. | Extremely clean vertical hierarchy; time remains king. |
| **Life Fasting Tracker** | Elapsed Time, Active Flame/Metabolic icons, Biological Milestones (Glucose, Ketones, Autophagy). | Tap milestone icons to view science modal; tap center to edit fast. | Focuses heavily on science badges and biological stage transitions. |
| **Fastic** | Countdown Time, Current Phase Title, Water Quick-Log button, Estimated Fat Burned (grams/kcal). | Tap water glass to log +250ml; tap phase title to view timeline. | Integrates active tracking actions (water, mood) directly inside dial. |
| **Simple** | Dual Time (Elapsed & Goal), Dynamic Progress Pill ("Phase 2 of 4"), Mood Emoji indicator. | Tap progress pill to open full journey; swipe ring interior for stats. | Focuses on psychological encouragement and stage progression. |
| **BodyFast** | Remaining Time, Target End Time, Next Stage Countdown ("Autophagy in 1h 20m"). | Tap timer to swap remaining/elapsed; tap next stage for notification setting. | High motivation via "Next Stage Countdown". |

---

## 3. High-Value Elements for FastingTracker Ring Interior

Based on user research and competitive benchmarks, the interior of our circular ring can accommodate **5 core data zones** arranged cleanly in a vertical stack while preserving the Geist & Geist Mono typographic aesthetic:

```
                  [ Zone 1: Active Stage Icon + Outer Thin Ring ]
                            (70.dp Circle @ y = -70.dp)
                                         │
                  [ Zone 2: Next Stage Preview / Biological Milestone ]
                       "Next: Ketosis in 1h 45m" (12.sp Geist)
                                         │
                  [ Zone 3: Main Display Timer (Geist Mono 48.sp) ]
                                    "167:59:58"
                                         │
                  [ Zone 4: Mode Label & Percentage Badge ]
                       "COUNTDOWN • 85%" (12.sp Geist Mono)
                                         │
                  [ Zone 5: Target End Time & Estimated Fat Burn ]
                      "Ends Today at 8:00 PM  •  ~420g Fat"
```

---

## 4. Detailed Breakdown of Proposed Interior Features

### Feature 1: "Next Biological Milestone" Countdown Pill
* **Concept**: Displays a subtle text indicator directly above or below the main timer showing when the *next* metabolic stage will be reached (e.g. `Next: Autophagy in 2h 15m`).
* **Value**: Gives the user a tangible short-term goal to look forward to rather than focusing solely on the total fast duration.
* **Styling**: `12.sp` Geist Mono in `PrimaryNeonYellow.copy(alpha = 0.8f)`.

### Feature 2: Target End Time Sub-Header
* **Concept**: Displays the calculated completion timestamp (e.g. `Ends Today at 8:00 PM` or `Ends Tomorrow, 7:30 AM`).
* **Value**: Eliminates mental math by answering "When can I eat next?" at a single glance.
* **Styling**: `13.sp` Geist in `onSurfaceVariant.copy(alpha = 0.6f)`.

### Feature 3: Estimated Fat / Calorie Metabolism Indicator
* **Concept**: Real-time calculated estimate of lipid metabolism based on fast duration (e.g. `~350g Fat Burned`).
* **Value**: Provides positive feedback and tangible validation of metabolic progress.
* **Styling**: `11.sp` Geist Mono with subtle flame icon tint (`#FF9500`).

### Feature 4: Interactive Micro-Actions (Inline Water & Mood Logging)
* **Concept**: Small, semi-transparent quick-action badges on the left and right flanks inside the ring:
  - **Left Flank**: `+💧 250ml` quick water logger.
  - **Right Flank**: `⚡ Energy` / `😊 Mood` quick-tap selector.
* **Value**: Increases daily app engagement without requiring navigation to sub-screens.

### Feature 5: Progress Percentage Ring Accent
* **Concept**: Small numeric completion percentage badge (e.g. `78%`) below the main timer digits.
* **Value**: Secondary progress metric for users who prefer percentage-based milestone tracking over raw hours.

---

## 5. Architectural Implementation Strategy (Jetpack Compose)

```kotlin
// Proposed Interior Content Structure for CircularTimerRing
Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier.fillMaxSize().zIndex(2f)
) {
    // 1. Active Stage Icon (70.dp Circle)
    ActiveStageIconBadge(...)

    Spacer(modifier = Modifier.height(6.dp))

    // 2. Next Milestone Preview
    Text(
        text = "Next: Ketosis in 1h 24m",
        style = TextStyle(fontSize = 12.sp, color = PrimaryNeonYellow.copy(alpha = 0.85f))
    )

    // 3. Central Main Timer (Countup / Countdown)
    TimerTextDisplay(...)

    // 4. Target End Time & Percentage
    Text(
        text = "Ends Today at 8:00 PM • 85%",
        style = TextStyle(fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
    )
}
```

---

## 6. Recommendations & Implementation Phasing

1. **Phase 1 (Immediate Quick Wins)**:
   - Add **Target End Time** (`Ends Today at X:XX PM`) directly under the main timer digits.
   - Add **Next Stage Countdown** (`Next: [Stage] in Xh Ym`) above the main timer digits.

2. **Phase 2 (Metabolic & Engagement Expansion)**:
   - Integrate **Estimated Fat Burned** indicator (`~Xg Fat Burned`).
   - Add **Quick Hydration Button** (+250ml) inside the ring flank.

3. **Phase 3 (Customization)**:
   - Allow users in Settings to select their primary interior metrics preference (Time Only vs. Full Science View).
