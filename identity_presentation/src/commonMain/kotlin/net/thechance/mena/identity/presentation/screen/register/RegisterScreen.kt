package net.thechance.mena.identity.presentation.screen.register

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.back
import net.thechance.mena.designsystem.presentation.component.button.NegativeButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.identity.presentation.base.BaseScreen
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

class RegisterScreen :
    BaseScreen<RegisterScreenModel, RegisterScreenUIState, RegisterScreenUIEffect, RegisterScreenInteractionListener>() {
    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @Composable
    override fun OnRender(
        state: RegisterScreenUIState, listener: RegisterScreenInteractionListener
    ) {
        Box(
            Modifier.fillMaxSize()
        , contentAlignment = Alignment.Center
        ) {
            NegativeButton(
                text = stringResource(Res.string.back),
                onClick = listener::onBackButtonClicked,
            )
        }
    }

    override fun onEffect(
        effect: RegisterScreenUIEffect, navigator: Navigator
    ) {
        when (effect) {
            RegisterScreenUIEffect.NavigateBack -> navigator.pop()
        }
    }
}


@Preview
@Composable
fun PreviewRegisterScreen() {
    MenaTheme {

    }
}