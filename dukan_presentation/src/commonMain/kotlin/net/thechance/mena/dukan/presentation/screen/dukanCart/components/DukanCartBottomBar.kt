package net.thechance.mena.dukan.presentation.screen.dukanCart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.go_to_checkout
import mena.dukan_presentation.generated.resources.sub_total_price
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.product.PriceWithIcon
import net.thechance.mena.dukan.presentation.util.formatPrice
import org.jetbrains.compose.resources.stringResource
import sv.lib.squircleshape.SquircleShape

@Composable
fun DukanCartBottomBar(totalPrice: Double, onCheckoutClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SquircleShape(topStart = Theme.radius.xl, topEnd = Theme.radius.xl))
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(Theme.spacing._16),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._12)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(resource = Res.string.sub_total_price),
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadeSecondary,
                modifier = Modifier.weight(1f)
            )
            PriceWithIcon(
                price = totalPrice.toString().formatPrice()
            )
        }
        PrimaryButton(
            text = stringResource(resource = Res.string.go_to_checkout),
            onClick = onCheckoutClick,
            modifier = Modifier
                .fillMaxWidth(),
            isEnabled = totalPrice > 0.0
        )
    }
}