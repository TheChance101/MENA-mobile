package net.thechance.mena.dukan.api

import androidx.compose.runtime.Composable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface DukanApi {
    @Composable
    fun TabEntry(updateBottomNavigationVisibility: (Boolean) -> Unit)

    @Composable
    fun OrderDetailsEntry(orderId: Uuid, onNavigateBack: () -> Unit)
}