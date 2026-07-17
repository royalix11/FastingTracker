---
name: compose-no-gray-highlight
description: Enforces the strict rule that gray highlight ripples and touch indication overlays must NEVER be shown anywhere in the FastingTracker Compose UI. Use this skill whenever adding interactive elements, clickable modifiers, buttons, cards, or screens to ensure zero gray highlight ripples occur.
---

# Zero Gray Highlight & No-Ripple Interaction Policy

In FastingTracker's **Obsidian Flux** dark theme, default Compose gray highlight overlays and touch ripples (`Ripple` / `Indication`) obscure the clean obsidian aesthetic and create unwanted visual clutter (as seen in `Gray Highlight-Entire UI.webm`).

**RULE: Gray touch highlight overlays and ripple indications are strictly forbidden across the entire application UI.**

---

## 1. Global Theme Enforcement (`Theme.kt`)

`FastingTrackerTheme` globally overrides `LocalIndication` with a custom `NoIndication` factory so standard Material 3 components (`Card`, `Button`, `NavigationBarItem`, `Surface`) draw zero gray highlight when tapped:

```kotlin
private object NoIndication : androidx.compose.foundation.IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): androidx.compose.ui.node.DelegatableNode {
        return object : androidx.compose.ui.Modifier.Node() {}
    }
    override fun hashCode(): Int = -1
    override fun equals(other: Any?): Boolean = other === this
}
```

Wrapped inside `FastingTrackerTheme`:
```kotlin
CompositionLocalProvider(
    LocalIndication provides NoIndication
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

---

## 2. Reusable Modifier Helper (`noRippleClickable`)

When making any component or container clickable, **NEVER** use plain `.clickable { ... }` or `.clickable(onClick = ...)`. Always use the `noRippleClickable` extension defined in `Theme.kt`:

```kotlin
import com.example.fastingtracker.ui.theme.noRippleClickable

// Correct Usage:
Box(
    modifier = Modifier
        .clip(RoundedCornerShape(50))
        .noRippleClickable { onAction() }
)
```

### Underlying Implementation
```kotlin
fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        enabled = enabled,
        onClick = onClick
    )
}
```

---

## 3. Checklist for All New UI Components & Screens
1. **Never pass default indication to `clickable`**: Ensure `indication = null` is set either via `.noRippleClickable(...)` or explicitly.
2. **Never draw gray overlays**: Avoid using semi-transparent gray backgrounds (`Color.Gray.copy(alpha = ...)`) on pressed/selected states.
3. **Use high-contrast neon or border changes instead**: For active or selected feedback, use `PrimaryNeonYellow` (`#FFDE4D`) borders or text highlights rather than gray background fills.
