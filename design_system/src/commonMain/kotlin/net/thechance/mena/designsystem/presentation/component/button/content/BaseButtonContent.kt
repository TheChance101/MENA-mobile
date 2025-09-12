package net.thechance.mena.designsystem.presentation.component.button.content

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import net.thechance.mena.designsystem.presentation.component.icon.MenaIcon
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
internal fun BaseButtonContent(
    text: String?,
    trailingIcon: Painter?,
    contentColor: Color,
    iconSize: Dp,
    iconStartPadding: Dp,
    contentDescription: String? = null,
    overflow: TextOverflow = TextOverflow.Ellipsis,
) {
    text?.let {
        MenaText(
            text = text,
            color = contentColor,
            overflow = overflow,
            style = Theme.typography.label.medium,
        )
    }

    trailingIcon?.let {
        MenaIcon(
            painter = trailingIcon,
            tint = contentColor,
            contentDescription = contentDescription,
            modifier = Modifier
                .padding(start = iconStartPadding)
                .size(iconSize)
        )
    }
}