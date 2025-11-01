package net.thechance.mena.identity.presentation.screen.profile.components.share

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.copy_to_clipboard_success
import mena.identity_presentation.generated.resources.download_icon_content_description
import mena.identity_presentation.generated.resources.ic_check_circle
import mena.identity_presentation.generated.resources.ic_download
import mena.identity_presentation.generated.resources.ic_link
import mena.identity_presentation.generated.resources.ic_share_02
import mena.identity_presentation.generated.resources.link_icon_content_description
import mena.identity_presentation.generated.resources.male
import mena.identity_presentation.generated.resources.share_icon_content_description
import mena.identity_presentation.generated.resources.share_profile_description
import mena.identity_presentation.generated.resources.share_profile_qr_code
import mena.identity_presentation.generated.resources.share_profile_title
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.dialog.BasicDialog
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape


@Composable
fun ScaffoldScope.ShareQrCode(
    showDialog: Boolean,
    fullName: String,
    urlString: String,
    qrCodePainter: Painter,
    onDismiss: () -> Unit,
    onShareProfile: () -> Unit,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier
) {

    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    var isCopied by remember { mutableStateOf(false) }

    CopyToClipboardSnackBar(
        isCopied = isCopied,
        urlString = urlString,
        onDismissSnackBar = { isCopied = false },
    )

    BasicDialog(
        onDismiss = onDismiss,
        onCancelClick = onDismiss,
        hasDismissButton = true,
        isVisible = showDialog,
        dialogCornerShape = SquircleShape(Theme.radius.xl),
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 12.dp)
                .align(Alignment.Center)
        ) {
            Text(
                text = stringResource(Res.string.share_profile_title),
                color = Theme.colorScheme.shadePrimary,
                style = Theme.typography.title.small
            )
            Text(
                text = stringResource(Res.string.share_profile_description),
                color = Theme.colorScheme.shadeSecondary,
                style = Theme.typography.label.small
            )
            //todo: this will be replaced with the qr code later
            Image(
                painter = qrCodePainter,
                colorFilter = ColorFilter.tint(
                    color = Theme.colorScheme.primary.primary
                ),
                contentDescription = stringResource(Res.string.share_profile_qr_code),
                modifier = Modifier
                    .padding(top = 32.dp)
                    .size(240.dp)
            )
            Text(
                text = fullName,
                color = Theme.colorScheme.shadePrimary,
                style = Theme.typography.label.medium,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                ShareProfileButton(
                    icon = painterResource(Res.drawable.ic_share_02),
                    contentDescription = stringResource(Res.string.share_icon_content_description),
                    onClick = onShareProfile
                )
                ShareProfileButton(
                    icon = painterResource(Res.drawable.ic_link),
                    contentDescription = stringResource(Res.string.link_icon_content_description),
                    onClick = {
                        scope.launch {
                            clipboard.setClipEntry(clipEntryOf(urlString))
                        }.invokeOnCompletion {
                            isCopied = true
                            onDismiss()
                        }
                    }
                )
                ShareProfileButton(
                    icon = painterResource(Res.drawable.ic_download),
                    contentDescription = stringResource(Res.string.download_icon_content_description),
                    onClick = onDownload
                )
            }
        }
    }
}


@Composable
private fun ShareProfileButton(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = SquircleShape(Theme.radius.md),
        borderStroke = BorderStroke(width = 1.dp, color = Theme.colorScheme.stroke),
        contentColor = Theme.colorScheme.primary.primary,
        containerColor = Color.Transparent,
        disabledContentColor = Theme.colorScheme.textDisabled,
        disabledContainerColor = Color.Transparent,
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 14.dp
        ),
        modifier = modifier
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription
        )
    }
}

@Composable
private fun CopyToClipboardSnackBar(
    isCopied: Boolean,
    urlString: String,
    duration: Long = 2000L,
    onDismissSnackBar: () -> Unit,
    modifier: Modifier = Modifier
) {

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            isVisible = true
            delay(duration)
            isVisible = false
            onDismissSnackBar()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it }),
        modifier = modifier.fillMaxWidth()
    ) {
        SnackBar(
            title = stringResource(Res.string.copy_to_clipboard_success),
            message = urlString,
            leadingIcon = painterResource(Res.drawable.ic_check_circle),
            modifier = Modifier.fillMaxWidth().padding(bottom = Theme.spacing._16)
                .padding(horizontal = Theme.spacing._16)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShareProfileQrCodePreview() {
    MenaTheme {
        Scaffold(
            overlays = {
                dialog(true) {
                    ShareQrCode(
                        showDialog = it,
                        fullName = "Hassan Nabil",
                        urlString = "",
                        qrCodePainter = painterResource(Res.drawable.male),
                        onDismiss = {},
                        onShareProfile = {},
                        onDownload = { },
                    )
                }
            }
        ) {}
    }

}