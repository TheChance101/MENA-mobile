package net.thechance.mena.identity.presentation.feature.profileFlow.privacyAndPolicy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.privacy_and_policy
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.identity.presentation.core.base.BaseScreen
import net.thechance.mena.identity.presentation.core.components.AuthAppBar
import net.thechance.mena.identity.presentation.core.components.ErrorSnackBar
import net.thechance.mena.identity.presentation.feature.profileFlow.privacyAndPolicy.components.PrivacyScreenContent
import net.thechance.mena.identity.presentation.feature.profileFlow.privacyAndPolicy.components.PrivacyScreenContentShimmer
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
            ){
                PrivacyScreenContentShimmer()
            }
            AnimatedVisibility(
                visible = !state.isLoading && !state.privacyAndPolicySections.isEmpty(),
                enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                exit = fadeOut(animationSpec = tween(durationMillis = 500))
            ){
                PrivacyScreenContent(state,listener)
            }
        }
        ErrorSnackBar(
            errorMessage = state.errorMessage?.let { stringResource(it) },
            onDismiss = {
                listener.onClearErrorMessage()
            },
            modifier = Modifier.statusBarsPadding()
        )
    }

    override fun onEffect(
        effect: PrivacyAndPolicyScreenUIEffect,
        navigator: Navigator
    ) {
        when (effect) {
            PrivacyAndPolicyScreenUIEffect.NavigateBack -> navigator.pop()
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
        override fun onClearErrorMessage() {}
    }

    MenaTheme {
        PrivacyAndPolicyScreen().OnRender(
            state = fakeState,
            listener = fakeListener
        )
    }
}