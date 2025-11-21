@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import net.thechance.mena.core_chat.presentation.components.snackBarHost.SnackBarData
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed interface ChatScreenEffect {
    object NavigateBack : ChatScreenEffect
    data class ShowSnackBar(val snackBarData: SnackBarData) : ChatScreenEffect
    object ScrollToBottom: ChatScreenEffect
    data class OpenUrl(val url: String) : ChatScreenEffect
    data class NavigateToConfirmPayment(val amount:String,val transactionId: Uuid): ChatScreenEffect

}