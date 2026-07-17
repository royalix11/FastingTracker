---
name: skill-end-fast-button
description: Canonical architectural rules and proportional layout guardrails for primary fasting action buttons (SkillEndFastButton), ensuring symmetrical positioning under the circular timer ring and exact width alignment.
---

# Skill: End Fast Primary Action Button (`SkillEndFastButton`)

This skill defines the canonical layout structure, proportional sizing guardrails, and aesthetic design tokens for the primary action button (`End Fast` / `SkillEndFastButton`) across **FastingTracker**.

---

## 1. Proportional Geometry & Layout Positioning

### Center HUD Preservation
- Never place primary action toggle buttons (`End Fast`) inside the circular progress ring's center HUD when the digital countdown timer (`TimerTextDisplay`) and metabolic stage text (`STAGE: ...`) are displayed.
- The inner ring HUD must remain cleanly preserved for temporal metrics, progress countdowns, and biological stage status only.

### Symmetrical Positioning Under Ring Canvas
- The `SkillEndFastButton` must be positioned directly underneath the `CircularTimerRing` canvas container and sequentially right above the `Row` holding the `STARTED` and `GOAL` informational cards.
- The button must sit symmetrically centered in the horizontal axis (`Alignment.CenterHorizontally`).

### Proportional Width (`ringDiameter / 2`)
- To maintain visual equilibrium with the circular ring geometry (`ringDiameter = 350.dp`), the button width MUST be set to exactly half of the ring's diameter:
  $$\text{buttonWidth} = \frac{\text{ringDiameter}}{2} = 175.\text{dp}$$
- Always assign `Modifier.width(175.dp)` (or dynamically compute `ringDiameter / 2`) with a height of `40.dp`.

---

## 2. Obsidian Flux & Technological Stoicism Aesthetics

### Outlined Pill Shape
- The primary toggle button must use an outlined pill-shape wrapper (`shape = RoundedCornerShape(50)`).
- Border width: `1.dp` using `elapsedColor` (`#FFDE4D` / `PrimaryNeonYellow`).
- Container background color (`buttonBgColor`): `Color.Black.copy(alpha = 0.5f)` in dark mode (or `Color.White` in light mode).

### Zero Gray Highlight Policy (`compose-no-gray-highlight`)
- Standard Compose gray ripple touch highlights are strictly forbidden.
- The button should trigger crisp haptic feedback (`HapticFeedbackType.TextHandleMove`) on tap.

### Typography
- Font Family: `GeistMonoFont`
- Font Size: `14.sp`
- Font Weight: `FontWeight.Medium`
- Letter Spacing: `0.7.sp`
- Text Color: `elapsedColor` (`#FFDE4D` / `PrimaryNeonYellow`)

---

## 3. Usage Example (`HomeScreen`)

```kotlin
// In HomeScreen item containing CircularTimerRing or above STARTED / GOAL Row:
Spacer(modifier = Modifier.height(16.dp))

Box(
    modifier = Modifier.fillMaxWidth(),
    contentAlignment = Alignment.Center
) {
    SkillEndFastButton(
        width = 175.dp,
        onClick = onEndFast
    )
}

Spacer(modifier = Modifier.height(16.dp))

// Row containing STARTED and GOAL cards...
```
