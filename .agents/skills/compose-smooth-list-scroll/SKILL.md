---
name: compose-smooth-list-scroll
description: Guidelines and architectural patterns for implementing fluid master-detail scrolling synchronization using Viewport-Aware Smart Smooth Scrolling (animateScrollBy) and cinematic directional sliding transitions using AnimatedContent in Jetpack Compose.
---

# Smooth List Scrolling & Fluid UI Transitions in Jetpack Compose

This skill defines the canonical patterns for creating cinematic, fluid user experiences when synchronizing master-detail views (e.g., calendar selection to scrollable detail feed) and handling state transitions across screens in **FastingTracker**.

---

## 1. Viewport-Aware Smart Smooth Scrolling (`animateScrollBy` + Layout Inspection)

When navigating a scrollable list programmatically based on user selection in a master header or calendar widget:
- **Never use instantaneous snapping (`scrollToItem`)**.
- **Never use naive `animateScrollToItem(index, scrollOffset = 0)`** if a master widget (e.g., calendar card) sits above the feed inside the same scrolling list, because scrolling a detail item to `offset = 0` pushes the master widget completely off the top of the screen.

### Implementation Protocol:
1. Check `listState.layoutInfo.visibleItemsInfo` to determine where the target item is currently positioned.
2. If the target item is **already fully visible inside the viewport** (`itemTop >= 0 && itemBottom <= viewportHeight`), **do not scroll the list**. Apply visual selection highlight only.
3. If the target item is **partially below the bottom of the viewport** (`itemBottom > viewportHeight`), calculate `scrollAmount = itemBottom - viewportHeight + bottomPadding` and scroll gently using `listState.animateScrollBy(scrollAmount, tween(600, FastOutSlowInEasing))`.
4. If the target item is **above the viewport** (`itemTop < 0`), smoothly scroll by `itemTop - topPadding`.
5. Only fallback to `animateScrollToItem(index)` if the item is not currently present in `visibleItemsInfo`.

```kotlin
suspend fun LazyListState.smartSmoothScrollToItem(targetIndex: Int) {
    val visibleItem = layoutInfo.visibleItemsInfo.find { it.index == targetIndex }
    if (visibleItem != null) {
        val viewportHeight = layoutInfo.viewportSize.height
        val itemTop = visibleItem.offset
        val itemBottom = visibleItem.offset + visibleItem.size
        if (itemTop >= 0 && itemBottom <= viewportHeight) {
            // Already fully visible! No scroll needed to prevent sudden shifts.
            return
        } else if (itemBottom > viewportHeight) {
            val scrollAmount = (itemBottom - viewportHeight + 48).toFloat()
            animateScrollBy(scrollAmount, tween(600, easing = FastOutSlowInEasing))
        } else if (itemTop < 0) {
            animateScrollBy((itemTop - 32).toFloat(), tween(600, easing = FastOutSlowInEasing))
        }
    } else {
        animateScrollToItem(targetIndex)
    }
}
```

---

## 2. Fluid Directional Transitions (`AnimatedContent`)

When switching between temporal states such as calendar months (`YearMonth`), prevent abrupt recomposition jumps by wrapping the stateful UI content in `AnimatedContent`.

### Implementation Protocol:
1. Compare `targetState` with `initialState` to determine directionality.
2. For forward navigation (e.g., Next Month `>`): slide incoming content in from the right (`slideInHorizontally { width -> width }`) while sliding outgoing content out to the left (`slideOutHorizontally { width -> -width }`), combined with smooth cross-fading (`fadeIn` / `fadeOut`).
3. For backward navigation (e.g., Previous Month `<`): reverse the horizontal slide directions.

```kotlin
AnimatedContent(
    targetState = currentYearMonth,
    transitionSpec = {
        if (targetState.isAfter(initialState)) {
            (slideInHorizontally(tween(350, easing = FastOutSlowInEasing)) { width -> width } +
                fadeIn(tween(350))) togetherWith
                (slideOutHorizontally(tween(350, easing = FastOutSlowInEasing)) { width -> -width } +
                    fadeOut(tween(350)))
        } else {
            (slideInHorizontally(tween(350, easing = FastOutSlowInEasing)) { width -> -width } +
                fadeIn(tween(350))) togetherWith
                (slideOutHorizontally(tween(350, easing = FastOutSlowInEasing)) { width -> width } +
                    fadeOut(tween(350)))
        }
    },
    label = "calendar_month_slide_transition"
) { displayedMonth ->
    // Render Month Header & Calendar Matrix for displayedMonth
}
```
