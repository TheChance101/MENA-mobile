package net.thechance.mena.identity.presentation.screen.contactUs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.back
import mena.identity_presentation.generated.resources.contact_email_address
import mena.identity_presentation.generated.resources.contact_facebook_account
import mena.identity_presentation.generated.resources.contact_info
import mena.identity_presentation.generated.resources.contact_phone_number
import mena.identity_presentation.generated.resources.contact_us
import mena.identity_presentation.generated.resources.ic_arrow_left
import mena.identity_presentation.generated.resources.ic_facebook
import mena.identity_presentation.generated.resources.ic_mailbox
import mena.identity_presentation.generated.resources.ic_telephone
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.screen.contactUs.components.ContactCard
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class ContactUsScreen : BaseScreen<
        ContactUsViewModel,
        ContactUsUIState,
        ContactUsUIEffect,
        ContactUsInteractionListener>() {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<ContactUsViewModel>()
        val uriHandler = LocalUriHandler.current

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                if (effect is ContactUsUIEffect.OpenUrl) {
                    uriHandler.openUri(effect.url)
                }
            }
        }

        InitScreen(viewModel)
    }

    @Composable
    override fun OnRender(
        state: ContactUsUIState,
        listener: ContactUsInteractionListener,
    ) {
        val scrollState = rememberScrollState()

        Scaffold(
            topBar = {
                AppBar(
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 14.dp),
                    title = stringResource(Res.string.contact_us),
                    leadingContent = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_left),
                            contentDescription = stringResource(Res.string.back),
                        )
                    },
                    onLeadingClick = listener::onClickBack,
                    modifier = Modifier.padding(horizontal = Theme.spacing._16)
                )
            }
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .verticalScroll(scrollState)
                    .background(Theme.colorScheme.background.surface)
                    .padding(Theme.spacing._16),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
            ) {
                Text(
                    text = stringResource(Res.string.contact_info),
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary,
                    modifier = Modifier.padding(bottom = Theme.spacing._4),
                )

                ContactCard(
                    icon = Res.drawable.ic_mailbox,
                    title = Res.string.contact_email_address,
                    info = state.displayedEmail,
                    onClick = listener::onClickEmailAddress
                )

                ContactCard(
                    icon = Res.drawable.ic_telephone,
                    title = Res.string.contact_phone_number,
                    info = state.displayedPhoneNumber,
                    onClick = listener::onClickPhoneNumber
                )

                ContactCard(
                    icon = Res.drawable.ic_facebook,
                    title = Res.string.contact_facebook_account,
                    info = state.displayedFacebookAccount,
                    onClick = listener::onClickFacebookAccount
                )
            }
        }
    }

    override fun onEffect(
        effect: ContactUsUIEffect,
        navigator: Navigator,
    ) {
        when (effect) {
            ContactUsUIEffect.NavigateBack -> navigator.pop()
            is ContactUsUIEffect.OpenUrl -> {}
        }
    }
}
