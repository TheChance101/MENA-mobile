package net.thechance.mena.identity.presentation.screen.register.enterName

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.complete_profile_description
import mena.identity_presentation.generated.resources.complete_profile_title
import mena.identity_presentation.generated.resources.first_name_label
import mena.identity_presentation.generated.resources.last_name_label
import mena.identity_presentation.generated.resources.login_background
import mena.identity_presentation.generated.resources.next
import mena.identity_presentation.generated.resources.username_label
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthScreenContainer
import net.thechance.mena.identity.presentation.components.PageDescription
import net.thechance.mena.identity.presentation.screen.editProfile.component.AtPrefixTransformation
import net.thechance.mena.identity.presentation.screen.editProfile.component.ProfileEditText
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

class EnterNameScreen() :
    BaseScreen<EnterNameViewModel,
            EnterNameUIState,
            EnterNameUIEffect,
            EnterNameInteractionListener>() {

    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @Composable
    override fun OnRender(
        state: EnterNameUIState, listener: EnterNameInteractionListener
    ) {
        Scaffold(
        )
        {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Theme.colorScheme.background.surface)
            ) {
                Image(
                    painter = painterResource(Res.drawable.login_background),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )

                AuthScreenContainer {
                    PageDescription(
                        title = stringResource(Res.string.complete_profile_title),
                        subtitle = stringResource(Res.string.complete_profile_description),
                    )
                    ProfileEditText(
                        title = stringResource(Res.string.first_name_label),
                        value = state.firstName,
                        onValueChange = listener::onChangeFirstName,
                    )

                    ProfileEditText(
                        title = stringResource(Res.string.last_name_label),
                        value = state.lastName,
                        onValueChange = listener::onLastNameChange,
                    )

                    ProfileEditText(
                        title = stringResource(Res.string.username_label),
                        value = state.username,
                        onValueChange = listener::onUsernameChange,
                        visualTransformation = AtPrefixTransformation
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    PrimaryButton(
                        text = stringResource(Res.string.next),
                        onClick = listener::onClickNext,
                        isEnabled = state.isNextEnabled,
                        isLoading = state.isLoading,
                        contentPadding = PaddingValues(vertical = 13.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Theme.spacing._12)
                            .imePadding()
                    )
                }
            }
        }
    }

    override fun onEffect(
        effect: EnterNameUIEffect,
        navigator: Navigator
    ) {
        when (effect) {
            EnterNameUIEffect.NavigateToNextStep -> {}
        }
    }

    @Preview
    @Composable
    private fun Preview_Empty() {
        MenaTheme {
            EnterNameScreen().OnRender(
                state = EnterNameUIState(
                    firstName = "",
                    lastName = "",
                    username = "",
                    isNextEnabled = false,
                    isLoading = false,
                ),
                listener = object : EnterNameInteractionListener {
                    override fun onChangeFirstName(name: String) {}
                    override fun onLastNameChange(name: String) {}
                    override fun onUsernameChange(username: String) {}
                    override fun onClickNext() {}
                    override fun onClearErrorMessage() {}
                }
            )
        }
    }

    @Preview
    @Composable
    private fun Preview_Filled() {
        MenaTheme {
            EnterNameScreen().OnRender(
                state = EnterNameUIState(
                    firstName = "Mohammed",
                    lastName = "Ahmed",
                    username = "mohammed_2025",
                    isNextEnabled = true,
                    isLoading = false
                ),
                listener = object : EnterNameInteractionListener {
                    override fun onChangeFirstName(name: String) {}
                    override fun onLastNameChange(name: String) {}
                    override fun onUsernameChange(username: String) {}
                    override fun onClickNext() {}
                    override fun onClearErrorMessage() {}
                }
            )
        }
    }
}