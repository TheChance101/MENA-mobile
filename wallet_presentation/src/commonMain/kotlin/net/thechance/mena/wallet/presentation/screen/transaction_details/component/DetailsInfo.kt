package net.thechance.mena.wallet.presentation.screen.transaction_details.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.transaction_id
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ColumnScope.DetailsInfo(
    title: String,
    content: String,
    icon: Painter? = null,
    iconContentDescription: String = "",
    iconTint: Color = Theme.colorScheme.success
) {
    Box(
        modifier = Modifier
            .padding(vertical = Theme.spacing._12)
            .height(1.dp)
            .fillMaxWidth()
            .background(color = Theme.colorScheme.stroke)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary,
            maxLines = 1
        )
        DetailsInfoContent(
            content = content,
            icon = icon,
            iconContentDescription = iconContentDescription,
            iconTint = iconTint
        )
    }
}

@Composable
private fun DetailsInfoContent(
    content: String,
    icon: Painter? = null,
    iconContentDescription: String = "",
    iconTint: Color = Theme.colorScheme.success
){
    Row {
        icon?.let {
            Icon(
                painter = icon,
                contentDescription = iconContentDescription,
                modifier = Modifier
                    .padding(end = Theme.spacing._4)
                    .size(20.dp),
                tint = iconTint
            )
        }
        Text(
            text = content,
            style = Theme.typography.label.medium,
            overflow = TextOverflow.Ellipsis,
            color = Theme.colorScheme.shadePrimary,
            maxLines = 1,
            textAlign = TextAlign.End
        )
    }
}

@Preview
@Composable
private fun DetailsInfoPreview() {
    MenaTheme {
        Column {
            DetailsInfo(
                title = stringResource(Res.string.transaction_id),
                content = "TX-420366",
            )
        }
    }
}