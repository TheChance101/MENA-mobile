package net.thechance.mena.dukan.presentation.screen.dukanCart.components

import androidx.compose.runtime.Composable
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_to_main_screen_icon
import mena.dukan_presentation.generated.resources.cart
import mena.dukan_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DukanCartTopBar(onBackClick: () -> Unit) {
    AppBar(
        title = stringResource(resource = Res.string.cart),
        titleColor = Theme.colorScheme.shadePrimary,
        leadingContent = {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_arrow_left),
                contentDescription = stringResource(resource = Res.string.back_to_main_screen_icon),
                tint = Theme.colorScheme.primary.primary
            )
        },
        onLeadingClick = onBackClick,
    )
}