package net.thechance.mena.dukan.presentation.screen.createDukan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview


val color = listOf<Long>(
    0xFFE91E63,
    0xFF2196F3,
    0xFF4CAF50,
    0xFFE91E63,
    0xFF2196F3,
    0xFF4CAF50,
    0xFFE91E63,
    0xFF2196F3,
)

@Composable
fun DukanThemeScreen() {
    DukanThemeContent()
}

@Composable
private fun DukanThemeContent() {
    Column(
        modifier = Modifier.fillMaxSize().background(Theme.colorScheme.background.surface)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        AppBar(
            title = "Create new Dukan",
            titleColor = Theme.colorScheme.shadePrimary,
            leadingContent = {

//                Icon(
//                    painter = painterResource(),
//                    contentDescription = "back icon"
//                )
            },
            onLeadingClick = {},
        )

        Text(
            text = "Customize your dukan",
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary
        )
        Text(
            text = "Pick color and style for dukan",
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary
        )
        Text(
            text = "Color",
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(top = Theme.spacing._16, bottom = Theme.spacing._4)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        ) {
            color.forEach {
                DukanColor(
                    backgroundColor = Color(it),
                    onClick = {},
                    isSelected = true
                )
            }
        }
        Text(
            text = "Style",
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(top = Theme.spacing._16, bottom = Theme.spacing._4)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DukanStyle(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(top = 4.dp, bottom = 8.dp, start = 4.dp, end = 4.dp)
            )
            DukanStyle(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(top = 4.dp, start = 4.dp, end = 4.dp)
            )
            DukanStyle(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(top = 24.dp, start = 4.dp),
                hasImage = false
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {},
            isEnabled = true,
            shape = RoundedCornerShape(Theme.radius.md),
            containerColor = Theme.colorScheme.primary.primary,
            contentColor = Theme.colorScheme.primary.onPrimary,
            disabledContainerColor = Theme.colorScheme.disabled,
            disabledContentColor = Theme.colorScheme.textDisabled,
            contentPadding = PaddingValues(vertical = 13.dp),
            content = {
                MenaText(
                    text = "Create",
                    style = Theme.typography.label.medium,
                    color = Theme.colorScheme.primary.onPrimary
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}


@Preview
@Composable
private fun DukanThemeScreenPreview() {
    MenaTheme {
        DukanThemeScreen()
    }
}