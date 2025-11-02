package net.thechance.mena.faith.presentation.feature.mosque

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.dellisd.spatialk.geojson.Position
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_outline_search
import mena.faith_presentation.generated.resources.search_area
import mena.faith_presentation.generated.resources.search_hint
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.utils.MapStyle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.style.BaseStyle

@Composable
internal fun NearbyMosquesScreen(
    viewModel: NearbyMosquesViewModel = koinViewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        uiState = state,
        listener = viewModel
    )
}

@Composable
private fun Content(
    uiState: NearbyMosquesMapUiState,
    listener: NearbyMosquesInteractionListener
) {
    val initialCameraPosition = CameraPosition(
        target = Position(
            longitude = uiState.centerOfMap?.longitude ?: 0.0,
            latitude = uiState.centerOfMap?.latitude ?: 0.0
        ),
        zoom = 14.0
    )
    val cameraState = rememberCameraState(firstPosition = initialCameraPosition)

    LaunchedEffect(cameraState) {
        snapshotFlow { cameraState.position }
            .collect {
                listener.mapPositionChanged(
                    coordinate = Coordinate(
                        latitude = cameraState.position.target.latitude,
                        longitude = cameraState.position.target.longitude
                    )
                )
            }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState,
            baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
        )

        if (uiState.isSearchButtonVisible) {
            SearchMosquesButton(
                onClick = {
                    val target = cameraState.position.target
                    listener.onSearchByCoordinatesClick(
                        coordinate = Coordinate(
                            latitude = target.latitude,
                            longitude = target.longitude
                        )
                    )
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = Theme.spacing._32)
            )
        }
        TextField(
            value = uiState.query,
            hint = stringResource(Res.string.search_hint),
            leadingIcon = painterResource(Res.drawable.ic_outline_search),
            leadingIconTint = Theme.colorScheme.shadeSecondary,
            onValueChanged = listener::onQueryChange,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = Theme.spacing._16)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun SearchMosquesButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        content = {
            Text(
                text = stringResource(Res.string.search_area),
                style = Theme.typography.label.small,
                color = Theme.colorScheme.primary.onPrimary
            )
        },
        containerColor = Theme.colorScheme.primary.primary,
        contentPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp),
        modifier = modifier.clip(shape = RoundedCornerShape(Theme.radius.xl))
    )
}

@Composable
@Preview
private fun NearbyMosquesScreenPreview() {
    Content(
        uiState = NearbyMosquesMapUiState(isSearchButtonVisible = true),
        listener = object : NearbyMosquesInteractionListener {
            override fun onBackClick() {}
            override fun onAddMosqueClick() {}
            override fun onCurrentUserLocationClick() {}
            override fun onViewMosqueDetailsClick(mosque: MosqueUiState) {}
            override fun onViewMosqueOnMapClick(coordinate: Coordinate) {}
            override fun onSearchByCoordinatesClick(coordinate: Coordinate) {}
            override fun mapPositionChanged(coordinate: Coordinate) {}
            override fun onQueryChange(query: String) {}
        }
    )
}
