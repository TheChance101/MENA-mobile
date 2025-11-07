package net.thechance.mena.identity.data.utils

import kotlinx.datetime.LocalDate
import org.junit.Test
import kotlin.test.assertEquals

class DateFormatterTest {

    @Test
    fun `formatAsString should format date with year correctly`() {
        val date = LocalDate(2000, 1, 1)

        val result = date.formatAsString()

        assertEquals("2000", result.substring(0, 4))
    }

    @Test
    fun `formatAsString should format date with month correctly`() {
        val date = LocalDate(2000, 1, 1)

        val result = date.formatAsString()

        assertEquals("2000-01-01", result)
    }

    @Test
    fun `formatAsString should format date with day correctly`() {
        val date = LocalDate(2000, 1, 15)

        val result = date.formatAsString()

        assertEquals("2000-01-15", result)
    }

    @Test
    fun `formatAsString should format date with two digit month`() {
        val date = LocalDate(2000, 12, 1)

        val result = date.formatAsString()

        assertEquals("2000-12-01", result)
    }

    @Test
    fun `formatAsString should format date with two digit day`() {
        val date = LocalDate(2000, 1, 31)

        val result = date.formatAsString()

        assertEquals("2000-01-31", result)
    }

    @Test
    fun `formatAsString should use hyphen separator`() {
        val date = LocalDate(2000, 1, 1)

        val result = date.formatAsString()

        assertEquals("2000-01-01", result)
    }

    @Test
    fun `formatAsString should format different year correctly`() {
        val date = LocalDate(2023, 6, 15)

        val result = date.formatAsString()

        assertEquals("2023-06-15", result)
    }
}

