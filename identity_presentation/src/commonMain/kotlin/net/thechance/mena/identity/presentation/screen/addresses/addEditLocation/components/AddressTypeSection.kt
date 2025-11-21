package net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.address_type
import mena.identity_presentation.generated.resources.home
import mena.identity_presentation.generated.resources.ic_add_location
import mena.identity_presentation.generated.resources.ic_home
import mena.identity_presentation.generated.resources.ic_marker
import mena.identity_presentation.generated.resources.ic_office
import mena.identity_presentation.generated.resources.office
import mena.identity_presentation.generated.resources.other
import mena.identity_presentation.generated.resources.type
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.domain.entity.AddressType
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AddressTypeSection(
    selectedAddressType: AddressType?,
    onClickAddressType: (AddressType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {

        Text(
            text = stringResource(Res.string.type),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier
                .padding(top = Theme.spacing._12, bottom = Theme.spacing._4)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
        ) {
            AddressTypeItem(
                addressIcon = painterResource(Res.drawable.ic_home),
                addressType = stringResource(Res.string.home),
                onAddressClick = { onClickAddressType(AddressType.Home) },
                isSelected = selectedAddressType == AddressType.Home,
            )
            AddressTypeItem(
                addressIcon = painterResource(Res.drawable.ic_office),
                addressType = stringResource(Res.string.office),
                onAddressClick = { onClickAddressType(AddressType.Office) },
                isSelected = selectedAddressType == AddressType.Office,
            )
            AddressTypeItem(
                addressIcon = painterResource(Res.drawable.ic_marker),
                addressType = stringResource(Res.string.other),
                onAddressClick = { onClickAddressType(AddressType.Other("")) },
                isSelected = selectedAddressType is AddressType.Other,
            )
        }
    }
}

@Composable
private fun AddressTypeItem(
    addressIcon: Painter,
    addressType: String,
    isSelected: Boolean,
    onAddressClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Theme.colorScheme.brand.brand
        else Theme.colorScheme.background.surfaceLow
    )
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Theme.colorScheme.primary.onPrimary
        else Theme.colorScheme.primary.primary
    )

    Column(
        modifier = modifier.padding(horizontal = Theme.spacing._8),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._4),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Icon(
            painter = addressIcon,
            tint = iconColor,
            contentDescription = "Address Icon",
            modifier = Modifier
                .size(60.dp)
                .background(color = backgroundColor, shape = CircleShape)
                .clip(CircleShape)
                .clickable { onAddressClick() }
                .padding(19.dp)

        )
        Text(
            text = addressType,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.clickable {
                onAddressClick()
            }
        )
    }

}


@Composable
 fun OtherAddressType(
    selectedAddressType: AddressType?,
    otherAddressType: String?,
    onChangeOtherAddressType: (String) -> Unit,
    modifier: Modifier = Modifier,

    ) {
    AnimatedVisibility(
        visible = selectedAddressType is AddressType.Other,
        enter = expandVertically(
            animationSpec = tween(durationMillis = 500)
        ),
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = 500)
        )
    ) {

        TextField(
            value = otherAddressType ?: "",
            onValueChanged = onChangeOtherAddressType,
            title = stringResource(Res.string.address_type),
            hint = "",
            leadingIcon = painterResource(Res.drawable.ic_add_location),
            modifier = modifier
                .fillMaxWidth()
                .padding(top = Theme.spacing._12),
            maxCharacters = 48
        )
    }
}

@Preview
@Composable
private fun AddressTypeSectionPreview() {
    MenaTheme {
        AddressTypeSection(
            selectedAddressType = AddressType.Home,
            onClickAddressType = {}
        )
    }
}

