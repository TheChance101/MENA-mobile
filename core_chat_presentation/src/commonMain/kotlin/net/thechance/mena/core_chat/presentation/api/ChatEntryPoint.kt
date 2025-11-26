package net.thechance.mena.core_chat.presentation.api

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.core_chat.presentation.components.LoadingView
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
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(userId){
        viewModel.getChatByUserId(userId)
    }

    LaunchedEffect(state.error) {
        if (state.error) {
            onNavigateBack()
        }
    }

    AnimatedContent(
        targetState = state.isContentVisible,
        modifier = Modifier.fillMaxSize()
    ) { isContentVisible ->
        if (isContentVisible) {
            ChatNavHost(
                startDestination = ChatDetailsRoute(
                    chatId = state.chatId?.toString() ?: "",
                    chatName = state.chatName.orEmpty(),
                ),
                onNavigateBackFromChat = onNavigateBack
            )
        } else {
            LoadingView()
        }
    }
}