package net.thechance.mena.dukan.presentation.component.productCard

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.edit_product_pencil
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EditProductIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconBackground(
        drawableResource = Res.drawable.edit_product_pencil,
        contentDescription = stringResource(Res.string.edit_product_pencil),
        backgroundColor = Theme.colorScheme.primary.primary,
        iconModifier = Modifier.padding(Theme.spacing._8),
        boxModifier = modifier,
        onClick = onClick,
    )
}

@Preview
@Composable
private fun EditProductIconPreview() {
    MenaTheme {
        EditProductIcon(onClick = {})
    }
}