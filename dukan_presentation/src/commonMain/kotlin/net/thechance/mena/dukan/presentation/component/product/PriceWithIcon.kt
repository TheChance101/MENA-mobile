package net.thechance.mena.dukan.presentation.component.product

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.koin_icon
import mena.dukan_presentation.generated.resources.silver_tc
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PriceWithIcon(
    price: String,
    modifier: Modifier = Modifier,
    iconRes: DrawableResource = Res.drawable.silver_tc,
    iconSize: Dp = 20.dp,
    priceStyle: TextStyle = Theme.typography.label.large,
    contentDescription: String = stringResource(Res.string.koin_icon),
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = price,
            style = priceStyle,
            color = Theme.colorScheme.shadePrimary,
        )

        Image(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            modifier = Modifier
                .padding(start = Theme.spacing._4)
                .size(iconSize)
        )
    }
}
