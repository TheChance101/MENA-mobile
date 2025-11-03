package net.thechance.mena.identity.presentation.screen.register.accountCreated

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.go_to_home
import mena.identity_presentation.generated.resources.ic_account_shield
import mena.identity_presentation.generated.resources.login_background
import mena.identity_presentation.generated.resources.success_account_created_description
import mena.identity_presentation.generated.resources.success_account_created_title
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

class AccountCreatedScreen :
            BaseScreen<AccountCreatedViewModel,
            AccountCreatedUIState,
            AccountCreatedUIEffect,
            AccountCreatedInteractionListener>() {

    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @Composable
    override fun OnRender(
        state: AccountCreatedUIState,
        listener: AccountCreatedInteractionListener
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colorScheme.background.surface)
        ) {
            Box(modifier = Modifier.fillMaxSize())
            {
                Image(
                    painter = painterResource(Res.drawable.login_background),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Theme.spacing._24)
                        .padding(top = Theme.spacing._24)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        SuccessMessageBlock()
                    }

                    PrimaryButton(
                        text = stringResource(Res.string.go_to_home),
                        onClick = listener::onClickGoToHome,
                        isLoading = false,
                        contentPadding = PaddingValues(vertical = 13.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Theme.spacing._24)
                            .imePadding()
                    )
                }
            }
        }
    }

    @Composable
    private fun SuccessMessageBlock(
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_account_shield),
                contentDescription = stringResource(Res.string.success_account_created_title),
                modifier = Modifier
                    .size(128.dp)
                    .padding(bottom = Theme.spacing._12)
            )
            Text(
                text = stringResource(Res.string.success_account_created_title),
                style = Theme.typography.title.medium,
                color = Theme.colorScheme.shadePrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = Theme.spacing._2)
            )
            Text(
                text = stringResource(Res.string.success_account_created_description),
                style = Theme.typography.label.large,
                color = Theme.colorScheme.shadeSecondary,
                textAlign = TextAlign.Center
            )
        }
    }

    override fun onEffect(
        effect: AccountCreatedUIEffect,
        navigator: Navigator
    ) {
        when (effect) {
            AccountCreatedUIEffect.NavigateToHome -> {}
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        AccountCreatedScreen().OnRender(
            state = AccountCreatedUIState,
            listener = object : AccountCreatedInteractionListener {
                override fun onClickGoToHome() {}
            }
        )
    }
}