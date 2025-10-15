package net.thechance.mena.identity.presentation.screen.enableLocationScreen


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthAppBar
import net.thechance.mena.identity.presentation.components.EnableLocationLayout
import net.thechance.mena.identity.presentation.components.ErrorSnackBar


class EnableLocationScreen : BaseScreen<
        EnableLocationScreenViewModel,
        EnableLocationScreenUIState,
        EnableLocationScreenUIEffect,
        EnableLocationScreenInteractionListener>() {
    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @Composable
    override fun OnRender(
        state: EnableLocationScreenUIState,
        listener: EnableLocationScreenInteractionListener
    ) {
        Scaffold(
            topBar = {
                AuthAppBar(
                    title = "Enable location",
                    onClickBack = listener::onClickBack
                )
            }
        ) {
            Box() {
                EnableLocationLayout(
                    onEnablePermissionClicked = listener::onClickEnablePermission,
                    modifier = Modifier.padding(horizontal = 28.dp)
                )
                ErrorSnackBar(
                    errorMessage = state.errorMessage,
                    onDismiss = listener::onClearErrorMessage,
                    modifier = Modifier.statusBarsPadding().align(Alignment.TopCenter)
                )
            }

        }
    }

    override fun onEffect(
        effect: EnableLocationScreenUIEffect,
        navigator: Navigator
    ) {
        when (effect) {
            EnableLocationScreenUIEffect.NavigateBack -> navigator.pop()
        }
    }
}

