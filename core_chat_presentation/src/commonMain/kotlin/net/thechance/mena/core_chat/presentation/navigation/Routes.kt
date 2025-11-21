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
data class ConfirmPaymentRoute(val amount: String, val transactionId: String) : ChatRoute
@Serializable
data object ContactsRoute : ChatRoute

@Serializable
data class SyncContactsRoute(val forceSync: Boolean) : ChatRoute

@Serializable
data class ChatDetailsRoute(val chatId: String, val chatName: String) : ChatRoute

@Serializable
data class ShareMessageRoute(val messageArgsJson: String) : ChatRoute