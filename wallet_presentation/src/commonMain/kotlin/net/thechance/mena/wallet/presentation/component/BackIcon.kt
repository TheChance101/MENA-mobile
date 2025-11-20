package net.thechance.mena.wallet.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BackIcon(
    modifier: Modifier = Modifier,
) {
    Icon(
        painter = painterResource(Res.drawable.ic_arrow_left),
        contentDescription = stringResource(Res.string.back_button),
        tint = Theme.colorScheme.primary.primary,
        modifier = modifier
    )
}