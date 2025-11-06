package net.thechance.mena.dukan.presentation.screen.checkout.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.deliver_to
import mena.dukan_presentation.generated.resources.desc_edit_address_icon
import mena.dukan_presentation.generated.resources.ic_home
import mena.dukan_presentation.generated.resources.ic_maps_editing
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DeliveryAddressCard(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        DeliveryTitle()
        DeliveryAddressContent()
    }
}

@Composable
private fun DeliveryTitle() {
    Text(
        modifier = Modifier.padding(bottom = Theme.spacing._8),
        text = stringResource(Res.string.deliver_to),
        style = Theme.typography.label.large,
        color = Theme.colorScheme.shadePrimary
    )
}

@Composable
private fun DeliveryAddressContent() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(Theme.spacing._12))
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(Theme.spacing._8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DeliveryAddressIcon()
        DeliveryAddressDetails(
            title = "Home",
            address = "Karrada, Baghdad 123 St."
        )
        EditAddressIcon()
    }
}

@Composable
private fun DeliveryAddressIcon() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(Theme.spacing._12))
            .background(Theme.colorScheme.background.surface)
    ) {
        Icon(
            modifier = Modifier.align(Alignment.Center),
            painter = painterResource(Res.drawable.ic_home),
            contentDescription = ""
        )
    }
}

@Composable
private fun RowScope.DeliveryAddressDetails(title: String, address: String) {
    Column(
        modifier = Modifier
            .padding(start = Theme.spacing._8)
            .weight(1f),
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
    Image(
        modifier = Modifier.padding(end = 6.dp),
        painter = painterResource(Res.drawable.ic_maps_editing),
        contentDescription = stringResource(Res.string.desc_edit_address_icon)
    )
}

@Preview
@Composable
private fun DeliveryAddressCardPreview() {
    MenaTheme {
        DeliveryAddressCard()
    }
}