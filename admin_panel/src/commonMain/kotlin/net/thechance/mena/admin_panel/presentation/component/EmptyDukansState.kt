package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.img_empty_dukan
import net.thechance.mena.admin_panel.resources.no_dukan_results
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmptyDukansState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        StatePlaceholder(
            image = painterResource(Res.drawable.img_empty_dukan),
            title = stringResource(Res.string.no_dukan_results),
            description = stringResource(Res.string.no_dukan_results),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}