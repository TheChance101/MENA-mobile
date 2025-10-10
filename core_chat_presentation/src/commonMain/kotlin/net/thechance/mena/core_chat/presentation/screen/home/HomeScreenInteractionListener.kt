package net.thechance.mena.core_chat.presentation.screen.home

interface HomeScreenInteractionListener {
    fun onNewChatClicked()
    fun onChatClicked(chat: HomeScreenState.HomeUiState)
    fun onWalletClicked()
}