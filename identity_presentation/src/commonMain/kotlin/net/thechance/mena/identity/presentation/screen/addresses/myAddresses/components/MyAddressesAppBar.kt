package net.thechance.mena.identity.presentation.screen.addresses.myAddresses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_add
import mena.identity_presentation.generated.resources.ic_arrow_left
import mena.identity_presentation.generated.resources.profile_share_icon_content_description
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.designsystem.presentation.util.rippleIndication
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MyAddressesAppBar(
    title: String,
    onBackClicked: () -> Unit,
    onAddClicked: () -> Unit,
) {
    AppBar(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        title = title,
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = "Back",
                tint = Theme.colorScheme.primary.primary
            )
        },
        onLeadingClick = onBackClicked,
        trailingContent = {
            AddIcon(onClick = onAddClicked)
        },
    )
}

@Composable
fun AddIcon(
    onClick: () -> Unit,
) {
    Icon(
        modifier = Modifier
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceLow)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rippleIndication(),
                onClick = onClick
            )
            .padding(10.dp)
            .size(20.dp),
        painter = painterResource(Res.drawable.ic_add),
        tint = Theme.colorScheme.primary.primary,
        contentDescription = stringResource(Res.string.profile_share_icon_content_description),
    )
}

@Preview
@Composable
private fun MyAddressesAppBarPreview() {
    MenaTheme {
        MyAddressesAppBar(
            title = "My Addresses",
            onBackClicked = {},
            onAddClicked = {},
        )
    }
}
