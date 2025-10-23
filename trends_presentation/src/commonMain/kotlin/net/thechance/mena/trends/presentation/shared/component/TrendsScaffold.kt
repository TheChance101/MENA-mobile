import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.component.NoConnection

@Composable
fun TrendsScaffold(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Theme.colorScheme.background.surface,
    topBar: (@Composable () -> Unit)? = null,
    bottomContent: (@Composable () -> Unit)? = null,
    overlays: (ScaffoldScope.() -> Unit)? = null,
    errorState: ErrorState? = null,
    onRetry: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Scaffold(
            topBar = { topBar?.invoke() },
            overlays = overlays ?: {},
            bottomBar = {
                when (errorState) {
                    null -> bottomContent?.let {
                        Box(modifier = Modifier.align(Alignment.BottomCenter)) { it() }
                    }
                    else -> Unit
                }
            },
            content = {
                when (errorState) {
                    is ErrorState.NoInternet -> NoConnection { onRetry?.invoke() }
                    else -> content()
                }
            }
        )
    }
}