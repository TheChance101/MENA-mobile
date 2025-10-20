package net.thechance.mena.wallet.presentation.screen.statement_details

import androidx.lifecycle.SavedStateHandle
import kotlinx.serialization.json.Json
import net.thechance.mena.wallet.presentation.navigation.navType.StorageLocationSerializer
import net.thechance.mena.wallet.presentation.utils.StorageLocation
import org.koin.core.annotation.Factory

private const val ARG_KEY = "statementLocation"

interface StatementDetailsArgs {
    val statementLocation: StorageLocation
}

@Factory(binds = [StatementDetailsArgs::class])
class StatementDetailsArgsImpl(savedStateHandle: SavedStateHandle): StatementDetailsArgs{
    val json = savedStateHandle.get<String>(ARG_KEY)
    override val statementLocation: StorageLocation
        get() = Json.decodeFromString(StorageLocationSerializer, json!!)
}