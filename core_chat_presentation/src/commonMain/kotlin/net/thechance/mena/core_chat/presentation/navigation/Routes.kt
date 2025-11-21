package net.thechance.mena.core_chat.presentation.navigation

import kotlinx.serialization.Serializable

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
data object ShareMessageRoute : ChatRoute