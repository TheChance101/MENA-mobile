package net.thechance.mena.designsystem.presentation.component.segment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.thechance.mena.designsystem.presentation.component.preview.PreviewComponent
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Segment(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(top = 16.dp),
    content: SegmentScope .() -> Unit
) {
    val scope = remember { SegmentScopeImpl() }.apply {
        items.clear()
        content()
    }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { scope.items.size })
    val coroutineScope = rememberCoroutineScope()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(Theme.radius.md))
                .background(Theme.colorScheme.background.surfaceHigh)
        ) {
            scope.items.forEachIndexed { index, item ->
                SegmentButton(
                    item.title,
                    item.title == scope.items[pagerState.currentPage].title,
                    onSelectChange = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    })
            }
        }

        HorizontalPager(modifier = modifier.padding(contentPadding), state = pagerState) { page ->
            scope.items.getOrNull(page)?.content(scope)
        }
    }
}

@Preview
@Composable
private fun SegmentPreview() {
    MenaTheme {
        PreviewComponent(
            title = "Segment buttons"
        ) {
            Segment(contentPadding = PaddingValues(top = 8.dp)) {
                item("Option1") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(Theme.radius.md))
                            .background(Theme.colorScheme.background.surfaceHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Option1")
                    }
                }
                item("Option2") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(Theme.radius.md))
                            .background(Theme.colorScheme.background.surfaceHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Option2")
                    }
                }
            }
        }
    }
}