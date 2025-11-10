package net.thechance.mena.identity.presentation.util

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.profile_theme_dark
import mena.identity_presentation.generated.resources.profile_theme_light
import org.jetbrains.compose.resources.StringResource

fun mapTheme(name: String): StringResource {
    return when (name) {
        "LIGHT" -> Res.string.profile_theme_light
        "DARK" -> Res.string.profile_theme_dark
        else -> Res.string.profile_theme_light
    }
}
