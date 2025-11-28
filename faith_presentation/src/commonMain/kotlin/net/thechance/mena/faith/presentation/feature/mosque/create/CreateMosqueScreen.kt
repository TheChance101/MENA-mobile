package net.thechance.mena.faith.presentation.feature.mosque.create

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.attafitamim.krop.core.images.ImageSrc
import io.github.dellisd.spatialk.geojson.Position
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.add
import mena.faith_presentation.generated.resources.add_new_mosque
import mena.faith_presentation.generated.resources.back
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.ic_edit
import mena.faith_presentation.generated.resources.ic_location
import mena.faith_presentation.generated.resources.ic_mosque
import mena.faith_presentation.generated.resources.image_size_required
import mena.faith_presentation.generated.resources.location
import mena.faith_presentation.generated.resources.mosque_address
import mena.faith_presentation.generated.resources.mosque_image_description
import mena.faith_presentation.generated.resources.mosque_name
import mena.faith_presentation.generated.resources.mosque_pin
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.FabButton
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.mosque.component.UploadImageContainer
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route
import net.thechance.mena.faith.presentation.utils.MapStyle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult

@Composable
internal fun CreateMosqueScreen(
    viewModel: CreateMosqueViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            CreateMosqueEffect.NavigateBack -> {
                uiState.successMessage?.let {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("add_mosque_message", uiState.successMessage)
                }
                navController.popBackStack()
            }

            CreateMosqueEffect.NavigateToUploadImageRoute -> navController.navigate(Route.UploadImageRoute)
            CreateMosqueEffect.NavigateToAddressesScreen -> navController.navigate(Route.UserAddresses)
        }
    }
    Content(uiState = uiState, listener = viewModel)
}


@Composable
private fun Content(
    uiState: CreateMosqueUiState,
    listener: CreateMosqueInteractionListener
) {
    Scaffold(
        topBar = { CreateMosqueAppBar(onBackClick = listener::onBackClick) },
        bottomBar = {
            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.spacing._16),
                text = stringResource(Res.string.add),
                onClick = listener::onAddClick,
                isEnabled = uiState.isButtonEnabled,
                contentPadding = PaddingValues(vertical = Theme.spacing._12)
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Theme.spacing._16)
        ) {
            item { MosqueLocationHeader(uiState, listener) }
            item { MosqueLocationMapSection(uiState, listener) }
            item { MosqueAddressSection(uiState, listener) }
            item { UploadMosqueImage(uiState, listener) }
        }
    }
}


@Composable
private fun MosqueLocationHeader(
    uiState: CreateMosqueUiState,
    listener: CreateMosqueInteractionListener
) {
    Text(
        text = stringResource(Res.string.mosque_name),
        style = Theme.typography.title.medium,
        color = Theme.colorScheme.shadePrimary,
        modifier = Modifier.padding(bottom = Theme.spacing._4)
    )
    TextField(
        value = uiState.name,
        onValueChanged = listener::onNameChange,
        hint = "",
        leadingIcon = painterResource(Res.drawable.ic_mosque),
    )
}

@Composable
private fun MosqueLocationMapSection(
    uiState: CreateMosqueUiState,
    listener: CreateMosqueInteractionListener
) {
    uiState.location?.let { coordinate ->
        val initialCameraPosition = CameraPosition(
            target = Position(
                longitude = coordinate.longitude,
                latitude = coordinate.latitude
            ),
            zoom = 14.0
        )
        val cameraState = rememberCameraState(firstPosition = initialCameraPosition)

        Text(
            modifier = Modifier.padding(top = Theme.spacing._16, bottom = Theme.spacing._4),
            text = stringResource(Res.string.location),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(244.dp),
            contentAlignment = Alignment.TopStart
        ) {
            MaplibreMap(
                cameraState = cameraState,
                baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
                onMapClick = { position, offset ->
                    listener.onMapClick(position = position, offset = offset)
                    ClickResult.Pass
                },
                options = MapOptions(
                    gestureOptions = if (uiState.offset == null) GestureOptions.Standard else GestureOptions.AllDisabled,
                )
            )
            MosqueMarker(uiState)
            FabButton(
                painter = painterResource(Res.drawable.ic_edit),
                contentPadding = PaddingValues(horizontal = Theme.spacing._16, vertical = 14.dp),
                modifier = Modifier
                    .padding(bottom = Theme.spacing._4, end = Theme.spacing._4)
                    .align(Alignment.BottomEnd),
                onClick = listener::onEditMarkerClick
            )
        }
    }
}

@Composable
private fun MosqueMarker(uiState: CreateMosqueUiState) {
    uiState.offset?.let { offset ->
        val sizeIcon = DpSize(width = 45.dp, height = 58.dp)
        Icon(
            painter = painterResource(Res.drawable.mosque_pin),
            contentDescription = stringResource(Res.string.mosque_image_description),
            modifier = Modifier
                .size(sizeIcon)
                .offset(
                    x = offset.x - (sizeIcon.width / 2),
                    y = offset.y - sizeIcon.height
                )
        )
    }
}

@Composable
private fun MosqueAddressSection(
    state: CreateMosqueUiState,
    listener: CreateMosqueInteractionListener
) {
    Text(
        modifier = Modifier.padding(top = Theme.spacing._12, bottom = Theme.spacing._4),
        text = stringResource(Res.string.mosque_address),
        style = Theme.typography.title.small,
        color = Theme.colorScheme.shadePrimary
    )
    TextField(
        value = state.address,
        onValueChanged = listener::onAddressChange,
        hint = "",
        leadingIcon = painterResource(Res.drawable.ic_location),
    )
}

@Composable
private fun CreateMosqueAppBar(
    onBackClick: () -> Unit
) {
    AppBar(
        title = stringResource(Res.string.add_new_mosque),
        onLeadingClick = onBackClick,
        contentPadding = PaddingValues(
            horizontal = Theme.spacing._12,
            vertical = Theme.spacing._8
        ),
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back),
                tint = Theme.colorScheme.primary.primary
            )
        }
    )
}

@Composable
private fun UploadMosqueImage(
    uiState: CreateMosqueUiState,
    listener: CreateMosqueInteractionListener
) {
    Text(
        text = stringResource(Res.string.image_size_required),
        style = Theme.typography.title.small,
        color = Theme.colorScheme.shadePrimary,
        modifier = Modifier.padding(
            top = Theme.spacing._16,
            bottom = Theme.spacing._4
        )
    )
    UploadImageContainer(
        onClick = listener::onClickUploadImage,
        image = uiState.croppedImage,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    )
}

@Composable
@Preview
private fun Preview() {
    MenaTheme {
        QuranTheme {
            Content(
                uiState = CreateMosqueUiState(),
                listener = object : CreateMosqueInteractionListener {
                    override fun onBackClick() {}
                    override fun onClickUploadImage(image: ImageSrc) {}
                    override fun onAddClick() {}
                    override fun onNameChange(name: String) {}
                    override fun onAddressChange(address: String) {}
                    override fun onMapClick(position: Position, offset: DpOffset) {}
                    override fun onEditMarkerClick() {}
                }
            )
        }
    }
}
