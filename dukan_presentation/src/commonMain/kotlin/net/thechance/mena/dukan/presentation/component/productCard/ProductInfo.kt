package net.thechance.mena.dukan.presentation.component.productCard

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
            color = Theme.colorScheme.shadePrimary
        )

        description?.let {
            Text(
                text = it,
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeTertiary,
                modifier = Modifier.padding(top = Theme.spacing._2)
            )
        }
    }
}
