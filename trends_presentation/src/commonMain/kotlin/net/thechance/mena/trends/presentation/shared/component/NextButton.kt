package net.thechance.mena.trends.presentation.shared.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.next
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import org.jetbrains.compose.resources.stringResource

@Composable
fun NextButton(
    onNextClick: () -> Unit,
    isButtonEnabled: Boolean,
    isButtonLoading: Boolean,
    modifier: Modifier = Modifier
) {
    PrimaryButton(
        modifier = modifier.fillMaxWidth(),
        text = stringResource(Res.string.next),
        onClick = onNextClick,
        isEnabled = isButtonEnabled,
        isLoading = isButtonLoading,
        contentPadding = PaddingValues(vertical = 13.dp)
    )
}