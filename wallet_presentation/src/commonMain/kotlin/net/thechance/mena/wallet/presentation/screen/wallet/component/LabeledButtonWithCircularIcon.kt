package net.thechance.mena.wallet.presentation.screen.wallet.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.ic_clock
import mena.wallet_presentation.generated.resources.transactions_history
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LabeledButtonWithCircularIcon(
    icon: Painter,
    contentDescription: String?,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.colorScheme.background.surfaceLow)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            modifier = Modifier
                .size(48.dp)
                .background(color = Theme.colorScheme.background.surface, shape = CircleShape)
                .padding(14.dp),
            painter = icon,
            contentDescription = contentDescription,
            tint = Theme.colorScheme.shadeSecondary
        )
        Text(
            text = label,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadePrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun LabeledButtonWithCircularIconPreview() {
    MenaTheme {
        LabeledButtonWithCircularIcon(
            icon = painterResource(Res.drawable.ic_clock),
            contentDescription = stringResource(Res.string.transactions_history),
            label = stringResource(Res.string.transactions_history),
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        )
    }
}