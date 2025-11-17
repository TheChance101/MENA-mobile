package net.thechance.mena.identity.presentation.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
internal fun AuthPrompt(
    message:String,
    actionLabel:String,
    onActionClick:()->Unit,
    modifier: Modifier = Modifier,
    isEnabled : Boolean = true
) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = message,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeSecondary,
            modifier = Modifier.padding(end = Theme.spacing._4)
        )
        TextButton(
            text = actionLabel,
            onClick = onActionClick,
            isEnabled = isEnabled
        )
    }
}