package net.thechance.mena.dukan.presentation.screen.pendingDukan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.dukan_pending
import mena.dukan_presentation.generated.resources.dukan_pending_dark
import mena.dukan_presentation.generated.resources.dukan_request_pending
import mena.dukan_presentation.generated.resources.dukan_waiting_approval
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.my_dukan
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.shared.AnnotatedText
import net.thechance.mena.dukan.presentation.component.state.ImageWithTextContainer
import net.thechance.mena.dukan.presentation.navigation.LocalDarkTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PendingDukanScreen(
    dukanName: String,
    onBackClick: () -> Unit,
) {
    val isDark = LocalDarkTheme.current
    val icon = if (isDark) Res.drawable.dukan_pending_dark else Res.drawable.dukan_pending


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface)
            .systemBarsPadding()
    ) {
        AppBar(
            title = stringResource(Res.string.my_dukan),
            leadingContent = {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_left),
                    contentDescription = stringResource(Res.string.back_arrow),
                    tint = Theme.colorScheme.primary.primary
                )
            },
            onLeadingClick = onBackClick
        )

        Spacer(modifier = Modifier.weight(1f))

        val titleText = buildPendingDukanTitle(
            brandName = dukanName,
            titleTemplate = stringResource(Res.string.dukan_request_pending)
        )

        ImageWithTextContainer(
            foregroundImageRes = icon,
            header = {
                AnnotatedText(
                    text = titleText,
                    style = TextStyle(textAlign = TextAlign.Center)
                )
            },
            bodyText = stringResource(Res.string.dukan_waiting_approval),
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun buildPendingDukanTitle(
    brandName: String,
    titleTemplate: String,
) = buildAnnotatedString {
    val parts = titleTemplate.split("%s")
    withStyle(Theme.typography.title.small.toSpanStyle()) {
        append(parts.getOrElse(0) { "" })
    }
    withStyle(Theme.typography.title.medium.toSpanStyle()) {
        append(brandName)
    }
    withStyle(Theme.typography.title.small.toSpanStyle()) {
        append(parts.getOrElse(1) { "" })
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        PendingDukanScreen(
            dukanName = "Calvin Klein",
            onBackClick = {},
        )
    }
}