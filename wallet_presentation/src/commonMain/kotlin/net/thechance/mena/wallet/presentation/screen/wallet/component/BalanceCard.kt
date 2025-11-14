package net.thechance.mena.wallet.presentation.screen.wallet.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.takeOrElse
import kotlinx.coroutines.delay
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.couldnt_load_tap_to_retry
import mena.wallet_presentation.generated.resources.current_balance
import mena.wallet_presentation.generated.resources.ic_reload
import mena.wallet_presentation.generated.resources.img_silver
import mena.wallet_presentation.generated.resources.no_internet_content
import mena.wallet_presentation.generated.resources.reload
import mena.wallet_presentation.generated.resources.silver_coin
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.component.ThreeDotsLoadingIndicator
import net.thechance.mena.wallet.presentation.screen.wallet.WalletScreenState
import net.thechance.mena.wallet.presentation.utils.formatBalance
import net.thechance.mena.wallet.presentation.utils.noRippleClickable
import net.thechance.mena.wallet.presentation.utils.toDp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BalanceCard(
    state: WalletScreenState.BalanceUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = Modifier
            .background(
                color = Theme.colorScheme.background.surface,
                shape = RoundedCornerShape(Theme.radius.xl)
            )
            .border(
                width = 2.dp,
                color = Theme.colorScheme.stroke,
                shape = RoundedCornerShape(Theme.radius.xl)
            )
            .clip(RoundedCornerShape(Theme.radius.xl))
            .then(modifier)
    ) {
        val parentWidthPx = with(LocalDensity.current) { maxWidth.toPx() }

        AnimatedCoinImage(
            isBalanceLoaded = !state.isLoading && state.errorState == null,
            modifier = Modifier
                .padding(top = 12.dp)
                .align(Alignment.TopCenter)
        )

        BalanceInfoSection(
            balance = state.balance,
            isLoading = state.isLoading,
            errorState = state.errorState,
            parentWidthPx = parentWidthPx,
            onRetry = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 120.dp)
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun AnimatedCoinImage(
    isBalanceLoaded: Boolean,
    modifier: Modifier = Modifier
) {
    var isCoinAnimationFinished by remember { mutableStateOf(false) }

    LaunchedEffect(isBalanceLoaded) {
        delay(100)
        isCoinAnimationFinished = isBalanceLoaded
    }

    val offsetY by animateIntAsState(
        targetValue = if (isCoinAnimationFinished) 0 else 300,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "coin_slide_up"
    )

    if (isCoinAnimationFinished) {
        Image(
            painter = painterResource(Res.drawable.img_silver),
            contentDescription = stringResource(Res.string.silver_coin),
            modifier = modifier
                .padding(top = 8.dp)
                .size(200.dp)
                .offset { IntOffset(0, offsetY) }
        )
    }
}

@Composable
private fun BalanceInfoSection(
    balance: Double,
    isLoading: Boolean,
    errorState: ErrorState?,
    parentWidthPx: Float,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val curvedShape = remember(parentWidthPx) {
        TopNotchShape(
            offset = parentWidthPx / 2,
            cutoutWidth = 100.dp,
            cutoutDepth = 26.dp,
            cornerRadius = 24.dp,
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .shadow(elevation = 32.dp, shape = curvedShape)
            .clip(curvedShape)
            .background(color = Theme.colorScheme.background.surface, shape = curvedShape)
    ) {
        BalanceContent(
            balance = balance,
            isLoading = isLoading,
            errorState = errorState,
            onRetry = onRetry,
            modifier = Modifier.padding(top = 45.dp).padding(horizontal = 24.dp)
        )

        Text(
            text = stringResource(Res.string.current_balance),
            style = Theme.typography.label.extraSmall,
            color = Theme.colorScheme.shadeSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 19.dp)
        )
    }
}

@Composable
private fun BalanceContent(
    balance: Double,
    isLoading: Boolean,
    errorState: ErrorState?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val balanceTextStyle = Theme.typography.headline.medium
    val balanceTextHeight = remember {
        balanceTextStyle.lineHeight.takeOrElse { balanceTextStyle.fontSize }.toDp(density)
    }

    Crossfade(
        targetState = balance,
        modifier = modifier.fillMaxWidth().height(balanceTextHeight)
    ) { balanceState ->
        when {
            isLoading -> {
                ThreeDotsLoadingIndicator(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 14.5.dp)
                )
            }

            errorState != null -> {
                val errorMessage = when (errorState) {
                    ErrorState.NoInternet -> stringResource(Res.string.no_internet_content)
                    else -> stringResource(Res.string.couldnt_load_tap_to_retry)
                }
                BalanceErrorContent(
                    errorMessage = errorMessage,
                    onRetry = onRetry
                )
            }

            else -> {
                Text(
                    text = formatBalance(balanceState),
                    style = balanceTextStyle,
                    color = Theme.colorScheme.shadePrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun BalanceErrorContent(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().noRippleClickable { onRetry() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = errorMessage,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.error,
        )
        Icon(
            painter = painterResource(Res.drawable.ic_reload),
            contentDescription = stringResource(Res.string.reload),
            tint = Theme.colorScheme.error,
            modifier = Modifier.padding(start = 8.dp).size(20.dp)
        )
    }
}

@Preview
@Composable
private fun BalanceCardPreview() {
    MenaTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BalanceCard(
                WalletScreenState.BalanceUiState(
                    balance = 0.0,
                    isLoading = true,
                    errorState = null
                ), onRetry = {})
            BalanceCard(
                WalletScreenState.BalanceUiState(
                    balance = 530320.55,
                    isLoading = false,
                    errorState = null
                ), onRetry = {})
            BalanceCard(
                WalletScreenState.BalanceUiState(
                    balance = 0.0,
                    isLoading = false,
                    errorState = ErrorState.NoInternet
                ),
                onRetry = {})
        }
    }
}