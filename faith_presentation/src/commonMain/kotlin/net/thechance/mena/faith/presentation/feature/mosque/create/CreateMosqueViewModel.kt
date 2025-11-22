package net.thechance.mena.faith.presentation.feature.mosque.create

import androidx.compose.ui.unit.DpOffset
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
import net.thechance.mena.faith.presentation.feature.mosque.Coordinate
import net.thechance.mena.faith.presentation.feature.mosque.shared.SharedImageViewModel
import net.thechance.mena.faith.presentation.utils.extentions.toByteArray
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.service.LocationService
import org.jetbrains.compose.resources.getString
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class CreateMosqueViewModel(
    private val repository: MosqueRepository,
    private val sharedImageViewModel: SharedImageViewModel,
    private val locationService: LocationService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    BaseViewModel<CreateMosqueUiState, CreateMosqueEffect>(
        CreateMosqueUiState()
    ), CreateMosqueInteractionListener {


    init {
        setInitialLocation()
        observeCroppedImage()
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
                    location = Coordinate(
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

    @OptIn(ExperimentalUuidApi::class)
    override fun onAddClick() {
        tryToExecute(
            execute = {
                repository.addMosque(
                    Mosque(
                        id = Uuid.random(),
                        name = uiState.value.name,
                        coordinates = Mosque.Coordinates(
                            latitude = uiState.value.mosqueLocation?.latitude ?: 0.0,
                            longitude = uiState.value.mosqueLocation?.longitude ?: 0.0
                        ),
                        address = uiState.value.address,
                        imageUrl = "",
                    ),
                    imageBytes = uiState.value.croppedImage?.toByteArray() ?: ByteArray(0)
                )
                val addMosqueMessage = getString(Res.string.add_mosque_message)
                updateState { it.copy(successMessage = addMosqueMessage) }
                sharedImageViewModel.clearImage()
                sendEffect(CreateMosqueEffect.NavigateBack)
            },
            onError = ::handleErrorSnackBar
        )
    }

    override fun onNameChange(name: String) {
        updateState { it.copy(name = name) }
        checkIfFormIsComplete()
    }

    override fun onAddressChange(address: String) {
        updateState { it.copy(address = address) }
        checkIfFormIsComplete()
    }

    override fun onMapClick(position: Position, offset: DpOffset) {
        if (uiState.value.offset != null) return
        val coordinate = Coordinate(
            latitude = position.latitude,
            longitude = position.longitude
        )
        updateState {
            it.copy(
                mosqueLocation = coordinate,
                offset = offset,
            )
        }
        checkIfFormIsComplete()
    }

    override fun onEditMarkerClick() {
        updateState { it.copy(mosqueLocation = null, offset = null) }
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