package net.thechance.mena.wallet.presentation.screen.confirm_payment

import net.thechance.mena.wallet.domain.entity.Transaction

fun Transaction.toReceiverInfoUiState() = ConfirmPaymentScreenState.ReceiverUiState(
    name = receiverName,
    profileImg = receiverImageUrl
)