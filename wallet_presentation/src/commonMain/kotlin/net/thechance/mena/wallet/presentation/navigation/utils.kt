package net.thechance.mena.wallet.presentation.navigation

import androidx.navigation.NavHostController

fun <T> NavHostController.getFromSavedState(key: String = DEFAULT_KRY): T? {
    val currentRoute = currentBackStackEntry?.destination?.route ?: return null
    return getBackStackEntry(currentRoute).savedStateHandle[key]
}

fun <T> NavHostController.saveInSavedState(value: T, key: String = DEFAULT_KRY) {
    val currentRoute = currentBackStackEntry?.destination?.route ?: return
    getBackStackEntry(currentRoute).savedStateHandle[key] = value
}

fun NavHostController.clearSavedState(key: String = DEFAULT_KRY) {
    val currentRoute = currentBackStackEntry?.destination?.route ?: return
    getBackStackEntry(currentRoute).savedStateHandle[key] = null
}

fun (() -> Unit).clearSavedStateAfterInvoke(navController: NavHostController, key: String = DEFAULT_KRY): (() -> Unit) {
    return {
        invoke()
        navController.clearSavedState(key)
    }
}

private const val DEFAULT_KRY = "DEFAULT_KRY"