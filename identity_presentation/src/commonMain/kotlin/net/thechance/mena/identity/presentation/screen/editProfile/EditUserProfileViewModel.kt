@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.identity.presentation.screen.editProfile

import androidx.compose.ui.graphics.ImageBitmap
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.datetime.LocalDate
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_age_restriction
import mena.identity_presentation.generated.resources.error_camera_permission_required
import mena.identity_presentation.generated.resources.error_first_name_required
import mena.identity_presentation.generated.resources.error_last_name_required
import mena.identity_presentation.generated.resources.error_username_required
import mena.identity_presentation.generated.resources.success_profile_info_updated
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.repository.ImagesRepository
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.domain.useCase.validation.age.AgeValidator
import net.thechance.mena.identity.domain.util.orCurrentDate
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.errorState.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.utils.ImageDecoder
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class EditUserProfileViewModel(
    val permissionsController: PermissionsController,
    private val ageValidator: AgeValidator,
    private val userRepository: UserRepository,
    private val imagesRepository: ImagesRepository,
    private val imageDecoder: ImageDecoder,
    private val authenticationRepository: AuthenticationRepository,
    private val registrationDraftRepository: RegistrationDraftRepository,
    private val addressesRepository: AddressesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseScreenModel<EditUserProfileUIState, EditUserProfileUIEffect>(EditUserProfileUIState()),
    EditUserProfileInteractionListener {

    @OptIn(ExperimentalUuidApi::class)
    var userId: Uuid? = null

    fun getInitialUserInfo(user: User?) {
        if (user == null)
            return

        userId = user.id
        updateState {
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
        if (!validateFormInputs()) return

        updateState { copy(isLoading = true) }
        tryToExecute(
            function = { saveUserProfile() },
            onSuccess = { handleSaveSuccess() },
            onError = ::handleSaveError,
            dispatcher = dispatcher
        )
    }

    override fun onClickCancelButton() {
        sendNewEffect(EditUserProfileUIEffect.NavigateBackToProfile())
    }

    override fun onClickShowLogoutOptions() {
        updateState { copy(showLogoutDialog = true) }
    }

    override fun onChangeDate(day: Int, month: Int, year: Int) {
        updateState { copy(birthDate = LocalDate(year, month, day)) }
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

    override fun onClickLogout() {
        updateState { copy(showLogoutDialog = false, showConfirmLogoutDialog = true) }
    }

    override fun onClickDeleteAccount() {
        updateState { copy(showLogoutDialog = false, showConfirmDeleteAccountDialog = true) }
    }

    override fun onDismissConfirmLogoutDialog() {
        updateState { copy(showConfirmLogoutDialog = false) }
    }

    override fun onDismissConfirmDeleteAccountDialog() {
        updateState { copy(showConfirmDeleteAccountDialog = false) }
    }

    override fun onConfirmLogout() {
        tryToExecute(
            function = ::performLogout,
            onSuccess = { onLogoutSuccess() },
            onError = ::onLogoutError,
            dispatcher = dispatcher
        )
    }

    private suspend fun performLogout() {
        authenticationRepository.logout()
        registrationDraftRepository.clearLastPhoneNumber()
        addressesRepository.clearAddresses()
    }

    private fun onLogoutSuccess() {
        updateState { copy(showConfirmLogoutDialog = false) }
    }

    private fun onLogoutError(throwable: Throwable) {
        updateState { copy(showConfirmLogoutDialog = false) }
        sendNewEffect(
            EditUserProfileUIEffect.ShowSnackBarError(
                errorStringResource = mapErrorMessage(throwable)
            )
        )
    }

    override fun onConfirmDeleteAccount() {
        tryToExecute(
            function = ::performDeleteAccount,
            onSuccess = { onDeleteAccountSuccess() },
            onError = ::onDeleteAccountError,
            dispatcher = dispatcher
        )
    }

    private suspend fun performDeleteAccount() {
        userRepository.deleteAccount()
        authenticationRepository.clearAuthTokens()
        registrationDraftRepository.clearLastPhoneNumber()
    }

    private fun onDeleteAccountSuccess() {
        updateState { copy(showConfirmDeleteAccountDialog = false) }
    }

    private fun onDeleteAccountError(throwable: Throwable) {
        updateState { copy(showConfirmDeleteAccountDialog = false) }

        sendNewEffect(
            EditUserProfileUIEffect.ShowSnackBarError(
                errorStringResource = mapErrorMessage(throwable)
            )
        )
    }

    override fun onRemoveProfileImage() {
        updateState {
            copy(
                profileImageUrl = "",
                profileImageBitmap = null,
                profileImageAction = EditUserProfileUIState.ProfileImageAction.DELETE
            )
        }
    }

    override fun onRequireCropImage(imageBitmap: ImageBitmap) {
        cacheRequiredCropImage(imageBitmap)
    }

    override fun onOpenCamera() {
        updateState { copy(showCamera = false) }
    }

    override fun onTakeImageFromCamera() {
        tryToExecute(
            function = ::requestCameraPermission,
            onSuccess = { onCameraPermissionSuccess() },
            onError = ::handleCameraPermissionError,
            dispatcher = dispatcher
        )
    }

    private fun validateFormInputs(): Boolean {
        val currentState = state.value

        return when {
            currentState.username.isEmpty() -> {
                sendNewEffect(
                    EditUserProfileUIEffect.ShowSnackBarError(
                        errorStringResource = Res.string.error_username_required
                    )
                )
                false
            }

            currentState.firstName.isEmpty() -> {
                sendNewEffect(
                    EditUserProfileUIEffect.ShowSnackBarError(
                        errorStringResource = Res.string.error_first_name_required
                    )
                )
                false
            }

            currentState.lastName.isEmpty() -> {
                sendNewEffect(
                    EditUserProfileUIEffect.ShowSnackBarError(
                        errorStringResource = Res.string.error_last_name_required
                    )
                )
                false
            }

            currentState.birthDate != null && !ageValidator.isValid(currentState.birthDate) -> {
                sendNewEffect(
                    EditUserProfileUIEffect.ShowSnackBarError(
                        errorStringResource = Res.string.error_age_restriction
                    )
                )
                false
            }

            else -> true
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun saveUserProfile() {
        if (userId == null) {
            throw Exception("User ID not found")
        }

        val user = with(state.value) {
            User(
                id = userId!!,
                firstName = firstName,
                lastName = lastName,
                username = username.lowercase(),
                profileImageUrl = profileImageUrl,
                birthDate = birthDate.orCurrentDate(),
                gender = gender,
            )
        }

        updateProfileImage()
        userRepository.updateUser(user = user)
    }

    private suspend fun updateProfileImage() {
        val imageBitmap = state.value.profileImageBitmap
        val action = state.value.profileImageAction
        when (action) {
            EditUserProfileUIState.ProfileImageAction.UPDATE -> {
                imageBitmap?.let {
                    userRepository.uploadUserProfileImage(
                        imageByteArray = imageDecoder.encodeImage(it)
                    )
                }
            }

            EditUserProfileUIState.ProfileImageAction.DELETE -> {
                userRepository.deleteUserProfileImage()
            }

            EditUserProfileUIState.ProfileImageAction.NONE -> Unit
        }
    }

    private fun handleSaveSuccess() {
        updateState { copy(isLoading = false) }
        sendNewEffect(
            EditUserProfileUIEffect.NavigateBackToProfile(
                successStringResource = Res.string.success_profile_info_updated
            )
        )
    }

    private fun handleSaveError(throwable: Throwable) {
        updateState { copy(isLoading = false) }
        sendNewEffect(
            EditUserProfileUIEffect.ShowSnackBarError(
                errorStringResource = mapErrorMessage(throwable)
            )
        )
    }

    private fun cacheRequiredCropImage(imageBitmap: ImageBitmap) {
        tryToExecute(
            function = {
                imagesRepository.cacheImage(
                    PROFILE_IMAGE,
                    imageDecoder.encodeImage(imageBitmap)
                )
            },
            onSuccess = { handleCacheImageSuccess() },
            onError = ::onCacheCropImageError,
            dispatcher = dispatcher
        )
    }

    private fun handleCacheImageSuccess() {
        sendNewEffect(
            EditUserProfileUIEffect.NavigateToCropScreen(
                imageKey = PROFILE_IMAGE,
                onResult = { croppedImageKey ->
                    val imageByteArray = imagesRepository.getCachedImage(croppedImageKey)
                    updateState {
                        copy(
                            profileImageBitmap = imageByteArray?.let { imageDecoder.decodeImage(it) },
                            profileImageAction = EditUserProfileUIState.ProfileImageAction.UPDATE
                        )
                    }
                }
            )
        )

    }

    private fun onCacheCropImageError(throwable: Throwable) {
        sendNewEffect(
            EditUserProfileUIEffect.ShowSnackBarError(
                errorStringResource = mapErrorMessage(throwable)
            )
        )
    }

    private suspend fun requestCameraPermission() {
        permissionsController.providePermission(Permission.CAMERA)
    }

    private fun onCameraPermissionSuccess() {
        updateState { copy(showCamera = true) }

    }

    private fun handleCameraPermissionError(throwable: Throwable) {
        throwable.printStackTrace()
        when (throwable) {
            is DeniedAlwaysException -> {
                permissionsController.openAppSettings()
            }

            is DeniedException -> {
                sendNewEffect(
                    EditUserProfileUIEffect.ShowSnackBarError(
                        errorStringResource = Res.string.error_camera_permission_required
                    )
                )
            }

            else -> sendNewEffect(
                EditUserProfileUIEffect.ShowSnackBarError(
                    errorStringResource = mapErrorMessage(throwable)
                )
            )
        }
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource {
        return when (throwable) {
            is AuthenticationException -> {
                mapAuthenticationErrorToMessage(handleEditUserProfileException(throwable))
            }

            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }

    companion object {
        const val PROFILE_IMAGE = "profile_image"
    }
}