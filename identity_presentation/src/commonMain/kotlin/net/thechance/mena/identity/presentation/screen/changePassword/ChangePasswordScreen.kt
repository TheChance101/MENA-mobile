package net.thechance.mena.identity.presentation.screen.changePassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.profile_change_password
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthAppBar
import net.thechance.mena.identity.presentation.components.snackBar.IdentitySnackBarController
import net.thechance.mena.identity.presentation.screen.changePassword.ChangePasswordScreenUIEffect.NavigateBack
import net.thechance.mena.identity.presentation.screen.changePassword.components.CurrentPasswordContent
import net.thechance.mena.identity.presentation.screen.changePassword.components.NewPasswordContent
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview


class ChangePasswordScreen() : BaseScreen<ChangePasswordScreenViewModel,
    ChangePasswordScreenUIState,
    ChangePasswordScreenUIEffect,
    ChangePasswordScreenInteractionListener>() {

    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun OnRender(
        state: ChangePasswordScreenUIState,
        listener: ChangePasswordScreenInteractionListener
    ) {
        val pagerState =
            rememberPagerState(initialPage = state.currentPage.index, pageCount = { 2 })

        LaunchedEffect(state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage.index)
        }
        Scaffold(
            topBar = {
                AuthAppBar(
                    title = stringResource(Res.string.profile_change_password),
                    onClickBack = listener::onClickBack
                )
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(Theme.colorScheme.background.surface)
                    .padding(horizontal = Theme.spacing._16)
                    .padding(top = Theme.spacing._24)
            ) {
                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = false,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    when (page) {
                        PasswordPage.CURRENT_PASSWORD.index -> CurrentPasswordContent(
                            state = state.currentPasswordUIState,
                            isLoading = state.isLoading,
                            onClickContinue = listener::onClickContinue,
                            onChangeCurrentPassword = listener::onChangeCurrentPassword,
                            onToggleCurrentPasswordVisibility = listener::onToggleCurrentPasswordVisibility
                        )

                        PasswordPage.NEW_PASSWORD.index -> NewPasswordContent(
                            state = state.newPasswordUIState,
                            isLoading = state.isLoading,
                            listener = listener
                        )
                    }
                }

            }
        }
        BackHandler(enabled = true)
        {
            listener.onClickBack()
        }
    }

    override fun onEffect(
        effect: ChangePasswordScreenUIEffect,
        navigator: Navigator,
        snackBarController: IdentitySnackBarController
    ) {
        when (effect) {
            is NavigateBack -> {
                effect.successStringResource?.let { successMessage ->
                    snackBarController.showSnackBarSuccess(
                        message = successMessage
                    )
                }
                navigator.pop()
            }

            is ChangePasswordScreenUIEffect.ShowSnackBarError -> {
                snackBarController.showSnackBarError(message = effect.errorStringResource)
            }
        }
    }
}


@Preview
@Composable
private fun ChangePasswordScreenPreview() {
    val listener = object : ChangePasswordScreenInteractionListener {
        override fun onClickBack() {}

        override fun onClickContinue() {}

        override fun onClickSave() {}

        override fun onChangeCurrentPassword(newValue: String) {}

        override fun onChangeNewPassword(newValue: String) {}

        override fun onChangeConfirmPassword(newValue: String) {}

        override fun onToggleCurrentPasswordVisibility() {}

        override fun onToggleNewPasswordVisibility() {}

        override fun onToggleConfirmPasswordVisibility() {}

    }
    MenaTheme {
        ChangePasswordScreen().OnRender(
            state = ChangePasswordScreenUIState(),
            listener = listener
        )
    }
}

