package net.thechance.mena.core_chat.presentation.utils

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed class UiText{
    data class DynamicString(val value: String = ""): UiText()
    data class StringRes(val resId: StringResource, val formatArgs: Any = 1): UiText()
}

@Composable
fun UiText?.asString(): String {
    return when (this) {
        is UiText.DynamicString -> value
        is UiText.StringRes -> stringResource(resId, formatArgs)
        else -> ""
    }
}