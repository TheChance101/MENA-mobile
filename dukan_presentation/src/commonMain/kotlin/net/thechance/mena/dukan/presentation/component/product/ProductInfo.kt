package net.thechance.mena.dukan.presentation.component.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun ProductInfo(
    name: String,
    description: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = name,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary,
            maxLines = 1,
        )

        description?.let {
            Text(
                modifier = Modifier.padding(top = Theme.spacing._2),
                text = it,
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeTertiary,
                maxLines = 2,
                minLines = 2,
            )
        }
    }
}
