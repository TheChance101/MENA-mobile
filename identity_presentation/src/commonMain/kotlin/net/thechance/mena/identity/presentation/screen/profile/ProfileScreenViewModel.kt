package net.thechance.mena.identity.presentation.screen.profile

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.repository.SettingsRepository
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.domain.util.AppLanguage
import net.thechance.mena.identity.domain.util.AppTheme
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.mapper.createNavigateToEditProfileEffect
import net.thechance.mena.identity.presentation.screen.profile.components.dialog.ShareDialogViewModel.Companion.SHARE_URL
import kotlin.uuid.ExperimentalUuidApi

class ProfileScreenViewModel(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
    val appVersion: String,
    val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) :
    BaseScreenModel<ProfileScreenUIState, ProfileScreenUIEffect>(
        ProfileScreenUIState()
    ),
    ProfileScreenInteractionListener {

    init {
        getUserInfo()
        setAppVersion()
        getAppSettings()
    }

    private fun getAppSettings() {
        val currentAppTheme=settingsRepository.observeAppTheme().value
        updateState {
            state.value.copy(
                languageDialogUiState = LanguageDialogUiState(
                    selectedAppLanguage = settingsRepository.getCurrentAppLanguage(),
                ),
                themeDialogUiState = ThemeDialogUiState(
                    selectedAppTheme =currentAppTheme,
                ),
                currentTheme = currentAppTheme
            )
        }
    }

    private fun setAppVersion() {
        updateState { copy(versionNumber = appVersion) }
    }

    private fun getUserInfo() {
        tryToCollect(
            function = { userRepository.getUser() },
            onNewValue = ::onUserInfoSuccess,
            onError = ::onUserInfoError,
            dispatcher = dispatcher
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun onUserInfoSuccess(user: User) {
        updateState {
            copy(
                userName = user.username,
                fullName = "${user.firstName.trim()} ${user.lastName.trim()}",
                profileImageUrl = user.profileImageUrl,
                isSuccess = true,
                inviteLinkUrl = "$SHARE_URL${user.id}"
            )
        }
    }

    private fun onUserInfoError(throwable: Throwable) {
        updateState { copy(isLoading = false, errorMessage = null) }
    }

    override fun onEditProfileInfoClicked() =
        sendNewEffect(createNavigateToEditProfileEffect())

    override fun onShareClicked() =
        updateState { copy(showShareProfileDialog = true) }

    override fun onInviteFriendsClicked() =
        updateState { copy(showShareBottomSheet = true) }

    override fun onChangePasswordClicked() {
        sendNewEffect(
            ProfileScreenUIEffect.NavigateToChangePasswordScreen(
                onSuccess = ::onChangePasswordSuccess

            )
        )
        onDismissSnackBar()
    }

    override fun onAddressesClicked() =
        sendNewEffect(ProfileScreenUIEffect.NavigateToLocationPickerScreen)

    override fun onPrivacySettingsClicked() =
        sendNewEffect(ProfileScreenUIEffect.NavigateToPrivacyAndPolicyScreen)

    override fun onLanguageClicked() =
        updateState { copy(languageDialogUiState = languageDialogUiState.copy(isVisible = true)) }

    override fun onThemeSettingsClicked() =
        updateState { copy(themeDialogUiState = themeDialogUiState.copy(isVisible = true)) }

    override fun onPrivacyAndPolicyClicked() =
        sendNewEffect(ProfileScreenUIEffect.NavigateToPrivacyAndPolicyScreen)

    override fun onContactUsClicked() =
        sendNewEffect(ProfileScreenUIEffect.NavigateContactUsScreen)


    override fun onConfirmLanguageSelection(appLanguage: AppLanguage) {
        updateState { copy(languageDialogUiState = languageDialogUiState.copy(selectedAppLanguage = appLanguage)) }
        tryToExecute(
            function = { settingsRepository.applyLanguage(appLanguage) },
            onSuccess = {
                updateState {
                    copy(
                        languageDialogUiState = languageDialogUiState.copy(
                            isVisible = false
                        )
                    )
                }
            },
            onError = ::onUserInfoError,
        )
    }

    override fun onConfirmThemeSelection() {
        tryToExecute(
            function = { settingsRepository.applyAppTheme(state.value.themeDialogUiState.selectedAppTheme) },
            onSuccess = { onThemeConfirmationSuccess() },
            onError = ::onUserInfoError,
            dispatcher = dispatcher
        )
    }

    private fun onThemeConfirmationSuccess() {
        updateState {
            copy(
                themeDialogUiState = themeDialogUiState.copy(
                    isVisible = false,
                ),
                currentTheme = state.value.themeDialogUiState.selectedAppTheme
            )
        }
    }

    override fun onSelectTheme(appTheme: AppTheme) {
        updateState { copy(themeDialogUiState = themeDialogUiState.copy(selectedAppTheme = appTheme)) }
    }

    override fun onDismissSnackBar() {
        updateState {
            copy(
                snackBarUiState = SnackBarUiState(
                    isVisible = false,
                )
            )
        }
    }

    override fun onDismissLanguageDialog() =
        updateState { copy(languageDialogUiState = languageDialogUiState.copy(isVisible = false)) }

    override fun onDismissBottomSheet() =
        updateState { copy(showShareBottomSheet = false) }

    override fun onDismissShareDialog() {
        updateState { copy(showShareProfileDialog = false) }
    }

    override fun onDismissThemeDialog() {
        updateState {
            copy(
                themeDialogUiState = themeDialogUiState.copy(
                    isVisible = false,
                    selectedAppTheme = state.value.currentTheme
                )
            )
        }
    }

    override fun clearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }

    private fun onChangePasswordSuccess(snackBarUiState: SnackBarUiState?) {
        updateState {
            copy(snackBarUiState = snackBarUiState ?: state.value.snackBarUiState)
        }
    }
}
