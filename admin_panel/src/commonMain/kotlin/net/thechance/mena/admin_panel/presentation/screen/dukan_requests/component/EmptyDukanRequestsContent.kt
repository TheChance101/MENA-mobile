package net.thechance.mena.admin_panel.presentation.screen.dukan_requests.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.thechance.mena.admin_panel.presentation.component.StatePlaceholder
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.img_empty_dukan
import net.thechance.mena.admin_panel.resources.no_dukan_requests
import net.thechance.mena.admin_panel.resources.no_dukan_results_description_for_requests
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmptyDukanRequests(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        StatePlaceholder(
            image = painterResource(Res.drawable.img_empty_dukan),
            title = stringResource(Res.string.no_dukan_requests),
            description = stringResource(Res.string.no_dukan_results_description_for_requests),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}