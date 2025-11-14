package net.thechance.mena.core_chat.presentation.navigation

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class OrderDetailsRoute(val orderId: Uuid): ChatRoute