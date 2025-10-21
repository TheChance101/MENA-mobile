package net.thechance.mena.dukan.presentation.screen.createProduct.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_product_
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TopAppBar(
    onBackClick: () -> Unit
) {
    AppBar(
        title = stringResource(resource = Res.string.add_product_),
        titleColor = Theme.colorScheme.shadePrimary,
        modifier = Modifier
            .background(color = Theme.colorScheme.background.surface)
            .padding(top = Theme.spacing._16, bottom = Theme.spacing._8),
        leadingContent = {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back_arrow)
            )
        },
        onLeadingClick = onBackClick,
    )
}

@Preview
@Composable
private fun AppBarPreview() {
    MenaTheme {
        TopAppBar(
            onBackClick = {}
        )
    }
}