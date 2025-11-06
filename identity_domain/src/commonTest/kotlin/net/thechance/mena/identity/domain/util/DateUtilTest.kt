package net.thechance.mena.identity.domain.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class DateUtilTest {

    @Test
    fun `getCurrentDate should return current date`() {
        val expectedDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val expectedLocalDate = LocalDate(expectedDate.year, expectedDate.month, expectedDate.day)
        val result = getCurrentDate()
        assertEquals(expectedLocalDate, result)
    }

    @Test
    fun `getCurrentDate should not be null`() {
        val result = getCurrentDate()
        assertNotNull(result)
    }

    @Test
    fun `getCurrentDate should return LocalDate type`() {
        val result = getCurrentDate()
        assertIs<LocalDate>(result )
    }

    @Test
    fun `getCurrentDate should have correct year`() {
        val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val result = getCurrentDate()
        assertEquals(currentDateTime.year, result.year)
    }

    @Test
    fun `getCurrentDate should have correct month`() {
        val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val result = getCurrentDate()
        assertEquals(currentDateTime.month, result.month)
    }

    @Test
    fun `getCurrentDate should have correct day`() {
        val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val result = getCurrentDate()
        assertEquals(currentDateTime.day, result.day)
    }

    @Test
    fun `DEFAULT_DATE_FORMAT should be correct`() {
        assertEquals("yyyy-MM-dd", DEFAULT_DATE_FORMAT)
    }
}