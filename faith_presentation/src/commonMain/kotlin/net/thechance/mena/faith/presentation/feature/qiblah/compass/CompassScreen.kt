package net.thechance.mena.faith.presentation.feature.qiblah.compass

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.arrow_left
import mena.faith_presentation.generated.resources.device_angle_to_qiblah
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.ic_directions
import mena.faith_presentation.generated.resources.ic_location
import mena.faith_presentation.generated.resources.ic_qiblah
import mena.faith_presentation.generated.resources.qibla_direction
import mena.faith_presentation.generated.resources.qiblah
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.qiblah.component.IslamicPattern
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CompassScreen(
    viewModel: CompassViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            is CompassEffect.NavigateBack -> navController.navigateUp()
            CompassEffect.NavigateToAddressesScreen -> navController.navigate(Route.UserAddresses)
        }
    }

    Content(
        uiState = state,
        listener = viewModel
    )
}

@Composable
private fun Content(
    uiState: CompassUiState,
    listener: CompassInteractionListener
) {
    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(Res.string.qiblah),
                contentPadding = PaddingValues(
                    horizontal = Theme.spacing._16, vertical = Theme.spacing._8
                ),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.arrow_left)
                    )
                },
                onLeadingClick = listener::onBackClick,
                trailingContent = {
                    QiblahTopBar(uiState, onChangeLocation = listener::onLocationClick)
                }
            )
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Theme.colorScheme.background.surface),
        ) {

            IslamicPattern(
                modifier = Modifier.align(Alignment.BottomStart)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Theme.spacing._16)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompassView(
                    azimuth = uiState.continuousAzimuth,
                    qiblahDirection = uiState.qiblahAngleValue
                )
                TextAngleToQiblah(
                    qiblahDirection = uiState.angleToQiblah.toInt().toString(),
                    modifier = Modifier.padding(top = Theme.spacing._16)
                )
            }
        }
    }
}

@Composable
private fun CompassView(
    azimuth: Float,
    qiblahDirection: Float
) {
    Box(
        modifier = Modifier.wrapContentSize(),
        contentAlignment = Alignment.Center
    ) {
        val animatedBearing by animateFloatAsState(
            targetValue = -azimuth,
            animationSpec = tween(durationMillis = 300),
            label = "compass_rotation"
        )
        val rotateModifier = remember(animatedBearing) { Modifier.rotate(animatedBearing) }
        Box(
            modifier = Modifier.size(224.dp).border(
                width = 3.dp,
                color = Theme.colorScheme.secondary.secondary,
                shape = CircleShape
            ),
            contentAlignment = Alignment.Center
        ) {
            DirectionPlaceHolder(modifier = rotateModifier)
            Image(
                painter = painterResource(Res.drawable.ic_directions),
                contentDescription = "direction_arrow",
                modifier = rotateModifier
                    .size(128.dp)
            )
        }
        QiblahImage(
            qiblahDirection = qiblahDirection,
            compassBearing = animatedBearing
        )
    }
}

@Composable
private fun DirectionPlaceHolder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        CompassDirection.entries.forEach { direction ->
            Text(
                text = direction.label,
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
                modifier = Modifier.align(direction.alignment)
            )
        }
        CirclesPlaceHolder()
    }
}

@Composable
private fun CirclesPlaceHolder(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(116.dp)) {
        BrownCircle(modifier = Modifier.align(Alignment.TopStart))
        BrownCircle(modifier = Modifier.align(Alignment.TopEnd))
        BrownCircle(modifier = Modifier.align(Alignment.BottomStart))
        BrownCircle(modifier = Modifier.align(Alignment.BottomEnd))
    }
}

@Composable
private fun TextAngleToQiblah(
    qiblahDirection: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = qiblahDirection,
                style = Theme.typography.title.medium,
                color = Theme.colorScheme.shadePrimary,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = " °N",
                style = Theme.typography.title.medium,
                color = Theme.colorScheme.shadePrimary,
                modifier = Modifier.weight(1f)
            )
        }
        Text(
            text = stringResource(Res.string.device_angle_to_qiblah),
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadeSecondary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun QiblahImage(qiblahDirection: Float, compassBearing: Float) {
    Box(
        modifier = Modifier
            .size(264.dp)
            .graphicsLayer {
                rotationZ = compassBearing + qiblahDirection
            },
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_qiblah),
            contentDescription = stringResource(Res.string.qibla_direction),
            modifier = Modifier
                .size(40.dp)
                .background(
                    shape = CircleShape,
                    color = Theme.colorScheme.background.surfaceLow
                )
                .padding(10.dp)
        )
    }
}

@Composable
private fun QiblahTopBar(uiState: CompassUiState, onChangeLocation: () -> Unit) {
    Row(
        modifier = Modifier.background(
            shape = RoundedCornerShape(Theme.radius.full),
            color = Theme.colorScheme.background.surfaceLow
        ).height(Theme.spacing._24)
            .clickable(onClick = onChangeLocation),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_location),
            contentDescription = "icon_location",
            modifier = Modifier
                .padding(start = Theme.spacing._4)
                .size(16.dp)
        )

        Text(
            text = uiState.address,
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.label.small,
            modifier = Modifier.padding(end = Theme.spacing._8),
            maxLines = 1
        )
    }
}

@Composable
private fun BrownCircle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(6.dp)
            .background(
                color = Theme.colorScheme.secondary.secondaryText,
                shape = CircleShape
            )
    )
}

enum class CompassDirection(
    val label: String,
    val alignment: Alignment
) {
    NORTH("N", Alignment.TopCenter),
    SOUTH("S", Alignment.BottomCenter),
    EAST("E", Alignment.CenterEnd),
    WEST("W", Alignment.CenterStart)
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            Content(
                uiState = CompassUiState(
                    continuousAzimuth = 45f,
                    qiblahAngleValue = 120f,
                    angleToQiblah = 75f,
                    address = "Cairo, Egypt",
                ),
                listener = object : CompassInteractionListener {
                    override fun onBackClick() {}
                    override fun onLocationClick() {}
                }
            )
        }
    }
}
