package net.thechance.mena.designsystem.presentation.component.bottomNavigation

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun BottomNavigationBarItem(
    isSelected: Boolean,
    unselectedIcon: Painter,
    selectedIcon: Painter,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val painter = if (isSelected) selectedIcon else unselectedIcon
    val animatedIconTint by animateColorAsState(
        targetValue = if (isSelected) Theme.colorScheme.brand.brand else Theme.colorScheme.shadeSecondary,
    )
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
            Icon(
                painter = painter,
                modifier = Modifier.size(24.dp),
                contentDescription = title,
                tint = animatedIconTint
            )

            if (isSelected) {
                Text(
                    text = title,
                    style = Theme.typography.label.medium,
                    color = Theme.colorScheme.brand.brand,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}