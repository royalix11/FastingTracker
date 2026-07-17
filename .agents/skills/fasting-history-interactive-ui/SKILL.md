---
name: fasting-history-interactive-ui
description: Enforces interactive temporal range filtering, full-height card edge indicator stripes, gold #E4C535 headers, month-scoped data isolation, and present-day temporal horizon capping in FastingTracker analytics and history screens.
---

# Fasting History & Analytics Interactive UI Protocol

Whenever building, modifying, or extending any History, Analytics, or Timeline screen in **FastingTracker**, you MUST adhere strictly to the following architectural rules:

## 1. Header Typography & Gold Accent (`#E4C535`)
- Page titles for History / Analytics MUST use `"History / Analytics"` rendered in **Geist Bold** (`GeistFont`) with color `#E4C535` (`Color(0xFFE4C535)`).

```kotlin
Text(
    text = "History / Analytics",
    fontFamily = GeistFont,
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp,
    color = Color(0xFFE4C535)
)
```

## 2. Full-Height Card Edge Indicator Strip (`compose-full-edge-indicator`)
- When creating card layouts with colored left-edge status strips (e.g., green `#4CAF50` completion stripes), the accent strip MUST stretch **100% from the top edge to the bottom edge of the card** regardless of content height.
- **Rule:** Wrap the inner card content in a `Row` with `Modifier.height(IntrinsicSize.Min)` and apply `Modifier.fillMaxHeight()` to the stripe `Box`.

```kotlin
Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = CharcoalCard)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) // Critical for child fillMaxHeight()
    ) {
        // Left stripe spanning 100% of card height
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(SuccessGreen)
        )
        // Main content column...
    }
}
```

## 3. Interactive Temporal Range Filtering (`Week` | `Month` | `Year` | `All-Time`)
- Never render static/fixed numbers across different temporal ranges.
- Switching between range selector items (`Week`, `Month`, `Year`, `All-Time`) MUST dynamically recalculate or output distinct outcomes for:
  1. **Hero KPI Cards:** Total Fasts, Success Rate, Current/Best Streak, Average Duration (+ delta), and Autophagy Time.
  2. **Section Titles:** e.g., `"THIS WEEK (PAST 7 DAYS)"`, `"THIS WEEK (JULY 2026)"`, `"2026 MONTHLY HIGHLIGHTS"`, `"ALL-TIME MILESTONES & ANNUAL SUMMARY"`.
  3. **Log Feed Items:** Relevant chronological log entries or aggregate milestone cards for the selected range.

## 4. Interactive Calendar Month Navigation (`YearMonth`) & Month-Scoped Isolation
- Calendar components MUST support real interactive month-over-month navigation via Previous (`<`) and Next (`>`) buttons.
- Maintain state using `java.time.YearMonth` (`var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }`).
- Dynamically calculate first-day-of-week offsets and month length (`currentYearMonth.lengthOfMonth()`) so the calendar grid accurately reflects the displayed month.
- Allow users to tap any day cell to select or deselect (`selectedDay`), displaying visual ring indicators (`ACHIEVED`, `DEEP_FAST`, `PARTIAL`, `SELECTED`).
- **Month-Scoped Data Isolation:** Calendar ring indicators must strictly evaluate against the displayed `YearMonth`. Future months or unlogged historical months MUST display normal unlogged days (`DayStatus.NORMAL`).

## 5. Present-Day Temporal Horizon Guard (`TemporalHorizonGuard`)
- Calendar indicators must strictly evaluate each cell against `LocalDate.now()` (or the present calendar date).
- Any day in the future (`cellDate.isAfter(LocalDate.now())`) MUST return `DayStatus.NORMAL` and never display completed, deep fast, or partial fasting rings.

```kotlin
val cellDate = yearMonth.atDay(day)
val today = LocalDate.now()
if (cellDate.isAfter(today)) {
    return DayStatus.NORMAL
}
```

## 6. Canonical Obsidian Flux Background & Surface Harmony
- The History screen root container MUST use `MaterialTheme.colorScheme.background` (`DarkBackground = Color(0xFF000000)`) instead of custom off-black shades, matching the pure black canvas across all other screens (`HomeScreen`, `PresetsScreen`, etc.).
- Cards and segmented pill containers MUST use standard `DarkSurface` (`Color(0xFF121212)`) and `DarkOutline` (`Color(0xFF1C1C1E)`) borders for unified surface consistency across the application.

## 7. Fluid Directional Calendar Transitions (`AnimatedContent`)
- Calendar month switching (`<` previous / `>` next) MUST use Jetpack Compose `AnimatedContent` with dynamic direction detection (`targetState.isAfter(initialState)`).
- Incoming months must smoothly slide horizontally while outgoing months slide out, combined with a `350ms` cross-fade.

## 8. Viewport-Aware Smart Smooth Scrolling & Border-Only Selection Highlight
- Tapping a calendar date MUST trigger a smooth auto-scroll to locate and focus on the corresponding fast entry in the feed below without causing abrupt viewport shifts.
- Never blindly call `animateScrollToItem(targetIndex)` if the item is already visible below the calendar, as scrolling to `offset = 0` pushes the calendar card offscreen.
- Always inspect `LazyListState.layoutInfo.visibleItemsInfo` first (`smartSmoothScrollToItem` protocol):
  - If already fully visible inside viewport: do not scroll, only highlight the card.
  - If partially below viewport: use `animateScrollBy` to gently bring it into view over `600ms` (`FastOutSlowInEasing`).
  - If above viewport: use `animateScrollBy` to gently scroll up.
- **Card Selection Styling (`isSelected`):** When a log card is selected via calendar day click, highlight the card border with `PrimaryNeonYellow` (`1.5.dp`), but keep the card container color strictly as `DarkSurface` (`#121212`). Do NOT tint or highlight the card's background container color in yellow.
