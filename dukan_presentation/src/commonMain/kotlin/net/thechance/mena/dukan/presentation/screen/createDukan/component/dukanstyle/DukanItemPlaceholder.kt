package net.thechance.mena.dukan.presentation.screen.createDukan.component.dukanstyle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_shopping_basket
import mena.dukan_presentation.generated.resources.ic_add_shopping_basket
import mena.dukan_presentation.generated.resources.ic_image
import mena.dukan_presentation.generated.resources.style_has_image
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DukanImageItemPlaceholder(
    modifier: Modifier = Modifier,
) {
    Icon(
        painter = painterResource(Res.drawable.ic_image),
        tint = Theme.colorScheme.stroke,
        contentDescription = stringResource(Res.string.style_has_image),
        modifier = modifier.size(24.dp)
    )
}

@Composable
fun ShoppingCartPlaceholder(
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(Res.drawable.ic_add_shopping_basket),
        tint = Theme.colorScheme.primary.onPrimary,
        contentDescription = stringResource(Res.string.add_shopping_basket),
        modifier = modifier
            .padding(3.dp)
            .size(10.dp)
    )
}

@Preview
@Composable
private fun DukanImageItemPlaceholderPreview() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        DukanImageItemPlaceholder()
        ShoppingCartPlaceholder()
    }
}
