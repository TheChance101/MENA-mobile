package net.thechance.mena.dukan.presentation.component.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.edit_product_pencil
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EditProductIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(Res.drawable.edit_product_pencil),
        contentDescription = stringResource(Res.string.edit_product_pencil),
        modifier = modifier
            .clickable(onClick = onClick)
            .background(
                color = Theme.colorScheme.primary.primary,
                shape = RoundedCornerShape(size = Theme.radius.full)
            )
            .padding(Theme.spacing._8)
    )
}

@Preview
@Composable
private fun EditProductIconPreview() {
    MenaTheme {
        EditProductIcon(onClick = {})
    }
}