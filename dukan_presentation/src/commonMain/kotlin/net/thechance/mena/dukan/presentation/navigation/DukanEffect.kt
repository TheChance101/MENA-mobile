package net.thechance.mena.dukan.presentation.navigation

import androidx.navigation.NavOptions

sealed interface DukanEffect {
    data class Navigate(
        val destination: DukanRoute,
        val navOptions: NavOptions?=null
    ) : DukanEffect

    object NavigateUp : DukanEffect

}