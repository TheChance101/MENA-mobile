package net.thechance.mena.trends.presentation.shared.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.next
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource

@Composable
fun NextButton(
    onNextClick: () -> Unit,
    isButtonEnabled: Boolean,
    isButtonLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onNextClick,
        isEnabled = isButtonEnabled,
        isLoading = isButtonLoading,
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colorScheme.background.surface)
            .padding(
                bottom = Theme.spacing._24
            ),
        shape = RoundedCornerShape(Theme.radius.md),
        containerColor = Theme.colorScheme.primary.primary,
        disabledContainerColor = Theme.colorScheme.primary.primary.copy(alpha = 0.5f),
        contentColor = Theme.colorScheme.primary.onPrimary,
        disabledContentColor = Theme.colorScheme.primary.onPrimary.copy(alpha = 0.5f),
    ) { contentColor ->
        Text(
            text = stringResource(Res.string.next),
            color = contentColor,
            style = Theme.typography.label.medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(
                    vertical = 13.dp,
                    horizontal = Theme.spacing._24
                )
        )
    }
}