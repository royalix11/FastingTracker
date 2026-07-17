# Qualitative User Feedback & Symptom Logging: Strategic Research & Architectural Blueprint

**Prepared by:** Expert Mobile Product Manager & UX Researcher  
**Target Application Profile:** Sleek, Premium Android Fasting Tracker (`FastingTracker`)  
**Aesthetic Theme:** Obsidian Flux & Technological Stoicism (`#0F0E0A` Dark Background, `#1C1B15` Surface, `#FFDE4D` Neon Yellow Accent, Zero Gray Highlight Policy)  
**Core Tech Stack:** Kotlin, Jetpack Compose, MVVM Architecture, Room Database  

---

## 1. The Competitor Landscape: Strategic Analysis of Top-Tier Fasting Apps

We conducted deep live web research across the industry’s top three fasting applications—**Zero**, **Simple**, and **Fastic**—to analyze how they capture, correlate, and visualize qualitative user check-ins during an active fast.

```mermaid
quadrantChart
    title Qualitative Logging Focus in Market Leaders
    x-axis Low Biological Integration --> High Biological Integration
    y-axis High UX Friction (Multi-Step) --> Low UX Friction (Contextual/Rapid)
    quadrant-1 Best in Class (Goal Zone)
    quadrant-2 Quick but Isolated
    quadrant-3 High Friction & Isolated
    quadrant-4 Deep but Complex
    "Zero": [0.78, 0.75]
    "Simple": [0.65, 0.88]
    "Fastic": [0.45, 0.60]
    "Obsidian Flux Target": [0.92, 0.95]
```

### 1.1 Zero (The Visual & Journaling Standard)
- **UI/UX Delivery Formats:** Zero relies heavily on **automated notification prompts** triggered roughly 2–3 hours after a fast begins or when transitioning across "metabolic zones" (e.g., entering Fat Burning or Ketosis). When opened, it presents a dedicated journaling overlay and mood check-in screen where users select emotional states and add notes.
- **Biological Correlation:** Zero excels at linking subjective feelings directly to its color-coded **Fasting Zones**. It prompts users with targeted reflection questions such as *"How does your focus differ now in Ketosis versus hour 4?"* and plots historical mood trends on a calendar/trends dashboard alongside fasting duration.
- **Strategic Gap:** While visually polished, Zero’s mood history is buried inside the "Me" tab and calendar history, requiring multiple taps to review legacy entries. Furthermore, full-screen check-in modals create a "hard stop" interruption.

### 1.2 Simple (AI-Driven & Contextual Check-ins)
- **UI/UX Delivery Formats:** Simple integrates **contextual quick check-ins** directly onto the primary timer screen and inside an AI-assisted chat window. Instead of static forms, the app asks conversational prompts (*"How is your energy right now? Feeling any hunger spikes?"*).
- **Biological Correlation:** Simple overlays subjective energy logs against fasting schedules (e.g., `16:8` vs. `18:6`) to help users identify personal fatigue thresholds and blood sugar dips.
- **Strategic Gap:** The AI assistant flow can feel conversational but visually cluttered, sacrificing sleek minimalist speed for back-and-forth chat prompts.

### 1.3 Fastic (Holistic Habit & Score Integration)
- **UI/UX Delivery Formats:** Fastic bundles mood check-ins into its **"Fastic Score"** ecosystem alongside sleep, water, and daily habits. Users log mood within profile/daily tracking sections.
- **Biological Correlation:** Rather than granular metabolic symptom tracking (like headache or chills), Fastic ties feelings to general vitality and streak continuity.
- **Strategic Gap:** Fastic lacks a dedicated physical symptom check-in (forcing many users to pair it with third-party apps like Bearable for physical side effects).

---

## 2. Biological & Algorithmic Correlation: Metabolic Timeline Mapping

To make qualitative logging biologically meaningful, our tracker must correlate subjective user inputs with the **5 Objective Metabolic Fasting Stages**. When users open the check-in sheet, the UI adapts its suggested tags and micro-prompts dynamically based on elapsed fasting hours.

| Fasting Stage | Elapsed Window | Biological State & Primary Fuel | Expected Symptoms & Mood Patterns | Adaptive Check-in Prompts |
| :--- | :--- | :--- | :--- | :--- |
| **1. Fed State** | `0h – 4h` | Elevated insulin & blood glucose; digesting nutrients. | Satiety, steady energy, occasional lethargy post-meal. | *"Digestive ease check: Are you feeling balanced or lethargic after your last meal?"* |
| **2. Early Fasting** | `4h – 16h` | Blood glucose & insulin drop; liver glycogen breakdown begins. | **Hunger Spikes**, sugar cravings, irritability ("hangry"), mild headache. | *"Blood sugar dip alert: Are you experiencing hunger spikes or mild headaches? Drink 500ml water + electrolytes."* |
| **3. Fat Burning** | `16h – 24h` | Glycogen depleted; lipolysis surges; free fatty acids release. | Hunger subsides due to ketone initiation; shift toward clean mental clarity. | *"Metabolic shift: Notice hunger fading? Log your focus and physical lightness."* |
| **4. Ketosis** | `24h – 72h` | Ketone bodies (`BHB`) become primary brain fuel. | **High Focus**, mental sharpness, sustained energy, potential cold hands/chills. | *"Peak Ketosis entered: Are you experiencing mental sharpness or cold extremities?"* |
| **5. Autophagy** | `24h+ / 48h+` | Deep cellular cleanup & repair; growth hormone elevation. | Deep calm, physical lightness, or potential electrolyte fatigue/dizziness. | *"Deep cellular restoration: Log physical sensations. Remember to dose sodium/potassium."* |

---

## 3. Feature & Skill Recommendation Matrix (Antigravity Skills Protocol)

To maintain a clean, modular architecture and prevent bloating primary screen composables (`DashboardScreen.kt`), each feature is encapsulated as a standalone **Antigravity Skill**.

| Proposed Feature Component | Target User Impact | Technical Complexity | Proposed Antigravity Skill File Name & Architectural Purpose |
| :--- | :--- | :--- | :--- |
| **Adaptive Metabolic Bottom Sheet** | **High** | Medium (`Compose` + `Draggable`) | `SkillFeelingSelectorSheet.kt`<br>An adaptive `ModalBottomSheet` (or custom gesture-driven drag sheet) that slides up over the dashboard timer. Presents stage-relevant prompt headers and zero-friction check-in controls without navigating away from the timer. |
| **Tactile Symptom Tag Selector** | **High** | Medium (`Bitmask` + `FlowLayout`) | `SkillSymptomBitmaskSelector.kt`<br>A multi-select tag grid featuring glowing neon yellow outlines when active (`compose-no-gray-highlight`). Maps selections directly into an integer bitmask (`Long`) for instant Room persistence. |
| **Rotary Energy/Mood Dial** | **High** | High (`pointerInput` + `graphicsLayer`) | `SkillRotaryEnergyDial.kt`<br>A custom circular drag wheel or haptic slider allowing users to dial their energy from 1 to 10. Uses raw `pointerInput` and `graphicsLayer { rotationZ }` to avoid recomposition lag. |
| **Timeline Correlation Overlay** | **Medium** | Medium (`Canvas` + `Room Flow`) | `SkillMoodTimelineOverlay.kt`<br>A custom Canvas graph that overlays historical mood scores (neon yellow dots) directly along the active fasting timer progress ring and weekly history charts. |
| **Stage-Aware Notification Engine** | **Medium** | Low (`WorkManager` + `Room`) | `SkillStagePromptCoordinator.kt`<br>A background coordinator that monitors elapsed timer state and triggers localized check-in prompts right as the user crosses into Fat Burning (`16h`) or Ketosis (`24h`). |

---

## 4. The "Black & Yellow" Edge: Bespoke Visual & Gamified Concepts

To elevate our app from a standard utility into a state-of-the-art **Obsidian Flux** tool, we introduce three highly customized visual twists that strictly enforce our user rules (`compose-no-gray-highlight`, `#FFDE4D` primary accent, Geist/Geist Mono typography):

### Twist 1: The "Metabolic Pulse" Ring & Orbital Check-in Nodes
Instead of burying check-ins inside a menu, small glowing **Orbital Nodes** (`#FFDE4D`) spawn directly on our custom thick circular timer ring at key metabolic milestones (`12h`, `16h`, `24h`).
- **Tactile Micro-Interaction:** Tapping an orbital node on the ring emits no gray ripple (`Modifier.noRippleClickable(indication = null)`). Instead, the ring pulses with a neon yellow glow, and our `SkillFeelingSelectorSheet` smoothly expands from the bottom.
- **Visual State:** Once a check-in is logged, the node transforms into a glowing icon representing the logged state (e.g., a neon lightning bolt for high focus, a water drop for hunger/electrolytes), turning the progress ring into a living metabolic diary.

```
       .-'''''-.
     .'  16h (Node: Logged Focus ⚡)
    /     .---.     \
   |     /     \     | <-- Thick Obsidian Progress Ring
   |    |  TIMER|    |     with glowing neon yellow fill (#FFDE4D)
   |     \     /     |
    \     '---'     /
     '.  24h (Node: Pulse Check-in) .'
       '-.....-'
```

### Twist 2: High-Contrast Cyber-Stoic Bitmask Tag Selector
When selecting symptoms (Headache, Chills, Hunger Spike, Deep Focus, Euphoria), we reject standard rounded gray pills.
- **Aesthetic:** We use sharp, geometrical angled chips (`CutCornerShape(4.dp)`) with `#1C1B15` solid dark surface fills.
- **Active State:** When toggled ON, the border instantly illuminates in **100% Neon Yellow (`#FFDE4D`)** with a subtle outer glow shadow (`Modifier.drawBehind { drawRect(...) }`), accompanied by crisp Geist Mono typography and haptic vibration (`HapticFeedbackType.LongPress`).

### Twist 3: The Rotary "Kinetic Energy Dial"
Instead of a standard horizontal bar slider (which feels generic on mobile), we implement a circular kinetic rotary dial in `SkillRotaryEnergyDial.kt`.
- **Interaction:** Users place their thumb on a neon yellow track and rotate smoothly around the center (`detectDragGestures`).
- **Typography Integration:** As the dial turns, a prominent numeric readout (`1.0` to `10.0`) renders in **Geist Mono** at the center of the sheet, instantly shifting color temperature from muted amber (`#FFB300`) to high-voltage neon yellow (`#FFDE4D`) at peak energy levels.

---

## 5. Data Architecture & Room Persistence Blueprint

To ensure zero storage lag and instant reactive UI rendering during frequent check-ins, we employ a hybrid storage schema:
1. **Bitmask Encoding for Fixed Symptoms:** Fast, zero-join filtering and minimal disk footprint (`Long` bitmask).
2. **Normalized Cross-References / Optional JSON (`@TypeConverter`) for Custom Notes:** Clean expansion without schema locking.

### 5.1 Symptom Bitmask Definitions (`SkillSymptomBitmaskSelector.kt`)
Each standard physical sensation or symptom is assigned a unique bit value:

```kotlin
object SymptomBitmask {
    const val NONE: Long          = 0L
    const val HUNGER_SPIKE: Long  = 1L << 0  // 1
    const val HEADACHE: Long      = 1L << 1  // 2
    const val FATIGUE: Long       = 1L << 2  // 4
    const val HIGH_FOCUS: Long    = 1L << 3  // 8
    const val CHILLS: Long        = 1L << 4  // 16
    const val EUPHORIA: Long      = 1L << 5  // 32
    const val DIZZINESS: Long     = 1L << 6  // 64
    const val CRAVINGS: Long      = 1L << 7  // 128

    fun hasSymptom(mask: Long, symptom: Long): Boolean = (mask and symptom) == symptom
    fun addSymptom(mask: Long, symptom: Long): Long = mask or symptom
    fun removeSymptom(mask: Long, symptom: Long): Long = mask and symptom.inv()
}
```

### 5.2 Room Entity & DAO Blueprint

```kotlin
@Entity(
    tableName = "qualitative_logs",
    indices = [
        Index(value = ["sessionId"]),
        Index(value = ["timestampEpochMs"])
    ]
)
data class QualitativeLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    
    /** Foreign key linking this check-in to the active or historical FastingSession */
    val sessionId: Long,
    
    /** UTC timestamp in milliseconds (standard Long epoch for instant range sorting) */
    val timestampEpochMs: Long,
    
    /** Elapsed fasting duration at the exact moment of check-in (in minutes) */
    val elapsedMinutesAtLog: Int,
    
    /** Biological Fasting Stage ID (1 = Fed, 2 = Early Fasting, 3 = Fat Burning, 4 = Ketosis, 5 = Autophagy) */
    val metabolicStageId: Int,
    
    /** Subjective mood rating from 1 to 5 (or 1 to 10 energy scale) */
    val energyScore: Int,
    
    /** Bitmask of selected physical symptoms (SymptomBitmask combined flags) */
    val symptomBitmask: Long,
    
    /** Optional user reflection notes */
    val userNotes: String? = null
)
```

### 5.3 Reactive Room DAO (`QualitativeLogDao.kt`)

```kotlin
@Dao
interface QualitativeLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: QualitativeLogEntity): Long

    /** Reactive stream for drawing real-time dots along the active progress ring */
    @Query("SELECT * FROM qualitative_logs WHERE sessionId = :sessionId ORDER BY timestampEpochMs ASC")
    fun observeLogsForSession(sessionId: Long): Flow<List<QualitativeLogEntity>>

    /** High-speed bitmask filter: Find all logs where user experienced headache or dizziness */
    @Query("SELECT * FROM qualitative_logs WHERE (symptomBitmask & :targetMask) > 0 ORDER BY timestampEpochMs DESC")
    fun observeLogsWithSymptoms(targetMask: Long): Flow<List<QualitativeLogEntity>>

    /** Analytics query: Average energy score during peak Ketosis (Stage 4) across all fasts */
    @Query("SELECT AVG(energyScore) FROM qualitative_logs WHERE metabolicStageId = 4")
    fun observeAverageKetosisEnergy(): Flow<Float?>
}
```

---

## 6. Summary of Execution Roadmap

With this strategic research and architectural blueprint finalized, our autonomous execution path when implementing these features into the codebase consists of:
1. **Phase 1 (Data Layer):** Implement `SymptomBitmask`, `QualitativeLogEntity`, and `QualitativeLogDao` inside Room DB.
2. **Phase 2 (Skills UI - Sheet & Controls):** Build `SkillFeelingSelectorSheet.kt`, `SkillSymptomBitmaskSelector.kt`, and `SkillRotaryEnergyDial.kt` using strict `compose-no-gray-highlight` (`Modifier.noRippleClickable(indication = null)`), `#FFDE4D` neon accents, and Geist/Geist Mono typography.
3. **Phase 3 (Dashboard & Ring Integration):** Connect `SkillMoodTimelineOverlay.kt` to spawn clickable orbital nodes along our thick progress ring, creating an immersive, gamified **Obsidian Flux** check-in experience.
