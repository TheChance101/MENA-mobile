package net.thechance.mena.identity.presentation.screen.addresses.component

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.dellisd.spatialk.geojson.Position
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_anchor_my_locations
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.domain.entity.AddressType
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.map.RenderOptions
import org.maplibre.compose.style.BaseStyle

@Composable
fun AddressCard(
    addressType: AddressType,
    isMainAddress: Boolean?,
    addressDetails: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column(
        Modifier.fillMaxWidth()
            .clip(shape = RoundedCornerShape(Theme.radius.lg))
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(Theme.spacing._8).padding(end = 4.dp)

    ) {
        AddressHeader(
            addressType = addressType,
            addressDetails = addressDetails,
            isMainAddress = isMainAddress
        )

        MyAddressesMap(
            modifier = Modifier
                .padding(vertical = Theme.spacing._8)
                .clip(RoundedCornerShape(Theme.radius.md))
                .fillMaxWidth()
                .height(88.dp),
            cameraPosition = CameraPosition(
                target = Position(31.0, 30.0),
                zoom = 15.0
            ),
        )

        AddressActions(onEditClick = onEditClick, onDeleteClick = onDeleteClick)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun MyAddressesMap(
    cameraPosition: CameraPosition,
    modifier: Modifier = Modifier
) {
    val camera = rememberCameraState(firstPosition = cameraPosition)
    LaunchedEffect(Unit) {
        camera.animateTo(
            finalPosition = cameraPosition,
        )
    }
    Box(
        modifier = modifier
    ) {
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = camera,
            baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
            options =
                MapOptions(
                    gestureOptions = GestureOptions.AllDisabled,
                    ornamentOptions = OrnamentOptions.AllDisabled,
                    renderOptions = RenderOptions.Standard
                )
        )
        Image(
            painter = painterResource(Res.drawable.ic_anchor_my_locations),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(22.dp, 28.38.dp)
                .offset(
                    y = Theme.spacing._16
                )
        )
    }
}

object MapStyle {
    const val BRIGHT = "https://tiles.openfreemap.org/styles/bright"
}

@Preview
@Composable
private fun PreviewAddressCard() {
    MenaTheme {
        Column {
            AddressCard(
                addressType = AddressType.Home,
                isMainAddress = true,
                addressDetails = "Karrada, Baghdad 123 St.",
                onEditClick = {},
                onDeleteClick = {}
            )
            AddressCard(
                addressType = AddressType.Office,
                isMainAddress = true,
                addressDetails = "Karrada, Baghdad 123 St.",
                onEditClick = {},
                onDeleteClick = {}
            )
            AddressCard(
                addressType = AddressType.Other,
                isMainAddress = true,
                addressDetails = "Karrada, Baghdad 123 St.",
                onEditClick = {},
                onDeleteClick = {}
            )
        }
    }
}