package net.thechance.mena.core_chat.api

import androidx.compose.runtime.Composable
import kotlin.uuid.ExperimentalUuidApi

interface CoreChatApi {
    @Composable
    fun TabEntry()

    @Composable
    fun ChatEntry(userId: String, onNavigateBack: () -> Unit)
}