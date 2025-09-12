package net.thechance.mena.designsystem.presentation.component.checkBox

import androidx.compose.ui.state.ToggleableState

fun ToggleableState.getNextCheckboxState() = when (this) {
    ToggleableState.Off -> ToggleableState.On
    ToggleableState.On -> ToggleableState.Indeterminate
    ToggleableState.Indeterminate -> ToggleableState.Off
}