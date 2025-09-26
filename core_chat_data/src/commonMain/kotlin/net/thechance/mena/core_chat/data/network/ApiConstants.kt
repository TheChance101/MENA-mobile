package net.thechance.mena.core_chat.data.network

object ApiConstants {
    const val BASE_URL = "http://10.0.2.2:8080"  // TODO: change to real url in production

    const val CONTACTS_ENDPOINT = "/chat/contacts"
    const val SYNC_CONTACTS_ENDPOINT = "$CONTACTS_ENDPOINT/sync"
}