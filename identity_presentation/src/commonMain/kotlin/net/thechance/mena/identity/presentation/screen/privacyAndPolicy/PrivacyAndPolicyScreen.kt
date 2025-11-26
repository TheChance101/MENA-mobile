package net.thechance.mena.identity.presentation.screen.privacyAndPolicy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.privacy_and_policy
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthAppBar
import net.thechance.mena.identity.presentation.components.snackBar.IdentitySnackBarController
import net.thechance.mena.identity.presentation.screen.privacyAndPolicy.components.PrivacyScreenContent
import net.thechance.mena.identity.presentation.screen.privacyAndPolicy.components.PrivacyScreenContentShimmer
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

class PrivacyAndPolicyScreen :
    BaseScreen<PrivacyAndPolicyScreenViewModel,
        PrivacyAndPolicyScreenUIState,
        PrivacyAndPolicyScreenUIEffect,
        PrivacyAndPolicyScreenInteractionListener>() {

    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @Composable
    override fun OnRender(
        state: PrivacyAndPolicyScreenUIState,
        listener: PrivacyAndPolicyScreenInteractionListener
    ) {
        Scaffold(
            topBar = {
                AuthAppBar(
                    title = stringResource(Res.string.privacy_and_policy),
                    onClickBack = listener::onClickBack
                )
            }
        ) {
            AnimatedVisibility(
                visible = state.isLoading && state.privacyAndPolicySections.isEmpty(),
                enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                exit = fadeOut(animationSpec = tween(durationMillis = 500))
            ) {
                PrivacyScreenContentShimmer()
            }
            AnimatedVisibility(
                visible = !state.isLoading && !state.privacyAndPolicySections.isEmpty(),
                enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                exit = fadeOut(animationSpec = tween(durationMillis = 500))
            ) {
                PrivacyScreenContent(state)
            }
        }
    }

    override fun onEffect(
        effect: PrivacyAndPolicyScreenUIEffect,
        navigator: Navigator,
        snackBarController: IdentitySnackBarController
    ) {
        when (effect) {
            PrivacyAndPolicyScreenUIEffect.NavigateBack -> navigator.pop()
            is PrivacyAndPolicyScreenUIEffect.ShowSnackBarError -> {
                snackBarController.showSnackBarError(
                    message = effect.errorStringResource
                )
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun PrivacyAndPolicyScreenPreview() {

    val fakeState = PrivacyAndPolicyScreenUIState(
        lastUpdateDate = "12/2/2025",
        privacyAndPolicySections = listOf(
            PrivacyAndPolicySectionUIState(
                title = "What is Lorem Ipsum?",
                content = "is simply dummy text of the printing and typesetting industry. " +
                          "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s," +
                          " when an unknown printer took a galley of type and scrambled it to make a type specimen book." +
                          " It has survived not only five centuries"
            ),
            PrivacyAndPolicySectionUIState(
                title = "What is Lorem Ipsum?",
                content = "is simply dummy text of the printing and typesetting industry. " +
                          "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s," +
                          " when an unknown printer took a galley of type and scrambled it to make a type specimen book." +
                          " It has survived not only five centuries"
            ),
            PrivacyAndPolicySectionUIState(
                title = "What is Lorem Ipsum?",
                content = "is simply dummy text of the printing and typesetting industry. " +
                          "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s," +
                          " when an unknown printer took a galley of type and scrambled it to make a type specimen book." +
                          " It has survived not only five centuries"
            )
        )
    )

    val fakeListener = object : PrivacyAndPolicyScreenInteractionListener {
        override fun onClickBack() {}
    }

    MenaTheme {
        PrivacyAndPolicyScreen().OnRender(
            state = fakeState,
            listener = fakeListener
        )
    }
}