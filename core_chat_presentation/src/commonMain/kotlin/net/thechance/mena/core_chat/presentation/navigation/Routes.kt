package net.thechance.mena.core_chat.presentation.navigation

import kotlinx.serialization.Serializable
import net.thechance.mena.core_chat.presentation.screen.chat.MessageUiState

interface ChatRoute

@Serializable
data object HomeRoute : ChatRoute

@Serializable
data object WalletRoute : ChatRoute

@Serializable
data object ContactsRoute : ChatRoute

@Serializable
data class SyncContactsRoute(val forceSync: Boolean) : ChatRoute

@Serializable
data class ChatDetailsRoute(val chatId: String, val chatName: String) : ChatRoute

@Serializable
data class ShareMessageRoute(val messageArgs: AyahMessageArgs) : ChatRoute