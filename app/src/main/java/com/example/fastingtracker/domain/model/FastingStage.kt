package com.example.fastingtracker.domain.model

enum class FastingStage(val title: String, val longTitle: String, val startHour: Int, val description: String) {
    BloodSugarIncreases("BLOOD SUGAR RISES", "Anabolic Phase", 0, "Blood sugar rises, insulin spikes to store nutrients. The body uses recent food for energy."),
    BloodSugarDecreases("BLOOD SUGAR FALLS", "Catabolic Phase", 2, "Blood sugar drops to normal. Insulin levels fall. Stomach empties and hunger pangs may begin."),
    BloodSugarNormalizes("NORMAL BLOOD SUGAR", "Stable Blood Sugar", 5, "Blood sugar remains stable. The body begins searching for alternative energy sources."),
    Gluconeogenesis("GLUCONEOGENESIS", "Gluconeogenesis", 8, "Glycogen stores are depleted. The liver starts producing new glucose from amino acids. Fat burning begins slowly."),
    FatBurning("FAT BURNING", "Fat Burning Ramps Up", 12, "Fat burning accelerates. The body starts shifting from glucose dependence to lipid metabolism."),
    Ketosis("KETOSIS", "Ketosis Accelerates", 18, "Fat burning ramps up significantly. Liver converts fatty acids into ketones for brain energy. HGH starts to rise."),
    Autophagy("AUTOPHAGY", "Autophagy Begins", 24, "Cellular recycling starts. The body identifies and breaks down old, damaged proteins and cells."),
    IncreasedGrowthHormones("INC. GROWTH HORMONE", "Growth Hormone Peak", 48, "Growth hormone levels multiply significantly, preserving muscle mass and promoting repair."),
    InsulinSensitivity("INSULIN SENSITIVITY", "Peak Insulin Sensitivity", 54, "Insulin drops to its lowest level. The body becomes highly sensitive to insulin, resetting metabolic health."),
    ImmuneCellRegeneration("IMMUNE CELL REGEN.", "Immune Regeneration", 72, "The body breaks down old immune cells and begins generating new, healthy white blood cells.");

    companion object {
        fun getStageForElapsed(elapsedMillis: Long): FastingStage {
            val hours = elapsedMillis / (1000 * 60 * 60.0)
            return entries.reversed().find { hours >= it.startHour } ?: BloodSugarIncreases
        }
    }
}
