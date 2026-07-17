---
name: compose-circular-ring-stage-sync
description: >-
  Synchronize a circular progress ring head (and active milestone indicators) with visually adjusted stage icons using Piecewise Angular Mapping in Jetpack Compose. Use when circular dials or timer arcs shift milestone icons to prevent visual overlap (e.g. minAngleGap) and need the progress indicator to align precisely at stage milestones.
---

# Compose Circular Ring Stage Synchronization

When designing circular progress dials or fasting/timer rings that feature milestone icons along the perimeter, visual collision prevention (such as `minAngleGap`) often shifts icon positions away from their pure linear time angles (`(stageTime / targetTime) * sweepAngle`).

If the active ring progress is computed linearly (`elapsedTime / targetTime`), the ring head will physically misalign with milestone icons—appearing either behind or ahead of an icon when `elapsedTime == stage.startTime`.

## Best Practice: Piecewise Angular Mapping

To guarantee 100% visual and logical harmony between:
1. The **Ring Head Position** (black dot / glowing tip)
2. The **Milestone Icon Activation State**
3. The **Stage Title / Header Text**

Always compute the ring progress (`targetProgress`) by **piecewise interpolating** along the exact visual angles (`idealAngles`) where milestone icons are drawn on the canvas.

### Implementation Pattern

```kotlin
// 1. Calculate visual angles for each reachable stage milestone (applying visual collision spacing)
val targetHours = targetMillis.toFloat() / 3600000f
val reachableStages = FastingStage.entries.filter { it.startHour <= targetHours }
val effectiveRingHours = if (targetHours >= 168f) 72f else targetHours.coerceAtLeast(0.1f)

val idealAngles = reachableStages.map { 
    (it.startHour.toFloat() / effectiveRingHours) * totalSweepAngle 
}.toMutableList()

// Apply minimum visual angular spacing to prevent overlapping icons
for (i in 1 until idealAngles.size) {
    if (idealAngles[i] - idealAngles[i - 1] < minAngleGap) {
        idealAngles[i] = idealAngles[i - 1] + minAngleGap
    }
}

// 2. Compute progress via Piecewise Angular Mapping
val targetProgress = if (isFasting && reachableStages.isNotEmpty()) {
    val elapsedHours = elapsedMillisProvider().toFloat() / 3600000f
    val visualAngle = when {
        elapsedHours <= reachableStages.first().startHour -> idealAngles.first()
        elapsedHours >= effectiveRingHours -> totalSweepAngle
        else -> {
            var idx = 0
            while (idx < reachableStages.lastIndex && reachableStages[idx + 1].startHour <= elapsedHours) {
                idx++
            }
            if (idx == reachableStages.lastIndex) {
                val h1 = reachableStages[idx].startHour.toFloat()
                val a1 = idealAngles[idx]
                val frac = ((elapsedHours - h1) / (effectiveRingHours - h1)).coerceIn(0f, 1f)
                a1 + frac * (totalSweepAngle - a1)
            } else {
                val h1 = reachableStages[idx].startHour.toFloat()
                val h2 = reachableStages[idx + 1].startHour.toFloat()
                val a1 = idealAngles[idx]
                val a2 = idealAngles[idx + 1]
                val frac = ((elapsedHours - h1) / (h2 - h1)).coerceIn(0f, 1f)
                a1 + frac * (a2 - a1)
            }
        }
    }
    (visualAngle / totalSweepAngle).coerceIn(0f, 360f / totalSweepAngle)
} else 0f
```

## Key Benefits
- **Zero Lag at Milestones**: When `elapsedHours == stage.startHour`, `visualAngle` is exactly `idealAngles[idx]`, placing the ring head dead-center on the stage icon.
- **Consistent Activation**: Stage activation logic (`currentRingAngle >= visualAngle`) triggers exactly at the stage threshold without premature or delayed visual states.
