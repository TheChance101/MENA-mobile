package net.thechance.mena.wallet.presentation.screen.transaction_details

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.wallet.presentation.navigation.TransactionDetailsScreenRoute
import org.koin.core.annotation.Factory
import kotlin.uuid.ExperimentalUuidApi


interface TransactionDetailsArgs {
    val id: String
}
@OptIn(ExperimentalUuidApi::class)
@Factory
class TransactionDetailsArgsImpl (
    savedStateHandle: SavedStateHandle
) : TransactionDetailsArgs {
    override val id: String = savedStateHandle.toRoute(TransactionDetailsScreenRoute::class).id
}