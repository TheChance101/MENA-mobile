package net.thechance.mena.identity.presentation.screen.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.dismiss
import mena.identity_presentation.generated.resources.error
import mena.identity_presentation.generated.resources.ic_close_circle
import mena.identity_presentation.generated.resources.profile_title
import mena.identity_presentation.generated.resources.version
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.bottomSheet.BottomSheet
import net.thechance.mena.designsystem.presentation.component.button.NegativeButton
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
import net.thechance.mena.identity.presentation.screen.register.RegisterScreen
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
        Scaffold(overlays = {
            bottomSheet(
                isVisible = state.showShareBottomSheet
            ) {
                BottomSheet(
                    isVisible = it,
                    onDismissRequest = listener::onDismissBottomSheet,
                ) {
                    Column(
                        Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Invite friends Not Yet Implemented",
                            style = Theme.typography.label.small
                        )
                        NegativeButton(
                            text = stringResource(Res.string.dismiss),
                            onClick = listener::onDismissBottomSheet,
                        )
                    }
                }
            }
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
        }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                Column(
                    Modifier.fillMaxSize()
                        .background(Theme.colorScheme.background.surface)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = Theme.spacing._16),
                ) {
                    AppBar(
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 14.dp),
                        title = stringResource(Res.string.profile_title),
                        trailingContent = { ShareIcon(onClick = listener::onShareClicked) }
                    )
                    AnimatedVisibility(
                        visible = state.isSuccess,
                        enter = expandVertically(),
                        exit = shrinkVertically(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ProfileInfoContainer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally),
                            profilePicture = state.profileImageUrl,
                            fullName = state.fullName,
                            userName = state.userName,
                        )
                    }

                    InviteFriendsCard(
                        onCLick = listener::onInviteFriendsClicked
                    )

                    AccountSettingsSection(
                        onEditProfileInfoClicked = listener::onEditProfileInfoClicked,
                        onChangePasswordClicked = listener::onChangePasswordClicked,
                        onAddressesClicked = listener::onAddressesClicked,
                        onPrivacySettingsClicked = listener::onPrivacySettingsClicked
                    )

                    AppSettingsSection(
                        onLanguageClicked = listener::onLanguageClicked,
                        onThemeClicked = listener::onThemeClicked
                    )

                    OtherSettingsSection(
                        onPrivacyAndPolicyClicked = listener::onPrivacyAndPolicyClicked,
                        onContactUsClicked = listener::onContactUsClicked
                    )

                    Text(
                        modifier = Modifier
                            .padding(vertical = Theme.spacing._16)
                            .align(Alignment.CenterHorizontally),
                        text = "${stringResource(Res.string.version)} ${state.versionNumber}",
                        style = Theme.typography.label.small,
                        color = Theme.colorScheme.shadeSecondary,
                    )
                }

                AnimatedVisibility(
                    visible = state.errorMessage != null,
                    enter = slideInHorizontally(initialOffsetX = { it }),
                    exit = slideOutHorizontally(targetOffsetX = { it }),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SnackBar(
                        title = stringResource(Res.string.error),
                        message =  stringResource(state.errorMessage!!),
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
                navigator.push(RegisterScreen())
            }

            ProfileScreenUIEffect.NavigateToChangePasswordScreen -> {
                navigator.push(RegisterScreen())
            }

            ProfileScreenUIEffect.NavigateToPrivacyAndPolicyScreen -> {
                navigator.push(RegisterScreen())
            }
        }
    }
}
