package net.thechance.mena.core_chat.presentation.api

import androidx.compose.runtime.Composable
import net.thechance.mena.core_chat.api.CoreChatApi
import net.thechance.mena.core_chat.presentation.navigation.ChatNavHost
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class CoreChatApiImp(private val chatEntryViewModel: ChatEntryViewModel) : CoreChatApi {
    @Composable
    override fun TabEntry() {
        ChatNavHost()
    }

    @OptIn(ExperimentalUuidApi::class)
    @Composable
    override fun Chat(userId: String, onNavigateBack: () -> Unit) {
        ChatEntryPoint(userId = userId, viewModel = chatEntryViewModel, onNavigateBack = onNavigateBack)
    }
}