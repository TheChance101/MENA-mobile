package net.thechance.mena.core_chat.presentation.api

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.thechance.mena.core_chat.api.CoreChatApi
import net.thechance.mena.core_chat.presentation.navigation.ChatNavHost
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class CoreChatApiImp() : CoreChatApi {
    @Composable
    override fun TabEntry() {
        ChatNavHost()
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
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Share Ayah To Chat Screen", style = Theme.typography.body.large)
        }
    }
}