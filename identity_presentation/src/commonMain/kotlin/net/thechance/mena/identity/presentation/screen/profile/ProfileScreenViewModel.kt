package net.thechance.mena.identity.presentation.screen.profile

import androidx.compose.ui.platform.Clipboard
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.mapper.createNavigateToEditProfileEffect
import net.thechance.mena.identity.presentation.screen.profile.components.share.clipEntryOf

class ProfileScreenViewModel(
    private val userRepository: UserRepository,
    val appVersion: String,
    val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    BaseScreenModel<ProfileScreenUIState, ProfileScreenUIEffect>
        (ProfileScreenUIState()),
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

    override fun onChangePasswordClicked() =
        sendNewEffect(ProfileScreenUIEffect.NavigateToChangePasswordScreen)

    override fun onAddressesClicked() =
        sendNewEffect(ProfileScreenUIEffect.NavigateToLocationPickerScreen)

    override fun onPrivacySettingsClicked() =
        sendNewEffect(ProfileScreenUIEffect.NavigateToPrivacyAndPolicyScreen)

    override fun onLanguageClicked() =
        updateState { copy(showLanguageDialog = true) }

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

    override fun onDismissLanguageDialog() =
        updateState { copy(showLanguageDialog = false) }

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
}