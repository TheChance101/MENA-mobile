package net.thechance.mena.faith.presentation.feature.mosque.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_edit
import mena.faith_presentation.generated.resources.mosque_pin
import net.thechance.mena.designsystem.presentation.component.button.FabButton
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.utils.MapStyle
import org.jetbrains.compose.resources.painterResource
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.map.RenderOptions
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult

@Composable
fun AddLocationMap(
    isMapClickable: Boolean,
    cameraPosition: CameraPosition,
    onEditClick: () -> Unit,
    onClickMap: () -> Unit,
    modifier: Modifier = Modifier
) {

    val camera = rememberCameraState(firstPosition = cameraPosition)

    LaunchedEffect(cameraPosition) {
        camera.animateTo(
            finalPosition = cameraPosition,
        )
    }

    Box(modifier = modifier) {
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = camera,
            baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
            onMapClick = { _, _ ->
                if (isMapClickable) onClickMap()
                ClickResult.Consume
            },
            options =
                MapOptions(
                    gestureOptions = GestureOptions.AllDisabled,
                    ornamentOptions = OrnamentOptions.AllDisabled,
                    renderOptions = RenderOptions.Standard
                )
        )

        AnimatedVisibility(
            visible = !isMapClickable,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Image(
                painter = painterResource(Res.drawable.mosque_pin),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.padding(bottom = 58.dp).height(58.dp)
            )
        }

        AnimatedVisibility(
            visible = !isMapClickable,
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            FabButton(
                painter = painterResource(Res.drawable.ic_edit),
                contentPadding = PaddingValues(horizontal = Theme.spacing._16, vertical = 14.dp),
                modifier = Modifier
                    .padding(bottom = Theme.spacing._4, end = Theme.spacing._4)
                    .align(Alignment.BottomEnd),
                onClick = onEditClick
            )
        }
    }
}