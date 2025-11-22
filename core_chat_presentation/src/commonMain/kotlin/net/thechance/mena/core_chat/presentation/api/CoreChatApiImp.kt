package net.thechance.mena.core_chat.presentation.api

import androidx.compose.runtime.Composable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.thechance.mena.core_chat.api.CoreChatApi
import net.thechance.mena.core_chat.presentation.navigation.AyahMessageArgs
import net.thechance.mena.core_chat.presentation.navigation.ChatNavHost
import net.thechance.mena.core_chat.presentation.navigation.ShareMessageRoute
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class CoreChatApiImp() : CoreChatApi {
    @Composable
    override fun TabEntry(updateBottomNavigationVisibility: (Boolean) -> Unit) {
        ChatNavHost(updateBottomNavigationVisibility = updateBottomNavigationVisibility)
    }

    @Composable
    override fun ChatEntry(userId: String, onNavigateBack: () -> Unit) {
        ChatEntryPoint(userId = userId, onNavigateBack = onNavigateBack)
    }

    @Composable
    override fun ShareAyahToChatEntry(
        surahId: String,
        ayahNumber: Int,
        ayahContent: String,
        onNavigateBack: () -> Unit
    ) {
        ChatNavHost(
            startDestination = ShareMessageRoute(
                Json.encodeToString<AyahMessageArgs>(
                    AyahMessageArgs(surahId.toInt(), ayahNumber, ayahContent)
                )
            ),
            onNavigateBackFromChat = onNavigateBack,
            onNavigateBackFromShareMessage = onNavigateBack
        )
    }
}