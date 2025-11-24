package net.thechance.mena.dukan.presentation.screen.checkout

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.cash.paging.compose.collectAsLazyPagingItems
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.summary_details
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.component.loading.LoadingDots
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.component.state.NoInternetContent
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.checkout.component.CheckoutAppBar
import net.thechance.mena.dukan.presentation.screen.checkout.component.CheckoutSummaryCard
import net.thechance.mena.dukan.presentation.screen.checkout.component.ConfirmOrderButton
import net.thechance.mena.dukan.presentation.screen.checkout.component.DeliveryAddressCard
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.viewModel.checkout.CheckoutEffect
import net.thechance.mena.dukan.presentation.viewModel.checkout.CheckoutInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.checkout.CheckoutUiState
import net.thechance.mena.dukan.presentation.viewModel.checkout.CheckoutViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current

    LaunchedEffect(Unit) {
        viewModel.loadDeliveryAddress()
    }
    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            CheckoutEffect.NavigateBack -> {
                navController.popBackStack()
            }

            CheckoutEffect.NavigateToChangeLocation -> {
                navController.navigate(DukanRoute.AddressesRoute)
            }

            is CheckoutEffect.NavigateToConfirmPayment -> {
                navController.navigate(
                    DukanRoute.ConfirmPaymentScreenRoute(
                        effect.transactionId,
                        state.dukanId
                    )
                )
            }
        }
    }
    CheckoutContent(state, viewModel)
}

@Composable
private fun CheckoutContent(
    state: CheckoutUiState,
    listener: CheckoutViewModel
) {
    AnimatedContent(
        targetState = state.checkoutStatus
    ) { targetState ->
        when (targetState) {
            CheckoutUiState.CheckoutStatus.LOADING -> {
                LoadingDots(modifier = Modifier.fillMaxSize())
            }

            CheckoutUiState.CheckoutStatus.LOADED -> {
                CheckoutLoadedContent(
                    state = state,
                    listener = listener
                )
            }

            CheckoutUiState.CheckoutStatus.NO_INTERNET -> {
                NoInternetContent(
                    onRetry = {
                        listener.onRetryClicked()
                    },
                    modifier = Modifier.fillMaxSize().padding(horizontal = Theme.spacing._16)
                )
            }
        }
    }
}

@Composable
private fun CheckoutLoadedContent(
    state: CheckoutUiState,
    listener: CheckoutViewModel
) {
    val products = state.items.collectAsLazyPagingItems()
    OnSystemBackPressed(listener::onBackClicked)
    Scaffold(
        topBar = {
            CheckoutAppBar(listener)
        },
        snakeBar = { CheckoutSnackbar(state.snackBarState, listener) },
        bottomBar = {
            ConfirmOrderButton(
                onConfirmOrderClicked = listener::onConfirmOrderClicked,
                isEnabled = state.isConfirmOrderButtonEnabled,
                isLoading = state.isTransactionLoading
            )
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Theme.spacing._16)
        ) {
            DeliveryAddressCard(
                modifier = Modifier.padding(top = Theme.spacing._8),
                state = state,
                onChangeAddressClicked = listener::onChangeLocationClicked
            )

            Text(
                text = stringResource(Res.string.summary_details),
                style = Theme.typography.title.medium,
                color = Theme.colorScheme.shadePrimary,
                modifier = Modifier.padding(top = Theme.spacing._16)
            )

            CheckoutSummaryCard(
                products = products,
               cartDetails = state.cartDetails,
                modifier = Modifier.padding(top = Theme.spacing._12)
            )
        }
    }
}

@Composable
private fun CheckoutSnackbar(
    snackBarState: SnackBarUiState?,
    listener: CheckoutInteractionListener
) {
    AnimatedContent(
        targetState = snackBarState != null,
        transitionSpec = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down
            ) togetherWith slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up
            )
        }
    ) {
        if (it) {
            snackBarState?.let {
                SnackBar(
                    snackBarUiState = snackBarState,
                    onDismiss = listener::onSnackBarDismissed
                )
            }
        }
    }
}

@Preview
@Composable
private fun CheckoutScreenPreview() {
    MenaTheme {
        CheckoutScreen()
    }
}