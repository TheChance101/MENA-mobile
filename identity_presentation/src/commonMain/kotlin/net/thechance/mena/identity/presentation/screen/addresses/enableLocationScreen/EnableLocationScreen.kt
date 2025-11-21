package net.thechance.mena.identity.presentation.screen.addresses.enableLocationScreen


import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.enable_location_title
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthAppBar
import net.thechance.mena.identity.presentation.components.EnableLocationLayout
import net.thechance.mena.identity.presentation.components.snackBar.IdentitySnackBarController
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
            EnableLocationLayout(
                onEnablePermissionClicked = listener::onClickEnablePermission,
                modifier = Modifier.padding(horizontal = 28.dp)
            )
        }
    }

    override fun onEffect(
        effect: EnableLocationScreenUIEffect,
        navigator: Navigator,
        snackBarController: IdentitySnackBarController
    ) {
        when (effect) {
            EnableLocationScreenUIEffect.NavigateBack -> navigator.pop()

            is EnableLocationScreenUIEffect.ShowSnackBarError -> {
                snackBarController.showSnackBarError(
                    message = effect.errorStringResource
                )
            }
        }
    }
}

