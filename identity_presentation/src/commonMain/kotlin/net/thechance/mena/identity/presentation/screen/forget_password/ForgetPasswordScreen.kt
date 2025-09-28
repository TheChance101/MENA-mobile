package net.thechance.mena.identity.presentation.screen.forget_password

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.identity.presentation.base.BaseScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

class ForgetPasswordScreen : BaseScreen<
        ForgetPasswordScreenModel,
        ForgetPasswordScreenUIState,
        ForgetPasswordScreenUIEffect,
        ForgetPasswordScreenInteractionListener>() {
    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @Composable
    override fun OnRender(
        state: ForgetPasswordScreenUIState,
        listener: ForgetPasswordScreenInteractionListener
    ) {
        ForgetPasswordScreenContent(

        )
    }

    @Composable
    fun ForgetPasswordScreenContent() {

    }

    override fun onEffect(
        effect: ForgetPasswordScreenUIEffect,
        navigator: Navigator
    ) {


    }
}


@Preview
@Composable
fun PreviewForgetPasswordScreen() {
    MenaTheme {

    }
}