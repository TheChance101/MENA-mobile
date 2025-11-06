package net.thechance.mena.core_chat.api

import androidx.compose.runtime.Composable
import kotlin.uuid.ExperimentalUuidApi

interface CoreChatApi {
    @Composable
    fun TabEntry()

    @OptIn(ExperimentalUuidApi::class)
    @Composable
    fun Chat(userId: String,onNavigateBack: () -> Unit)
}