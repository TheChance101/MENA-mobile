package net.thechance.mena.faith.presentation.feature.mosque.create

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.attafitamim.krop.core.images.ImageSrc
import io.github.dellisd.spatialk.geojson.Position
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.add
import mena.faith_presentation.generated.resources.add_new_mosque
import mena.faith_presentation.generated.resources.back
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.ic_location
import mena.faith_presentation.generated.resources.ic_mosque
import mena.faith_presentation.generated.resources.image_size_required
import mena.faith_presentation.generated.resources.location
import mena.faith_presentation.generated.resources.mosque_address
import mena.faith_presentation.generated.resources.mosque_name
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.mosque.Coordinate
import net.thechance.mena.faith.presentation.feature.mosque.component.UploadImageContainer
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
internal fun CreateMosqueScreen(viewModel: CreateMosqueViewModel = koinViewModel()) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { CreateMosqueAppBar(viewModel) },
        bottomBar = {
            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.spacing._16),
                text = stringResource(Res.string.add),
                onClick = viewModel::onAddClick,
                isEnabled = uiState.isButtonEnabled,
                contentPadding = PaddingValues(vertical = Theme.spacing._12)
            )
        }
    ) {
        Content(uiState, viewModel)
    }
}


@Composable
private fun Content(
    uiState: CreateMosqueUiState,
    listener: CreateMosqueInteractionListener
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


@Composable
private fun MosqueLocationHeader(
    uiState: CreateMosqueUiState,
    listener: CreateMosqueInteractionListener
) {
    Text(
        text = stringResource(Res.string.mosque_name),
        style = Theme.typography.title.medium,
        color = Theme.colorScheme.shadePrimary
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

    val initialCameraPosition = CameraPosition(
        target = Position(
            longitude = uiState.location?.longitude ?: 0.0,
            latitude = uiState.location?.latitude ?: 0.0
        ),
        zoom = 14.0
    )
    val cameraState = rememberCameraState(firstPosition = initialCameraPosition)

    LaunchedEffect(cameraState) {
        snapshotFlow { cameraState.position }
            .collect {
                listener.mapPositionChange(
                    coordinate = Coordinate(
                        latitude = cameraState.position.target.latitude,
                        longitude = cameraState.position.target.longitude
                    )
                )
            }
    }

    Text(
        modifier = Modifier.padding(top = Theme.spacing._16, bottom = Theme.spacing._4),
        text = stringResource(Res.string.location),
        style = Theme.typography.title.small,
        color = Theme.colorScheme.shadePrimary
    )
    MaplibreMap(
        modifier = Modifier.fillMaxWidth().height(244.dp),
        cameraState = cameraState,
        baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
    )
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
    listener: CreateMosqueInteractionListener
) {
    AppBar(
        title = stringResource(Res.string.add_new_mosque),
        onLeadingClick = listener::onBackClick,
        contentPadding = PaddingValues(
            horizontal = Theme.spacing._12,
            vertical = Theme.spacing._8
        ),
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back),
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
private fun MosqueCreateScreenPreview() {
    QuranTheme {
        Content(
            uiState = CreateMosqueUiState(),
            listener = object : CreateMosqueInteractionListener {
                override fun onBackClick() {}
                override fun onEditImageMosqueClick() {}
                override fun onClickUploadImage(image: ImageSrc) {}
                override fun onAddClick() {}
                override fun onNameChange(name: String) {}
                override fun onAddressChange(address: String) {}
                override fun mapPositionChange(coordinate: Coordinate) {}
            }
        )
    }
}