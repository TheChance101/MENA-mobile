package net.thechance.mena.identity.presentation.screen.profile

import androidx.compose.ui.platform.Clipboard
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.repository.SettingsRepository
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.domain.util.AppLanguage
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.mapper.createNavigateToEditProfileEffect
import net.thechance.mena.identity.presentation.screen.profile.components.share.clipEntryOf

class ProfileScreenViewModel(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
    val appVersion: String,
    val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) :
    BaseScreenModel<ProfileScreenUIState, ProfileScreenUIEffect>
        (ProfileScreenUIState(
            languageDialogUiState = LanguageDialogUiState(
                selectedAppLanguage = AppLanguage.entries.find { it.iso == settingsRepository.getCurrentAppLanguage().iso }?: AppLanguage.ENGLISH,
            ),
        )),
    ProfileScreenInteractionListener {

    init {
        getUserInfo()
        setAppVersion()
        setUrlLinks()
    }

    private fun setUrlLinks() {
        // todo: links will not remain hardcoded
        updateState {
            copy(
                shareLinkUrl = "https:mena.dev?uresname=hassan",
                inviteLinkUrl = "https://MENA_app.com"
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

    private fun onUserInfoSuccess(user: User) {
        updateState {
            copy(
                userName = user.username,
                fullName = "${user.firstName} ${user.lastName}",
                profileImageUrl = user.profileImageUrl.orEmpty(),
                isSuccess = true
            )
        }
    }

    private fun onUserInfoError(throwable: Throwable) {
        updateState { copy(isLoading = false, errorMessage = null) }
    }

    private fun onCopyToClipboardSuccess() {
        updateState { copy(showCopiedMessage = true, showShareProfileDialog = false) }
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

    override fun onThemeClicked() =
        updateState { copy(showThemeDialog = true) }

    override fun onPrivacyAndPolicyClicked() =
        sendNewEffect(ProfileScreenUIEffect.NavigateToPrivacyAndPolicyScreen)

    override fun onContactUsClicked() =
        sendNewEffect(ProfileScreenUIEffect.NavigateContactUsScreen)

    override fun onCopyToClipboard(clipboard: Clipboard) {
        tryToExecute(
            function = { clipboard.setClipEntry(clipEntryOf(state.value.shareLinkUrl)) },
            onSuccess = { onCopyToClipboardSuccess() },
            onError = { onDismissShareDialog() }
        )
    }

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

    override fun onDismissCopyLinkSnackBar() {
        updateState { copy(showCopiedMessage = false) }
    }

    override fun onDismissThemeDialog() =
        updateState { copy(showThemeDialog = false) }

    override fun clearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }

    private fun onChangePasswordSuccess(snackBarUiState: SnackBarUiState?) {
        updateState {
            copy(snackBarUiState = snackBarUiState ?: state.value.snackBarUiState)
        }
    }
}