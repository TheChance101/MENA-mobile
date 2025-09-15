package net.thechance.mena.core_chat.presentation.navigation

import kotlinx.serialization.Serializable

interface ChatRoute

@Serializable
data object ChatsRoute : ChatRoute

@Serializable
data object ContactsRoute : ChatRoute

@Serializable
data object SyncContactsRoute : ChatRoute