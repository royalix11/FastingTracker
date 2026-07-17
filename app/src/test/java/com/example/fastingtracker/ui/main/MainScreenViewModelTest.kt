package com.example.fastingtracker.ui.main

import org.junit.Assert.assertEquals
import org.junit.Test

class MainScreenFormatterTest {

    @Test
    fun testFormatMillis_zeroValue() {
        val formatted = formatMillis(0L)
        assertEquals("00:00:00", formatted)
    }

    @Test
    fun testFormatMillis_underOneHour() {
        val millis = (45 * 60 + 12) * 1000L // 45m 12s
        val formatted = formatMillis(millis)
        assertEquals("00:45:12", formatted)
    }

    @Test
    fun testFormatMillis_multipleHours() {
        val millis = (16 * 3600 + 8 * 60 + 45) * 1000L // 16h 8m 45s
        val formatted = formatMillis(millis)
        assertEquals("16:08:45", formatted)
    }

    @Test
    fun testFormatDuration_onlySeconds() {
        val formatted = formatDuration(45 * 1000L)
        assertEquals("45s", formatted)
    }

    @Test
    fun testFormatDuration_minutesAndSeconds() {
        val formatted = formatDuration((8 * 60 + 15) * 1000L)
        assertEquals("8m 15s", formatted)
    }

    @Test
    fun testFormatDuration_hoursAndMinutes() {
        val formatted = formatDuration((16 * 3600 + 45 * 60 + 12) * 1000L)
        assertEquals("16h 45m", formatted)
    }
}
