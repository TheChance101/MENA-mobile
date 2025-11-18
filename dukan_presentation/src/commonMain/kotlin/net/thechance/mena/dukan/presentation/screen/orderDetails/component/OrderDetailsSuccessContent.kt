package net.thechance.mena.dukan.presentation.screen.orderDetails.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.getScreenWidth
import net.thechance.mena.dukan.presentation.viewModel.orderDetails.OrderDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.orderDetails.OrderDetailsUiState

@Composable
fun OrderDetailsSuccessContent(
    state : OrderDetailsUiState,
    interactionListener : OrderDetailsInteractionListener
){
    val lazyVerticalState = rememberLazyStaggeredGridState()
    val isFirstItemVisibleInSecondColumn = derivedStateOf {
        lazyVerticalState.layoutInfo.visibleItemsInfo.any {
            it.index == 1 && it.lane == 1
        }
    }
    val staggeredGridCellsType = derivedStateOf {
        if ( getScreenWidth() < 600.dp) {
            StaggeredGridCells.Adaptive(305.dp)
        } else {
            StaggeredGridCells.Fixed(2)
        }
    }

    LazyVerticalStaggeredGrid(
        columns = staggeredGridCellsType.value,
        state = lazyVerticalState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = Theme.spacing._16,
            vertical = Theme.spacing._12
        ),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._12),
    ) {
        item(
            key = "dukan_order_summary_section",
            contentType = { "order_summary_section" },
        ) {
            OrderSummary(
                orderDate = state.orderUiState.orderDate,
                productsInOrder = state.orderUiState.productInOrder,
                discountAmount = state.orderUiState.discount,
                platformFeesAmount = state.orderUiState.platformFees,
                totalAmount = state.orderUiState.totalAmount,
            )
        }
        item(
            key = "dukan_delivery_address_section",
            contentType = { "delivery_address_section" },
        ) {
            val topPaddingValue =
                if (isFirstItemVisibleInSecondColumn.value) 0.dp
                else Theme.spacing._24

            DeliveryAddressSection(
                address = state.orderUiState.addressDeliveryUiState.addressDeliveryTitle,
                isUserOwnerToEnableAddressClick = state.orderUiState.isUserOwner,
                onClick = { interactionListener.onAddressDeliveryClicked(address = state.orderUiState.addressDeliveryUiState ) },
                modifier = Modifier.padding(top = topPaddingValue),
            )
        }
        item(
            key = "dukan_customer_information_section",
            contentType = { "customer_information_section" },
        ) {
            CustomerInformationSection(
                userName = state.orderUiState.customerName,
                userPhoneNumber = state.orderUiState.customerPhone,
                modifier = Modifier.padding(top = Theme.spacing._12)
            )
        }
    }
}