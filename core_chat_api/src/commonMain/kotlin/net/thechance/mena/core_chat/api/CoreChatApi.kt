package net.thechance.mena.core_chat.api

import androidx.compose.runtime.Composable

interface CoreChatApi {
    @Composable
    fun TabEntry(updateBottomNavigationVisibility: (Boolean) -> Unit)

    @Composable
    fun ChatEntry(userId: String, onNavigateBack: () -> Unit)

    @Composable
    fun ShareAyahToChatEntry(surahId: String, ayahNumber: Int, ayahContent: String, onNavigateBack: () -> Unit)
}