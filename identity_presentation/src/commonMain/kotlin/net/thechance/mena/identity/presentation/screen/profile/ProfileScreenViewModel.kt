package net.thechance.mena.identity.presentation.screen.profile

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.repository.SettingsRepository
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.domain.util.AppLanguage
import net.thechance.mena.identity.domain.util.AppTheme
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.errorState.ErrorState
import net.thechance.mena.identity.presentation.mapper.createNavigateToEditProfileEffect
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.screen.profile.components.share.ShareDialogViewModel.Companion.SHARE_URL
import org.jetbrains.compose.resources.StringResource
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
        val currentAppTheme = settingsRepository.observeAppTheme().value
        val currentAppLanguage = settingsRepository.getCurrentAppLanguage()
        updateState {
            state.value.copy(
                languageDialogUiState = LanguageDialogUiState(
                    selectedAppLanguage = currentAppLanguage,
                ),
                themeDialogUiState = ThemeDialogUiState(
                    selectedAppTheme = currentAppTheme,
                ),
                currentTheme = currentAppTheme,
                currentLanguage = currentAppLanguage
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
        updateState { copy(isLoading = false) }
        sendNewEffect(
            ProfileScreenUIEffect.ShowSnackBarError(
                mapErrorMessage(throwable)
            )
        )
    }

    override fun onEditProfileInfoClicked() =
        sendNewEffect(createNavigateToEditProfileEffect())

    override fun onShareClicked() =
        updateState { copy(showShareProfileDialog = true) }

    override fun onInviteFriendsClicked() =
        updateState { copy(showShareBottomSheet = true) }

    override fun onChangePasswordClicked() {
        sendNewEffect(
            ProfileScreenUIEffect.NavigateToChangePasswordScreen
        )
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


    override fun onConfirmLanguageSelection() {
        tryToExecute(
            function = { settingsRepository.applyLanguage(state.value.languageDialogUiState.selectedAppLanguage) },
            onSuccess = { onLanguageConfirmationSuccess() },
            onError = ::onUserInfoError,
            dispatcher = dispatcher
        )
    }

    private fun onLanguageConfirmationSuccess() {
        updateState {
            copy(
                languageDialogUiState = languageDialogUiState.copy(
                    isVisible = false,
                ),
                currentLanguage = state.value.languageDialogUiState.selectedAppLanguage
            )
        }
    }

    override fun onSelectLanguage(appLanguage: AppLanguage) {
        updateState { copy(languageDialogUiState = languageDialogUiState.copy(selectedAppLanguage = appLanguage)) }
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

    override fun onDismissLanguageDialog() {
        updateState {
            copy(
                languageDialogUiState = languageDialogUiState.copy(
                    isVisible = false,
                    selectedAppLanguage = state.value.currentLanguage
                )
            )
        }
    }

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


    private fun mapErrorMessage(throwable: Throwable): StringResource {
        return when (throwable) {
            is AuthenticationException -> mapAuthenticationErrorToMessage(
                handleProfileException(throwable)
            )

            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }
}
