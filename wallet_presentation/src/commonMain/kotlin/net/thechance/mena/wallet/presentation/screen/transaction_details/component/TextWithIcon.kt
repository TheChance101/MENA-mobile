package net.thechance.mena.wallet.presentation.screen.transaction_details.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.img_silver
import mena.wallet_presentation.generated.resources.silver_coin
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun TextWithIcon(
    text: String,
    textStyle: TextStyle,
    textColor: Color,
    icon: Painter,
    iconContentDescription: String,
    iconSize: Dp,
    modifier: Modifier = Modifier,
    iconTint: Color = Color.Unspecified,
    gap: Dp = 4.dp,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = textStyle,
            color = textColor
        )
        Icon(
            painter = icon,
            contentDescription = iconContentDescription,
            modifier = Modifier
                .padding(start = gap)
                .size(iconSize),
            tint = iconTint
        )
    }
}

@Preview
@Composable
private fun TextWithIconPreview() {
    MenaTheme {
        TextWithIcon(
            text = "50000",
            textStyle = Theme.typography.headline.medium,
            textColor = Theme.colorScheme.shadePrimary,
            icon = painterResource(Res.drawable.img_silver),
            iconContentDescription = stringResource(Res.string.silver_coin),
            iconSize = 24.dp,
            gap = 8.dp,
        )
    }
}