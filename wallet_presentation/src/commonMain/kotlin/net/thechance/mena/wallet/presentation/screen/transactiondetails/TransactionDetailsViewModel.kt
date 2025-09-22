package net.thechance.mena.wallet.presentation.screen.transactiondetails

import net.thechance.mena.wallet.presentation.base.BaseViewModel
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class TransactionDetailsViewModel() :
    BaseViewModel<TransactionDetailsScreenState, TransactionDetailsEffect>(
        TransactionDetailsScreenState()
    ) , TransactionDetailsInteractionListener
{
    override fun onBackBtnClicked() {
        TODO("Not yet implemented")
    }

    override fun onShareReceiptBtnClicked() {
        TODO("Not yet implemented")
    }

    override fun onRefresh() {
        TODO("Not yet implemented")
    }
}