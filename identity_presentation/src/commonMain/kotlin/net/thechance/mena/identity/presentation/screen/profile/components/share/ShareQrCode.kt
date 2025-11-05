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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.coroutines.delay
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.copy_to_clipboard_success
import mena.identity_presentation.generated.resources.download_icon_content_description
import mena.identity_presentation.generated.resources.ic_check_circle
import mena.identity_presentation.generated.resources.ic_download
import mena.identity_presentation.generated.resources.ic_link
import mena.identity_presentation.generated.resources.ic_share_02
import mena.identity_presentation.generated.resources.link_icon_content_description
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
    isCopied: Boolean,
    fullName: String,
    urlString: String,
    qrCodePainter: Painter,
    onShareProfile: () -> Unit,
    onCopyToClipboard: (clipboard: Clipboard) -> Unit,
    onDownload: () -> Unit,
    onDismissShareDialog: () -> Unit,
    onDismissSnackBar: () -> Unit,
    modifier: Modifier = Modifier
) {

    val clipboard = LocalClipboard.current

    CopyToClipboardSnackBar(
        isVisible = isCopied,
        urlString = urlString,
        onDismissSnackBar = onDismissSnackBar
    )

    BasicDialog(
        onDismiss = onDismissShareDialog,
        onCancelClick = onDismissShareDialog,
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
            Image(
                painter = qrCodePainter,
                colorFilter = ColorFilter.tint(
                    color = Theme.colorScheme.primary.primary
                ),
                contentDescription = stringResource(Res.string.share_profile_qr_code),
                modifier = Modifier
                    .size(240.dp)
                    .padding(all = 20.dp)
                    .padding(top = 12.dp)
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
                    onClick = { onCopyToClipboard(clipboard) }
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
    isVisible: Boolean,
    urlString: String,
    onDismissSnackBar: () -> Unit,
    modifier: Modifier = Modifier
) {

    LaunchedEffect(isVisible) {
        delay(2000L)
        onDismissSnackBar()
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it }),
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
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
                        isCopied = false,
                        fullName = "Hassan Nabil",
                        urlString = "",
                        qrCodePainter = rememberQrCodePainter("Any Text"),
                        onDismissShareDialog = {},
                        onDismissSnackBar = {},
                        onCopyToClipboard = {},
                        onShareProfile = {},
                        onDownload = {},
                    )
                }
            }
        ) {}
    }
}