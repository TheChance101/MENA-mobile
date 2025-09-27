package net.thechance.mena.core_chat.presentation.navigation

import kotlinx.serialization.Serializable

interface ChatRoute

@Serializable
data object ChatsRoute : ChatRoute

@Serializable
data object ContactsRoute : ChatRoute

@Serializable
data class SyncContactsRoute(val forceSync: Boolean) : ChatRoute

@Serializable
data class ChatDetailsRoute(val chatId: String) : ChatRoute