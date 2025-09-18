package net.thechance.mena.designsystem.presentation.component.button.content

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
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
        Text(
            text = text,
            style = Theme.typography.label.medium,
            color = contentColor,
            overflow = overflow,
        )
    }

    trailingIcon?.let {
        Icon(
            painter = trailingIcon,
            contentDescription = contentDescription,
            modifier = Modifier
                .padding(start = iconStartPadding)
                .size(iconSize),
            tint = contentColor
        )
    }
}