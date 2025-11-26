package net.thechance.mena.dukan.presentation.component.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.discount_icon
import mena.dukan_presentation.generated.resources.koin_icon
import mena.dukan_presentation.generated.resources.silver_tc
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.product.PriceWithIcon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun ProductPrice(basePrice: Double, finalPrice: Double) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        if (basePrice > finalPrice) {
            Icon(
                painter = painterResource(Res.drawable.discount_icon),
                contentDescription = stringResource(Res.string.discount_icon),
                tint = Theme.colorScheme.shadePrimary,
                modifier = Modifier.padding(end = Theme.spacing._4)
                    .size(12.dp)
            )

            Text(
                modifier = Modifier
                    .padding(end = Theme.spacing._4)
                    .weight(1f),
                text = basePrice.toString(),
                style = Theme.typography.label.small.copy(
                    textDecoration = TextDecoration.LineThrough
                ),
                color = Theme.colorScheme.shadeTertiary,
                maxLines = 1,
            )
        }

        PriceWithIcon(
            price = finalPrice.toString(),
            iconRes = Res.drawable.silver_tc,
            priceStyle = Theme.typography.label.small,
            iconSize = 16.dp,
            contentDescription = stringResource(Res.string.koin_icon),
        )
    }
}