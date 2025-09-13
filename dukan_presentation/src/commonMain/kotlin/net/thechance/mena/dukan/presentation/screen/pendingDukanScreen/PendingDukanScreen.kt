package net.thechance.mena.dukan.presentation.screen.pendingDukanScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.dukan_blur
import mena.dukan_presentation.generated.resources.dukan_pending
import mena.dukan_presentation.generated.resources.dukan_request_pending
import mena.dukan_presentation.generated.resources.dukan_waiting_approval
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.my_dukan
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.MenaIcon
import net.thechance.mena.designsystem.presentation.component.image.MenaImage
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
private fun PendingDukanScreen(
    dukanName: String,
    onBackClick: () -> Unit,
) {
    MenaTheme {
        Box(
            modifier = Modifier.fillMaxSize().background(Theme.colorScheme.background.surface),
        ) {
            AppBar(
                title = stringResource(Res.string.my_dukan),
                leadingContent = {
                    MenaIcon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = "left_arrow",
                        modifier = Modifier.clickable(onClick = onBackClick)
                    )
                },
                modifier = Modifier.align(Alignment.TopStart)
            )
            MenaImage(
                painter = painterResource(Res.drawable.dukan_blur),
                contentDescription = "dukan_pending_blur",
                modifier = Modifier.align(Alignment.Center).blur(30.dp).offset(y = 10.dp)
            )
            Column(
                modifier = Modifier.align(Alignment.Center).padding(horizontal = Theme.spacing._24)
            ) {
                MenaImage(
                    painter = painterResource(Res.drawable.dukan_pending),
                    contentDescription = "dukan_pending",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                val titleText = BuildPendingDukanTitle(
                    brandName = dukanName,
                    titleTemplate = stringResource(Res.string.dukan_request_pending),
                )

                Text(
                    text = titleText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = Theme.spacing._12)
                )

                MenaText(
                    stringResource(Res.string.dukan_waiting_approval),
                    style = Theme.typography.body.small,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(Theme.spacing._2)
                )
            }
        }
    }
}

@Composable
private fun BuildPendingDukanTitle(
    brandName: String,
    titleTemplate: String,
): AnnotatedString {
    return buildAnnotatedString {
        val parts = titleTemplate.split("%s")
        withStyle(Theme.typography.title.small.toSpanStyle()) {
            append(parts[PendingDukan.PREFIX_TITLE.ordinal])
        }
        withStyle(Theme.typography.title.medium.toSpanStyle()) {
            append(brandName)
        }
        withStyle(Theme.typography.title.small.toSpanStyle()) {
            append(parts[PendingDukan.SUFFIX_TITLE.ordinal])
        }
    }
}

@Preview()
@Composable
private fun Preview() {
    MenaTheme {
        PendingDukanScreen(
            dukanName = "Calvin Klein",
            onBackClick = {}
        )
    }
}
