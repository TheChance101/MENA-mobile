package net.thechance.mena.wallet.presentation.screen.transactiondetails.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.date
import mena.wallet_presentation.generated.resources.ic_failed
import mena.wallet_presentation.generated.resources.ic_send
import mena.wallet_presentation.generated.resources.img_silver
import mena.wallet_presentation.generated.resources.silver_coin
import mena.wallet_presentation.generated.resources.status
import mena.wallet_presentation.generated.resources.transaction_id
import mena.wallet_presentation.generated.resources.type
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DetailsSection(
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(shape = RoundedCornerShape(16.dp))
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ){
        Column(Modifier.fillMaxWidth()) {

            TextWithIcon(
                modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally),
                text = "Send",
                textStyle = Theme.typography.label.small,
                textColor = Theme.colorScheme.shadeSecondary,
                icon = painterResource(Res.drawable.ic_send),
                iconContentDescription = "",
                iconTint = Theme.colorScheme.shadeSecondary,
                iconSize = 16.dp,
                gap = 4.dp,
            )

            TextWithIcon(
                modifier = Modifier.padding(vertical = 8.dp).align(Alignment.CenterHorizontally),
                text = "530,320",
                textStyle = Theme.typography.headline.medium,
                textColor = Theme.colorScheme.shadePrimary,
                icon = painterResource(Res.drawable.img_silver),
                iconContentDescription = stringResource(Res.string.silver_coin),
                iconSize = 24.dp,
                gap = 8.dp,
            )

            DetailsInfo(
                title = stringResource(Res.string.status),
                content = "Failed",
                icon = painterResource(Res.drawable.ic_failed),
                iconContentDescription = "",
                iconTint = Theme.colorScheme.error
            )

            DetailsInfo(
                title = stringResource(Res.string.type),
                content = "Transfer",
            )

            DetailsInfo(
                title = "To",
                content = "Ahmed Ali",
            )

            DetailsInfo(
                title = stringResource(Res.string.date),
                content = "23 Aug 2025, 2:15 PM",
            )

            DetailsInfo(
                title = stringResource(Res.string.transaction_id),
                content = "TX-239481",
            )
        }
    }
}

@Composable
private fun TextWithIcon(
    text: String,
    textStyle: TextStyle,
    textColor: Color,
    icon: Painter,
    iconContentDescription: String,
    iconTint: Color = Color.Unspecified,
    iconSize: Dp,
    gap: Dp = 4.dp,
    modifier: Modifier = Modifier
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
            modifier = Modifier.padding(start = gap).size(iconSize),
            tint = iconTint
        )
    }
}

@Composable
private fun DetailsInfo(
    title: String,
    content: String,
    icon: Painter? = null,
    iconContentDescription: String = "",
    iconTint: Color = Theme.colorScheme.success
){
    HorizontalDivider(
        modifier = Modifier.padding(top = 12.dp),
        thickness = 1.dp,
        color = Theme.colorScheme.stroke
    )
    Row (
        modifier = Modifier.padding(top = 12.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary
        )
        icon?.let {
            Icon(
                painter = icon,
                contentDescription = iconContentDescription,
                modifier = Modifier.padding(end = 4.dp).size(20.dp),
                tint = iconTint
            )
        }
        Text(
            text = content,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary
        )
    }
}

@Preview
@Composable
private fun DetailsSectionPreview(){
    MenaTheme {
        DetailsSection()
    }
}