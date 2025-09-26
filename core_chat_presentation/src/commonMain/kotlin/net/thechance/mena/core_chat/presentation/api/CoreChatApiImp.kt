package net.thechance.mena.core_chat.presentation.api

import androidx.compose.runtime.Composable
import net.thechance.mena.core_chat.api.CoreChatApi
import net.thechance.mena.core_chat.presentation.navigation.ChatNavHost

class CoreChatApiImp: CoreChatApi {
    @Composable
    override fun launch() {
        ChatNavHost()
    }
}