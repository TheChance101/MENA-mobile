package net.thechance.mena.faith.presentation.feature.mosque.create

import androidx.lifecycle.viewModelScope
import com.attafitamim.krop.core.images.ImageSrc
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.add_mosque_message
import net.thechance.mena.faith.domain.entity.Mosque
import net.thechance.mena.faith.domain.repository.MosqueRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.feature.mosque.MosqueUiState
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.AddressModel
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.toCoordinates
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.toCoordinatesUiState
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.toPosition
import net.thechance.mena.faith.presentation.feature.mosque.shared.SharedImageViewModel
import net.thechance.mena.faith.presentation.navigation.createNavigateToMapEffect
import net.thechance.mena.faith.presentation.utils.extentions.toByteArray
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.service.LocationService
import org.jetbrains.compose.resources.getString
import org.maplibre.compose.camera.CameraPosition
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class CreateMosqueViewModel(
    private val repository: MosqueRepository,
    private val sharedImageViewModel: SharedImageViewModel,
    private val locationService: LocationService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) :
    BaseViewModel<CreateMosqueUiState, CreateMosqueEffect>(
        CreateMosqueUiState()
    ), CreateMosqueInteractionListener {

    init {
        sharedImageViewModel.clearImage()
        setInitialLocation()
        observeCroppedImage()
    }

    fun onLocationPicked(addressModel: AddressModel?) {
        if (addressModel == null) return
        updateState {
            it.copy(
                location = addressModel.coordinates?.toCoordinates(),
                mosqueLocation = addressModel.coordinates?.toCoordinates(),
                cameraPosition = CameraPosition(
                    target = addressModel.coordinates?.toPosition() ?: Position(0.0,0.0)
                    , zoom = 13.0
                ),
                address = addressModel.address,
            )
        }
    }

    private fun setInitialLocation() {
        tryToExecute(
            execute = locationService::getActiveAddress,
            onSuccess = ::onGetActiveAddressSuccess,
            onError = { sendEffect(CreateMosqueEffect.NavigateToAddressesScreen) },
            dispatcher = dispatcher
        )
    }

    private fun onGetActiveAddressSuccess(address: Address?) {
        address?.let { address ->
            updateState {
                it.copy(
                    location = MosqueUiState.Coordinate(
                        latitude = address.latitude,
                        longitude = address.longitude
                    )
                )
            }
        }
    }

    private fun observeCroppedImage() {
        viewModelScope.launch {
            sharedImageViewModel.croppedImage.collect { image ->
                image?.let {
                    updateState { it.copy(croppedImage = image) }
                    checkIfFormIsComplete()
                }
            }
        }
    }

    override fun onBackClick() {
        sendEffect(CreateMosqueEffect.NavigateBack)
    }

    override fun onClickUploadImage(image: ImageSrc) {
        sharedImageViewModel.updateImageSrc(image)
        sendEffect(CreateMosqueEffect.NavigateToUploadImageRoute)
        checkIfFormIsComplete()
    }

    override fun onAddClick() {
        tryToExecute(
            execute = ::createMosque,
            onError = ::handleErrorSnackBar,
            onSuccess = {
                sharedImageViewModel.clearImage()
                sendEffect(CreateMosqueEffect.NavigateBack)
            }
        )
    }

    private suspend fun createMosque() {
        val mosque = buildMosqueFromState()
        val imageBytes = getImageBytes()

        repository.addMosque(mosque, imageBytes)
        handleSuccessfulMosqueCreation()
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun buildMosqueFromState(): Mosque {
        val state = uiState.value
        return Mosque(
            id = Uuid.random(),
            name = state.name,
            coordinates = Mosque.Coordinates(
                latitude = state.mosqueLocation?.latitude ?: 0.0,
                longitude = state.mosqueLocation?.longitude ?: 0.0
            ),
            address = state.address,
            imageUrl = "",
        )
    }

    private fun getImageBytes(): ByteArray {
        return uiState.value.croppedImage?.toByteArray() ?: ByteArray(0)
    }

    private suspend fun handleSuccessfulMosqueCreation() {
        val addMosqueMessage = getString(Res.string.add_mosque_message)
        updateState { it.copy(successMessage = addMosqueMessage) }
    }

    override fun onNameChange(name: String) {
        updateState { it.copy(name = name) }
        checkIfFormIsComplete()
    }

    override fun onAddressChange(address: String) {
        updateState { it.copy(address = address) }
        checkIfFormIsComplete()
    }

    override fun onClickMap() {
        sendEffect(createNavigateToMapEffect())
    }

    override fun onEditMarkerClick() {
        sendEffect(createNavigateToMapEffect(uiState.value.location?.toCoordinatesUiState()))
        checkIfFormIsComplete()
    }

    private fun checkIfFormIsComplete() {
        updateState { currentState ->
            currentState.copy(
                isButtonEnabled = currentState.name.isNotBlank() &&
                        currentState.address.isNotBlank() &&
                        currentState.mosqueLocation != null &&
                        currentState.croppedImage != null
            )
        }
    }
}