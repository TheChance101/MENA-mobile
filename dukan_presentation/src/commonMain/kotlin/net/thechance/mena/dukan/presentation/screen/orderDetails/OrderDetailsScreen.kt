@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.screen.orderDetails

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.LocalPlatformContext
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.order_title
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.loading.LoadingDots
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.component.state.NoInternetContent
import net.thechance.mena.dukan.presentation.screen.orderDetails.component.OrderDetailsSuccessContent
import net.thechance.mena.dukan.presentation.util.MapsNavigator
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.util.animation.fadeTransitionSpec
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewOrderDetailsInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewOrderDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.orderDetails.OrderDetailsEffect
import net.thechance.mena.dukan.presentation.viewModel.orderDetails.OrderDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.orderDetails.OrderDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.orderDetails.OrderDetailsViewModel
import net.thechance.mena.identity.domain.util.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun OrderDetailsScreen(
    orderId: Uuid,
    onNavigationBackToChat: () -> Unit,
    viewModel: OrderDetailsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val localContext = LocalPlatformContext.current
    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            OrderDetailsEffect.NavigateBack -> {
                onNavigationBackToChat()
            }

            is OrderDetailsEffect.NavigateToAddressOnMap -> {
                MapsNavigator.getDirections(
                    startLat = effect.startLatitude,
                    startLng = effect.startLongitude,
                    endLat = effect.endLatitude,
                    endLng = effect.endLongitude,
                    context = localContext
                )
            }
        }
    }

    LaunchedEffect(key1 = orderId) {
        viewModel.loadOrderDetails(orderId)
    }

    OrderDetailsContent(
        orderId = orderId,
        state = state,
        interactionListener = viewModel
    )

}

@Composable
private fun OrderDetailsContent(
    orderId: Uuid,
    state: OrderDetailsUiState,
    interactionListener: OrderDetailsInteractionListener
) {

    Scaffold(
        topBar = {
            AppBar(
                title = if (state.orderDetailsScreenState == OrderDetailsUiState.OrderDetailsScreenState.Success)
                    stringResource(Res.string.order_title, state.orderUiState.orderNumber)
                else
                    stringResource(Res.string.order_title, ""),
                titleColor = Theme.colorScheme.shadePrimary,
                contentPadding = PaddingValues(
                    horizontal = Theme.spacing._16,
                    vertical = Theme.spacing._8
                ),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.back_arrow),
                        tint = Theme.colorScheme.primary.primary
                    )
                },
                onLeadingClick = interactionListener::onBackClicked,
            )
        },
        snakeBar = {
            state.snackBarUiState?.let { snackBarState ->
                SnackBar(
                    snackBarUiState = snackBarState,
                    onDismiss = interactionListener::onSnackBarDismissed
                )
            }
        }
    ) {
        AnimatedContent(
            targetState = state.orderDetailsScreenState,
            label = "Order Details Screen State Animation",
            transitionSpec = { fadeTransitionSpec() },
            modifier = Modifier.fillMaxSize()
        ) { orderDetailsState ->
            when (orderDetailsState) {
                OrderDetailsUiState.OrderDetailsScreenState.Loading -> LoadingDots(Modifier.fillMaxSize())
                OrderDetailsUiState.OrderDetailsScreenState.Error -> NoInternetContent(
                    onRetry = { interactionListener.onRetryLoadingOrderDetailsClicked(orderId) },
                    modifier = Modifier.fillMaxSize()
                )
                OrderDetailsUiState.OrderDetailsScreenState.Success -> OrderDetailsSuccessContent(
                    state = state,
                    interactionListener = interactionListener
                )
            }
        }
    }
}

@Preview(name = "Order Details english",locale = "en")
@Preview(name = "Order Details arabic",locale = "ar")
@Composable
private fun OrderDetailsScreenPreviewLightTheme() {
    MenaTheme (
        appTheme = AppTheme.LIGHT.name
    ){
        OrderDetailsContent(
            state = PreviewOrderDetailsUiState.orderDetailsUiState,
            interactionListener = PreviewOrderDetailsInteractionListener,
            orderId = Uuid.random()
        )
    }
}
@Preview(name = "Order Details english",locale = "en")
@Preview(name = "Order Details arabic",locale = "ar")
@Composable
private fun OrderDetailsScreenPreviewDarkTheme() {
    MenaTheme {
        OrderDetailsContent(
            state = PreviewOrderDetailsUiState.orderDetailsUiState,
            interactionListener = PreviewOrderDetailsInteractionListener,
            orderId = Uuid.random()
        )
    }
}