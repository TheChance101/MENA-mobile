package net.thechance.mena.core_chat.presentation.screen.chat

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.core_chat.presentation.navigation.ChatDetailsRoute

interface ChatArgs {

    val chatId: String
}

class ChatArgsImpl(savedStateHandle: SavedStateHandle) : ChatArgs {

    override val chatId: String = savedStateHandle.toRoute<ChatDetailsRoute>().chatId
}