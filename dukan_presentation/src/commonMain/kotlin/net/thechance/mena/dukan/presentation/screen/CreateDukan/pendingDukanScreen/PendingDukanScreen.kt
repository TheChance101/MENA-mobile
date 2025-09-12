package net.thechance.mena.dukan.presentation.screen.createDukan.pendingDukanScreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.dukan_blur
import mena.dukan_presentation.generated.resources.ellipse_1
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.test2svg
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PendingDukanScreen(
    viewModel: PendingDukanViewModel
) {
    val state by viewModel.state.collectAsState()
    Content(state)
}

@Composable
private fun Content(
    uiState: PendingDukanUiState,
) {
    MenaTheme {
        Box(
            modifier = Modifier.fillMaxSize().background(Theme.colorScheme.background.surface),
        ) {
            AppBar(
                title = uiState.appBarTitle,
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = null
                    )
                },
                modifier = Modifier.align(Alignment.TopStart)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center).padding(horizontal = 24.dp)
            ) {
                Box {
                    Image(
                        painter = painterResource(Res.drawable.test2svg),
                        contentDescription = null,
                    )
                    Image(
                        painter = painterResource(Res.drawable.ellipse_1),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.BottomCenter).blur(40.dp)
                    )
                }
                val titleText = buildPendingDukanTitle(
                    brandName = uiState.brandName,
                    titleTemplate = uiState.titleTemplate,
                )

                Text(
                    text = titleText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp)
                )

                Text(
                    uiState.subtitle,
                    style = Theme.typography.body.small,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}


@Composable
fun BlurredCircleCanvas(
    circleSize: Dp = 40.dp,
    blurStrength: Int = 6,
    color: Color = Theme.colorScheme.primary.primary
) {
    val density = LocalDensity.current
    val radiusPx = with(density) { (circleSize / 2).toPx() }

    Canvas(modifier = Modifier.size(circleSize * 2)) {
        for (i in blurStrength downTo 1) {
            val alpha = 0.05f * i
            val scale = 1f + (i * 0.15f)

            drawCircle(
                color = color.copy(alpha = alpha),
                radius = radiusPx * scale,
                center = center
            )
        }

        // Core circle
        drawCircle(
            color = color,
            radius = radiusPx,
            center = center
        )
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
private fun PendingDukanScreenPreview() {
    MenaTheme {
        Content(
            uiState = PendingDukanUiState(
                brandName = "Calvin Klein"
            )
        )
    }
}
