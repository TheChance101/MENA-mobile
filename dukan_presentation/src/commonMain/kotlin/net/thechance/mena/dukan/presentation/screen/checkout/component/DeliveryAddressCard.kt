@file:OptIn(ExperimentalUuidApi::class)
package net.thechance.mena.dukan.presentation.screen.checkout.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import mena.dukan_presentation.generated.resources.deliver_to
import mena.dukan_presentation.generated.resources.desc_edit_address_icon
import mena.dukan_presentation.generated.resources.ic_home
import mena.dukan_presentation.generated.resources.ic_location
import mena.dukan_presentation.generated.resources.ic_maps_editing
import mena.dukan_presentation.generated.resources.ic_office
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.animation.fadeTransitionSpec
import net.thechance.mena.dukan.presentation.viewModel.checkout.CheckoutUiState
import net.thechance.mena.identity.domain.util.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun DeliveryAddressCard(
    state: CheckoutUiState,
    onChangeAddressClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(bottom = Theme.spacing._8),
            text = stringResource(Res.string.deliver_to),
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(SquircleShape(Theme.radius.md))
                .background(Theme.colorScheme.background.surfaceLow)
                .clickable(onClick = onChangeAddressClicked)
                .padding(Theme.spacing._8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DeliveryAddressIcon(state.deliveryAddress.label)
            DeliveryAddressDetails(
                modifier = Modifier.weight(1f),
                title = state.deliveryAddress.label.name,
                address = state.deliveryAddress.street
            )
            EditAddressIcon()
        }
    }
}

@Composable
private fun DeliveryAddressIcon(
    label: CheckoutUiState.AddressLabel
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(SquircleShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surface),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = label,
            label = "Delivery Address Icon Animation",
            transitionSpec = { fadeTransitionSpec() }
        ) { addressLabel ->
            when (addressLabel) {
                CheckoutUiState.AddressLabel.Home -> {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(Res.drawable.ic_home),
                        contentDescription = "",
                        tint = Theme.colorScheme.primary.primary
                    )
                }

                CheckoutUiState.AddressLabel.Office -> {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(Res.drawable.ic_office),
                        contentDescription = "",
                        tint = Theme.colorScheme.primary.primary
                    )
                }

                CheckoutUiState.AddressLabel.Other -> {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(Res.drawable.ic_location),
                        contentDescription = "",
                        tint = Theme.colorScheme.primary.primary
                    )
                }
            }

        }
    }
}

@Composable
private fun DeliveryAddressDetails(
    title: String,
    address: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(start = Theme.spacing._8),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary
        )
        Text(
            text = address,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeSecondary
        )
    }
}

@Composable
private fun EditAddressIcon() {
    Icon(
        modifier = Modifier.padding(end = 6.dp),
        painter = painterResource(Res.drawable.ic_maps_editing),
        contentDescription = stringResource(Res.string.desc_edit_address_icon),
        tint = Theme.colorScheme.primary.primary
    )
}

@Preview
@Composable
private fun DeliveryAddressCardHomePreview() {
    MenaTheme(
        appTheme = AppTheme.LIGHT.name
    ) {
        DeliveryAddressCard(
            CheckoutUiState(
                deliveryAddress = CheckoutUiState.Address(
                    label = CheckoutUiState.AddressLabel.Home,
                    street = "123 Main St, City, Country"
                )
            ), {})
    }
}

@Preview
@Composable
private fun DeliveryAddressCardWorkPreview() {
    MenaTheme {
        DeliveryAddressCard(
            CheckoutUiState(
                deliveryAddress = CheckoutUiState.Address(
                    label = CheckoutUiState.AddressLabel.Office,
                    street = "123 Main St, City, Country"
                )
            ), {})
    }
}

@Preview
@Composable
private fun DeliveryAddressCardOtherPreview() {
    MenaTheme {
        DeliveryAddressCard(
            CheckoutUiState(
                deliveryAddress = CheckoutUiState.Address(
                    label = CheckoutUiState.AddressLabel.Other,
                    street = "123 Main St, City, Country"
                )
            ), {})
    }
}