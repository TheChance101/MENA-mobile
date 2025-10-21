package net.thechance.mena.wallet.presentation.screen.confirm_payment

import net.thechance.mena.wallet.domain.model.TransactionReceiver

fun TransactionReceiver.toUiState() = ConfirmPaymentScreenState.ReceiverUiState(
    name = name,
    profileImg = imgUrl
)