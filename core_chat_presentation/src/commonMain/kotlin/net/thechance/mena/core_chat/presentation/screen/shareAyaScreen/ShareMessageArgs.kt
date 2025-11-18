package net.thechance.mena.core_chat.presentation.screen.shareAyaScreen

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import kotlinx.serialization.json.Json
import net.thechance.mena.core_chat.presentation.navigation.AyahMessageArgs
import net.thechance.mena.core_chat.presentation.navigation.ShareMessageRoute

interface ShareMessageArgs {
    val messageArgs: AyahMessageArgs
}

class ShareMessageArgsImpl(savedStateHandle: SavedStateHandle) : ShareMessageArgs {
    override val messageArgs: AyahMessageArgs = Json.decodeFromString<AyahMessageArgs>(savedStateHandle.toRoute<ShareMessageRoute>().messageArgsJson)
}