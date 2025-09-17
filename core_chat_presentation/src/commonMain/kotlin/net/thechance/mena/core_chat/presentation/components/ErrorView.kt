package net.thechance.mena.core_chat.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_phone_back
import mena.core_chat_presentation.generated.resources.retry
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ErrorView(
    title: String,
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MenaText(
            text = title,
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        MenaText(
            text = message,
            modifier = Modifier.padding(top = Theme.spacing._2, bottom = Theme.spacing._12),
            textAlign = TextAlign.Center,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary
        )
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.retry),
            contentPadding = PaddingValues(vertical = Theme.spacing._12, horizontal = Theme.spacing._16),
            trailingIcon = painterResource(Res.drawable.ic_phone_back),
            onClick = onRetry,
        )
    }
}