package net.thechance.mena.identity.presentation.screen.profile

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.ErrorState

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
    }

    private fun setAppVersion() {
        updateState {
            copy(
                versionNumber = appVersion
            )
        }
    }

    private fun onErrorOccurred(errorState: ErrorState) {
        updateState {
            copy(
                isLoading = false,
                errorMessage = null
            )
        }
    }

    private fun getUserInfo() {
        tryToCollect(
            function = { userRepository.getUser() },
            onNewValue = ::updateUserInfo,
            onError = ::onErrorOccurred,
            dispatcher = dispatcher
        )
    }

    private fun updateUserInfo(user: User) {
        updateState {
            copy(
                userName = user.username,
                fullName = "${user.firstName} ${user.lastName}",
                profileImageUrl = user.profileImageUrl,
                isSuccess = true
            )
        }
    }

    override fun onEditProfileInfoClicked() =
        sendNewEffect(ProfileScreenUIEffect.NavigateToEditProfileScreen)

    override fun onShareClicked() =
        sendNewEffect(ProfileScreenUIEffect.NavigateToEditProfileScreen)

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

    override fun onDismissLanguageDialog() =
        updateState { copy(showLanguageDialog = false) }

    override fun onDismissBottomSheet() =
        updateState { copy(showShareBottomSheet = false) }

    override fun onDismissThemeDialog() =
        updateState { copy(showThemeDialog = false) }

    override fun clearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }
}