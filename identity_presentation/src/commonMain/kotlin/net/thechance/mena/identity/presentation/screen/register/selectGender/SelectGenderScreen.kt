package net.thechance.mena.identity.presentation.screen.register.selectGender

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.register
import mena.identity_presentation.generated.resources.select_gender_screen_prompt
import mena.identity_presentation.generated.resources.select_gender_screen_prompt_title
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthScreenContainer
import net.thechance.mena.identity.presentation.components.ErrorSnackBar
import net.thechance.mena.identity.presentation.components.PageDescription
import net.thechance.mena.identity.presentation.screen.editProfile.components.GenderToggle
import net.thechance.mena.identity.presentation.screen.register.shared.uiState.RegisterUIState
import net.thechance.mena.identity.presentation.screen.register.uploadProfileImage.UploadProfileImageScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

class SelectGenderScreen(
    private val registerUIState: RegisterUIState
) : BaseScreen<
        SelectGenderScreenViewModel,
        SelectGenderScreenUIState,
        SelectGenderScreenUIEffect,
        SelectGenderScreenInteractionListener>() {

    @Composable
    override fun Content() {
        InitScreen(getScreenModel(parameters = { parametersOf(registerUIState) }))
    }

    @Composable
    override fun OnRender(
        state: SelectGenderScreenUIState,
        listener: SelectGenderScreenInteractionListener
    ) {
        Scaffold {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()

            ) {
                AuthScreenContainer {
                    PageDescription(
                        title = stringResource(Res.string.select_gender_screen_prompt_title),
                        subtitle = stringResource(Res.string.select_gender_screen_prompt),
                    )

                    GenderToggle(gender = state.gender, onChangeGender = listener::onChangeGender)

                    Spacer(modifier = Modifier.weight(1f))

                    PrimaryButton(
                        text = stringResource(Res.string.register),
                        onClick = listener::onClickRegister,
                        isEnabled = state.isRegisterEnabled,
                        isLoading = state.isRegisterLoading,
                        contentPadding = PaddingValues(vertical = 13.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        ErrorSnackBar(
            errorMessage = state.errorMessage?.let { stringResource(it) },
            onDismiss = listener::onClearErrorMessage
        )
    }

    override fun onEffect(
        effect: SelectGenderScreenUIEffect,
        navigator: Navigator
    ) {
        when (effect) {
            is SelectGenderScreenUIEffect.NavigateToUploadProfileImage -> {
                navigator.push(
                    UploadProfileImageScreen(
                        authTokens = effect.authTokens,
                        phoneNumber = effect.phoneNumber
                    )
                )
            }
        }
    }
}