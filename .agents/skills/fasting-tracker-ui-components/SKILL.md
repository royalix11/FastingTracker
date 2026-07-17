---
name: fasting-tracker-ui-components
description: Comprehensive UI component specifications, layout patterns, and screen architecture rules for the FastingTracker Android Compose app. Covers circular progress rings, timeline stage nodes, custom wheel pickers, navigation bars, cards, and buttons. Use this skill when implementing or modifying any visual component or screen layout in FastingTracker.
---

# FastingTracker UI Component Library & Layout Patterns

This skill catalogues the canonical UI components, layout structures, and interactive patterns used across the FastingTracker application. Every component is designed to align with the **Obsidian Flux** theme and **Geist** typography system.

---

## 1. Circular Rings & Stage Nodes (`FastingStagesTimelineScreen` & Home Ring)

Circular progress indicators and stage circles are central visual motifs in FastingTracker.

### Timeline Stage Circles Specification
- **Visual Equality**: In the Fasting Stages timeline, every stage node circle maintains a consistent, substantial diameter (matching the primary fasting stage circle size). Circles must **never** be rendered as tiny decorative dots around larger rings.
- **Node Styling**:
  - **Active / Achieved Stage**:
    - Outer Glow/Ring: `PrimaryNeonYellow` (`#FFDE4D`).
    - Icon / Stage Index: `GeistMonoFont` bold or high-contrast iconography.
  - **Upcoming / Locked Stage**:
    - Border/Outline: `DarkOutline` (`#1C1C1E`) or `UnselectedGray` (`#8E8E93`).
    - Fill: Deep charcoal (`#121212`).
- **Progress Track**: Connecting lines or circular tracks between stages use sleek 2dp–4dp strokes, transitioning smoothly from neon yellow (completed) to muted charcoal/red (remaining).

---

## 2. Numerical Pickers & Time Selection (`WheelTimePicker`)

Fasting duration and hour/minute selections utilize custom vertical wheel pickers (`WheelTimePicker.kt`).

### Wheel Picker Styling Rules
- **Selected Item**:
  - Font: `GeistMonoFont` (`FontWeight.Bold`).
  - Color: `PrimaryNeonYellow` (`#FFDE4D`) or Pure White (`#FFFFFF`).
  - Size: Highlighted typography scale (`20.sp` to `24.sp`).
- **Unselected / Adjacent Items**:
  - Font: `GeistMonoFont` (`FontWeight.Normal`).
  - Color: `DarkOnSurfaceVariant` (`#C8C6C5`) with fading opacity towards top/bottom edges.
- **Haptic & Visual Feedback**: Smooth snapping to discrete intervals with clear center vertical alignment.

---

## 3. Cards & Surfaces Hierarchy

All content blocks (Stats, Preset Plans, Stage Explanations) are wrapped in Compose `Surface` or `Card` components.

### Canonical Card Pattern
```kotlin
Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
        containerColor = DarkSurface // #121212
    ),
    border = BorderStroke(1.dp, DarkOutline) // #1C1C1E
) {
    // Card content with 16.dp internal padding
}
```
- **Highlighted / Featured Card**: Use `DarkSurfaceBright` (`#3C392E`) as the container color or add a subtle `PrimaryNeonYellow` (`#FFDE4D`) left accent bar or border.

### Plan Card Selection & Icon Tint Specification (`PlanCard` Selection Rule)
- **Strict Selection-Only Accent Rule**: In list items and selectable cards (such as Fasting Plan presets in `PresetsScreen`), primary accent coloring (`PrimaryNeonYellow`) on leading icons (e.g., clock/timer icon) and card borders **must strictly depend on `isSelected`**.
- Never color leading icons yellow based on static flags like `plan.isPopular` when the card is unselected (`isSelected == false`).
- **Selected Card (`isSelected == true`)**:
  - Leading Icon Tint: `PrimaryNeonYellow` (`#FFDE4D`).
  - Card Border: `BorderStroke(2.dp, PrimaryNeonYellow)`.
  - Selection Badge: Show `SELECTED` badge in bottom right.
- **Unselected Card (`isSelected == false`)**:
  - Leading Icon Tint: `subtitleColor` / muted neutral (`#8E8E93`).
  - Card Border: `BorderStroke(1.dp, Color.Transparent)`.
  - Popular Badge: If `isPopular == true`, show `POPULAR` badge in bottom right, but keep the card's leading icon neutral.

---

## 4. Interactive Buttons & Actions

### Primary Call-To-Action (CTA) Button
Used for starting/stopping fasts, saving plans, and major confirmations.
```kotlin
Button(
    onClick = { /* action */ },
    modifier = Modifier.fillMaxWidth().height(56.dp),
    shape = RoundedCornerShape(16.dp),
    colors = ButtonDefaults.buttonColors(
        containerColor = PrimaryNeonYellow,
        contentColor = Color.Black
    )
) {
    Text(
        text = "START FASTING",
        fontFamily = GeistFont,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
}
```

### Secondary Button / Outline Pill
Used for secondary choices, editing presets, or viewing details.
- Container Color: `Color.Transparent` or `DarkSurface`
- Border: `BorderStroke(1.dp, UnselectedGray)`
- Text Color: `DarkOnBackground` (`#FFFFFF`)

### Zero Gray Highlight & No-Ripple Clickable Rule
Never use standard `.clickable { ... }` which produces a gray highlight ripple overlay. Always use `Modifier.noRippleClickable { ... }` from `com.example.fastingtracker.ui.theme.noRippleClickable` so that interactive elements show zero gray highlight.

---

## 5. Bottom Navigation Bar Architecture

The primary navigation consists of five distinct destinations managed via `androidx.navigation3`.

### Navigation Bar Visual Specification
- **Background**: Pure Black (`DarkBackground` `#000000`) or Dark Charcoal (`DarkSurface` `#121212`) with a top divider line (`DarkOutline` `#1C1C1E`).
- **Selected Tab**:
  - Icon & Label Color: `PrimaryNeonYellow` (`#FFDE4D`).
  - Label Font: `GeistFont` (`FontWeight.Bold`).
- **Unselected Tab**:
  - Icon & Label Color: `UnselectedGray` (`#8E8E93`).
  - Label Font: `GeistFont` (`FontWeight.Normal`).

### Core Screens & Patterns
1. **Home (`HomeScreen`)**: Features the prominent central fasting timer circle, status readout, and quick action CTA.
2. **Timeline (`FastingStagesTimelineScreen`)**: Chronicles the 10 metabolic fasting stages with equally sized circular stage indicators.
3. **Plan (`PresetsScreen` / `CustomFastingPlanScreen`)**: Lists structured fasting plans (16:8, 18:6, OMAD) inside distinct dark surface cards.
4. **History (`HistoryScreen`)**: Logs completed fasts with Geist Mono duration metrics.
5. **Me (`MeScreen`)**: Profile and app configuration. Enforces dark theme permanence (Theme Settings navigation locked).
