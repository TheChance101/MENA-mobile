package net.thechance.mena.faith.presentation.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun FaithScaffold(
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    snackBar: (@Composable () -> Unit)? = null,
    backgroundColor: Color = Theme.colorScheme.background.surface,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .then(modifier)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            topBar?.let { topBarContent -> topBarContent() }
            content()
        }

        snackBar?.let { snackBarContent ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(start = 16.dp, end = 16.dp, top = 68.dp)
            ) {
                snackBarContent()
            }
        }
    }
}