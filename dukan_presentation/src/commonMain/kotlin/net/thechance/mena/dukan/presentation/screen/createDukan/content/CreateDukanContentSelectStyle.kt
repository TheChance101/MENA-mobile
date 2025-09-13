package net.thechance.mena.dukan.presentation.screen.createDukan.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.createDukan.content.component.DukanStyle
import net.thechance.mena.dukan.presentation.screen.createDukan.content.component.DukanColor
import org.jetbrains.compose.ui.tooling.preview.Preview

val color = listOf(
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
fun CreateDukanContentSelectStyle() {
    Column(
        modifier = Modifier.fillMaxSize().background(Theme.colorScheme.background.surface)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
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
                    isSelected = false
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
                orientation = DukanStyle.HORIZONTAL,
                hasImage = true,
                onClick = {},
                isSelected = false,
                contentPadding = PaddingValues(top = 4.dp, bottom = 8.dp, start = 4.dp, end = 4.dp)
            )
            DukanStyle(
                modifier = Modifier.weight(1f),
                orientation = DukanStyle.VERTICAL,
                hasImage = true,
                onClick = {},
                isSelected = false,
                contentPadding = PaddingValues(top = 4.dp, start = 4.dp, end = 4.dp)
            )
            DukanStyle(
                modifier = Modifier.weight(1f),
                orientation = DukanStyle.HORIZONTAL,
                hasImage = false,
                onClick = {},
                isSelected = false,
                contentPadding = PaddingValues(top = 24.dp, start = 4.dp),
            )
        }
    }
}

@Preview
@Composable
private fun CreateDukanContentSelectStylePreview() {
    MenaTheme {
        CreateDukanContentSelectStyle()
    }
}