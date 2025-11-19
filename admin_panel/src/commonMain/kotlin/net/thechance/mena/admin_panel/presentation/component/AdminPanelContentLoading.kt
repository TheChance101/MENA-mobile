package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator

@Composable
fun AdminPanelContentLoading() {
    Box(modifier = Modifier.fillMaxSize()) {
        DotsProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -(76.dp)),
            dotSize = 16.dp,
            spaceBetween = 4.dp
        )
    }
}