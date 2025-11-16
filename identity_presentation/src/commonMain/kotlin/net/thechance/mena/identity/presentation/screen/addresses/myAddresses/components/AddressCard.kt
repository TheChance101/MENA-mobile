package net.thechance.mena.identity.presentation.screen.addresses.myAddresses.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.github.dellisd.spatialk.geojson.Position
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_anchor
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.designsystem.presentation.util.rippleIndication
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.presentation.components.util.MapStyle
import net.thechance.mena.identity.presentation.util.animation.shimmerLoading
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
    onClickAddress: () -> Unit,
    longitude: Double?,
    latitude: Double?,
    animateToCurrentLocation: Boolean,
    isDeleting: Boolean = false,
    isActivating: Boolean = false,
) {
    val scale by animateFloatAsState(
        targetValue = if (isDeleting) 0f else 1f,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearEasing
        ),
        label = "DeleteScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isDeleting) 0f else 1f,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearEasing
        ),
        label = "DeleteAlpha"
    )

    Column(
        Modifier.fillMaxWidth()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                alpha = alpha,
                transformOrigin = TransformOrigin.Center
            )
            .background(
                Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.lg)
            )
            .clip(shape = RoundedCornerShape(Theme.radius.lg))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rippleIndication(),
                onClick = onClickAddress,
                enabled = isMainAddress != true && !isDeleting && !isActivating
            )
            .padding(Theme.spacing._8)
            .padding(end = 4.dp)
            .then(
                if (isActivating) Modifier.shimmerLoading(isLoading = true) else Modifier
            )
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
            animateToCurrentLocation = animateToCurrentLocation,
            longitude = longitude,
            latitude = latitude,
        )


        AddressActions(
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick,
            isDeleting = isDeleting,
            isActivating = isActivating
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun MyAddressesMap(
    animateToCurrentLocation: Boolean,
    longitude: Double?,
    latitude: Double?,
    modifier: Modifier = Modifier
) {
    val defaultPosition = remember { Position(longitude = 20.0, latitude = 20.0) }
    val targetPosition = remember(longitude, latitude) {
        if (longitude != null && latitude != null) {
            Position(longitude = longitude, latitude = latitude)
        } else null
    }

    val camera = rememberCameraState(
        firstPosition = CameraPosition(
            target = targetPosition ?: defaultPosition,
            zoom = 14.0
        )
    )

    LaunchedEffect(targetPosition, animateToCurrentLocation) {
        if (targetPosition != null && animateToCurrentLocation) {
            camera.animateTo(
                finalPosition = CameraPosition(
                    target = targetPosition,
                    zoom = 14.0
                ),
            )
        }
    }

    Box(modifier = modifier) {
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = camera,
            baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
            options = MapOptions(
                gestureOptions = GestureOptions.AllDisabled,
                ornamentOptions = OrnamentOptions.AllDisabled,
                renderOptions = RenderOptions.Standard
            )
        )

        Image(
            painter = painterResource(Res.drawable.ic_anchor),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 28.dp)
                .size(28.dp)
        )
    }
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
                onDeleteClick = {},
                onClickAddress = {},
                longitude = 20.0,
                latitude = 20.0,
                animateToCurrentLocation = false
            )
            AddressCard(
                addressType = AddressType.Office,
                isMainAddress = true,
                addressDetails = "Karrada, Baghdad 123 St.",
                onEditClick = {},
                onClickAddress = {},
                onDeleteClick = {},
                longitude = 20.0,
                latitude = 20.0,
                animateToCurrentLocation = false
            )
            AddressCard(
                addressType = AddressType.Other(""),
                isMainAddress = true,
                addressDetails = "Karrada, Baghdad 123 St.",
                onEditClick = {},
                onClickAddress = {},
                onDeleteClick = {},
                longitude = 20.0,
                latitude = 20.0,
                animateToCurrentLocation = false
            )
        }
    }
}