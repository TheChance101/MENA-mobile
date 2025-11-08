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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cash.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
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
import net.thechance.mena.faith.presentation.utils.MapView
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@Composable
internal fun NearbyMosquesScreen(
    viewModel: NearbyMosquesViewModel = koinViewModel(),
    mapNavigator: MapNavigator = koinInject(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    LaunchedEffect(Unit) {
        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

        savedStateHandle?.getStateFlow<String?>("add_mosque_message", null)
            ?.collect { successMessage ->
                successMessage?.let {
                    viewModel.showSuccessMessage(successMessage)
                    savedStateHandle.remove<String?>("add_mosque_message")
                }
            }
    }

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            NearbyMosquesEffect.NavigateBack -> {}
            NearbyMosquesEffect.NavigateToAddMosque -> navController.navigate(Route.CreateMosqueRoute)
            NearbyMosquesEffect.NavigateToAddressesScreen -> navController.navigate(Route.UserAddresses)
            is NearbyMosquesEffect.NavigateToMap -> mapNavigator.openMapAtCoordinate(coordinate = effect.coordinate)
        }
    }

    Content(uiState = state, listener = viewModel)
}

@OptIn(ExperimentalUuidApi::class)
@Composable
private fun Content(
    uiState: NearbyMosquesMapUiState,
    listener: NearbyMosquesInteractionListener
) {
    val searchResultsPaging = uiState.mosquesSearchResults?.collectAsLazyPagingItems()

    val markers: List<MosqueUiState> = buildList {
        uiState.selectedMosque?.let { selected ->
            add(selected)
        }

        if (uiState.selectedMosque == null) {
            addAll(uiState.mosques)

            if (searchResultsPaging != null && searchResultsPaging.itemCount > 0) {
                (0 until searchResultsPaging.itemCount).forEach { index ->
                    searchResultsPaging[index]?.let { mosque ->
                        if (none { it.id == mosque.id }) add(mosque)
                    }
                }
            }
        }
    }

    val allMosques = buildList {
        addAll(uiState.mosques)
        if (searchResultsPaging != null && searchResultsPaging.itemCount > 0) {
            (0 until searchResultsPaging.itemCount).forEach { index ->
                searchResultsPaging[index]?.let { mosque ->
                    if (none { it.id == mosque.id }) add(mosque)
                }
            }
        }
    }

    var mapCenter by remember { mutableStateOf(uiState.centerOfMap) }

    LaunchedEffect(uiState.centerOfMap) {
        mapCenter = uiState.centerOfMap
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
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(onClick = listener::onAddMosqueClick),
                        painter = painterResource(Res.drawable.ic_add),
                        contentDescription = stringResource(Res.string.add),
                    )
                },
                onLeadingClick = listener::onBackClick
            )
        },
        overlays = {
            bottomSheet(isVisible = uiState.isSearchResultsBottomSheetVisible) { isVisible ->
                SearchResultsBottomSheet(
                    isVisible = isVisible,
                    mosques = uiState.mosquesSearchResults ?: emptyFlow(),
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
        Box(modifier = Modifier.fillMaxSize()) {
            val coroutineScope = rememberCoroutineScope()
            var lastCenter by remember { mutableStateOf<Coordinate?>(null) }

            MapView(
                modifier = Modifier.fillMaxSize(),
                centerLatitude = mapCenter?.latitude ?: 0.0,
                centerLongitude = mapCenter?.longitude ?: 0.0,
                zoomLevel = 15.0,
                markers = markers,
                onMarkerClick = { marker ->
                    val mosque = allMosques.find { it.id == marker.id }
                    mosque?.let { listener.selectMosque(it) }
                },
                onMapClick = { lat, lon ->
                    listener.mapPositionChanged(Coordinate(lat, lon))
                },
                onCameraMove = { lat, lon ->
                    val newCenter = Coordinate(lat, lon)
                    lastCenter = newCenter
                    listener.mapPositionChanged(newCenter)
                }
            )

            LaunchedEffect(lastCenter) {
                listener.changeSearchButtonVisibility(false)
                delay(800L)
                listener.changeSearchButtonVisibility(true)
            }
            val keyboardController = LocalSoftwareKeyboardController.current

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
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            listener.onSearchSubmit()
                        }
                    )
                )

                if (uiState.isSearchButtonVisible) {
                    SearchMosquesButton(onClick = {
                        listener.onSearchByCoordinatesClick(
                            coordinate = Coordinate(
                                latitude = mapCenter?.latitude ?: 0.0,
                                longitude = mapCenter?.longitude ?: 0.0
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
                    .clip(RoundedCornerShape(Theme.radius.md))
                    .background(color = Theme.colorScheme.primary.primary)
                    .clickable {
                        listener.getUserLocation()
                        coroutineScope.launch {
                            delay(300)
                            mapCenter = uiState.centerOfMap
                        }
                    }
                    .padding(horizontal = Theme.spacing._16, vertical = 14.dp)
                    .align(Alignment.BottomStart),
                painter = painterResource(Res.drawable.ic_gps),
                contentDescription = stringResource(Res.string.icon_location)
            )
        }
    }
}

@Composable
private fun SearchMosquesButton(onClick: () -> Unit) {
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
        modifier = Modifier.clip(RoundedCornerShape(Theme.radius.xl))
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
            override fun onSearchSubmit() {}
            override fun changeSearchButtonVisibility(isVisible: Boolean) {}
            override fun onDismissSearchBottomSheet() {}
            override fun selectMosque(mosque: MosqueUiState) {}
            override fun unselectMosque() {}
            override fun showSuccessMessage(message: String) {}
        }
    )
}