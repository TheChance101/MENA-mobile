package net.thechance.mena.identity.presentation.screen.editProfile

import androidx.compose.ui.graphics.ImageBitmap
import io.github.vinceglb.filekit.dialogs.compose.util.encodeToByteArray
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.domain.util.getCurrentDate
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.ErrorState
import net.thechance.mena.identity.presentation.util.PermissionManager
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class EditUserProfileViewModel(
    private val userRepository: UserRepository,
    private val permissionManager: PermissionManager,
    val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<EditUserProfileUIState, EditUserProfileUIEffect>(EditUserProfileUIState()),
    EditUserProfileInteractionListener {
    @OptIn(ExperimentalUuidApi::class)
    var userId: Uuid? = null

    init {
        getUserInfo()
    }

    private fun getUserInfo() {
        tryToCollect(
            function = { userRepository.getUser() },
            onNewValue = ::updateUserInfo,
            onError = ::onErrorOccurred,
            dispatcher = dispatcher
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun updateUserInfo(user: User) {
        updateState {
            userId = user.id
            copy(
                username = user.username.lowercase(),
                firstName = user.firstName,
                lastName = user.lastName,
                profileImageUrl = user.profileImageUrl,
                birthDate = user.birthDate,
                gender = user.gender,
            )
        }
    }

    override fun onChangeFirstName(firstName: String) {
        updateState { copy(firstName = firstName) }
    }

    override fun onChangeLastName(lastName: String) {
        updateState { copy(lastName = lastName) }
    }

    override fun onChangeUsername(username: String) {
        updateState { copy(username = username) }
    }

    override fun onChangeGender(gender: Gender) {
        updateState { copy(gender = gender) }
    }

    override fun onClickSaveButton() {
        if (state.value.username.isEmpty()) {
            updateState { copy(errorMessage = "Please enter the Username field") }
            return
        }
        if (state.value.firstName.isEmpty()) {
            updateState { copy(errorMessage = "Please enter the First name field") }
            return
        }
        if (state.value.lastName.isEmpty()) {
            updateState { copy(errorMessage = "Please enter the Last name field") }
            return
        }

        updateState { copy(isLoading = true) }

        tryToExecute(
            function = ::onSave,
            onError = ::onErrorOccurred,
            onSuccess = ::onSaveSuccess
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun onSave() {
        if (userId == null) {
            onErrorOccurred(ErrorState.InvalidMobileNumber)
            return
        }
        userId?.let { userId ->
            val value = state.value
            val user = User(
                id = userId,
                firstName = value.firstName,
                lastName = value.lastName,
                username = value.username.lowercase(),
                profileImageUrl = value.profileImageUrl,
                birthDate = value.birthDate ?: getCurrentDate(),
                gender = value.gender,
            )
            userRepository.updateUser(
                user = user,
                shouldUpdateImage = value.shouldUpdateImage,
                imageByteArray = value.profileImageBitmap?.encodeToByteArray()
            )
        }
    }

    private fun onSaveSuccess() {
        updateState { copy(isLoading = false) }
        sendNewEffect(EditUserProfileUIEffect.NavigateBackToProfile)
    }

    override fun onClickCancelButton() {
        sendNewEffect(EditUserProfileUIEffect.NavigateBackToProfile)
    }

    override fun onClickShowLogoutOptions() {
        updateState { copy(showLogoutDialog = true) }
    }

    override fun onChangeDate(day: Int, month: Int, year: Int) {
        updateState { copy(birthDate = LocalDate(year, month, day)) }
    }

    override fun clearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }

    override fun onClickEditImage() {
        updateState { copy(showEditImageDialog = true) }
    }

    override fun onDismissEditImageDialog() {
        updateState { copy(showEditImageDialog = false) }
    }

    override fun onDismissLogoutDialog() {
        updateState { copy(showLogoutDialog = false) }
    }

    override fun onRemoveProfileImage() {
        updateState {
            copy(
                profileImageUrl = "",
                profileImageBitmap = null,
            )
        }
    }

    override fun onRequireCropImage(imageBitmap: ImageBitmap) {
        sendNewEffect(
            EditUserProfileUIEffect.NavigateToCropScreen(
                imageBitmap = imageBitmap,
                onResult = { croppedImageBitmap ->
                    updateState {
                        copy(
                            profileImageBitmap = croppedImageBitmap,
                            shouldUpdateImage = true
                        )
                    }
                }
            )
        )
    }

    override fun onTakeImageFromCamera() {
        tryToExecute(
            function = ::onAskForCameraPermission,
            onError = ::onErrorOccurred
        )
    }

    private suspend fun onAskForCameraPermission() {
        permissionManager.requestCameraPermission(
            onGranted = { updateState { copy(showCamera = true) } },
            onDenied = { updateState { copy(errorMessage = "Camera permission required") } }
        )
    }

    override fun onOpenCamera() {
        updateState { copy(showCamera = false) }
    }

    private fun onErrorOccurred(errorState: ErrorState) {
        updateState { copy(isLoading = false, errorMessage = errorState.toString()) }
    }
}