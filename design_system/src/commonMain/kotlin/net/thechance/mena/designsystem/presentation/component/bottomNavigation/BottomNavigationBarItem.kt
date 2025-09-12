package net.thechance.mena.designsystem.presentation.component.bottomNavigation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.icon.MenaIcon
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun BottomNavigationBarItem(
    isSelected: Boolean,
    notSelectedIcon: Painter,
    selectedIcon: Painter,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val painter = if (isSelected) selectedIcon else notSelectedIcon
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .height(76.dp)
            .then(
                if (isSelected) Modifier
                else Modifier.clickable(
                    onClick = onClick,
                    indication = null,
                    interactionSource = interactionSource
                )
            )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(
                    alignment = Alignment.TopCenter
                )
        ) {
            MenaIcon(
                painter = painter,
                modifier = Modifier.size(24.dp)
            )

            if (isSelected) {
                MenaText(
                    text = title,
                    style = Theme.typography.label.medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}