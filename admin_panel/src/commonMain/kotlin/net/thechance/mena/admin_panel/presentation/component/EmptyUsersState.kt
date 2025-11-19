package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.empty_users
import net.thechance.mena.admin_panel.resources.no_users_yet
import net.thechance.mena.admin_panel.resources.users_will_appear
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmptyUsersState(modifier: Modifier = Modifier){
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        StatePlaceholder(
            image = painterResource(Res.drawable.empty_users),
            title = stringResource(Res.string.no_users_yet),
            description = stringResource(Res.string.users_will_appear),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}