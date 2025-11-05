package net.thechance.mena.identity.presentation.screen.profile

import androidx.compose.ui.platform.Clipboard
import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface ProfileScreenInteractionListener : BaseInteractionListener {

    fun onShareClicked()
    fun onInviteFriendsClicked()
    fun onEditProfileInfoClicked()
    fun onChangePasswordClicked()

    fun onAddressesClicked()
    fun onPrivacySettingsClicked()
    fun onLanguageClicked()
    fun onThemeClicked()
    fun onPrivacyAndPolicyClicked()
    fun clearErrorMessage()
    fun onDismissLanguageDialog()
    fun onDismissThemeDialog()
    fun onDismissBottomSheet()
    fun onDismissShareDialog()
    fun onCopyToClipboard(clipboard: Clipboard)

    fun onDismissCopyLinkSnackBar()

    fun onContactUsClicked()

}