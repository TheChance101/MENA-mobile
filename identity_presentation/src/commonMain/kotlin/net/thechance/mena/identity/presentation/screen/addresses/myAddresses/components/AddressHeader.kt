package net.thechance.mena.identity.presentation.screen.addresses.myAddresses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.address_priority_main
import mena.identity_presentation.generated.resources.address_type_home
import mena.identity_presentation.generated.resources.address_type_office
import mena.identity_presentation.generated.resources.ic_home
import mena.identity_presentation.generated.resources.ic_office
import mena.identity_presentation.generated.resources.ic_other_address
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.domain.entity.AddressType.*
import net.thechance.mena.identity.domain.entity.AddressType.AddressTypeMapper.getAddressType
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AddressHeader(
    addressType: AddressType,
    addressDetails: String,
    isMainAddress: Boolean?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .clip(RoundedCornerShape(Theme.radius.sm))
                .background(Theme.colorScheme.background.surface)
                .size(40.dp)
                .padding(Theme.spacing._8),

            painter = painterResource (mapAddressTypeIcon(addressType)),
            contentDescription = null,
            tint = Theme.colorScheme.primary.primary
        )
        Column(
            Modifier.weight(1f)
        ) {
            Text(
                text = mapAddressTypeTitle(addressType),
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadePrimary,
                maxLines = 1
            )
            Text(
                text = addressDetails,
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeSecondary
            )
        }
        isMainAddress?.let {
            if (it) {
                Text(
                    text = stringResource(Res.string.address_priority_main),
                    style = Theme.typography.label.small,
                    color = Theme.colorScheme.primary.primary
                )
            }
        }
    }
}
@Composable
fun mapAddressTypeTitle(addressType: AddressType): String {
    return when (addressType){
        Home -> stringResource(Res.string.address_type_home)
        Office -> stringResource(Res.string.address_type_office)
        is Other -> addressType.getAddressType()

    }
}
fun mapAddressTypeIcon(addressType: AddressType): DrawableResource {
   return when (addressType){
        Home -> Res.drawable.ic_home
        Office -> Res.drawable.ic_office
       is Other -> Res.drawable.ic_other_address

   }
}

@Preview
@Composable
private fun AddressHeaderPreview() {
    MenaTheme {
        Column {
            AddressHeader(
                addressType = Home,
                addressDetails = "Karrada, Baghdad 123 St.",
                isMainAddress = true
            )
            AddressHeader(
                addressType = Office,
                addressDetails = "Mansour, Baghdad 456 St.",
                isMainAddress = false
            )
        }
    }
}