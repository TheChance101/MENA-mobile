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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.download_app_title
import mena.identity_presentation.generated.resources.profile_title
import mena.identity_presentation.generated.resources.version
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.dialog.Dialog
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.ProfileImage
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.AddressesScreen
import net.thechance.mena.identity.presentation.screen.changePassword.ChangePasswordScreen
import net.thechance.mena.identity.presentation.screen.editProfile.EditUserProfileScreen
import net.thechance.mena.identity.presentation.screen.notImplemented.NotImplementedScreen
import net.thechance.mena.identity.presentation.screen.profile.components.AccountSettingsSection
import net.thechance.mena.identity.presentation.screen.profile.components.AppSettingsSection
import net.thechance.mena.identity.presentation.screen.profile.components.InviteFriendsCard
import net.thechance.mena.identity.presentation.screen.profile.components.LanguageDialog
import net.thechance.mena.identity.presentation.screen.profile.components.OtherSettingsSection
import net.thechance.mena.identity.presentation.screen.profile.components.ProfileInfoContainer
import net.thechance.mena.identity.presentation.screen.profile.components.ProfileSnackBar
import net.thechance.mena.identity.presentation.screen.profile.components.ShareIcon
import net.thechance.mena.identity.presentation.screen.profile.components.dialog.share.ShareQrCode
import net.thechance.mena.identity.presentation.screen.profile.components.dialog.share.ShareSheet
import org.jetbrains.compose.resources.stringResource

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
                url = state.inviteLinkUrl,
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
                        currentAppLanguage = state.languageDialogUiState.selectedAppLanguage
                    )
                }
                dialog(state.showThemeDialog) {
                    Dialog(
                        isVisible = it,
                        title = "HI",
                        message = "Not Yet Implemented",
                        onDismiss = listener::onDismissThemeDialog,
                        actionButtons = {}
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
            snakeBar = {
                ProfileSnackBar(
                    snackBarState = state.snackBarUiState,
                    onDismiss = listener::onDismissSnackBar,
                )
            }) {
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
                        AppBar(
                            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 14.dp),
                            title = stringResource(Res.string.profile_title),
                            trailingContent = { ShareIcon(onClick = listener::onShareClicked) }
                        )
                    }
                    item {
                        AnimatedVisibility(
                            visible = state.isSuccess,
                            enter = expandVertically(),
                            exit = shrinkVertically(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box {
                                    ProfileImage(
                                        profileImageUrl = state.profileImageUrl,
                                        profileImageBitmap = null
                                    )
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 15.dp, bottom = 3.dp)
                                            .align(Alignment.BottomEnd)
                                            .size(10.dp)
                                            .border(1.dp, Theme.colorScheme.stroke, CircleShape)
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
                            onPrivacySettingsClicked = listener::onPrivacySettingsClicked
                        )
                    }
                    item {
                        AppSettingsSection(
                            onLanguageClicked = listener::onLanguageClicked,
                            onThemeClicked = listener::onThemeClicked,
                            currentLanguage = state.languageDialogUiState.selectedAppLanguage.iso
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

                LaunchedEffect(state.errorMessage) {
                    delay(3000)
                    listener.clearErrorMessage()
                }
            }
        }
    }

    override fun onEffect(
        effect: ProfileScreenUIEffect, navigator: Navigator
    ) {
        when (effect) {
            ProfileScreenUIEffect.NavigateToEditProfileScreen -> {
                navigator.push(EditUserProfileScreen())
            }

            ProfileScreenUIEffect.NavigateToLocationPickerScreen -> {
                navigator.push(AddressesScreen())
            }

            ProfileScreenUIEffect.NavigateContactUsScreen -> {
                navigator.push(NotImplementedScreen())
            }

            is ProfileScreenUIEffect.NavigateToChangePasswordScreen -> {
                navigator.push(ChangePasswordScreen(effect.onSuccess))
            }

            ProfileScreenUIEffect.NavigateToPrivacyAndPolicyScreen -> {
                navigator.push(NotImplementedScreen())
            }
        }
    }
}
