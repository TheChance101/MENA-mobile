package net.thechance.mena.wallet.presentation.screen.remove_statements

sealed interface RemoveStatementsEffect{
    data object navigateToStatements : RemoveStatementsEffect
}