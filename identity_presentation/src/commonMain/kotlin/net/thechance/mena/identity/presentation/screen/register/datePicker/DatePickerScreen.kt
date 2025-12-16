package net.thechance.mena.identity.presentation.screen.register.datePicker

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.date_picker_screen_prompt
import mena.identity_presentation.generated.resources.date_picker_screen_prompt_title
import mena.identity_presentation.generated.resources.next
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthScreenContainer
import net.thechance.mena.identity.presentation.components.GregorianDatePicker
import net.thechance.mena.identity.presentation.components.PageDescription
import net.thechance.mena.identity.presentation.components.snackBar.IdentitySnackBarController
import net.thechance.mena.identity.presentation.screen.register.selectGender.SelectGenderScreen
import net.thechance.mena.identity.presentation.screen.register.shared.convertJsonStringToRegisterUIState
import net.thechance.mena.identity.presentation.screen.register.shared.toRegisterJsonString
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

data class DatePickerScreen(
    val registerUIStateJsonString: String
) :
    BaseScreen<
            DatePickerScreenViewModel,
            DatePickerScreenUIState,
            DatePickerScreenUIEffect,
            DatePickerScreenInteractionListener>() {

    @Composable
    override fun Content() {
        InitScreen(getScreenModel(parameters = { parametersOf(convertJsonStringToRegisterUIState(registerUIStateJsonString)) }))
    }

    @Composable
    override fun OnRender(
        state: DatePickerScreenUIState,
        listener: DatePickerScreenInteractionListener
    ) {
        Scaffold {
            AuthScreenContainer {
                PageDescription(
                    title = stringResource(Res.string.date_picker_screen_prompt_title),
                    subtitle = stringResource(Res.string.date_picker_screen_prompt),
                )
                Spacer(Modifier.weight(1f))
                GregorianDatePicker(
                    selectedDate = state.selectedDate,
                    onDateChange = listener::onChangeDate
                )
                Spacer(Modifier.weight(1f))
                PrimaryButton(
                    text = stringResource(Res.string.next),
                    onClick = listener::onClickNext,
                    isEnabled = state.isNextEnabled,
                    isLoading = state.isNextLoading,
                    contentPadding = PaddingValues(vertical = 13.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    override fun onEffect(
        effect: DatePickerScreenUIEffect,
        navigator: Navigator,
        snackBarController: IdentitySnackBarController
    ) {
        when (effect) {
            is DatePickerScreenUIEffect.NavigateToSelectGender -> {
                navigator.push(SelectGenderScreen(registerUIStateJsonString = effect.registerUIState.toRegisterJsonString()))
            }
        }
    }
}