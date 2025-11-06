package net.thechance.mena.core_chat.presentation.api

import androidx.compose.runtime.Composable
import net.thechance.mena.core_chat.api.CoreChatApi
import net.thechance.mena.core_chat.presentation.navigation.ChatNavHost
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class CoreChatApiImp() : CoreChatApi {
    @Composable
    override fun TabEntry() {
        ChatNavHost()
    }

    @Composable
    override fun ChatEntry(userId: String, onNavigateBack: () -> Unit) {
        ChatEntryPoint(userId = userId, onNavigateBack = onNavigateBack)
    }
}