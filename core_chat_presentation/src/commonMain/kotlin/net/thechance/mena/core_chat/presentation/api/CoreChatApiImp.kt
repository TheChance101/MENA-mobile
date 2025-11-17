package net.thechance.mena.core_chat.presentation.api

import androidx.compose.runtime.Composable
import net.thechance.mena.core_chat.api.CoreChatApi
import net.thechance.mena.core_chat.presentation.navigation.AyahMessageArgs
import net.thechance.mena.core_chat.presentation.navigation.ChatNavHost
import net.thechance.mena.core_chat.presentation.navigation.ShareMessageRoute
import net.thechance.mena.core_chat.presentation.screen.chat.AyahMessageUiState
import net.thechance.mena.core_chat.presentation.screen.chat.MessageDetailsUiState
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

    @Composable
    override fun ShareAyahToChatEntry(
        surahId: String,
        ayahNumber: Int,
        ayahContent: String,
        onNavigateBack: () -> Unit
    ) {
        // TODO :(" 1- ShareMessageScreen 2- send ayah to ShareMessageScreen 3- add pending message 4- navigate to chat screen")
        ChatNavHost(
            startDestination = ShareMessageRoute(
                AyahMessageArgs(surahId.toInt(), ayahNumber, ayahContent)
            )
        )
    }
}