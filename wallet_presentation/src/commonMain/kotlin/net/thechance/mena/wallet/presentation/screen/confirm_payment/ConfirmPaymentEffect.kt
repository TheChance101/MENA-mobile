@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.confirm_payment

import net.thechance.mena.wallet.presentation.model.SubmissionStatus
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed interface ConfirmPaymentEffect {
    data object NavigateBack : ConfirmPaymentEffect
    data class NavigateToPaymentResultScreen(
        val receiverName: String,
        val amount: Double,
        val transactionId: Uuid,
        val submissionStatus: SubmissionStatus
        ) : ConfirmPaymentEffect
}