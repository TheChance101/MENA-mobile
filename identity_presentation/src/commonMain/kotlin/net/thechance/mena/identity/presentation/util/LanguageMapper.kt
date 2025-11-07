package net.thechance.mena.identity.presentation.util

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.profile_language_arabic
import mena.identity_presentation.generated.resources.profile_language_english
import org.jetbrains.compose.resources.StringResource

fun mapLanguage(iso: String): StringResource {
    return when (iso) {
        "en" -> Res.string.profile_language_english
        "ar" -> Res.string.profile_language_arabic
        else -> Res.string.profile_language_english
    }
}