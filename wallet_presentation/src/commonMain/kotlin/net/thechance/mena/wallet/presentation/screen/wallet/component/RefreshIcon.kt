package net.thechance.mena.wallet.presentation.screen.wallet.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.ic_refresh
import mena.wallet_presentation.generated.resources.refresh_icon
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.screen.wallet.WalletInteractionListener
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun RefreshIcon(
    interactionListener: WalletInteractionListener,
    modifier: Modifier = Modifier,
) {
    Icon(
        painter = painterResource(Res.drawable.ic_refresh),
        contentDescription = stringResource(Res.string.refresh_icon),
        tint = Theme.colorScheme.primary.primary,
        modifier = modifier
            .size(40.dp)
            .background(
                Theme.colorScheme.background.surfaceLow,
                RoundedCornerShape(Theme.radius.md)
            )
            .clip(RoundedCornerShape(Theme.radius.md))
            .clickable { interactionListener.onRetryLoadBalanceClicked() }
            .padding(10.dp)
    )
}