package net.thechance.mena.core_chat.data.network

object ApiConstants {
    const val WEB_SOCKETS_ENDPOINT = "/ws"

    const val CHAT_ENDPOINT = "/chat"
    const val CHAT_CLIENT = "chatClient"
    const val BASE_URL = "baseUrl"
    const val CHAT_JSON = "chatJson"

    const val CONTACTS_ENDPOINT = "$CHAT_ENDPOINT/contacts"
    const val SYNC_CONTACTS_ENDPOINT = "$CONTACTS_ENDPOINT/sync"
    const val CHAT_HISTORY_ENDPOINT = "/chat/history"
}