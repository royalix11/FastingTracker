---
name: compose-smooth-ring-navigation
description: >-
  Implement interactive circular UI ring touch hit-detection and cinematic slow smooth scrolling in Jetpack Compose. Use when building interactive circular dials, timer rings with clickable radial node icons, or navigating to a list/timeline with delayed, continuous smooth scrolling that synchronizes cleanly with screen transitions.
---

# Compose Smooth Ring Navigation

## Overview

This skill provides architectural and implementation patterns for building interactive radial/circular dials (such as countdown timer rings with perimeter node icons) and connecting them to detail timeline lists with cinematic, slow, continuous smooth scrolling in Jetpack Compose.

## Dependencies

- **Jetpack Compose Foundation (`androidx.compose.foundation`)**: Required for `LazyColumn`, `LazyListState`, `pointerInput`, and gesture detection.
- **Compose Animation Core (`androidx.compose.animation.core`)**: Required for `CubicBezierEasing`.

## Quick Start

### 1. Slow Smooth Cinematic Scrolling Extension (Single Continuous Bezier Loop)

Copy and add this extension function for `LazyListState` to enable cinematic, continuous deceleration scrolling across any distance without intermediate stuttering or end-of-scroll spring jumps:

```kotlin
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.withFrameNanos

/**
 * Smoothly scrolls a LazyColumn/LazyRow to [targetIndex] using a single continuous
 * cubic bezier frame loop inside a scroll block. Eliminates the abrupt end-jump
 * caused by chaining animateScrollBy + animateScrollToItem.
 */
suspend fun LazyListState.slowSmoothScrollToItem(targetIndex: Int) {
    if (targetIndex < 0) return
    val currentIndex = firstVisibleItemIndex
    if (currentIndex == targetIndex && firstVisibleItemScrollOffset == 0) return

    val visibleItems = layoutInfo.visibleItemsInfo
    val avgItemSize = if (visibleItems.isNotEmpty()) {
        visibleItems.map { it.size }.average().toFloat()
    } else {
        500f
    }

    val itemsToScroll = targetIndex - currentIndex
    val estimatedTotalDistance = itemsToScroll * avgItemSize - firstVisibleItemScrollOffset

    // Smooth duration proportional to distance
    val duration = (kotlin.math.abs(itemsToScroll) * 350L).coerceIn(800L, 1800L)
    val startTime = withFrameNanos { it }
    val easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)

    var previousProgress = 0f

    scroll {
        while (true) {
            val now = withFrameNanos { it }
            val elapsedMillis = (now - startTime) / 1_000_000L
            val rawProgress = (elapsedMillis.toFloat() / duration).coerceIn(0f, 1f)
            val easedProgress = easing.transform(rawProgress)
            val deltaProgress = easedProgress - previousProgress
            previousProgress = easedProgress

            val targetItem = layoutInfo.visibleItemsInfo.find { it.index == targetIndex }
            if (targetItem != null) {
                // Once target item enters composition, scroll proportionally to its exact remaining offset
                val remainingOffset = targetItem.offset.toFloat()
                val remainingProgress = 1f - easedProgress
                if (remainingProgress > 0.001f) {
                    val step = remainingOffset * (deltaProgress / remainingProgress)
                    scrollBy(step)
                } else {
                    scrollBy(remainingOffset)
                    break
                }
            } else {
                val step = estimatedTotalDistance * deltaProgress
                scrollBy(step)
            }

            if (rawProgress >= 1f) break
        }
    }
}
```

### 2. Synchronizing Scroll with Screen Transitions

When navigating to a timeline screen, calling scroll animations immediately causes them to finish while the screen transition animation is still entering (making the scroll look instantaneous). Always delay until the screen has settled:

```kotlin
LaunchedEffect(key1 = initialStageIndex) {
    val targetIndex = initialStageIndex ?: 0
    if (targetIndex > 0) {
        // Wait 400ms for screen enter animation so user observes the continuous scroll
        delay(400)
        listState.slowSmoothScrollToItem(targetIndex)
    }
}
```

## Workflow

### Step 1: Radial Touch Hit-Detection (`detectTapGestures`)
To make icons arranged around a circular ring clickable without interfering with central buttons:
1. Wrap the ring canvas/layout in a `Box` with `Modifier.pointerInput(Unit)` using `detectTapGestures`.
2. Convert touch `Offset(x, y)` relative to ring center `(centerX, centerY)` to polar coordinates `(r, theta)`:
   - Calculate distance: `r = hypot(dx, dy)`
   - Filter out touches outside the perimeter ring band: `r in minRadius..maxRadius`.
   - Calculate angle: `theta = (atan2(dy, dx) * 180 / PI + 360) % 360`.
3. Map `theta` to the corresponding node index around the ring perimeter.

### Step 2: Trigger Navigation with Parameter
Pass the detected node index (`stageIndex`) as an argument in your navigation route/key (e.g. `FastingStagesTimeline(initialStageIndex = stageIndex)`).

### Step 3: Delayed Continuous Bezier Scroll
In the destination screen, use `LaunchedEffect(initialStageIndex)` with `delay(400)` and execute `listState.slowSmoothScrollToItem(targetIndex)`.

## Common Mistakes

1. **Chaining `animateScrollBy` + `animateScrollToItem`**:
   - Calling `animateScrollToItem(targetIndex)` after `animateScrollBy` causes a sudden spring snap/jump across the remaining distance at the end of the scroll. Always use a single continuous bezier frame loop inside `listState.scroll { ... }`.
2. **Item-by-item loops (`for (i in start..target) animateScrollToItem(i)`)**:
   - Calling `animateScrollToItem(i)` sequentially stops and starts velocity at every intermediate item, causing severe visual stutter.
3. **Missing `delay(400)` after navigation**:
   - Without a brief delay, the scroll animation runs concurrently with the screen fade/slide-in transition, causing the list to appear already scrolled when the page settles.
