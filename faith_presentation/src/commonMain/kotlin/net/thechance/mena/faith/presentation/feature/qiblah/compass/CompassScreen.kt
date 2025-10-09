package net.thechance.mena.faith.presentation.feature.qiblah.compass

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_circle
import mena.faith_presentation.generated.resources.ic_direction
import mena.faith_presentation.generated.resources.ic_location
import mena.faith_presentation.generated.resources.ic_qiblah
import mena.faith_presentation.generated.resources.qiblah
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.component.BackIcon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CompassScreen(
    viewModel: CompassViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Content(
        uiState = state,
        listener = viewModel
    )
}

@Composable
private fun Content(
    uiState: CompassScreenState,
    listener: CompassViewModel
) {

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(Res.string.qiblah),
                contentPadding = PaddingValues(
                    horizontal = Theme.spacing._16, vertical = Theme.spacing._8
                ),
                leadingContent = { BackIcon() },
                onLeadingClick = listener::onBackClick,
                trailingContent = {
                    Row(
                        modifier = Modifier.background(
                            shape = RoundedCornerShape(Theme.radius.full),
                            color = Theme.colorScheme.primary.onPrimary
                        ).height(24.dp)
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ic_location),
                            contentDescription = null,
                            modifier = Modifier.padding(start = 4.dp).padding(4.dp)
                                .size(16.dp)
                                .align(Alignment.CenterVertically)
                        )

                        Text(
                            text = "Baghdad, Iraq",
                            color = Theme.colorScheme.shadePrimary,
                            style = Theme.typography.label.small,
                            modifier = Modifier.align(Alignment.CenterVertically)
                                .padding(end = 8.dp)
                        )
                    }
                }
            )
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .background(color = Theme.colorScheme.background.surface)
                .padding(horizontal = Theme.spacing._16), verticalArrangement = Arrangement.Center
        ) {
            CompassView(
                azimuth = uiState.azimuth,
                qiblahDirection = uiState.qiblahDirection
            )
        }

    }
}

@Composable
private fun CompassView(
    azimuth: Float,
    qiblahDirection: Float
) {
    val animatedBearing by animateFloatAsState(
        targetValue = azimuth,
        animationSpec = tween(durationMillis = 300),
        label = "compass_rotation"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(224.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 3.dp,
                        color = Theme.colorScheme.secondary.secondary,
                        shape = CircleShape
                    )
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "N",
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = 16.dp)
                )

                Image(
                    painter = painterResource(Res.drawable.ic_circle),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = (-48).dp, y = (-64).dp)
                )

                Text(
                    text = "S",
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-16).dp)
                )

                Image(
                    painter = painterResource(Res.drawable.ic_circle),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = (48).dp, y = (-64).dp)
                )

                Text(
                    text = "E",
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = (-20).dp)
                )

                Image(
                    painter = painterResource(Res.drawable.ic_circle),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = (-48).dp, y = (64).dp)
                )

                Text(
                    text = "W",
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = 20.dp)
                )

                Image(
                    painter = painterResource(Res.drawable.ic_circle),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = (48).dp, y = (64).dp)
                )
            }

            Image(
                painter = painterResource(Res.drawable.ic_direction),
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .rotate(animatedBearing)
            )

            TextAngleToQiblah(azimuth, qiblahDirection)
        }

        QiblahImage(qiblahDirection)
    }
}


@Composable
private fun BoxScope.TextAngleToQiblah(azimuth: Float, qiblahDirection: Float) {
    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .offset(y = 64.dp),
        Arrangement.Center
    ) {
        Text(
            text = "${azimuth - qiblahDirection}°N",
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Device’s angle to qiblah",
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadeSecondary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun QiblahImage(qiblahDirection: Float) {
    Box(
        modifier = Modifier
            .size(270.dp)
            .graphicsLayer {
                rotationZ = qiblahDirection
            },
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_qiblah),
            contentDescription = "Qibla direction",
            modifier = Modifier
                .size(40.dp)
                .background(
                    shape = CircleShape,
                    color = Theme.colorScheme.primary.onPrimary
                )
                .padding(10.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CompassScreenPreview() {
    MenaTheme {
        Content(uiState = CompassScreenState(), listener = CompassViewModel())
    }
}