package net.thechance.mena.faith.presentation.feature.downloadedSur.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.delete
import mena.faith_presentation.generated.resources.delete_surah
import mena.faith_presentation.generated.resources.delete_surah_dialog_message
import mena.faith_presentation.generated.resources.icon_cancel
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DeleteConfirmationDialog(
    onDeleteClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Box(
            modifier =
                modifier
                    .fillMaxWidth()
                    .background(
                        color = Theme.colorScheme.background.surfaceLow,
                        shape = RoundedCornerShape(Theme.radius.lg),
                    ).padding(Theme.spacing._12),
        ) {
            Button(
                onClick = onDismiss,
                containerColor = Theme.colorScheme.background.surface,
                modifier =
                    Modifier
                        .size(32.dp)
                        .clip(CircleShape),
            ) {
                Image(
                    painter = painterResource(Res.drawable.icon_cancel),
                    contentDescription = "Dismiss",
                    modifier = Modifier.size(Theme.spacing._16),
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = Theme.spacing._12),
            ) {
                Text(
                    text = stringResource(Res.string.delete_surah),
                    style = Theme.typography.title.medium,
                    color = Theme.colorScheme.shadePrimary,
                )
                Text(
                    text = stringResource(Res.string.delete_surah_dialog_message),
                    style = Theme.typography.body.small,
                    color = Theme.colorScheme.shadeSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = Theme.spacing._4),
                )

                Button(
                    onClick = onDeleteClick,
                    modifier =
                        Modifier
                            .padding(top = Theme.spacing._24, end = Theme.spacing._8)
                            .align(Alignment.End),
                ) {
                    Text(
                        text = stringResource(Res.string.delete),
                        style = Theme.typography.label.medium,
                        color = Theme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDeleteConfirmationDialog() {
    QuranTheme {
        DeleteConfirmationDialog(
            {},
            {},
        )
    }
}
