package net.thechance.mena.designsystem.presentation.component.snackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.thechance.mena.designsystem.presentation.component.icon.MenaIcon
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun SnackBar(
    title: String,
    message: String,
    leadingIcon: Painter,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    contentDescription: String? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(
                top = Theme.spacing._8,
                bottom = Theme.spacing._8,
                start = Theme.spacing._12,
                end = Theme.spacing._24
            )
    ) {
        MenaIcon(
            painter = leadingIcon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(28.dp)
        )

        Column {
            MenaText(
                text = title,
                color = Theme.colorScheme.shadePrimary,
                style = Theme.typography.label.large,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )

            MenaText(
                text = message,
                color = Theme.colorScheme.shadeSecondary,
                style = Theme.typography.body.small
            )
        }
    }
}