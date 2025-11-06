package net.thechance.mena.core_chat.presentation.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.core_chat.presentation.navigation.ChatDetailsRoute
import net.thechance.mena.core_chat.presentation.navigation.ChatNavHost
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun ChatEntryPoint(
    userId: String,
    viewModel: ChatEntryViewModel = koinViewModel<ChatEntryViewModel>(),
    onNavigateBack: () -> Unit
) {
    viewModel.getChatId(userId)
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (state.chatId != null && state.chatName != null) {
        ChatNavHost(
            startDestination = ChatDetailsRoute(
                chatId = state.chatId?.toString() ?: "",
                chatName = state.chatName.orEmpty(),
            ),
            onNavigateBackFromChat = onNavigateBack
        )
    }
    if (state.isError) {
        ChatNavHost(
            startDestination = ChatDetailsRoute(
                chatId = "",
                chatName = "",
            ),
            onNavigateBackFromChat = onNavigateBack
        )
    }
}