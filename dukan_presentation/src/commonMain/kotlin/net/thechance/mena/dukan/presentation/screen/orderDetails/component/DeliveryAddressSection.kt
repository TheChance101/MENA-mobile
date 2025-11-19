package net.thechance.mena.dukan.presentation.screen.orderDetails.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.arrow_right_icon
import mena.dukan_presentation.generated.resources.deliver_to
import mena.dukan_presentation.generated.resources.ic_arrow_right
import mena.dukan_presentation.generated.resources.ic_order_location
import mena.dukan_presentation.generated.resources.location
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewOrderDetailsUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun DeliveryAddressSection(
    address: String,
    isUserOwner: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(Res.string.deliver_to),
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(bottom = Theme.spacing._8),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(SquircleShape(Theme.spacing._12))
                .background(Theme.colorScheme.background.surfaceLow)
                .clickable(onClick = onClick, enabled = isUserOwner)
                .padding(Theme.spacing._8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OrderAddressIcon()
            Text(
                text = address,
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadeSecondary,
                modifier = Modifier
                    .padding(start = Theme.spacing._8)
                    .weight(1f)
            )
            if (isUserOwner) {
                Icon(
                    modifier = Modifier.padding(start = Theme.spacing._8),
                    painter = painterResource(Res.drawable.ic_arrow_right),
                    contentDescription = stringResource(Res.string.arrow_right_icon),
                    tint = Theme.colorScheme.primary.primary
                )
            }
        }
    }
}

@Composable
private fun OrderAddressIcon() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(SquircleShape(Theme.spacing._12))
            .background(Theme.colorScheme.background.surfaceHigh)
    ) {
        Icon(
            modifier = Modifier.align(Alignment.Center),
            painter = painterResource(Res.drawable.ic_order_location),
            contentDescription = stringResource(Res.string.location),
            tint = Theme.colorScheme.primary.primary
        )
    }
}

@Preview
@Composable
private fun DeliveryAddressSectionPreview() {
    MenaTheme {
        DeliveryAddressSection(
            address = PreviewOrderDetailsUiState.orderDetailsUiState.orderUiState.addressDeliveryUiState.addressDeliveryTitle,
            isUserOwner = true,
            onClick = {},
        )
    }
}
