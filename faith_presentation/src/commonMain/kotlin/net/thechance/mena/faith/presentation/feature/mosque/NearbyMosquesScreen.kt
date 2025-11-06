package net.thechance.mena.faith.presentation.feature.mosque

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import kotlinx.coroutines.delay
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.add
import mena.faith_presentation.generated.resources.arrow_left
import mena.faith_presentation.generated.resources.ic_add
import mena.faith_presentation.generated.resources.ic_gps
import mena.faith_presentation.generated.resources.ic_outline_search
import mena.faith_presentation.generated.resources.icon_location
import mena.faith_presentation.generated.resources.nearby_mosques
import mena.faith_presentation.generated.resources.no_nearby_mosques_found
import mena.faith_presentation.generated.resources.search_area
import mena.faith_presentation.generated.resources.search_hint
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.feature.mosque.component.MosqueDetailsBottomSheet
import net.thechance.mena.faith.presentation.feature.mosque.component.NoMosquesFoundCard
import net.thechance.mena.faith.presentation.feature.mosque.component.SearchResultsBottomSheet
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route
import net.thechance.mena.faith.presentation.utils.MapNavigator
import net.thechance.mena.faith.presentation.utils.MapStyle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.style.BaseStyle

@Composable
internal fun NearbyMosquesScreen(
    mapNavigator: MapNavigator = koinInject(),
    viewModel: NearbyMosquesViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current
    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            NearbyMosquesEffect.NavigateBack -> {
                //TODO()
            }

            NearbyMosquesEffect.NavigateToAddMosque -> {
                //TODO()
            }

            NearbyMosquesEffect.NavigateToAddressesScreen -> navController.navigate(Route.UserAddresses)

            is NearbyMosquesEffect.NavigateToMap -> mapNavigator.openMapAtCoordinate(coordinate = effect.coordinate)
        }
    }
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
                if (!cameraState.isCameraMoving) {
                    listener.mapPositionChanged(
                        coordinate = Coordinate(
                            latitude = cameraState.position.target.latitude,
                            longitude = cameraState.position.target.longitude
                        )
                    )
                }
            }
    }
    LaunchedEffect(cameraState.isCameraMoving) {
        listener.changeSearchButtonVisibility(!cameraState.isCameraMoving)
        delay(500)
        listener.changeSearchButtonVisibility(true)
    }
    LaunchedEffect(uiState.centerOfMap) {
        cameraState.animateTo(initialCameraPosition)
    }
    Scaffold(
        topBar = {
            AppBar(
                modifier = Modifier.background(Theme.colorScheme.background.surfaceLow),
                title = stringResource(Res.string.nearby_mosques),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_left),
                        contentDescription = stringResource(Res.string.arrow_left)
                    )
                },
                trailingContent = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(Res.drawable.ic_add),
                        contentDescription = stringResource(Res.string.add)
                    )
                },
                onLeadingClick = listener::onBackClick
            )
        },
        overlays = {
            bottomSheet(isVisible = uiState.isSearchResultsBottomSheetVisible) { isVisible ->
                SearchResultsBottomSheet(
                    isVisible = isVisible,
                    mosques = uiState.mosquesSearchResults,
                    onMosqueClick = listener::onSearchResultClick,
                    onDismiss = listener::onDismissSearchBottomSheet
                )
            }
            bottomSheet(isVisible = uiState.isMosqueBottomSheetVisible) { isVisible ->
                uiState.selectedMosque?.let { mosque ->
                    MosqueDetailsBottomSheet(
                        isVisible = isVisible,
                        mosque = mosque,
                        onNavigationClick = {
                            listener.onViewOnMapClick(mosque.coordinate)
                        },
                        onDismiss = listener::unselectMosque
                    )
                }
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            MaplibreMap(
                modifier = Modifier.fillMaxSize(),
                cameraState = cameraState,
                baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = Theme.spacing._12, vertical = 10.dp)
                    .align(Alignment.TopCenter),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = uiState.query,
                    hint = stringResource(Res.string.search_hint),
                    leadingIcon = painterResource(Res.drawable.ic_outline_search),
                    leadingIconTint = Theme.colorScheme.shadeSecondary,
                    onValueChanged = listener::onQueryChange,
                    modifier = Modifier.fillMaxWidth()
                )
                if (uiState.isSearchButtonVisible) {
                    SearchMosquesButton(onClick = {
                        listener.onSearchByCoordinatesClick(
                            coordinate = Coordinate(
                                latitude = cameraState.position.target.latitude,
                                longitude = cameraState.position.target.longitude
                            )
                        )
                    })
                }
            }
            AnimatedVisibility(
                visible = uiState.isNoMosquesCardVisible,
                enter = fadeIn(tween()),
                exit = fadeOut(tween()),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                NoMosquesFoundCard(
                    message = stringResource(Res.string.no_nearby_mosques_found),
                    modifier = Modifier
                        .padding(horizontal = Theme.spacing._12)
                        .padding(bottom = Theme.spacing._24)
                )
            }
            Icon(
                modifier = Modifier
                    .padding(Theme.spacing._16)
                    .clip(shape = RoundedCornerShape(Theme.radius.md))
                    .background(color = Theme.colorScheme.primary.primary)
                    .clickable(onClick = listener::getUserLocation)
                    .padding(horizontal = Theme.spacing._16, vertical = 14.dp)
                    .align(Alignment.BottomStart),
                painter = painterResource(Res.drawable.ic_gps),
                contentDescription = stringResource(Res.string.icon_location)
            )
        }
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
            override fun getUserLocation() {}
            override fun onViewMosqueDetailsClick(mosque: MosqueUiState) {}
            override fun onViewOnMapClick(coordinate: Coordinate) {}
            override fun onSearchByCoordinatesClick(coordinate: Coordinate) {}
            override fun onSearchResultClick(mosque: MosqueUiState) {}
            override fun mapPositionChanged(coordinate: Coordinate) {}
            override fun onQueryChange(query: String) {}
            override fun changeSearchButtonVisibility(isVisible: Boolean) {}
            override fun onDismissSearchBottomSheet() {}
            override fun selectMosque(mosque: MosqueUiState) {}
            override fun unselectMosque() {}
        }
    )
}