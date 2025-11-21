package net.thechance.mena.identity.presentation.screen.privacyAndPolicy.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.last_update
import mena.identity_presentation.generated.resources.privacy_logo
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.screen.privacyAndPolicy.PrivacyAndPolicyScreenUIState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PrivacyScreenContent(
    state: PrivacyAndPolicyScreenUIState,
) {
    PrivacyScreenContainer {

        item {
            Image(
                painter = painterResource(Res.drawable.privacy_logo),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(64.dp)
            )
        }

        item {
            Text(
                text = stringResource(Res.string.last_update, state.lastUpdateDate),
                textAlign = TextAlign.Center,
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeTertiary,
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = Theme.spacing._4)
            )
        }

        items(state.privacyAndPolicySections) { item ->
            PrivacySection(
                title = item.title,
                content = item.content
            )
        }

    }
}