package net.thechance.mena.dukan.presentation.component.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_product
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.delete_icon
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_delete
import mena.dukan_presentation.generated.resources.manage_product
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TopAppBar(
    title: String,
    onBackClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null
) {
    AppBar(
        title = title,
        titleColor = Theme.colorScheme.shadePrimary,
        modifier = Modifier
            .background(color = Theme.colorScheme.background.surface)
            .padding(bottom = Theme.spacing._8),
        leadingContent = {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back_arrow),
                tint = Theme.colorScheme.primary.primary
            )
        },
        onLeadingClick = onBackClick,
        trailingContent = onDeleteClick?.let { deleteClick ->
            {
                AppBarOptionContainer(
                    onClick = deleteClick,
                    content = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_delete),
                            contentDescription = stringResource(Res.string.delete_icon),
                        )
                    }
                )
            }
        }
    )
}

@Preview
@Composable
private fun AppBarPreview() {
    MenaTheme {
        TopAppBar(
            title = stringResource(Res.string.add_product),
            onBackClick = {},
        )
    }
}

@Preview
@Composable
private fun AppBarWithDeletePreview() {
    MenaTheme {
        TopAppBar(
            onBackClick = {},
            title = stringResource(Res.string.manage_product),
            onDeleteClick = {}
        )
    }
}
