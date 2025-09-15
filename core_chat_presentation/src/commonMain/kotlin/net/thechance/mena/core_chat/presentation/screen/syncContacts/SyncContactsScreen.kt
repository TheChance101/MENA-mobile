package net.thechance.mena.core_chat.presentation.screen.syncContacts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_arrow_left
import mena.core_chat_presentation.generated.resources.sync_contacts
import net.thechance.mena.core_chat.presentation.navigation.LocalNavController
import net.thechance.mena.core_chat.presentation.screen.syncContacts.components.ContactsSyncedView
import net.thechance.mena.core_chat.presentation.screen.syncContacts.components.NoContactsSyncView
import net.thechance.mena.core_chat.presentation.screen.syncContacts.components.PhoneIcon
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.MenaIcon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun SyncContactsScreen(
) {
    var isSyncing by remember { mutableStateOf(false) }
    SyncContactsContent(
        isSyncing = isSyncing,
        onSyncClick = {
            isSyncing = true
        },
    )
}

@Composable
private fun SyncContactsContent(
    isSyncing: Boolean,
    onSyncClick: () -> Unit,
) {
    val navController = LocalNavController.current
    Column(
        modifier = Modifier.fillMaxSize()
            .background(color = Theme.colorScheme.background.surface)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        AppBar(
            title = stringResource(Res.string.sync_contacts),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            leadingContent = {
                MenaIcon(
                    painter = painterResource(Res.drawable.ic_arrow_left),
                    modifier = Modifier.size(20.dp),
                    contentDescription = null,
                    tint = Theme.colorScheme.primary.primary,
                )
            },
            onLeadingClick = {
                navController.popBackStack()
            },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Theme.spacing._24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            PhoneIcon()
            if (isSyncing) {
                ContactsSyncedView(
                    modifier = Modifier.padding(top = Theme.spacing._24),
                )
            } else {
                NoContactsSyncView(
                    modifier = Modifier.padding(top = Theme.spacing._12),
                    onSyncClick = onSyncClick,
                )
            }
        }
    }
}

@Composable
@Preview()
private fun SyncContactsScreenPreview() {
    MenaTheme {
        SyncContactsScreen()
    }
}
