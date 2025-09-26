package net.thechance.mena.dukan.presentation.navigation

import androidx.navigation.NavOptions

sealed interface DukanEffect {
    data class Navigate(
        val route: DukanRoute,
        val navOptions: NavOptions?=null
    ) : DukanEffect

    object NavigateUp : DukanEffect

    data class PopBackStackWithArgs(val arguments: Map<String, Any> = emptyMap()) : DukanEffect
}