package net.thechance.mena.identity.presentation.screen.notImplemented

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

class NotImplementedScreen :
    BaseScreen<NotImplementedScreenViewModel, NotImplementedScreenUIState, NotImplementedScreenUIEffect, NotImplementedScreenInteractionListener>() {
    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @Composable
    override fun OnRender(
        state: NotImplementedScreenUIState,
        listener: NotImplementedScreenInteractionListener
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            NegativeButton(
                text = stringResource(Res.string.back),
                onClick = listener::onBackButtonClicked,
            )
        }
    }

    override fun onEffect(
        effect: NotImplementedScreenUIEffect,
        navigator: Navigator
    ) {
        when (effect) {
            NotImplementedScreenUIEffect.NavigateBack -> navigator.pop()
        }
    }
}

@Preview
@Composable
fun PreviewNotImplementedScreen() {
    MenaTheme {
        NotImplementedScreen().OnRender(
            state = NotImplementedScreenUIState(),
            listener = object : NotImplementedScreenInteractionListener {
                override fun onBackButtonClicked() {}
            }
        )
    }
}