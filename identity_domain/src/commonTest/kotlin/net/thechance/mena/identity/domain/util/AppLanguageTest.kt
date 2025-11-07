package net.thechance.mena.identity.domain.util

import kotlin.test.Test
import kotlin.test.assertEquals

class AppLanguageTest {

    @Test
    fun `fromIso() should return ENGLISH when given en`() {
        val isoCode = "en"
        val result = AppLanguage.fromIso(isoCode)
        assertEquals(AppLanguage.ENGLISH, result)
    }

    @Test
    fun `fromIso() should return ARABIC when given ar`() {
        val isoCode = "ar"
        val result = AppLanguage.fromIso(isoCode)
        assertEquals(AppLanguage.ARABIC, result)
    }

    @Test
    fun `fromIso() should return ENGLISH for unknown iso code`() {
        val isoCode = "fr"
        val result = AppLanguage.fromIso(isoCode)
        assertEquals(AppLanguage.ENGLISH, result)
    }

    @Test
    fun `fromIso() should be case insensitive`() {
        val isoCode = "EN"
        val result = AppLanguage.fromIso(isoCode)
        assertEquals(AppLanguage.ENGLISH, result)
    }

    @Test
    fun `fromIso() should return ENGLISH for empty string`() {
        val isoCode = ""
        val result = AppLanguage.fromIso(isoCode)
        assertEquals(AppLanguage.ENGLISH, result)
    }
}
