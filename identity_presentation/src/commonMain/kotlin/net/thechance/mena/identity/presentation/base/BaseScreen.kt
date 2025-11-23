@file:Suppress("ERROR_SUPPRESSION")

package net.thechance.mena.identity.presentation.base

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.identity.presentation.base.util.collectAsEffectWithLifeCycle
import net.thechance.mena.identity.presentation.components.snackBar.IdentitySnackBarController
import net.thechance.mena.identity.presentation.components.snackBar.LocalSnackBarController
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.mp.KoinPlatform

@Suppress("BOUNDS_NOT_ALLOWED_IF_BOUNDED_BY_TYPE_PARAMETER")
abstract class BaseScreen<VM, S, E, I> : Screen
    where  VM : BaseScreenModel<S, E>, I : BaseInteractionListener, VM : I {
    @Composable
    fun InitScreen(viewModel: VM) {
        var observedState by remember { mutableStateOf(viewModel.state.value) }
        val snackBarController = LocalSnackBarController.current

        LaunchedEffect(Unit) {
            viewModel.state.collect { stateValue ->
                observedState = stateValue
            }
        }

        val state = observedState
        val navigator = LocalNavigator.currentOrThrow

        viewModel.effect.collectAsEffectWithLifeCycle {
            onEffect(effect = it, navigator = navigator, snackBarController = snackBarController)
        }

        OnRender(state, viewModel)

        snackBarController.currentSnackBarData?.let { snackBarData ->
            SnackBar(
                isVisible = snackBarData.isVisible,
                title = stringResource(snackBarData.title),
                message = stringResource(snackBarData.message),
                leadingIcon = painterResource(snackBarData.type.icon),
                displayDurationMs = snackBarData.duration,
                onDismiss = snackBarController::dismissSnackBar,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }

    @Composable
    abstract fun OnRender(state: S, listener: I)

    abstract fun onEffect(
        effect: E,
        navigator: Navigator,
        snackBarController: IdentitySnackBarController
    )

    @Composable
    inline fun <reified T : ScreenModel> getScreenModel(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null,
    ): T {
        val koin = KoinPlatform.getKoin()
        return rememberScreenModel(tag = qualifier?.value) { koin.get(qualifier, parameters) }
    }
}