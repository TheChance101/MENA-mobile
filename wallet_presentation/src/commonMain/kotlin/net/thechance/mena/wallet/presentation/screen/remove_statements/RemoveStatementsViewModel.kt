package net.thechance.mena.wallet.presentation.screen.remove_statements

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class RemoveStatementsViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<RemoveStatementsScreenState, RemoveStatementsEffect>(RemoveStatementsScreenState()),
    RemoveStatementsInteractionListener {
    override fun onCancelClicked() {
    }

    override fun onDeleteClicked() {

    }
}