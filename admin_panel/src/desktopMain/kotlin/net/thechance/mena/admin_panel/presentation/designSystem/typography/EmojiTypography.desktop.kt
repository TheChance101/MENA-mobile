package net.thechance.mena.admin_panel.presentation.designSystem.typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.twemoji_mozilla
import org.jetbrains.compose.resources.Font
@Composable
actual fun getEmojiFontFamily(): FontFamily? {
    val osName = System.getProperty("os.name").lowercase()

    return when {
        osName.contains("mac") -> null
        osName.contains("win") || osName.contains("nux") -> {
            FontFamily(
                Font(
                    resource = Res.font.twemoji_mozilla,
                    weight = FontWeight.Normal
                )
            )
        }
        else -> null
    }
}