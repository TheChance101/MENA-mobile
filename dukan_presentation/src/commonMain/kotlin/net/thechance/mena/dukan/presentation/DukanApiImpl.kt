package net.thechance.mena.dukan.presentation

import androidx.compose.runtime.Composable
import net.thechance.mena.dukan.api.DukanApi
import net.thechance.mena.dukan.presentation.navigation.DukanNavHost
import net.thechance.mena.dukan.presentation.screen.orderDetails.OrderDetailsScreen
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DukanApiImpl : DukanApi {
    @Composable
    override fun TabEntry(updateBottomNavigationVisibility: (Boolean) -> Unit) {
        DukanNavHost(
            updateBottomNavigationVisibility = updateBottomNavigationVisibility
        )
    }

    @Composable
    override fun OrderDetailsEntry(orderId: Uuid, onNavigateBack: () -> Unit) {
        OrderDetailsScreen(
            orderId = orderId,
            onNavigationBackToChat = onNavigateBack
        )
    }
}