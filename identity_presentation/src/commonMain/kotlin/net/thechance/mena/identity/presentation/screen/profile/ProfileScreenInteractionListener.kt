package net.thechance.mena.identity.presentation.screen.profile

import net.thechance.mena.identity.domain.util.AppLanguage
import net.thechance.mena.identity.domain.util.AppTheme
import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface ProfileScreenInteractionListener : BaseInteractionListener {

    fun onShareClicked()
    fun onInviteFriendsClicked()
    fun onEditProfileInfoClicked()
    fun onChangePasswordClicked()

    fun onAddressesClicked()
    fun onPrivacySettingsClicked()
    fun onLanguageClicked()
    fun onThemeSettingsClicked()
    fun onPrivacyAndPolicyClicked()
    fun onDismissLanguageDialog()
    fun onDismissThemeDialog()
    fun onDismissBottomSheet()
    fun onDismissShareDialog()


    fun onContactUsClicked()
    fun onConfirmLanguageSelection()
    fun onSelectLanguage(appLanguage: AppLanguage)
    fun onConfirmThemeSelection()
    fun onSelectTheme(appTheme: AppTheme)
}