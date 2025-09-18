package net.thechance.mena.dukan.presentation.screen.pendingDukanScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
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
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.image.Image
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.componetns.AnnotatedText
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PendingDukanScreen(
    dukanName: String,
    onBackClick: () -> Unit,
) {
    val titleText = BuildPendingDukanTitle(
        brandName = dukanName,
        titleTemplate = stringResource(Res.string.dukan_request_pending),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface)

    ) {
        AppBar(
            title = stringResource(Res.string.my_dukan),
            leadingContent = {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_left),
                    contentDescription = "left_arrow",
                )
            },
            onLeadingClick = onBackClick
        )
        Spacer(modifier = Modifier.weight(1f))
        LazyColumn(
            contentPadding = PaddingValues(Theme.spacing._24)
        ) {
            item(key = "content") {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.dukan_blur),
                        contentDescription = "dukan_pending_blur",
                        modifier = Modifier
                            .blur(
                                radius = 30.dp,
                                edgeTreatment = BlurredEdgeTreatment.Unbounded
                            )
                            .offset(y = 20.dp)
                            .align(Alignment.BottomCenter)
                    )

                    Image(
                        painter = painterResource(Res.drawable.dukan_pending),
                        contentDescription = "dukan_pending",
                    )
                }

                AnnotatedText(
                    text = titleText,
                    style = TextStyle(textAlign = TextAlign.Center),
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
        Spacer(modifier = Modifier.weight(1f))
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

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        PendingDukanScreen(
            dukanName = "Calvin Klein",
            onBackClick = {}
        )
    }
}
