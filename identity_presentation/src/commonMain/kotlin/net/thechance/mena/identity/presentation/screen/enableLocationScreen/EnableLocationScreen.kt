package net.thechance.mena.identity.presentation.screen.enableLocationScreen


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.enable_location_title
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthAppBar
import net.thechance.mena.identity.presentation.components.EnableLocationLayout
import net.thechance.mena.identity.presentation.components.ErrorSnackBar
import org.jetbrains.compose.resources.stringResource


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
                    title = stringResource(Res.string.enable_location_title),
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
                    errorMessage = state.errorMessage?.let { stringResource(it) },
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

