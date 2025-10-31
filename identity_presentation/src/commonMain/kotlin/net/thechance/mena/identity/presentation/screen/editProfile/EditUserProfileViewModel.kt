package net.thechance.mena.identity.presentation.screen.editProfile

import androidx.compose.ui.graphics.ImageBitmap
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.datetime.LocalDate
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_camera_permission_required
import mena.identity_presentation.generated.resources.error_first_name_required
import mena.identity_presentation.generated.resources.error_last_name_required
import mena.identity_presentation.generated.resources.error_username_required
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.repository.CachedImageRepository
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.domain.util.getCurrentDate
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.error.ErrorState
import net.thechance.mena.identity.presentation.base.error.handleAuthenticationException
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.utils.ImageDecoder
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class EditUserProfileViewModel(
    private val userRepository: UserRepository,
    private val permissionsController: PermissionsController,
    private val cachedImageRepository: CachedImageRepository,
    private val imageDecoder: ImageDecoder,
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
            onError = ::onGetUserInfoError,
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

    private fun onGetUserInfoError(throwable: Throwable) {
        updateState { copy(errorMessage = mapErrorMessage(throwable)) }
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

        updateState { copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            function = { saveUserProfile() },
            onSuccess = { handleSaveSuccess() },
            onError = ::handleSaveError,
            dispatcher = dispatcher
        )
    }

    private fun validateFormInputs(): Boolean {
        val currentState = state.value

        return when {
            currentState.username.isEmpty() -> {
                updateState { copy(errorMessage = Res.string.error_username_required) }
                false
            }

            currentState.firstName.isEmpty() -> {
                updateState { copy(errorMessage = Res.string.error_first_name_required) }
                false
            }

            currentState.lastName.isEmpty() -> {
                updateState { copy(errorMessage = Res.string.error_last_name_required) }
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

        val value = state.value
        val user = User(
            id = userId!!,
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
            imageByteArray = value.profileImageBitmap?.let{imageDecoder.encodeImage(it)}
        )
    }

    private fun handleSaveSuccess() {
        updateState { copy(isLoading = false) }
        sendNewEffect(EditUserProfileUIEffect.NavigateBackToProfile)
    }

    private fun handleSaveError(throwable: Throwable) {
        updateState { copy(isLoading = false, errorMessage = mapErrorMessage(throwable)) }
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
        cacheRequiredCropImage(imageBitmap)
    }
    private fun cacheRequiredCropImage(imageBitmap: ImageBitmap){
        tryToExecute(
            function = {
                cachedImageRepository.cacheImage(PROFILE_IMAGE, imageDecoder.encodeImage(imageBitmap))
            },
            onSuccess = { handleCacheImageSuccess() },
            onError = ::onCacheCropImageError,
            dispatcher = dispatcher
        )
    }

    private fun handleCacheImageSuccess(){
        sendNewEffect(
            EditUserProfileUIEffect.NavigateToCropScreen(
                imageKey = PROFILE_IMAGE,
                onResult = { croppedImageKey ->
                    val imageByteArray = cachedImageRepository.getCachedImage(croppedImageKey)
                    updateState {
                        copy(
                            profileImageBitmap =imageByteArray?.let { imageDecoder.decodeImage(it)} ,
                            shouldUpdateImage = true
                        )
                    }
                }
            )
        )

    }

    private fun onCacheCropImageError(throwable: Throwable) {
        updateState { copy(errorMessage = mapErrorMessage(throwable)) }
    }

    override fun onTakeImageFromCamera() {
        tryToExecute(
            function = ::requestCameraPermission,
            onSuccess = { onCameraPermissionSuccess() },
            onError = ::handleCameraPermissionError,
            dispatcher = dispatcher
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
                updateState { copy(errorMessage = Res.string.error_camera_permission_required) }
            }

            else -> updateState { copy(errorMessage = mapErrorMessage(throwable)) }
        }
    }

    override fun onOpenCamera() {
        updateState { copy(showCamera = false) }
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource{
        return when (throwable) {
            is AuthenticationException -> mapAuthenticationErrorToMessage(handleAuthenticationException(throwable))
            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }

    companion object {
        const val PROFILE_IMAGE = "profile_image"
    }
}