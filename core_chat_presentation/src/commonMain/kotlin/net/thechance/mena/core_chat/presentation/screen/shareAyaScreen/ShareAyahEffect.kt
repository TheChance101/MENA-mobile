package net.thechance.mena.core_chat.presentation.screen.shareAyaScreen

import net.thechance.mena.core_chat.presentation.components.snackBarHost.SnackBarData
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
sealed interface ShareAyahEffect {
    object NavigateBack : ShareAyahEffect
    data class NavigateToChatScreen(val chatId: Uuid, val chatName: String) : ShareAyahEffect
    data class ShowSnackBar(val snackBarData: SnackBarData) : ShareAyahEffect
}