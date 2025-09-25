package net.thechance.mena.dukan.presentation.navigation

import androidx.navigation.NavOptions
import kotlinx.coroutines.flow.SharedFlow

interface DukanNavigator {
    val startDestination: DukanRoute
    val dukanEffects: SharedFlow<DukanEffect>
    suspend fun navigate(route: DukanRoute, navOptions: NavOptions? = null)
    suspend fun navigateUp()
}