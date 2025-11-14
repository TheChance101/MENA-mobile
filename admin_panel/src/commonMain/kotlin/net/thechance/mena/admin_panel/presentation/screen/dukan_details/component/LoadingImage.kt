package net.thechance.mena.admin_panel.presentation.screen.dukan_details.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator

@Composable
internal fun LoadingImage(modifier: Modifier = Modifier){
    DotsProgressIndicator(
        modifier = modifier,
        dotSize = 4.dp,
        spaceBetween = 2.dp
    )
}