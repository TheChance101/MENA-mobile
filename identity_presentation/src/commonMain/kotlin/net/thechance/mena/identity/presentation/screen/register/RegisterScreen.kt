package net.thechance.mena.identity.presentation.screen.register

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.identity.presentation.base.BaseScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

class RegisterScreen : BaseScreen<
        RegisterScreenModel,
        RegisterScreenUIState,
        RegisterScreenUIEffect,
        RegisterScreenInteractionListener>() {
    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @Composable
    override fun OnRender(
        state: RegisterScreenUIState,
        listener: RegisterScreenInteractionListener
    ) {
        RegisterScreenContent(

        )
    }

    @Composable
    fun RegisterScreenContent() {

    }

    override fun onEffect(
        effect: RegisterScreenUIEffect,
        navigator: Navigator
    ) {


    }
}


@Preview
@Composable
fun PreviewRegisterScreen() {
    MenaTheme {

    }
}