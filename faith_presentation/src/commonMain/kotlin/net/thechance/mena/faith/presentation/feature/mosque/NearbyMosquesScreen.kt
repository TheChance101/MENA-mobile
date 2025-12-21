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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.emptyFlow
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.add_mosque_message
import mena.faith_presentation.generated.resources.ic_gps
import mena.faith_presentation.generated.resources.ic_outline_search
import mena.faith_presentation.generated.resources.icon_location
import mena.faith_presentation.generated.resources.no_nearby_mosques_found
import mena.faith_presentation.generated.resources.search_area
import mena.faith_presentation.generated.resources.search_hint
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.components.FaithSnackBar
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.mosque.component.MapView
import net.thechance.mena.faith.presentation.feature.mosque.component.MosqueDetailsBottomSheet
import net.thechance.mena.faith.presentation.feature.mosque.component.NearbyMosqueTopbar
import net.thechance.mena.faith.presentation.feature.mosque.component.NoMosquesFoundCard
import net.thechance.mena.faith.presentation.feature.mosque.component.SearchResultsBottomSheet
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route
import net.thechance.mena.faith.presentation.utils.MapNavigator
import org.jetbrains.compose.resources.StringResource
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
    val snackBarState by viewModel.snackBarState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val successMessage by savedStateHandle?.getStateFlow<String?>("add_mosque_message", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            viewModel.showSuccessMessage(Res.string.add_mosque_message)
            savedStateHandle?.remove<String?>("add_mosque_message")
        }
    }

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            NearbyMosquesEffect.NavigateBack -> navController.navigateUp()
            NearbyMosquesEffect.NavigateToAddMosque -> navController.navigate(Route.CreateMosqueRoute)
            NearbyMosquesEffect.NavigateToAddressesScreen -> navController.navigate(Route.UserAddresses)
            is NearbyMosquesEffect.NavigateToMap -> mapNavigator.openMapAtCoordinate(coordinate = effect.coordinate)
        }
    }
    Content(uiState = state, snackBarState = snackBarState, listener = viewModel)
}

@OptIn(ExperimentalUuidApi::class)
@Composable
private fun Content(
    uiState: NearbyMosquesMapUiState,
    snackBarState: SnackBarState,
    listener: NearbyMosquesInteractionListener
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Scaffold(
        statusBarColor = Theme.colorScheme.background.surfaceLow,
        topBar = {
            NearbyMosqueTopbar(
                onBackClick = listener::onBackClick,
                onAddMosqueClick = listener::onAddMosqueClick,
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
        },
        snakeBar = {
            FaithSnackBar(
                message = snackBarState.message,
                isVisible = snackBarState.isVisible,
                status = snackBarState.status,
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.centerOfMap?.latitude != null && uiState.centerOfMap?.longitude != null) {
                MapView(
                    modifier = Modifier.fillMaxSize(),
                    centerLatitude = uiState.centerOfMap.latitude,
                    centerLongitude = uiState.centerOfMap.longitude,
                    zoomLevel = 18.0,
                    markers = uiState.mosques,
                    canMove = uiState.canMove,
                    onMarkerClick = {
                        listener.selectMosque(it)
                    },
                    onCameraMove = { _, _ ->
                        listener.onCameraMove()
                    },
                    onMapIdle = listener::onMapIdle
                )
            }
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
                AnimatedVisibility(
                    visible = uiState.isSearchButtonVisible,
                    enter = fadeIn(tween()),
                    exit = fadeOut(tween()),
                ) {
                    SearchMosquesButton(onClick = {
                        listener.onSearchByCoordinates(
                            coordinate = MosqueUiState.Coordinate(
                                latitude = uiState.centerOfMap?.latitude ?: 0.0,
                                longitude = uiState.centerOfMap?.longitude ?: 0.0
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
                    }
                    .padding(horizontal = Theme.spacing._16, vertical = 14.dp)
                    .align(Alignment.BottomStart),
                painter = painterResource(Res.drawable.ic_gps),
                contentDescription = stringResource(Res.string.icon_location),
                tint = Theme.colorScheme.shadeTertiary
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

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            Content(
                uiState = NearbyMosquesMapUiState(isSearchButtonVisible = true),
                listener = object : NearbyMosquesInteractionListener {
                    override fun onBackClick() {}
                    override fun onAddMosqueClick() {}
                    override fun getUserLocation() {}
                    override fun onViewMosqueDetailsClick(mosque: MosqueUiState) {}
                    override fun onViewOnMapClick(coordinate: MosqueUiState.Coordinate) {}
                    override fun onSearchByCoordinates(coordinate: MosqueUiState.Coordinate) {}
                    override fun onSearchResultClick(mosque: MosqueUiState) {}
                    override fun onQueryChange(query: String) {}
                    override fun onSearchSubmit() {}
                    override fun onDismissSearchBottomSheet() {}
                    override fun selectMosque(mosque: MosqueUiState) {}
                    override fun unselectMosque() {}
                    override fun showSuccessMessage(message: StringResource) {}
                    override fun onCameraMove() {}
                    override fun onMapIdle(latitude: Double, longitude: Double) {}
                },
                snackBarState = SnackBarState()
            )
        }
    }
}
