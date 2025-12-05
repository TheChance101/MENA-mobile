package net.thechance.mena.identity.presentation.screen.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.download_app_title
import mena.identity_presentation.generated.resources.profile_title
import mena.identity_presentation.generated.resources.share_message
import mena.identity_presentation.generated.resources.version
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.ProfileImage
import net.thechance.mena.identity.presentation.components.snackBar.IdentitySnackBarController
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.MyAddressesScreen
import net.thechance.mena.identity.presentation.screen.changePassword.ChangePasswordScreen
import net.thechance.mena.identity.presentation.screen.contactUs.ContactUsScreen
import net.thechance.mena.identity.presentation.screen.editProfile.EditUserProfileScreen
import net.thechance.mena.identity.presentation.screen.privacyAndPolicy.PrivacyAndPolicyScreen
import net.thechance.mena.identity.presentation.screen.profile.components.AccountSettingsSection
import net.thechance.mena.identity.presentation.screen.profile.components.AppSettingsSection
import net.thechance.mena.identity.presentation.screen.profile.components.InviteFriendsCard
import net.thechance.mena.identity.presentation.screen.profile.components.LanguageDialog
import net.thechance.mena.identity.presentation.screen.profile.components.OtherSettingsSection
import net.thechance.mena.identity.presentation.screen.profile.components.ProfileInfoContainer
import net.thechance.mena.identity.presentation.screen.profile.components.ShareIcon
import net.thechance.mena.identity.presentation.screen.profile.components.ThemeDialog
import net.thechance.mena.identity.presentation.screen.profile.components.share.ShareQrCode
import net.thechance.mena.identity.presentation.screen.profile.components.share.utils.ShareSheet
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.ExperimentalUuidApi

class ProfileScreen : BaseScreen<
    ProfileScreenViewModel,
    ProfileScreenUIState,
    ProfileScreenUIEffect,
    ProfileScreenInteractionListener>() {
    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @Composable
    override fun OnRender(
        state: ProfileScreenUIState,
        listener: ProfileScreenInteractionListener,
    ) {

        AnimatedVisibility(state.showShareBottomSheet) {
            ShareSheet(
                title = stringResource(Res.string.download_app_title),
                message = stringResource(Res.string.share_message),
                shareLink = state.inviteLinkUrl,
                onDismiss = listener::onDismissBottomSheet
            )
        }

        Scaffold(
            overlays = {
                dialog(state.languageDialogUiState.isVisible) {
                    LanguageDialog(
                        isVisible = it,
                        onDismissRequest = listener::onDismissLanguageDialog,
                        appLanguages = state.languageDialogUiState.options,
                        onConfirmLanguageSelection = listener::onConfirmLanguageSelection,
                        currentAppLanguage = state.currentLanguage,
                        selectedAppLanguage = state.languageDialogUiState.selectedAppLanguage,
                        onLanguageChanged = listener::onSelectLanguage
                    )
                }
                dialog(state.themeDialogUiState.isVisible) {
                    ThemeDialog(
                        isVisible = it,
                        onDismissRequest = listener::onDismissThemeDialog,
                        appThemes = state.themeDialogUiState.options,
                        onConfirmThemeSelection = listener::onConfirmThemeSelection,
                        currentAppTheme = state.currentTheme,
                        onThemeChanged = listener::onSelectTheme,
                        selectedAppTheme = state.themeDialogUiState.selectedAppTheme
                    )
                }
                dialog(state.showShareProfileDialog) {
                    ShareQrCode(
                        isVisible = state.showShareProfileDialog,
                        fullName = state.fullName,
                        onClickShare = listener::onInviteFriendsClicked,
                        onDismissShareDialog = listener::onDismissShareDialog,
                    )
                }
            },
            topBar = {
                AppBar(
                    title = stringResource(Res.string.profile_title),
                    trailingContent = { ShareIcon(onClick = listener::onShareClicked) }
                )
            }
        )
        {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .background(Theme.colorScheme.background.surface),
                    contentPadding = PaddingValues(horizontal = Theme.spacing._16),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        AnimatedVisibility(
                            visible = state.isSuccess,
                            enter = expandVertically(),
                            exit = shrinkVertically(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier.offset(y = 4.dp)
                                ) {
                                    ProfileImage(
                                        profileImageUrl = state.profileImageUrl,
                                        profileImageBitmap = null
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(end = 15.dp, bottom = 3.dp)
                                            .size(10.dp)
                                            .border(1.dp, Theme.colorScheme.stroke, CircleShape)
                                            .padding(1.dp)
                                            .background(Theme.colorScheme.success, CircleShape)
                                    )
                                }
                                ProfileInfoContainer(
                                    fullName = state.fullName,
                                    userName = state.userName,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                InviteFriendsCard(
                                    onClick = listener::onInviteFriendsClicked
                                )
                            }
                        }
                    }
                    item {
                        AccountSettingsSection(
                            onEditProfileInfoClicked = listener::onEditProfileInfoClicked,
                            onChangePasswordClicked = listener::onChangePasswordClicked,
                            onAddressesClicked = listener::onAddressesClicked,
                        )
                    }
                    item {
                        AppSettingsSection(
                            onLanguageClicked = listener::onLanguageClicked,
                            onThemeClicked = listener::onThemeSettingsClicked,
                            currentLanguage = state.languageDialogUiState.selectedAppLanguage.iso,
                            currentTheme = state.currentTheme.name
                        )
                    }
                    item {
                        OtherSettingsSection(
                            onPrivacyAndPolicyClicked = listener::onPrivacyAndPolicyClicked,
                            onContactUsClicked = listener::onContactUsClicked
                        )
                    }
                    item {
                        Text(
                            modifier = Modifier
                                .padding(vertical = Theme.spacing._16),
                            text = "${stringResource(Res.string.version)} ${state.versionNumber}",
                            style = Theme.typography.label.small,
                            color = Theme.colorScheme.shadeSecondary,
                        )
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalUuidApi::class)
    override fun onEffect(
        effect: ProfileScreenUIEffect,
        navigator: Navigator,
        snackBarController: IdentitySnackBarController,
    ) {
        when (effect) {
            is ProfileScreenUIEffect.NavigateToEditProfileScreen -> {
                navigator.push(
                    EditUserProfileScreen(
                        id = effect.userInfo?.id.toString(),
                        firstName = effect.userInfo?.firstName.toString(),
                        lastName = effect.userInfo?.lastName.toString(),
                        profileImageUrl = effect.userInfo?.profileImageUrl.toString(),
                        username = effect.userInfo?.username.toString(),
                        birthDate = effect.userInfo?.birthDate.toString(),
                        gender = effect.userInfo?.gender!!
                    )
                )
            }

            ProfileScreenUIEffect.NavigateToLocationPickerScreen -> {
                navigator.push(MyAddressesScreen())
            }

            ProfileScreenUIEffect.NavigateContactUsScreen -> {
                navigator.push(ContactUsScreen())
            }

            is ProfileScreenUIEffect.NavigateToChangePasswordScreen -> {
                navigator.push(ChangePasswordScreen())
            }

            ProfileScreenUIEffect.NavigateToPrivacyAndPolicyScreen -> {
                navigator.push(PrivacyAndPolicyScreen())
            }

            is ProfileScreenUIEffect.ShowSnackBarError -> {
                snackBarController.showSnackBarError(
                    message = effect.errorStringResource
                )
            }
        }
    }
}
