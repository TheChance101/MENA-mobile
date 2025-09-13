package net.thechance.mena.dukan.presentation.screen.pendingDukanScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
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
import mena.dukan_presentation.generated.resources.dukan_pending
import mena.dukan_presentation.generated.resources.dukan_request_pending
import mena.dukan_presentation.generated.resources.dukan_waiting_approval
import mena.dukan_presentation.generated.resources.dukan_blur
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.my_dukan
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
private fun pendingDukanScreen(
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
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = null,
                        modifier = Modifier.clickable(onClick = onBackClick)
                    )
                },
                modifier = Modifier.align(Alignment.TopStart)
            )
            Image(
                painter = painterResource(Res.drawable.dukan_blur),
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center).blur(30.dp).offset(y = (-20).dp)
            )
            Column(
                modifier = Modifier.align(Alignment.Center).padding(horizontal = Theme.spacing._24)
            ) {
                Image(
                    painter = painterResource(Res.drawable.dukan_pending),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                val titleText = buildPendingDukanTitle(
                    brandName = dukanName,
                    titleTemplate = stringResource(Res.string.dukan_request_pending),
                )

                Text(
                    text = titleText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = Theme.spacing._12)
                )

                Text(
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
private fun buildPendingDukanTitle(
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
        pendingDukanScreen(
            dukanName = "Calvin Klein",
            onBackClick = {}
        )
    }
}
