package net.thechance.mena.core_chat.presentation.screen.contacts

interface ContactSyncer {
    suspend fun sync()
}