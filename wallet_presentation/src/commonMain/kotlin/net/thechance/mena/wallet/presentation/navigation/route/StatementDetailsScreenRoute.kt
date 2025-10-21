package net.thechance.mena.wallet.presentation.navigation.route

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.thechance.mena.wallet.presentation.navigation.navType.StorageLocationNavType
import net.thechance.mena.wallet.presentation.screen.statement_details.StatementDetailsScreen
import net.thechance.mena.wallet.presentation.utils.StorageLocation
import kotlin.reflect.typeOf

@Serializable
data class StatementDetailsScreenRoute(val statementLocation: StorageLocation) : WalletRoute()

fun NavGraphBuilder.statementDetailsScreenRoute(navController: NavController){
    composable<StatementDetailsScreenRoute>(
        typeMap = mapOf(typeOf<StorageLocation>() to StorageLocationNavType)
    ) { backStackEntry ->
        StatementDetailsScreen()
    }
}