package net.thechance.mena.identity.presentation.util

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.profile_language_arabic
import mena.identity_presentation.generated.resources.profile_language_english
import org.junit.Test
import kotlin.test.assertEquals

class LanguageMapperTest {

    @Test
    fun `mapLanguage should return English resource for en ISO code`() {
        val result = mapLanguage("en")

        assertEquals(Res.string.profile_language_english, result)
    }

    @Test
    fun `mapLanguage should return Arabic resource for ar ISO code`() {
        val result = mapLanguage("ar")

        assertEquals(Res.string.profile_language_arabic, result)
    }

    @Test
    fun `mapLanguage should return English resource for unknown ISO code`() {
        val result = mapLanguage("fr")

        assertEquals(Res.string.profile_language_english, result)
    }

    @Test
    fun `mapLanguage should return English resource for empty string`() {
        val result = mapLanguage("")

        assertEquals(Res.string.profile_language_english, result)
    }
}