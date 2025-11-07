package net.thechance.mena.dukan.api

import androidx.compose.runtime.Composable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface DukanApi {
    @Composable
    fun TabEntry()

    @Composable
    fun OrderDetailsEntry(orderId: Uuid)
}