package net.thechance.mena.wallet.presentation.screen.transaction_details.args

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.wallet.presentation.navigation.TransactionDetailsScreenRoute
import org.koin.core.annotation.Factory

interface TransactionDetailsArgs{
    val id: String
}
@Factory(binds = [TransactionDetailsArgs::class])
class TransactionDetailsArgsImpl(private val savedStateHandle: SavedStateHandle) : TransactionDetailsArgs {
    override val id: String
        get() = savedStateHandle.toRoute<TransactionDetailsScreenRoute>().id
}