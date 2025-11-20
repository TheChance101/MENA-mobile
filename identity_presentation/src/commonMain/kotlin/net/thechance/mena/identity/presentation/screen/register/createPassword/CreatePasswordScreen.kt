package net.thechance.mena.identity.presentation.screen.register.createPassword

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
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.confirm_password_label
import mena.identity_presentation.generated.resources.create_password_description
import mena.identity_presentation.generated.resources.create_password_title
import mena.identity_presentation.generated.resources.new_password_title
import mena.identity_presentation.generated.resources.next
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthScreenContainer
import net.thechance.mena.identity.presentation.components.LabeledInputPassword
import net.thechance.mena.identity.presentation.components.PageDescription
import net.thechance.mena.identity.presentation.components.snackBar.IdentitySnackBarController
import net.thechance.mena.identity.presentation.screen.register.datePicker.DatePickerScreen
import net.thechance.mena.identity.presentation.screen.register.shared.uiState.RegisterUIState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.parameter.parametersOf

class CreatePasswordScreen(
    private val registerUIState: RegisterUIState
) : BaseScreen<
    CreatePasswordViewModel,
    CreatePasswordUIState,
    CreatePasswordUIEffect,
    CreatePasswordInteractionListener>() {

    @Composable
    override fun Content() {
        InitScreen(getScreenModel(parameters = { parametersOf(registerUIState) }))
    }

    @Composable
    override fun OnRender(
        state: CreatePasswordUIState,
        listener: CreatePasswordInteractionListener
    ) {
        Scaffold {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Theme.colorScheme.background.surface)
            ) {
                AuthScreenContainer {
                    PageDescription(
                        title = stringResource(Res.string.create_password_title),
                        subtitle = stringResource(Res.string.create_password_description),
                    )

                    LabeledInputPassword(
                        password = state.newPassword,
                        isPasswordVisible = state.isNewPasswordVisible,
                        onChangePassword = listener::onChangeNewPassword,
                        onTogglePasswordVisibility = listener::onToggleNewPasswordVisibility,
                        label = stringResource(Res.string.new_password_title),
                        errorMessage = state.newPasswordErrorMessage?.let { stringResource(it) },
                        modifier = Modifier.padding(bottom = Theme.spacing._16)
                    )

                    LabeledInputPassword(
                        password = state.confirmPassword,
                        isPasswordVisible = state.isConfirmPasswordVisible,
                        onChangePassword = listener::onChangeConfirmPassword,
                        onTogglePasswordVisibility = listener::onToggleConfirmPasswordVisibility,
                        label = stringResource(Res.string.confirm_password_label),
                        errorMessage = state.confirmPasswordErrorMessage?.let {
                            stringResource(it)
                        },
                        modifier = Modifier.padding(bottom = Theme.spacing._16)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    PrimaryButton(
                        text = stringResource(Res.string.next),
                        onClick = listener::onClickCreatePassword,
                        isEnabled = state.isCreateEnabled,
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
        effect: CreatePasswordUIEffect,
        navigator: Navigator,
        snackBarController: IdentitySnackBarController
    ) {
        when (effect) {
            is CreatePasswordUIEffect.NavigateToDatePicker -> {
                navigator.push(DatePickerScreen(registerUIState = effect.registerUIState))
            }

            is CreatePasswordUIEffect.ShowSnackBarError -> {
                snackBarController.showSnackBarError(effect.errorStringResource)
            }
        }
    }
}

@Preview
@Composable
fun PreviewCreatePasswordScreen() {
    MenaTheme {
        CreatePasswordScreen(
            registerUIState = RegisterUIState(
                phoneNumber = PhoneNumber(
                    countryCode = "+971",
                    localNumber = "555555555"
                ),
                countryCode = "AE"
            )
        ).OnRender(
            state = CreatePasswordUIState(
                newPassword = "Password123",
                confirmPassword = "Password123",
                isCreateEnabled = true
            ),
            listener = object : CreatePasswordInteractionListener {
                override fun onChangeNewPassword(password: String) {}
                override fun onChangeConfirmPassword(password: String) {}
                override fun onToggleNewPasswordVisibility() {}
                override fun onToggleConfirmPasswordVisibility() {}
                override fun onClickCreatePassword() {}
            }
        )
    }
}