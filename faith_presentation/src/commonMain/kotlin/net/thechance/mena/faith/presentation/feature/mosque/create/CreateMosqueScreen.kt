package net.thechance.mena.faith.presentation.feature.mosque.create

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.attafitamim.krop.core.images.ImageSrc
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.add
import mena.faith_presentation.generated.resources.add_new_mosque
import mena.faith_presentation.generated.resources.back
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.ic_location
import mena.faith_presentation.generated.resources.ic_mosque
import mena.faith_presentation.generated.resources.image_size_required
import mena.faith_presentation.generated.resources.mosque_address
import mena.faith_presentation.generated.resources.mosque_name
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.mosque.MosqueUiState
import net.thechance.mena.faith.presentation.feature.mosque.component.MapSection
import net.thechance.mena.faith.presentation.feature.mosque.component.UploadImageContainer
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.convertAddressStringToAddressModel
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CreateMosqueScreen(
    viewModel: CreateMosqueViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val addressModelJsonString by savedStateHandle
        ?.getStateFlow<String?>("address_model_json_string", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }

    val addressModel = convertAddressStringToAddressModel(addressModelJsonString)
    LaunchedEffect(addressModel) {
        if (addressModel != null) {
            viewModel.onLocationPicked(addressModel)
            savedStateHandle?.remove<MosqueUiState.Coordinate?>("selected_location")
        }
    }

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
            is CreateMosqueEffect.NavigateToMap -> navController.navigate(Route.PickLocationRoute(effect.coordinates?.latitude , effect.coordinates?.longitude))
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
                modifier = Modifier.fillMaxWidth().padding(Theme.spacing._16),
                text = stringResource(Res.string.add),
                onClick = listener::onAddClick,
                isEnabled = uiState.isButtonEnabled,
                contentPadding = PaddingValues(vertical = Theme.spacing._12)
            )
        }
    ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = Theme.spacing._16)
    ) {
        item { MosqueLocationHeader(uiState, listener) }
        item {
            MapSection(
                isMapClickable = uiState.address.isBlank(),
                cameraPosition = uiState.cameraPosition,
                onClickEdit = listener::onEditMarkerClick,
                onClickMap = listener::onClickMap,
            )
        }
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
                    override fun onClickMap() {}
                    override fun onEditMarkerClick() {}
                }
            )
        }
    }
}
