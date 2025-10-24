package net.thechance.mena.core_chat.presentation.components.snackBarHost

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

val LocalSnackBarHostController = staticCompositionLocalOf<SnackBarHostController> {
    error("No NavController provided")
}

class SnackBarHostController {
    private val _state = MutableStateFlow<SnackBarHostState>(SnackBarHostState())
    val state = _state.asStateFlow()

    fun showSnackBar(snackBarData: SnackBarData) {
        _state.update { it.copy(snackBarData = snackBarData, isVisible = true) }
    }

    fun dismissSnackBar() = _state.update { it.copy(isVisible = false) }
}