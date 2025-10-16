package net.thechance.mena.core_chat.presentation.screen.home

import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState

interface HomeScreenInteractionListener {
    fun onNewChatClicked()
    fun onChatClicked(chat: ChatUiState)
    fun onWalletClicked()
    fun onChatsListScrolled()
}