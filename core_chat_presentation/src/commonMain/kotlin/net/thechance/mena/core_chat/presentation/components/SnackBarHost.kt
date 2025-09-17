package net.thechance.mena.core_chat.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_warning
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource

@Composable
fun AnimatedSnackBarHost(
    data: SnackBarData?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    durationMillis: Long = 2500,
) {
    AnimatedVisibility(
        visible = data != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Theme.spacing._12)
    ) {
        if (data != null) {
            LaunchedEffect(data) {
                delay(durationMillis)
                onDismiss()
            }
            SnackBar(
                title = data.title,
                message = data.message,
                leadingIcon = painterResource(Res.drawable.ic_warning),
            )
        }
    }
}

data class SnackBarData(
    val title: String,
    val message: String,
)