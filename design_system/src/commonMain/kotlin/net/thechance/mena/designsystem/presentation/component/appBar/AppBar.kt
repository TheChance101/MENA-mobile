package net.thechance.mena.designsystem.presentation.component.appBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mena.design_system.generated.resources.Res
import mena.design_system.generated.resources.checkmark
import mena.design_system.generated.resources.ic_arrow_left
import mena.design_system.generated.resources.ic_user
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppBar(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: Color = Theme.colorScheme.shadePrimary,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    leadingContent: (@Composable () -> Unit)? = null,
    onLeadingClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        leadingContent?.let { content ->
            AppBarOptionContainer(
                onClick = onLeadingClick,
                modifier = Modifier.padding(end = 8.dp),
                content = content
            )
        }
        Text(
            text = title,
            color = titleColor,
            style = Theme.typography.title.medium
        )
        Spacer(modifier = Modifier.weight(1f))
        trailingContent?.let {
            Row(
                modifier = Modifier.padding(start = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                trailingContent()
            }
        }
    }
}

@Preview
@Composable
private fun AppBarPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colorScheme.background.surface),
            contentAlignment = Alignment.Center
        ) {
            AppBar(
                title = "Screen title"
            )
        }
    }
}

@Preview
@Composable
private fun AppBarWithBackNavigationPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colorScheme.background.surfaceLow),
            contentAlignment = Alignment.Center
        ) {
            AppBar(
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = null
                    )
                },
                title = "Screen title",
            )
        }
    }
}

@Preview
@Composable
private fun AppBarWithOptionsPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colorScheme.background.surfaceLow),
            contentAlignment = Alignment.Center
        ) {
            AppBar(
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = null
                    )
                },
                onLeadingClick = {},
                title = "Screen title",
                trailingContent = {
                    AppBarOptionContainer(
                        isBadgeVisible = true,
                        onClick = {},
                        badgeColor = Theme.colorScheme.error
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_user),
                            contentDescription = null
                        )
                    }
                    AppBarOptionContainer(
                        isBadgeVisible = true,
                        onClick = {},
                        badgeColor = Theme.colorScheme.primary.primary
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.checkmark),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}
