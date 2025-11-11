package net.thechance.mena.identity.presentation.util

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_theme
import mena.identity_presentation.generated.resources.ic_dark_theme
import mena.identity_presentation.generated.resources.profile_theme_dark
import mena.identity_presentation.generated.resources.profile_theme_light
import net.thechance.mena.identity.domain.util.AppTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

fun mapThemeStringResource(name: String): StringResource {
    return when (name) {
        AppTheme.LIGHT.name -> Res.string.profile_theme_light
        AppTheme.DARK.name -> Res.string.profile_theme_dark
        else -> Res.string.profile_theme_light
    }
}
fun mapThemeDrawableResource(name: String): DrawableResource {
    return when (name) {
        AppTheme.LIGHT.name -> Res.drawable.ic_theme
        AppTheme.DARK.name -> Res.drawable.ic_dark_theme
        else -> Res.drawable.ic_theme
    }
}
