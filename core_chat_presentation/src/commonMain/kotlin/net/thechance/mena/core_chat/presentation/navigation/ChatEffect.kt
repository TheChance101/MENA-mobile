package net.thechance.mena.core_chat.presentation.navigation

import androidx.navigation.NavOptions
import net.thechance.mena.core_chat.presentation.components.SnackBarData

sealed class ChatEffect {
    data class Navigate(val route: ChatRoute, val navOptions: NavOptions? = null) : ChatEffect()
    data class PopBackStack(val arguments: Map<String, Any> = emptyMap()) : ChatEffect()
    data class ShowSnackBar(val snackBarData: SnackBarData) : ChatEffect()
    data class SetNavigationArgs(val arguments: Map<String, Any> = emptyMap()) : ChatEffect()
}