package net.thechance.mena.core_chat.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_snackbar_error
import mena.core_chat_presentation.generated.resources.ic_snackbar_success
import net.thechance.mena.core_chat.presentation.utils.UiText
import net.thechance.mena.core_chat.presentation.utils.asString
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource

@Composable
fun AnimatedSnackBarHost(
    isVisible: Boolean,
    data: SnackBarData,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val iconId = if (data.isError) Res.drawable.ic_snackbar_error else Res.drawable.ic_snackbar_success

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Theme.spacing._12)
    ) {
        LaunchedEffect(data) {
            delay(data.duration)
            onDismiss()
        }
        SnackBar(
            modifier = modifier.clickable(
                onClick = onDismiss,
                indication = null,
                interactionSource = null
            ),
            title = data.title.asString(),
            message = data.message.asString(),
            leadingIcon = painterResource(iconId),
        )
    }
}

data class SnackBarData(
    val title: UiText,
    val message: UiText,
    val isError: Boolean = true,
    val duration: Long = 2500
)