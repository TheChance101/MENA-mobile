package net.thechance.mena.dukan.presentation.screen.checkout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.cash.paging.compose.collectAsLazyPagingItems
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.checkout.component.CheckoutAppBar
import net.thechance.mena.dukan.presentation.screen.checkout.component.CheckoutSummaryCard
import net.thechance.mena.dukan.presentation.screen.checkout.component.ConfirmOrderButton
import net.thechance.mena.dukan.presentation.screen.checkout.component.DeliveryAddressCard
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.viewModel.checkout.CheckoutEffect
import net.thechance.mena.dukan.presentation.viewModel.checkout.CheckoutUiState
import net.thechance.mena.dukan.presentation.viewModel.checkout.CheckoutViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            CheckoutEffect.NavigateBack -> {
                navController.popBackStack()
            }

            CheckoutEffect.NavigateToChangeLocation -> {
                // TODO
            }
        }
    }

    CheckoutContent(
        state = state,
        listener = viewModel
    )

}

@Composable
private fun CheckoutContent(state: CheckoutUiState, listener: CheckoutViewModel) {
    val products = state.items.collectAsLazyPagingItems()
    OnSystemBackPressed(listener::onBackClicked)
    Scaffold(
        topBar = {
            CheckoutAppBar(listener)
        },
        bottomBar = {
            ConfirmOrderButton()
        }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = Theme.spacing._16)
        ) {
            DeliveryAddressCard(modifier = Modifier.padding(top = Theme.spacing._8))
            CheckoutSummaryCard(
                products = products,
                modifier = Modifier.padding(top = Theme.spacing._16)
            )
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