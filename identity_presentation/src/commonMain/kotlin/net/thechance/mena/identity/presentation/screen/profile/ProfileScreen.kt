package net.thechance.mena.identity.presentation.screen.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error
import mena.identity_presentation.generated.resources.ic_close_circle
import mena.identity_presentation.generated.resources.profile_title
import mena.identity_presentation.generated.resources.version
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.dialog.Dialog
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.AddressesScreen
import net.thechance.mena.identity.presentation.screen.editProfile.EditUserProfileScreen
import net.thechance.mena.identity.presentation.screen.profile.components.AccountSettingsSection
import net.thechance.mena.identity.presentation.screen.profile.components.AppSettingsSection
import net.thechance.mena.identity.presentation.screen.profile.components.InviteFriendsCard
import net.thechance.mena.identity.presentation.screen.profile.components.OtherSettingsSection
import net.thechance.mena.identity.presentation.screen.profile.components.ProfileInfoContainer
import net.thechance.mena.identity.presentation.screen.profile.components.ShareIcon
import net.thechance.mena.identity.presentation.screen.notImplemented.NotImplementedScreen
import net.thechance.mena.identity.presentation.screen.profile.components.ShareQrCode
import net.thechance.mena.identity.presentation.screen.profile.components.bottomSheet.ShareSheet
import org.jetbrains.compose.resources.painterResource
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
                        title = "MENA app-download app",
                url = "https://MENA_app.com",
                onDismiss = listener::onDismissBottomSheet
            )
        }

        Scaffold(overlays = {
                    dialog(state.showLanguageDialog) {
                        Dialog(
                            isVisible = it,
                            title = "HI",
                            message = "Not Yet Implemented",
                            onDismiss = listener::onDismissLanguageDialog,
                            actionButtons = {}
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
                            showDialog = it,
                            qrCodePainter = rememberAsyncImagePainter(
                                "https://upload.wikimedia.org/wikipedia/commons/thumb/4/41/QR_Code_Example.svg/2048px-QR_Code_Example.svg.png"
                            ),
                            onDismiss = listener::onDismissShareProfileDialog,
                            fullName = state.fullName,
                            onShareProfile = {},
                            onClipboardContent = { },
                            onDownload = {},
                        )
                    }
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
                            ProfileInfoContainer(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                profilePicture = state.profileImageUrl,
                                fullName = state.fullName,
                                userName = state.userName,
                            )
                        }
                    }
                    item {
                        InviteFriendsCard(
                            onCLick = listener::onInviteFriendsClicked
                        )
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
                            onThemeClicked = listener::onThemeClicked
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

                AnimatedVisibility(
                    visible = state.errorMessage != null,
                    enter = slideInHorizontally(initialOffsetX = { it }),
                    exit = slideOutHorizontally(targetOffsetX = { it }),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SnackBar(
                        title = stringResource(Res.string.error),
                        message = stringResource(state.errorMessage!!),
                        leadingIcon = painterResource(Res.drawable.ic_close_circle),
                        modifier = Modifier.fillMaxWidth().padding(bottom = Theme.spacing._16)
                            .padding(horizontal = Theme.spacing._16)
                    )
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

            ProfileScreenUIEffect.NavigateToChangePasswordScreen -> {
                navigator.push(NotImplementedScreen())
            }

            ProfileScreenUIEffect.NavigateToPrivacyAndPolicyScreen -> {
                navigator.push(NotImplementedScreen())
            }
        }
    }
}
