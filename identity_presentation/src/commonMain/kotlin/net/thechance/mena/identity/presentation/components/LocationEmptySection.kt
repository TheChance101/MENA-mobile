package net.thechance.mena.identity.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LocationEmptySection(
    iconPainter: Painter,
    iconContentDescriptionResource: StringResource,
    titleResource: StringResource,
    messageResource: StringResource,
    buttonTextResource: StringResource,
    onButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = iconPainter,
            modifier = Modifier
                .size(128.dp)
                .padding(bottom = Theme.spacing._24),
            contentDescription = stringResource(iconContentDescriptionResource),
        )

        Text(
            text = stringResource(titleResource),
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = stringResource(messageResource),
            modifier = Modifier.padding(top = Theme.spacing._8, bottom = Theme.spacing._24),
            textAlign = TextAlign.Center,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary
        )

        PrimaryButton(
            modifier = Modifier.fillMaxWidth().padding(bottom = Theme.spacing._12),
            text = stringResource(buttonTextResource),
            contentPadding = PaddingValues(horizontal = Theme.spacing._24),
            onClick = onButtonClicked,
            isEnabled = !isLoading,
            isLoading = isLoading
        )
    }
}