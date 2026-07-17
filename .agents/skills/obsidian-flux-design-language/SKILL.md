---
name: obsidian-flux-design-language
description: Comprehensive design system and aesthetic guidelines for the FastingTracker Android application, enforcing the Obsidian Flux / Technological Stoicism dark theme, Geist typography system, color tokens, and theme lockdown rules. Use this skill whenever creating, modifying, or reviewing Compose UI layouts, screens, themes, or visual elements in FastingTracker.
---

# Obsidian Flux Design Language & Theme Foundation

This skill defines the visual identity, aesthetic principles, typography architecture, color palette, and theme rules for the FastingTracker application. Every Compose UI modification or new screen implementation MUST conform strictly to these design language specifications.

---

## 1. Core Aesthetic Philosophy: Technological Stoicism & Obsidian Flux

FastingTracker embodies **Technological Stoicism**: a high-contrast, distraction-free, precision-oriented interface designed for clarity, self-mastery, and focus.

- **Dark-First Canvas (Obsidian Flux)**: The interface relies on pure black (`#000000`) and deep charcoal surfaces (`#121212`, `#3C392E`) to eliminate screen glare and visual clutter.
- **Precision Neon Accents**: High-importance callouts, active timers, primary CTAs, and selected states use vivid **Primary Neon Yellow (`#FFDE4D`)** against deep dark surfaces.
- **Zero Decorative Fluff**: Every visual token serves a functional purpose—progress indicators, active fasting states, and precise numerical readouts.

---

## 2. Complete Color Token Reference

All colors are centralized in `com.example.fastingtracker.ui.theme.Color.kt` and consumed via Jetpack Compose `MaterialTheme.colorScheme` or direct semantic tokens.

### Dark Theme Palette (Obsidian Flux - Active Default)
```kotlin
val PrimaryNeonYellow = Color(0xFFFFDE4D)    // Primary accent, active CTA, progress glow
val DarkBackground = Color(0xFF000000)       // Pure Black app canvas
val DarkSurface = Color(0xFF121212)          // Charcoal surface for standard cards
val DarkSurfaceBright = Color(0xFF3C392E)    // Elevated surface / highlighted card background
val DarkOnBackground = Color(0xFFFFFFFF)     // Primary white text on canvas
val DarkOnSurface = Color(0xFFFFFFFF)        // Primary white text on surfaces
val DarkOnSurfaceVariant = Color(0xFFC8C6C5) // Muted secondary text / labels
val DarkOutline = Color(0xFF1C1C1E)          // Subtle structural card / divider borders
val DarkOutlineVariant = Color(0xFF1C1C1E)   // Subtle inner borders

val UnselectedGray = Color(0xFF8E8E93)       // Inactive navigation icons & unselected tabs
val DeepCrimsonRed = Color(0xFF8B0000)       // Secondary progress / warning track indicator
```

### Color Usage Guidelines
- **Primary Text (`DarkOnBackground` / `DarkOnSurface`)**: Use for primary screen headers, card titles, active values, and primary metrics.
- **Secondary Text (`DarkOnSurfaceVariant` / `UnselectedGray`)**: Use for subtitles, descriptions, inactive labels, and historical timestamps.
- **Primary Accent (`PrimaryNeonYellow`)**:
  - Main action buttons (`ButtonDefaults.buttonColors(containerColor = PrimaryNeonYellow, contentColor = Color.Black)`).
  - Active fasting stage markers, active ring tracks, selected navigation bar items.
- **Card Surfaces (`DarkSurface`)**: All feature cards, metric blocks, and section containers use `DarkSurface` with optional `DarkOutline` borders.

---

## 3. Typography Architecture: The Geist Font System

FastingTracker exclusively uses the **Geist** typeface family across the entire application. **No system default sans-serif or default monospace fonts are permitted.**

### Font Family Setup (`Type.kt`)
```kotlin
val GeistFont = FontFamily(
    Font(R.font.geist_bold, FontWeight.Bold),
    Font(R.font.geist_regular, FontWeight.Normal)
)

val GeistMonoFont = FontFamily(
    Font(R.font.geist_mono_bold, FontWeight.Bold)
)
```

### Typography Usage Matrix

| Role / Element | Font Family | Weight | Size / Line Height | Compose `TextStyle` Token |
| :--- | :--- | :--- | :--- | :--- |
| **Hero Timers / Large Metrics** | `GeistFont` | Bold | `48.sp` / `56.sp` | `MaterialTheme.typography.displayLarge` |
| **Section Display / Screen Titles** | `GeistFont` | Bold | `36.sp` / `44.sp` | `MaterialTheme.typography.displayMedium` |
| **Card Headers / Modals** | `GeistFont` | Bold/SemiBold | `24.sp` to `28.sp` | `headlineSmall` / `displaySmall` |
| **Standard Section Titles** | `GeistFont` | Bold | `20.sp` / `28.sp` | `titleLarge` |
| **Body Paragraphs / Descriptions** | `GeistFont` | Normal | `14.sp`–`16.sp` | `bodyMedium` / `bodyLarge` |
| **Precision Numerical Readouts / Pickers** | `GeistMonoFont` | Bold | `12.sp`–`20.sp` | `labelMedium` / Custom Monospace style |
| **Button Labels / Navigation Items** | `GeistFont` | Medium/Bold | `14.sp` | `labelLarge` |

### Strict Typography Rules
1. **Never use `FontFamily.SansSerif` or `FontFamily.Monospace` directly.** Always reference `GeistFont` or `GeistMonoFont` or consume `MaterialTheme.typography.*`.
2. **Geist Mono Rule**: Use `GeistMonoFont` for numerical precision displays:
   - Fasting elapsed / remaining countdown timers.
   - Stage duration labels and timestamps.
   - Wheel time/duration pickers (`WheelTimePicker`).
   - Technical health metrics (blood glucose, ketone levels, weight readouts).

---

## 4. Theme Lockdown & Dark Mode Enforcement

To maintain the high-contrast **Obsidian Flux** aesthetic, the application enforces Dark Mode as the canonical user experience.

### Theme Configuration (`Theme.kt`)
- `FastingTrackerTheme(darkTheme = true)` is applied across `MainActivity` and Compose previews.
- **Theme Settings Row (`MeScreen.kt`)**: While the "Theme Settings" row remains visible in the profile/settings interface for visual completeness, tapping it is intentionally disabled (`onClick = {}`) to prevent accidentally switching to light mode, which breaks the Obsidian Flux contrast harmony.

---

## 5. Spacing, Elevation & Shape Principles

- **Corner Radii**:
  - Feature Cards & Modal Sheets: `RoundedCornerShape(16.dp)` to `24.dp`.
  - Buttons & Interactive Pills: `RoundedCornerShape(12.dp)` or fully stadium-shaped (`CircleShape`).
- **Surface Elevation**: Avoid heavy drop shadows. Rely on surface contrast (`#000000` background vs `#121212` card surface) and subtle 1px outlines (`DarkOutline`) for structural separation.
- **Padding & Alignment**:
  - Screen Horizontal Padding: `16.dp` to `20.dp`.
  - Component Vertical Spacing: Multiples of `4.dp` (`8.dp`, `12.dp`, `16.dp`, `24.dp`).
