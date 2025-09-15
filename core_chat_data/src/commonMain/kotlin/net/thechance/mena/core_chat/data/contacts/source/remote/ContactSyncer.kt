package net.thechance.mena.core_chat.data.contacts.source.remote

interface ContactSyncer {
    suspend fun sync()
}